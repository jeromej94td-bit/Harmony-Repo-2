const cors = {
  "access-control-allow-origin": "*",
  "access-control-allow-headers": "authorization, x-client-info, apikey, content-type",
  "access-control-allow-methods": "POST, OPTIONS",
  "content-type": "application/json; charset=utf-8",
};

const reply = (body, status = 200) => new Response(JSON.stringify(body), { status, headers: cors });
const text = (value, max = 4000) => typeof value === "string" ? value.trim().slice(0, max) : "";

const decode = (value) => String(value)
  .replace(/<[^>]+>/g, "")
  .replace(/&amp;/g, "&")
  .replace(/&quot;/g, "\"")
  .replace(/&#x27;|&#39;/g, "'")
  .replace(/&nbsp;/g, " ")
  .trim();

async function webFallback(fetchImpl, query) {
  const response = await fetchImpl(`https://html.duckduckgo.com/html/?q=${encodeURIComponent(query)}`, {
    headers: { "user-agent": "Mozilla/5.0 HarmonyBrain/1.0" },
  });
  if (!response.ok) return [];
  const html = await response.text();
  const result = [];
  const pattern = /<a[^>]*class="[^"]*result__a[^"]*"[^>]*>([\s\S]*?)<\/a>/gi;
  for (const match of html.matchAll(pattern)) {
    const title = decode(match[1]);
    if (title && !result.includes(title)) result.push(title);
    if (result.length === 3) break;
  }
  return result;
}

function sources(metadata) {
  const seen = new Set();
  return (Array.isArray(metadata?.groundingChunks) ? metadata.groundingChunks : [])
    .flatMap((chunk) => {
      const url = text(chunk?.web?.uri, 2048);
      if (!/^https?:\/\//i.test(url) || seen.has(url)) return [];
      seen.add(url);
      return [{ title: text(chunk?.web?.title, 180) || "Treffer öffnen", url }];
    })
    .slice(0, 3);
}

function answer(candidate) {
  return (candidate?.content?.parts ?? [])
    .map((part) => text(part?.text))
    .filter(Boolean)
    .join("\n")
    .replace(/```[\s\S]*?```/g, "")
    .replace(/[\*_`#]/g, "")
    .replace(/\n{3,}/g, "\n\n")
    .trim()
    .slice(0, 1250);
}

function isLocalSearch(query) {
  const q = query.toLocaleLowerCase("de-DE");
  return ["restaurant", "hotel", "café", "cafe", "bar", "brunch", "essen", "laden", "shop", "shopping", "in der nähe", "adresse", "öffnungszeiten", "wedding", "tiergarten", "berlin", "nürnberg", "hamburg", "köln", "münchen"].some((word) => q.includes(word));
}

const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function requestGemini(fetchImpl, apiKey, query, mode, context = {}) {
  const search = mode === "search";
  const interests = JSON.stringify(context?.interests ?? context?.preferences ?? context?.profile?.couple ?? [])
    .replace(/[{}\[\]"]/g, " ")
    .replace(/\s+/g, " ")
    .trim()
    .slice(0, 700);
  const prompt = mode === "questions"
    ? `Erzeuge genau 5 neue Paarfragen als gültiges JSON ohne Markdown: {"questions":[{"text":"...","category":"...","difficulty":"easy|medium|deep","topic":"..."}]}. Keine Wiederholungen und jede Frage maximal 180 Zeichen. Auftrag: ${query}`
    : mode === "recommendations"
      ? `Erzeuge maximal 3 kurze Empfehlungen als gültiges JSON ohne Markdown: {"recommendations":[{"title":"...","description":"..."}]}. Auftrag: ${query}`
      : search
        ? `Du bist eine einfache Websuche für ein Paar. Suche genau 3 reale, aktuell auffindbare Orte, passend zur Nutzeranfrage und exakt zum genannten Ort oder Stadtteil. Berücksichtige diese Paar-Interessen nur zur Auswahl, sofern sie dazu passen: ${interests || "keine gespeicherten Interessen"}. Antworte ausschließlich so, ohne Einleitung, Quellen, Links, Maps oder Erklärungen: 1. Name – ein kurzer Grund, warum es zu den Interessen passt. 2. Name – kurzer Grund. 3. Name – kurzer Grund. Erfinde niemals Namen, Adressen oder Öffnungszeiten. Anfrage: ${query}`
        : `Du bist Harmony Brain. Antworte auf Deutsch, warm und sehr kompakt: maximal 5 kurze Sätze. Anfrage: ${query}`;
  const payload = {
    contents: [{ role: "user", parts: [{ text: prompt }] }],
    generationConfig: {
      temperature: mode === "questions" ? 0.8 : search ? 0.25 : 0.35,
      maxOutputTokens: mode === "questions" ? 1300 : search ? 650 : 500,
      ...(["questions", "recommendations"].includes(mode) ? { responseMimeType: "application/json" } : {}),
    },
    ...(search ? { tools: [{ google_search: {} }] } : {}),
  };
  let lastResponse;
  for (let attempt = 0; attempt < 3; attempt += 1) {
    lastResponse = await fetchImpl(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${encodeURIComponent(apiKey)}`,
      { method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify(payload) },
    );
    if (lastResponse.ok || ![429, 500, 502, 503, 504].includes(lastResponse.status)) break;
    await delay(250 * (attempt + 1));
  }
  if (!lastResponse?.ok) return null;
  return lastResponse.json();
}

export function createBrainGenerateHandler({ fetchImpl = fetch, getSecret } = {}) {
  return async (req) => {
    if (req.method === "OPTIONS") return new Response(null, { status: 204, headers: cors });
    if (req.method !== "POST") return reply({ ok: false, errorType: "method_not_allowed" }, 405);
    const body = await req.json().catch(() => null);
    const query = text(body?.query, 900);
    const requestedMode = ["chat", "search", "questions", "recommendations"].includes(body?.mode) ? body.mode : "chat";
    const search = requestedMode === "search" || isLocalSearch(query);
    if (!query) return reply({ ok: false, errorType: "invalid_query", errorMessage: "Bitte stelle eine Frage." }, 400);
    const apiKey = getSecret?.();
    if (!apiKey) return reply({ ok: false, errorType: "gemini_not_configured", errorMessage: "Der KI-Schlüssel ist serverseitig nicht eingerichtet." }, 503);
    try {
      const payload = await requestGemini(fetchImpl, apiKey, query, search ? "search" : requestedMode, body?.context);
      const candidate = payload?.candidates?.[0];
      const result = answer(candidate);
      const realSources = sources(candidate?.groundingMetadata);
      if (["questions", "recommendations"].includes(requestedMode) && result) {
        const parsed = JSON.parse(result);
        return reply({ ok: true, data: parsed, ...parsed, answer: result, model: "gemini-2.5-flash" });
      }
      if (result) return reply({ ok: true, grounded: true, answer: result, sources: [], searchQueries: [], model: "gemini-2.5-flash" });
    } catch (error) {
      console.error("harmony_brain_provider_error", error?.message ?? error);
    }
    if (search) {
      const webResults = await webFallback(fetchImpl, query).catch(() => []);
      if (webResults.length) return reply({ ok: true, grounded: true, answer: webResults.map((title, index) => `${index + 1}. ${title}`).join("\n"), sources: [], searchQueries: [], model: "web-fallback" });
      return reply({ ok: true, grounded: true, answer: "Die Websuche ist gerade kurz nicht verfügbar. Bitte versuche es gleich noch einmal.", sources: [], searchQueries: [], model: "fallback" });
    }
    return reply({ ok: false, errorType: "gemini_request_failed", errorMessage: "Harmony Brain ist gerade kurz nicht erreichbar." }, 502);
  };
}

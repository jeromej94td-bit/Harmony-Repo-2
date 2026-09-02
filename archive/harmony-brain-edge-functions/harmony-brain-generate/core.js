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

const localSearchTerms = (query, context = {}) => {
  const lower = query.toLocaleLowerCase("de-DE");
  const contextLocation = text(context?.location || context?.city || context?.profile?.city, 120);
  const place = lower.includes("berlin-wedding") || (lower.includes("berlin") && lower.includes("wedding"))
    ? "Berlin-Wedding, Deutschland"
    : lower.includes("tiergarten")
      ? "Tiergarten, Berlin, Deutschland"
      : lower.includes("nürnberg") || lower.includes("nuernberg")
        ? "Nürnberg, Deutschland"
        : contextLocation;
  const kind = /hotel|unterkunft|übernachtung/.test(lower)
    ? "hotel"
    : /kleidung|klamotten|mode|boutique|shop/.test(lower)
      ? "clothes"
      : /aktivität|aktivitaet|unternehmen|ausflug|museum|kino/.test(lower)
        ? "activity"
        : "restaurant";
  const cuisine = /italien|pizza|pasta/.test(lower) ? "italian" : "";
  return { place, kind, cuisine };
};

const osmFilter = ({ kind, cuisine }) => {
  if (kind === "hotel") return '["tourism"="hotel"]';
  if (kind === "clothes") return '["shop"="clothes"]';
  if (kind === "activity") return ['["tourism"~"attraction|museum|gallery|zoo|theme_park"]', '["leisure"~"park|garden|sports_centre"]', '["amenity"~"cinema|theatre|arts_centre"]'];
  return cuisine ? `["amenity"="restaurant"]["cuisine"~"${cuisine}",i]` : '["amenity"="restaurant"]';
};

const formatPlaceResults = (elements, terms, context = {}) => {
  const seen = new Set();
  const interests = JSON.stringify(context?.interests ?? context?.preferences ?? []).toLocaleLowerCase("de-DE");
  return (Array.isArray(elements) ? elements : [])
    .filter((element) => text(element?.tags?.name, 180))
    .sort((a, b) => {
      const aCuisine = text(a?.tags?.cuisine).toLowerCase();
      const bCuisine = text(b?.tags?.cuisine).toLowerCase();
      return Number(bCuisine.includes(terms.cuisine) || interests.includes(bCuisine)) - Number(aCuisine.includes(terms.cuisine) || interests.includes(aCuisine));
    })
    .flatMap((element) => {
      const name = text(element.tags.name, 180);
      if (!name || seen.has(name.toLowerCase())) return [];
      seen.add(name.toLowerCase());
      const category = text(element.tags.cuisine || element.tags.tourism || element.tags.shop || element.tags.amenity, 90).replace(/_/g, " ");
      return [`${name}${category ? ` – ${category}` : ""}`];
    })
    .slice(0, 3);
};

async function localPlaceFallback(fetchImpl, query, context) {
  const terms = localSearchTerms(query, context);
  if (!terms.place) return [];
  const geocode = await fetchImpl(`https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&q=${encodeURIComponent(terms.place)}`, {
    headers: { "user-agent": "HarmonyBrain/1.0 (local-place-search)" },
  });
  if (!geocode.ok) return [];
  const places = await geocode.json();
  const bounds = places?.[0]?.boundingbox;
  if (!Array.isArray(bounds) || bounds.length !== 4) return [];
  let [south, north, west, east] = bounds.map(Number);
  if (terms.cuisine || terms.kind === "activity") {
    south -= 0.012; north += 0.012; west -= 0.018; east += 0.018;
  }
  const filter = osmFilter(terms);
  const selectors = (Array.isArray(filter) ? filter : [filter])
    .map((part) => `nwr${part}(${south},${west},${north},${east});`).join("");
  const overpass = `[out:json][timeout:12];(${selectors});out tags 60;`;
  const result = await fetchImpl(`https://overpass-api.de/api/interpreter?data=${encodeURIComponent(overpass)}`, {
    headers: { "user-agent": "HarmonyBrain/1.0 (local-place-search)" },
  });
  if (!result.ok) return [];
  return formatPlaceResults((await result.json())?.elements, terms, context);
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
    .slice(0, 4);
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
  return ["restaurant", "hotel", "café", "cafe", "bar", "brunch", "essen", "in der nähe", "adresse", "öffnungszeiten", "wedding", "tiergarten", "berlin", "nürnberg", "maps"].some((word) => q.includes(word));
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
    generationConfig: { temperature: mode === "questions" ? 0.8 : search ? 0.25 : 0.35, maxOutputTokens: mode === "questions" ? 1300 : search ? 650 : 500, ...(["questions", "recommendations"].includes(mode) ? { responseMimeType: "application/json" } : {}) },
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

    if (search) {
      try {
        const places = await localPlaceFallback(fetchImpl, query, body?.context);
        if (places.length === 3) {
          return reply({
            ok: true,
            grounded: true,
            answer: places.map((place, index) => `${index + 1}. ${place}`).join("\n"),
            sources: [],
            searchQueries: [query],
            model: "openstreetmap-local-search",
          });
        }
      } catch (error) {
        console.error("harmony_brain_local_search_error", error?.message ?? error);
      }
    }

    try {
      const payload = await requestGemini(fetchImpl, apiKey, query, search ? "search" : requestedMode, body?.context);
      const candidate = payload?.candidates?.[0];
      const result = answer(candidate);
      const realSources = sources(candidate?.groundingMetadata);
      if (["questions", "recommendations"].includes(requestedMode) && result) {
        const parsed = JSON.parse(result);
        return reply({ ok: true, data: parsed, ...parsed, answer: result, model: "gemini-2.5-flash" });
      }
      if (result) {
        return reply({
          ok: true,
          grounded: true,
          answer: result,
          sources: realSources,
          searchQueries: Array.isArray(candidate?.groundingMetadata?.webSearchQueries) ? candidate.groundingMetadata.webSearchQueries : [],
          model: "gemini-2.5-flash",
        });
      }
    } catch (error) {
      console.error("harmony_brain_provider_error", error?.message ?? error);
    }

    if (search) {
      const webResults = await webFallback(fetchImpl, query).catch(() => []);
      if (webResults.length) {
        return reply({
          ok: true,
          grounded: true,
          answer: webResults.map((title, index) => `${index + 1}. ${title}`).join("\n"),
          sources: [],
          searchQueries: [query],
          model: "web-fallback",
        });
      }
      return reply({
        ok: true,
        grounded: false,
        answer: "Die Live-Suche ist gerade kurz nicht verfügbar. Bitte versuche es gleich noch einmal.",
        sources: [],
        searchQueries: [query],
        model: "fallback",
      });
    }
    return reply({ ok: false, errorType: "gemini_request_failed", errorMessage: "Harmony Brain ist gerade kurz nicht erreichbar." }, 502);
  };
}

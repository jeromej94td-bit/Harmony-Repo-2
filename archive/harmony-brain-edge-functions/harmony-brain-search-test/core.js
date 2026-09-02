const GEMINI_MODEL = "gemini-2.5-flash";
const GEMINI_ENDPOINT =
  `https://generativelanguage.googleapis.com/v1beta/models/${GEMINI_MODEL}:generateContent`;

const CORS_HEADERS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
};

const jsonResponse = (body, status = 200) =>
  new Response(JSON.stringify(body), {
    status,
    headers: {
      ...CORS_HEADERS,
      "content-type": "application/json; charset=utf-8",
    },
  });

const buildPrompt = (query) => [
  "Beantworte die folgende Frage auf Deutsch.",
  "Nutze Google Search, damit die Antwort auf aktuellen, überprüfbaren Informationen basiert.",
  "Nenne konkrete Daten, wenn sie relevant sind, und erfinde keine Quellen.",
  "Frage:",
  query,
].join("\n");

const extractText = (candidate) =>
  (candidate?.content?.parts ?? [])
    .map((part) => part?.text)
    .filter((text) => typeof text === "string")
    .join("\n")
    .trim();

const extractSources = (groundingMetadata) => {
  const sources = [];
  const seenUrls = new Set();

  for (const chunk of groundingMetadata?.groundingChunks ?? []) {
    const url = chunk?.web?.uri;
    if (typeof url !== "string" || url.length === 0 || seenUrls.has(url)) {
      continue;
    }
    seenUrls.add(url);
    sources.push({ title: chunk.web.title || url, url });
  }

  return sources;
};

export function createHarmonySearchHandler({
  fetchImpl = fetch,
  getSecret,
  now = Date.now,
} = {}) {
  return async (request) => {
    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: CORS_HEADERS });
    }

    if (request.method !== "POST") {
      return jsonResponse({ ok: false, error: "method_not_allowed" }, 405);
    }

    const geminiApiKey = getSecret?.();
    if (typeof geminiApiKey !== "string" || geminiApiKey.length === 0) {
      return jsonResponse({ ok: false, error: "gemini_not_configured" }, 503);
    }

    const startedAt = now();
    const body = await request.json().catch(() => null);
    const query = body?.query;
    const normalizedQuery = typeof query === "string" ? query.trim() : "";
    if (normalizedQuery.length === 0 || normalizedQuery.length > 500) {
      return jsonResponse({ ok: false, error: "invalid_query" }, 400);
    }
    let upstreamResponse;
    try {
      upstreamResponse = await fetchImpl(GEMINI_ENDPOINT, {
        method: "POST",
        headers: {
          "content-type": "application/json",
          "x-goog-api-key": geminiApiKey,
        },
        body: JSON.stringify({
          contents: [
            {
              role: "user",
              parts: [{ text: buildPrompt(normalizedQuery) }],
            },
          ],
          tools: [{ google_search: {} }],
          generationConfig: {
            temperature: 0.2,
            maxOutputTokens: 1200,
          },
        }),
      });
    } catch {
      return jsonResponse({ ok: false, error: "gemini_unreachable" }, 502);
    }
    if (!upstreamResponse.ok) {
      return jsonResponse(
        {
          ok: false,
          error: "gemini_request_failed",
          upstreamStatus: upstreamResponse.status,
        },
        502,
      );
    }
    const upstreamBody = await upstreamResponse.json();
    const candidate = upstreamBody.candidates?.[0];
    const groundingMetadata = candidate?.groundingMetadata;
    const sources = extractSources(groundingMetadata);
    const searchQueries = groundingMetadata?.webSearchQueries ?? [];

    return jsonResponse({
      ok: true,
      grounded: sources.length > 0 || searchQueries.length > 0,
      model: upstreamBody.modelVersion || GEMINI_MODEL,
      answer: extractText(candidate),
      sources,
      searchQueries,
      latencyMs: now() - startedAt,
    });
  };
}

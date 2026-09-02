import "jsr:@supabase/functions-js/edge-runtime.d.ts";

import { createHarmonySearchHandler } from "./core.js";

Deno.serve(
  createHarmonySearchHandler({
    getSecret: () => Deno.env.get("GEMINI_API_KEY"),
  }),
);

import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { createBrainGenerateHandler } from "./core.js";

Deno.serve(createBrainGenerateHandler({
  getSecret: () => Deno.env.get("GEMINI_API_KEY"),
}));

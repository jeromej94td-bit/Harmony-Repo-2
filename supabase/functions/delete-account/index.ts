import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { createDeleteAccountHandler } from "./delete-account-core.mjs";

Deno.serve(createDeleteAccountHandler({
  getServiceRole: () => Deno.env.get("SUPABASE_SERVICE_ROLE_KEY"),
  getSupabaseUrl: () => Deno.env.get("SUPABASE_URL"),
}));

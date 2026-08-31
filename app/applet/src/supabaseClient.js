import { createClient } from '@supabase/supabase-js';

// ---------------------------------------------------------
// PASTE YOUR SUPABASE URL AND PUBLIC KEY BELOW
// ---------------------------------------------------------
const SUPABASE_URL = "https://rspgnonlpkxdudbjxnrl.supabase.co/rest/v1/";
const SUPABASE_PUBLIC_KEY = "sb_publishable_qNtemRRaLIW0nbFb52uKLw_rWwlgUo1";

export const supabase = createClient(SUPABASE_URL, SUPABASE_PUBLIC_KEY);

# Harmony replay attempt isolation

## Why this is needed

The current paired-answer schema has one `harmony_question_rounds` row per `(couple_id, pack_id, question_index)` and one answer per user/round. That is correct for "latest answer wins" inside one run, but a replay can still see a partner answer left from the previous run until the partner answers again.

Do not solve this in the Android client with timestamps, random local run ids, or by deleting the partner's answer. Those approaches either cannot distinguish "same answer chosen again" or violate the answer-secrecy/RLS model.

## Server-side target

Introduce a shared pack attempt owned by the couple:

- `harmony_pack_attempts(id, couple_id, pack_id, sequence, started_at, started_by, finished_at)`
- exactly one current attempt per `(couple_id, pack_id)`
- `harmony_question_rounds` references `attempt_id`
- uniqueness becomes `(attempt_id, question_index)` rather than `(couple_id, pack_id, question_index)`

Add SECURITY DEFINER RPCs:

1. `start_pack_attempt(p_pack_id)`
   - verifies membership in the couple
   - closes the prior current attempt for the same pack
   - creates the next attempt atomically
   - returns `attempt_id` and `sequence`
2. update `submit_question_answer` so it writes only into the current attempt
3. update `get_pack_question_results` so it reads only the current attempt

## Android integration after the migration is live

- `openPackForPlay(..., freshRun = true)` calls `start_pack_attempt(packId)` before clearing local answers.
- Normal resume does not create a new attempt.
- `CouplePackRevealScreen` remains unchanged because `get_pack_question_results` becomes attempt-scoped server-side.
- If starting a new attempt fails, keep the existing run and show a retryable error instead of silently mixing attempts.

## Required regression cases

- A completes pack, B completes pack, both see results.
- A replays first: A must not see B's old answers as current-run answers.
- B joins the replay later and chooses the same answer as last time: it must still count as a new answer.
- Changing an answer inside one attempt still replaces the earlier answer.
- Reopening results reads the latest completed/current attempt only.

This plan is intentionally not placed in `supabase/migrations` until the live RPC definitions can be inspected and migrated atomically; shipping a partial schema change would break current paired answering.

# Scenario Adventure Flow Spec

## Goal
Turn Harmony 360 `SCENARIO` packs into one continuous, resumable mini-adventure instead of treating each question as an isolated full-screen interaction.

## Approved experience
- A fresh scenario pack starts with a short **„Euer Abenteuer beginnt“** intro.
- The pack's existing scenario questions become real chapters (normally 8); each chapter keeps its own prompt and answer choices.
- After a choice, the next actual pack question becomes the next chapter. Do not repeat one question to simulate progress.
- Progress uses the existing persisted per-question answers. Reopening a pack resumes at the first unanswered chapter.
- The route summary and play-style calculation must be reconstructed from persisted answers so process/app restarts do not erase the visible journey.
- The standard global Skip control must not appear while the adventure board is active.
- The final chapter does not immediately leave the adventure. Its pending choice is included in a dedicated finale first.
- The finale shows the route, a concise shared play-style result, and **„Abenteuer abschließen“**. Only that action commits the final answer and returns to the normal runner completion flow.
- Existing answer persistence, paired submission, package routing, Google auth, Android signing, and unrelated game mechanics remain unchanged.

## Compatibility
If a remotely loaded or otherwise unknown scenario question cannot be matched unambiguously to an embedded scenario pack, render a safe one-chapter scenario experience without inventing a pack or corrupting another pack's route.
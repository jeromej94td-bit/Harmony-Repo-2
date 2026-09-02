# Production-isolation archive snapshot

This branch preserves the complete Harmony source state immediately before disabled/archived features are removed from the production Android source set.

Preserved on purpose:
- Harmony Brain implementation and UI experiments
- legacy wiring and supporting source needed to study or rebuild the idea later
- the current Google login, account/session and password-recovery work as of this snapshot

Do not merge this archive branch wholesale back into `main`. Reintroduce an archived idea intentionally on a fresh feature branch and port only the pieces that are wanted.

Snapshot date: 2026-09-02
Snapshot base: main @ 306ef0560df3bf519badacb148007612c514a967

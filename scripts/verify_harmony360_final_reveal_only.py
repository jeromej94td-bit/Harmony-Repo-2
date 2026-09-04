from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

router = (ROOT / "app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt").read_text(encoding="utf-8")
experience = (ROOT / "app/src/main/java/com/example/ui/screens/ExperiencePartnerPredictionBoard.kt").read_text(encoding="utf-8")
legacy = (ROOT / "app/src/main/java/com/example/ui/screens/PrivatePairRevealBoards.kt").read_text(encoding="utf-8")

partner_branch = router.split("FullscreenGameMechanicKind.PARTNER_PREDICTION ->", 1)[1].split(
    "FullscreenGameMechanicKind.SECRET_CHOICE ->", 1
)[0]

failures = []

if "PartnerPredictionCollectionBoard(" not in partner_branch:
    failures.append(
        "Harmony-360 PARTNER_PREDICTION still does not route to the no-intermediate-result collection board"
    )
if "PartnerPredictionRevealBoard(" in partner_branch:
    failures.append(
        "Harmony-360 PARTNER_PREDICTION still routes through the per-question reveal/result board"
    )
if "PartnerPredictionCollectionBoard(" not in experience:
    failures.append(
        "Experience partner prediction still does not use the final-reveal-only collection flow"
    )
if "PartnerPredictionRevealBoard(" in experience:
    failures.append(
        "Experience partner prediction still exposes the per-question reveal/result board"
    )
if "internal fun PartnerPredictionRevealBoard(" not in legacy:
    failures.append(
        "Standalone legacy reveal board was removed; it must remain available for flows that intentionally reveal immediately"
    )

if failures:
    print("Harmony 360 final-reveal-only contract FAILED:")
    for failure in failures:
        print(f" - {failure}")
    raise SystemExit(1)

print("Harmony 360 final-reveal-only contract passed.")

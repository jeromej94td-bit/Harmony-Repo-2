from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

router = (ROOT / "app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt").read_text(encoding="utf-8")
experience = (ROOT / "app/src/main/java/com/example/ui/screens/ExperiencePartnerPredictionBoard.kt").read_text(encoding="utf-8")
reveal_board = (ROOT / "app/src/main/java/com/example/ui/screens/PrivatePairRevealBoards.kt").read_text(encoding="utf-8")

partner_branch = router.split("FullscreenGameMechanicKind.PARTNER_PREDICTION ->", 1)[1].split(
    "FullscreenGameMechanicKind.SECRET_CHOICE ->", 1
)[0]

failures = []

if "PartnerPredictionRevealBoard(" not in partner_branch:
    failures.append("Harmony-360 PARTNER_PREDICTION lost its partner-prediction board")
if "revealAfterPartnerChoice = false" not in partner_branch:
    failures.append(
        "Harmony-360 PARTNER_PREDICTION still allows the per-question Treffer/reveal screen"
    )
if "revealAfterPartnerChoice = false" not in experience:
    failures.append(
        "Experience partner prediction still allows the per-question Treffer/reveal screen"
    )
if "revealAfterPartnerChoice: Boolean = true" not in reveal_board:
    failures.append(
        "Standalone reveal behavior is not preserved as the explicit default"
    )
if "if (revealAfterPartnerChoice)" not in reveal_board:
    failures.append(
        "Partner prediction board does not branch between immediate submit and explicit reveal"
    )

if failures:
    print("Harmony 360 final-reveal-only contract FAILED:")
    for failure in failures:
        print(f" - {failure}")
    raise SystemExit(1)

print("Harmony 360 final-reveal-only contract passed.")

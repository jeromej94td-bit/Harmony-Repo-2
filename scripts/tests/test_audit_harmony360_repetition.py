import sys
from pathlib import Path
import unittest

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT))

from audit_harmony360_repetition import analyze_texts


GENERIC_OPTIONS = (
    "Eigenen Wunsch offen sagen",
    "Partner zuerst verstehen",
    "Gemeinsamen dritten Weg suchen",
    "Kurz Abstand und dann entscheiden",
)


def question(subject: str) -> str:
    options = ", ".join(f'"{value}"' for value in GENERIC_OPTIONS)
    return (
        f'GenQuestion(q = "Ihr merkt bei „{subject}“, dass ihr komplett unterschiedliche Vorstellungen habt. '
        f'Was machst du zuerst?", options = listOf({options})),\n'
    )


class Harmony360RepetitionAuditTest(unittest.TestCase):
    def test_detects_repeated_subject_template_and_option_quartet(self):
        text = question("Morgenroutine") + question("Sportliche Ziele") + question("Bücher")

        report = analyze_texts([text], min_occurrences=3)

        self.assertEqual(1, len(report.prompt_templates))
        prompt = next(iter(report.prompt_templates))
        self.assertIn("„<SUBJECT>“", prompt)
        self.assertEqual(3, report.prompt_templates[prompt])
        self.assertEqual({GENERIC_OPTIONS: 3}, report.option_quartets)

    def test_threshold_filters_two_occurrence_noise(self):
        text = question("Morgenroutine") + question("Sportliche Ziele")

        report = analyze_texts([text], min_occurrences=3)

        self.assertEqual({}, report.prompt_templates)
        self.assertEqual({}, report.option_quartets)


if __name__ == "__main__":
    unittest.main()

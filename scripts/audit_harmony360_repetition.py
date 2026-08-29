from __future__ import annotations

import argparse
import glob
import json
import re
from collections import Counter
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

QUESTION_RE = re.compile(
    r'GenQuestion\(q = "(?P<prompt>(?:\\.|[^"\\])*)"'
    r'(?:, options = listOf\((?P<options>.*?)\))?'
)
STRING_RE = re.compile(r'"((?:\\.|[^"\\])*)"')
SUBJECT_RE = re.compile(r'„[^”]+“')


@dataclass(frozen=True)
class AuditReport:
    prompt_templates: dict[str, int]
    option_quartets: dict[tuple[str, ...], int]


def _decode_kotlin_string(value: str) -> str:
    return value.replace(r'\"', '"').replace(r'\\', '\\')


def _normalize_prompt(prompt: str) -> str:
    prompt = SUBJECT_RE.sub('„<SUBJECT>“', prompt)
    return ' '.join(prompt.split())


def _parse_questions(text: str) -> Iterable[tuple[str, tuple[str, ...]]]:
    for line in text.splitlines():
        match = QUESTION_RE.search(line)
        if match is None:
            continue

        prompt = _decode_kotlin_string(match.group('prompt'))
        options_raw = match.group('options') or ''
        options = tuple(
            _decode_kotlin_string(option)
            for option in STRING_RE.findall(options_raw)
        )
        yield prompt, options


def analyze_texts(texts: Iterable[str], min_occurrences: int = 3) -> AuditReport:
    if min_occurrences < 2:
        raise ValueError('min_occurrences must be at least 2')

    prompt_counts: Counter[str] = Counter()
    option_counts: Counter[tuple[str, ...]] = Counter()

    for text in texts:
        for prompt, options in _parse_questions(text):
            prompt_counts[_normalize_prompt(prompt)] += 1
            if len(options) == 4:
                option_counts[options] += 1

    return AuditReport(
        prompt_templates={
            prompt: count
            for prompt, count in prompt_counts.items()
            if count >= min_occurrences
        },
        option_quartets={
            options: count
            for options, count in option_counts.items()
            if count >= min_occurrences
        },
    )


def _markdown(report: AuditReport) -> str:
    lines = ['# Harmony 360 repetition audit', '']
    lines.append('## Repeated prompt templates')
    if report.prompt_templates:
        for prompt, count in sorted(report.prompt_templates.items(), key=lambda item: (-item[1], item[0])):
            lines.append(f'- {count}× `{prompt}`')
    else:
        lines.append('- none')

    lines.extend(['', '## Repeated option quartets'])
    if report.option_quartets:
        for options, count in sorted(report.option_quartets.items(), key=lambda item: (-item[1], item[0])):
            lines.append(f'- {count}× `{" | ".join(options)}`')
    else:
        lines.append('- none')
    return '\n'.join(lines) + '\n'


def main() -> int:
    parser = argparse.ArgumentParser(
        description='Audit generated Harmony 360 Kotlin content for repeated prompt templates and option quartets.'
    )
    parser.add_argument(
        'patterns',
        nargs='*',
        default=['app/src/main/java/com/example/data/GeneratedHarmonyAdrenaline360Section*.kt'],
        help='File glob(s) to audit.',
    )
    parser.add_argument('--min-occurrences', type=int, default=3)
    parser.add_argument('--json', action='store_true', dest='as_json')
    args = parser.parse_args()

    paths = sorted({Path(path) for pattern in args.patterns for path in glob.glob(pattern)})
    if not paths:
        parser.error('no files matched the supplied patterns')

    report = analyze_texts(
        (path.read_text(encoding='utf-8') for path in paths),
        min_occurrences=args.min_occurrences,
    )

    if args.as_json:
        payload = {
            'prompt_templates': report.prompt_templates,
            'option_quartets': {
                ' | '.join(options): count
                for options, count in report.option_quartets.items()
            },
        }
        print(json.dumps(payload, ensure_ascii=False, indent=2, sort_keys=True))
    else:
        print(_markdown(report), end='')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())

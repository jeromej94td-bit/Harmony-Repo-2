from pathlib import Path

path = Path("app/src/main/java/com/example/ui/screens/RankingSlotBoard.kt")
text = path.read_text()

replacements = [
    (
        "    val prompt = mechanicPrompt(question, items, profile)\n",
        "    val prompt = fineDiningVisualPrompt(question) ?: mechanicPrompt(question, items, profile)\n",
    ),
    (
        "                            label = slots[slotIndex]?.let { labels[it] },\n                            highlighted = hoveredSlot == slotIndex,\n",
        "                            label = slots[slotIndex]?.let { labels[it] },\n                            visual = slots[slotIndex]?.let { fineDiningRankingCard(question, it) },\n                            highlighted = hoveredSlot == slotIndex,\n",
    ),
    (
        "                            label = labels[raw] ?: raw,\n                            fromSlot = null,\n",
        "                            label = labels[raw] ?: raw,\n                            visual = fineDiningRankingCard(question, raw),\n                            fromSlot = null,\n",
    ),
    (
        "    label: String?,\n    highlighted: Boolean,\n",
        "    label: String?,\n    visual: FineDiningVisualCard?,\n    highlighted: Boolean,\n",
    ),
    (
        "                    label = label ?: raw,\n                    fromSlot = position,\n",
        "                    label = label ?: raw,\n                    visual = visual,\n                    fromSlot = position,\n",
    ),
    (
        "    raw: String,\n    label: String,\n    fromSlot: Int?,\n",
        "    raw: String,\n    label: String,\n    visual: FineDiningVisualCard? = null,\n    fromSlot: Int?,\n",
    ),
    (
        "        Text(\n            text = label,\n",
        "        if (visual != null) {\n            FineDiningRankingThumbnail(card = visual)\n            Spacer(Modifier.width(if (compact) 7.dp else 10.dp))\n        }\n\n        Text(\n            text = visual?.displayLabel ?: label,\n",
    ),
]

for old, new in replacements:
    if new in text:
        continue
    if old not in text:
        raise SystemExit(f"Expected RankingSlotBoard fragment missing:\n{old}")
    text = text.replace(old, new, 1)

path.write_text(text)
print("Fine Dining visual ranking patch applied")

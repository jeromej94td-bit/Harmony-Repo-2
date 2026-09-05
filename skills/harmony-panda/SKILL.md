---
name: harmony-panda
description: Use when designing, generating, reviewing, or implementing Harmony image-choice games that show romantic couple situations, especially when a consistent panda-couple visual identity and animated card transitions are required.
---

# Harmony Panda

## Overview
Harmony Panda is the reusable visual and interaction standard for Harmony relationship-focused image games. It combines a globally relatable panda couple, premium Harmony card styling, and a strict staggered flip transition so image rounds feel emotional, polished, and consistent.

Read `STYLE_GUIDE.md` before producing assets or implementing the card renderer.

## When to use
Use Harmony Panda for romantic or relationship-centered visual choices such as:
- Herz oder Kopf
- Liebessprache
- Date Night
- Cozy Couple / Alltag
- Zukunfts- und Nähe-Situationen
- seasonal couple specials

Do not force pandas into cards where the subject itself should be the visual focus, for example pure travel destinations, landscapes, places, objects, foods, or symbolic choices.

## Non-negotiable character rules
- Recurring couple: one male panda + one female panda with a tasteful pink bow.
- Preserve the approved round, cuddly proportions; do not make them noticeably slim and do not exaggerate them into caricature obesity.
- Cute, warm, romantic, expressive, globally relatable.
- Premium rather than childish; safe and non-sexual unless a separate adult-only game explicitly requires otherwise.
- Keep faces, eye style, fur pattern, proportions, and bow treatment as consistent as possible across cards.

## Card standard
- Default image-choice layout: four cards in a 2x2 grid.
- Rounded corners, soft pink/magenta/violet glow, elegant border, image-dominant composition.
- Bottom information area may contain a small icon, short title, optional short subtitle, and restrained decorative heart detail.
- Keep copy short. The image must carry most of the meaning.

## Required transition sequence
When a user selects a card:
1. Show a brief selected-state response such as glow or subtle scale.
2. Lock all card input immediately.
3. Flip the four current cards out with staggered timing, not all at once.
4. Only after the old cards have finished flipping out, remove/fade the current question.
5. Flip the four new cards in, again staggered.
6. Only after the new cards are fully visible, reveal the new question.
7. Re-enable input only after the complete transition finishes.

No double taps, no selection during animation, no question text changing in the middle of the card flip.

## Visual direction
Use Harmony's dark violet / aubergine base with magenta, pink, rose, soft gold, warm candlelight, subtle particles and glow. Scenes should feel cozy, cinematic, affectionate, emotionally readable, and suitable for a premium global relationship app.

## Source of truth
The detailed rules, image-language guidance, animation timing principles, exceptions, and generation prompt template live in `STYLE_GUIDE.md`. When there is a conflict, follow the more specific rule in that guide.
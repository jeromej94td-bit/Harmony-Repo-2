# Harmony Panda — Style Guide v1

## Purpose
Harmony Panda is the visual system for Harmony image-choice games where the content is a romantic or emotional couple situation. It exists to make global relationship content feel immediately understandable, charming, premium, and recognizable without tying the imagery to one ethnicity, country, or human appearance.

The panda couple is a recurring Harmony identity, not a mandatory mascot in every image.

---

## 1. Character rules

### Main couple
- Male panda: sweet, expressive, rounded, cuddly silhouette.
- Female panda: same visual family, clearly recognizable by a tasteful pink bow on her head.
- Both characters should look like the same recurring couple from card to card.

### Approved proportions
The source visual direction is the first approved Harmony Panda concept: soft, rounded, cuddly, slightly plush proportions.

Do:
- keep cheeks, torso and paws soft and rounded;
- keep silhouettes cute and substantial;
- preserve an appealing plush quality;
- make the pair feel romantic rather than baby-like.

Do not:
- make them noticeably slim or lanky;
- make them extremely obese or caricatured;
- switch to realistic wildlife anatomy;
- change facial proportions radically between cards.

### Expression
Preferred emotions:
- affection;
- delight;
- curiosity;
- warmth;
- cozy calm;
- thoughtful attention;
- gentle surprise;
- feeling understood.

Avoid exaggerated slapstick expressions unless a specific game needs comedy.

### Global neutrality
The pandas are intended as neutral recurring protagonists so people in different cultures can project themselves into the scene more easily than with one narrowly defined human couple.

---

## 2. When pandas belong in the image

Use the panda couple when the card is about a relationship moment or shared behavior, for example:
- cuddling on a sofa;
- romantic dinner;
- breakfast in bed;
- giving a surprise;
- walking in the rain;
- planning the future;
- talking through a decision;
- shared adventure;
- showing affection or support.

Do not force pandas into content where the visual subject should stand alone, for example:
- Italy vs. Japan as travel destinations;
- landscapes;
- hotels or cities;
- food-only comparisons;
- symbolic object selections;
- architecture or scenery.

Rule: use the pandas only when their presence adds emotional relationship meaning.

---

## 3. Harmony Panda color world

### Base
- deep violet;
- aubergine;
- blackberry / near-black purple;
- dark plum.

### Harmony accents
- magenta;
- vivid but elegant pink;
- rose;
- soft lavender;
- restrained neon-purple glow.

### Warm emotional accents
- rose gold;
- candlelight amber;
- soft gold;
- warm cream;
- muted peach.

### Lighting
Use cinematic warm lighting against the darker purple Harmony environment. The image should glow, but the glow must not flatten the subject or become cheap neon overload.

---

## 4. Image language

### Desired feel
Each image should feel like a small romantic world:
- cozy;
- intimate without being explicit;
- affectionate;
- polished;
- premium mobile-game art;
- cinematic;
- emotionally readable at a glance.

### Preferred visual devices
- soft depth of field;
- warm bokeh;
- gentle heart motifs when appropriate;
- candlelight;
- rain reflections;
- fairy lights;
- soft blankets and fabrics;
- subtle particles;
- elegant environmental storytelling.

### Avoid
- generic clip-art look;
- harsh realism;
- cluttered scenes;
- random brand logos;
- watermarks;
- unnecessary text inside the artwork;
- childish nursery aesthetics;
- inconsistent character anatomy.

---

## 5. Card construction

### Standard layout
For a typical Harmony image-choice round:
- one question area above;
- four selectable cards;
- 2x2 grid;
- image-first composition;
- short text support only.

### Card shell
- rounded corners;
- premium soft border;
- pink/magenta/violet glow;
- dark Harmony-compatible framing;
- subtle glass-like or illuminated finish;
- clear selected state.

### Card content
Preferred bottom information strip:
- small circular icon on the left;
- short card title;
- optional one-line subtitle;
- restrained heart or decorative accent on the right.

### Text
Keep titles short and emotionally clear. Avoid turning image cards into text cards.

Examples:
- Kuscheln auf dem Sofa
- Romantisches Dinner
- Frühstück im Bett
- Abenteuer zu zweit

Short subtitle examples:
- Einfach gemeinsam sein.
- Kleine Momente. Großes Glück.
- Gemeinsam mehr erleben.

---

## 6. Interaction and animation contract

The flip animation is part of the Harmony Panda identity and must be treated as a state-controlled transition, not decorative motion.

### Selection sequence
When the user taps a card:
1. show immediate selection feedback, such as a glow and/or subtle scale response;
2. lock all card input immediately;
3. keep the current question visible while the old cards begin leaving;
4. flip the old four cards out with staggered timing;
5. after the final old card has completed its flip, fade/remove the old question;
6. prepare the next round while input remains locked;
7. flip the new four cards in with staggered timing;
8. after the final new card is fully visible, reveal/fade in the new question;
9. only then unlock input.

### Critical rule
The question must not change while cards are midway through the transition.

### Stagger behavior
The four cards should not animate simultaneously. Use a small offset between cards so the motion feels flowing and intentional.

Conceptual sequence:
- old card 1 out;
- old card 2 out shortly after;
- old card 3 out;
- old card 4 out;
- old question fades;
- new card 1 in;
- new card 2 in shortly after;
- new card 3 in;
- new card 4 in;
- new question appears.

### Input safety
During the whole transition:
- no card is tappable;
- no double-answer can be recorded;
- no skipping ahead;
- no conflicting selected states;
- no stale question/card combination may appear.

---

## 7. Selected-state behavior

A chosen card should briefly feel special before the transition begins.

Recommended:
- brighter rose/pink edge glow;
- slight scale-up;
- tiny vertical lift;
- subtle haptic feedback where available.

Non-selected cards may remain neutral or recede slightly. Keep the transition elegant rather than punitive.

---

## 8. Question style

Questions should be:
- short;
- warm;
- conversational;
- easy to understand internationally after localization;
- emotionally suggestive rather than clinical.

Good examples:
- Was fühlt sich heute mehr nach euch an?
- Welcher Moment macht euren Abend schöner?
- Was wärmt euer Herz gerade mehr?

Avoid long questionnaire-like wording.

---

## 9. Accessibility and usability

- Preserve readable contrast for titles and subtitles.
- Keep touch targets large enough for mobile use.
- Do not communicate selected state by color alone.
- Respect reduced-motion settings when the platform provides them; use a simpler non-disorienting transition while preserving the same state order.
- The input lock must be logic-based, not merely visual.

---

## 10. Generation prompt template

Use this as a starting point for future image generation and adapt only the scene description:

> Create a premium Harmony Panda image-card scene for a global relationship app. Show the recurring cute panda couple in the established rounded, cuddly proportions: one male panda and one female panda with a tasteful pink bow. Keep their faces, eyes, fur pattern, proportions, and character identity consistent. The mood is warm, romantic, emotionally readable, non-childish, and polished. Use a dark violet / aubergine Harmony environment with magenta, pink, rose, soft-gold and warm cinematic light accents. Add subtle bokeh, soft depth of field and restrained romantic details where appropriate. No logos, no watermark, no unnecessary text. The scene is: [SCENE]. Compose it so it works cleanly inside a rounded vertical mobile game card.

When the requested content is a place, landscape, destination, object, or other non-couple subject, omit the pandas unless the relationship context genuinely benefits from them.

---

## 11. Review checklist

Before approving a Harmony Panda card set, check:
- same recurring panda couple across cards;
- female bow remains recognizable and tasteful;
- proportions match the rounded approved look;
- not too slim and not caricature-fat;
- Harmony violet/pink color family remains visible;
- cards feel premium rather than childish;
- scene meaning is readable without relying on long text;
- panda presence is actually relevant;
- no accidental text, logos, or watermarks in the illustration;
- four-card layout remains visually balanced;
- flip transition follows the locked staggered sequence exactly.

---

## 12. Core principle
Harmony Panda should make a user feel that the same lovable couple is living through many different relationship moments inside one coherent Harmony universe. The style is recognizable, but it must never overpower the actual question or force pandas into imagery where they do not belong.
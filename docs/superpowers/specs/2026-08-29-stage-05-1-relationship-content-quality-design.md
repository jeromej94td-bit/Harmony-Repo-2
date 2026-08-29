# 360 Rework — Stage 05.1 Relationship / Communication / Everyday-Life Content Quality Design

Date: 2026-08-29
Status: DESIGN APPROVED IN CHAT — awaiting written-spec review before implementation
Scope: Stage 05.1 only

## Goal

Rework the Harmony-360 relationship-facing content so the experience is driven by concrete couple insight, laughter, surprise, memory and useful conversation instead of template-generated filler. The priority is quality over raw question count.

This design covers the existing Harmony-360 areas that form Stage 05.1:

- Section 01 — Beziehung & Nähe
- Section 02 — Kommunikation
- Section 06 — Alltag & Zuhause
- Section 12 — Kommunikation & Konflikte

The current generated source already contains a large amount of content, but many packs reuse the same stems and option quartets with only the subject noun changed. Existing `Harmony360ContentRework` confirms this problem by maintaining an explicit list of overused option sets and by providing manual overrides for stronger Work & Career content. Stage 05.1 will apply the same principle more deliberately to relationship content.

## Product rule

A retained question must do at least one of these things:

1. reveal a concrete preference or need;
2. reveal a relationship habit or behavior;
3. create a useful partner prediction;
4. trigger a real memory or story;
5. expose a boundary, friction point or repair preference;
6. create genuine laughter or playful recognition;
7. surface a future wish worth talking about.

If a question does none of these, it is filler and should be rewritten, merged into a stronger pack, or archived from the visible Harmony-360 set.

Six excellent questions are preferred over eight weak questions. Pack size is not a quality target.

## Editorial strategy: Keep / Rewrite / Merge / Archive

Every Stage-05.1 pack receives one deterministic disposition.

### KEEP

Use when the theme and most prompts already create relationship-specific insight. Small wording polish is allowed.

Initial examples worth preserving as concepts:

- `Direkte Worte – Wie gut kennt ihr euch?`
- `Zwischen den Zeilen – Matchcheck`
- the useful core of `Streitkultur`
- concrete questions inside `Kleine Gesten`, `Vermissen`, `Wiedersehen`, `Schlafen` and similar packs when they describe a real couple behavior rather than a generic template.

### REWRITE

Use when the subject is valuable but the current questions are generic, unnatural or semantically disconnected.

Confirmed examples:

- `Körpernähe` currently ranks generic sets such as “Sofort ansprechen / Erst fühlen / Nähe suchen / Raum geben”. It should instead ask about touch, initiation, public/private affection, comfort, sleep contact, reassurance and boundaries.
- `Zuhören` currently treats listening like a generic plan or event. It should instead ask whether the person wants empathy, questions, solutions, silence, eye contact, follow-up and timing.
- `Ehrlichkeit` currently includes unrelated option sets such as time/energy/freedom/specialness. It should ask about timing, completeness, tact, omission, difficult truths and what needs immediate disclosure.
- `Haushalt`, `Ordnung`, `Einkaufen` and `Kochen im Alltag` should be grounded in mental load, standards, division of labor, shopping style, meal planning and real shared routines.
- `Entschuldigung`, `Schweigen`, `Kompromisse`, `Feedback` and `Missverständnisse` should focus on concrete conflict and repair behavior instead of recycled generic quartets.

### MERGE

Use when two or more visible packs are effectively the same relationship conversation wearing different labels.

The stronger prompts from duplicate or overlapping packs are moved into one canonical pack. The weaker source pack is then archived from the visible generated list. Source files are not destructively deleted in Stage 05.1.

Examples of likely merge families to evaluate during implementation:

- general communication style + “Zwischen den Zeilen” overlaps;
- duplicate `Missverständnisse` packs across Section 02 and Section 12;
- overlapping “Zuhören / Feedback / schwierige Gespräche” prompts where the only distinction is a template noun;
- routine/household packs where several packs ask the same planning/compromise questions.

A merge is approved only when the canonical pack remains conceptually coherent after receiving the strongest questions.

### ARCHIVE

Use when the pack is predominantly filler, semantically absurd, redundant after a merge, or produces less value than the visible clutter it creates.

Archiving means removing it from the final Stage-05.1 generated runtime list while leaving original source content intact for traceability and possible recovery.

Examples already identified as archive/rebuild candidates:

- generic “Geheime Wahl” packs that ask what is secretly attractive or luxurious about a negative topic such as `Missverständnisse`;
- memory prompts that make little sense for the topic, e.g. asking for the household scene someone wants to relive merely because the template is `Memory`;
- any pack whose eight questions are mostly noun substitutions of the same generic structure.

## Four user-facing content clusters

Stage 05.1 should feel like four coherent relationship areas rather than a random catalog of generated nouns.

### 1. Nähe & Zuneigung

Topics can include:

- small gestures
- quality time
- body contact
- affection in public vs private
- compliments
- being missed / reunion
- reassurance
- feeling noticed
- initiating closeness
- personal space

Desired emotional effect: warmth, recognition, gentle surprises.

### 2. Wie wir miteinander reden

Topics can include:

- listening
- direct vs indirect communication
- text messages
- phone calls
- tone of voice
- timing
- difficult conversations
- asking questions vs giving advice
- saying “I need…” clearly

Desired effect: “Ah, that is how you actually want me to communicate with you.”

### 3. Streit & Wiederannäherung

Topics can include:

- first reaction during conflict
- need for distance vs closeness
- repair attempts
- apology style
- receiving feedback
- silence
- compromise
- misunderstood intentions
- when humor helps or hurts
- what makes a conflict feel resolved

Desired effect: meaningful insight without presenting Harmony as therapy.

### 4. Unser echter Alltag

Topics can include:

- mental load
- household standards
- shopping
- cooking and meal planning
- sleep routines
- morning / evening routines
- phone use at home
- shared downtime
- chores nobody likes
- planning weekends
- “who notices what needs doing?”

Desired effect: high recognition, playful friction and useful everyday knowledge.

## New Quick Game: “Was brauchst du gerade?”

Stage 05.1 adds one new lightweight relationship game using existing question rendering/mechanics. It must not require the new Experience engine or any Stage-02 implementation.

### Product concept

A short sequence of concrete situations. Each question forces a simple preference between two meaningful responses.

Examples:

- “Du kommst nach einem richtig miesen Tag nach Hause. Was hilft dir eher?” — `Umarmung ohne Fragen` / `Erst mal 20 Minuten Ruhe`
- “Du erzählst mir ein Problem. Was willst du zuerst?” — `Zuhören` / `Mit mir eine Lösung suchen`
- “Wir hatten einen kleinen Streit. Was bringt dich eher zurück?” — `Nähe` / `Erst einmal Abstand`
- “Du fühlst dich übersehen. Was würde heute mehr bedeuten?” — `Zeit nur für uns` / `Eine kleine persönliche Geste`
- “Du bist stiller als sonst. Was soll ich eher tun?” — `Nachfragen` / `Warten, bis du selbst anfängst`
- “Du bist gestresst und wir haben etwas geplant. Was wäre liebevoller?” — `Plan vereinfachen` / `Dich entscheiden lassen`
- “Du zweifelst gerade an dir. Was hilft dir eher?” — `Konkrete Bestärkung` / `Einfach bei dir sein`
- “Du brauchst Nähe, aber ich merke es nicht. Was wäre dir lieber?” — `Ich frage aktiv nach` / `Du sagst es direkt`

Target length: 8–12 questions, quick enough for roughly 1–2 minutes.

### Mechanic

Use an already-supported simple choice flow. Do not add a new runtime engine in Stage 05.1. If the existing renderer supports two-option questions directly, use two options. If a concrete runner limitation is found during implementation, preserve the two-choice product behavior using the smallest compatible existing mechanism; do not broaden Stage 05.1 into engine work.

### Future compatibility

The content should be structured so it can later be reused by Partner Prediction: “What do you think your partner needs?” That later mechanic is not part of Stage 05.1.

## Additional playful content direction

Stage 05.1 should deliberately include a small number of highly recognizable couple moments so the area is not uniformly serious.

Examples of tone and specificity:

- “Du schreibst ‘alles gut’. Was bedeutet das bei dir meistens wirklich?”
- “Einer sagt ‘mir egal, such du aus’. Wie ungefährlich ist dieser Satz bei euch wirklich?”
- “Wer merkt zuerst, dass der andere eigentlich nur hungrig und nicht wirklich sauer ist?”
- “Was ist bei euch eher der echte Liebesbeweis: die letzte Pommes abgeben oder das Handy weglegen?”
- “Welche Kleinigkeit im Alltag würde dich stärker treffen als ein großes Geschenk?”

These questions should be used sparingly and only where they fit the pack. The target is recognition and warmth, not meme spam.

## Technical integration design

### Source preservation

Do not bulk-edit the four large generated section files. They remain the traceable raw source.

### Dedicated Stage-05.1 curation layer

Add a focused list-level transformation such as `Harmony360RelationshipQualityRework` that receives the already-cleaned Harmony-360 pack list and returns the curated Stage-05.1 list.

Expected responsibilities:

- manual question overrides for canonical packs;
- deterministic archive-ID filtering;
- deterministic merge behavior: strongest prompts are represented in the canonical pack and redundant source packs are filtered;
- append the new `Was brauchst du gerade?` quick-game pack;
- leave all non-05.1 sections unchanged.

The transform should be applied after the existing per-pack cleanup pipeline so the hand-curated Stage-05.1 wording is the final authority for affected packs.

Conceptual pipeline:

`raw sections -> scenario cleanup -> text cleanup -> generic Harmony360ContentRework -> Stage05.1 relationship curation`

### Isolation

The Stage-05.1 curation layer must identify affected content by explicit IDs/tags. It must not silently rewrite unrelated Stage-05.2–05.5 content, Stage 02 proposal experiences or Stage 06 cleanup rules.

## Implementation slicing

Do not ship 05.1 as one giant PR. Use small mergeable slices.

Recommended sequence:

1. **05.1a — Curation infrastructure + tests**
   - list-level curation hook
   - explicit scope detection for Sections 01/02/06/12
   - archive/override mechanics
   - no broad content rewrite yet

2. **05.1b — Nähe & Zuneigung**
   - keep/rewrite/merge/archive the relevant Section-01 packs

3. **05.1c — Kommunikation**
   - Section 02 content pass

4. **05.1d — Alltag & Zuhause**
   - Section 06 content pass

5. **05.1e — Streit & Wiederannäherung**
   - Section 12 content pass

6. **05.1f — Quick Game “Was brauchst du gerade?”**
   - 8–12 concrete two-choice situations

7. **05.1g — Cross-section duplicate audit + final verification**
   - confirm merged/archived duplicates no longer appear twice
   - confirm non-05.1 content is unchanged
   - update 360 Rework tracker only when Stage 05.1 Definition of Done is actually satisfied

The exact repository work-package number should be assigned when implementation starts, using the then-current 360-Rework state to avoid collisions with parallel Stage-02 work.

## Testing and verification

At minimum, focused tests must cover:

- only Sections 01/02/06/12 are affected;
- archived IDs are absent from the final Harmony-360 runtime list;
- canonical merged packs remain present;
- rewritten packs contain the intended concrete prompts/options;
- the new quick game is registered exactly once;
- the quick game has 8–12 questions and valid options;
- non-05.1 packs remain byte-/value-equivalent through the curation layer;
- no duplicate pack IDs are introduced;
- previously repaired Stage-06 text/brand cleanup remains preserved because this layer runs after existing cleanup rather than replacing it.

A full Android/Gradle verification should be attempted when the relevant CI/tooling is available. Existing repository-wide GitHub Actions infrastructure failures must be reported as infrastructure failures, not misrepresented as passing tests or application-code failures.

## Definition of Done for Stage 05.1

Stage 05.1 is complete only when:

- the four target content areas have been audited pack-by-pack;
- every visible pack has an explicit Keep/Rewrite/Merge/Archive decision;
- obvious noun-substitution filler and semantically absurd prompts are no longer visible at runtime;
- duplicate/overlapping topics are consolidated where appropriate;
- retained questions are concrete, relationship-relevant and worth answering;
- `Was brauchst du gerade?` is available as a short playable quick game using existing mechanics;
- focused regression tests pass;
- the 360 Rework roadmap/current-state/worklog are updated with actual merge evidence and verification limitations.

Stage 05 overall then moves from `0/5` to `1/5 = 20%`. No progress is awarded merely for writing this design or for partial content slices.

## Non-goals

- no work on Stage 02 proposal experience;
- no reusable Experience-engine work from Stage 03;
- no Stage 04 legacy proposal consolidation;
- no broad Stage 05.2–05.5 rewrite;
- no unrelated Stage 06 defect cleanup;
- no Daily feature;
- no new network/API dependency;
- no destructive deletion of original generated source files.

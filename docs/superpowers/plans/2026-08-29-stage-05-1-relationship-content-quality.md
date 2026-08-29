# Stage 05.1 Relationship Content Quality Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Curate Harmony-360 relationship, communication, conflict and everyday-life content into a smaller, stronger set with deterministic Keep/Rewrite/Merge/Archive decisions and a new two-choice Quick Game, without depending on the Stage-02 Experience engine.

**Architecture:** Preserve the four large generated section files as raw source. Add one list-level `Harmony360RelationshipQualityRework` transformation after the existing scenario/text/generic cleanup pipeline; it applies explicit pack-ID overrides, archive filtering and canonical merge decisions only to Sections 01/02/06/12. Add the Quick Game through a dedicated small content object and append it from the same curation layer.

**Tech Stack:** Kotlin, existing `GenPack` / `GenQuestion` models, JUnit4 unit tests, current Harmony-360 generated-content pipeline.

**Spec:** `docs/superpowers/specs/2026-08-29-stage-05-1-relationship-content-quality-design.md`

## Global Constraints

- Stage 05.1 only: Sections 01, 02, 06 and 12.
- Do not modify or depend on Stage-02 proposal Experience implementation.
- Do not destructively edit the four generated section source files.
- Do not broaden into Stage 05.2–05.5 or unrelated Stage-06 cleanup.
- Existing Stage-06 cleanup output must remain preserved.
- Use explicit IDs/tags for curation; no heuristic rewriting of unrelated packs.
- Six strong questions are preferred over eight filler questions.
- Stage 05.1 progress changes only after an independently completed slice; Stage 05 overall becomes 1/5 = 20% only after all seven slices are complete.

---

### Task 1 / 05.1a / Work Package 25.1: Curation infrastructure

**Files:**
- Create: `app/src/main/java/com/example/data/Harmony360RelationshipQualityRework.kt`
- Modify: `app/src/main/java/com/example/data/GeneratedHarmonyAdrenaline360.kt`
- Create: `app/src/test/java/com/example/data/Harmony360RelationshipQualityReworkTest.kt`

**Interfaces:**
- Consumes: `List<GenPack>` after `GeneratedHarmony360ScenarioCleanup`, `GeneratedHarmony360TextCleanup`, and `Harmony360ContentRework`.
- Produces: `Harmony360RelationshipQualityRework.apply(packs: List<GenPack>): List<GenPack>`.

- [ ] **Step 1: Write failing scope tests**

Create tests proving that the new transform exists, preserves pack order/values for unrelated sections, and recognizes only tags `h360_section_01_beziehung_naehe`, `h360_section_02_kommunikation`, `h360_section_06_alltag_zuhause`, `h360_section_12_kommunikation_konflikte` as Stage-05.1 scope.

```kotlin
@Test
fun `unrelated section stays value equivalent`() {
    val pack = GenPack(id = "h500_211_test", title = "Test", tags = listOf("harmony360", "h360_section_10_arbeit_karriere"))
    assertEquals(listOf(pack), Harmony360RelationshipQualityRework.apply(listOf(pack)))
}

@Test
fun `stage 05 1 section is recognized without mutating by default`() {
    val pack = GenPack(id = "h500_001_test", title = "Test", tags = listOf("harmony360", "h360_section_01_beziehung_naehe"))
    assertEquals(listOf(pack), Harmony360RelationshipQualityRework.apply(listOf(pack)))
}
```

- [ ] **Step 2: Run the focused test and verify RED**

Run: `./gradlew testDebugUnitTest --tests com.example.data.Harmony360RelationshipQualityReworkTest`

Expected: FAIL because `Harmony360RelationshipQualityRework` does not exist.

- [ ] **Step 3: Implement minimal list-level transform**

```kotlin
object Harmony360RelationshipQualityRework {
    private val sectionTags = setOf(
        "h360_section_01_beziehung_naehe",
        "h360_section_02_kommunikation",
        "h360_section_06_alltag_zuhause",
        "h360_section_12_kommunikation_konflikte"
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.map { pack ->
        if (pack.tags.none(sectionTags::contains)) pack else pack
    }
}
```

Wire it as the final step in `GeneratedHarmonyAdrenaline360.PACKS`:

```kotlin
Harmony360RelationshipQualityRework.apply(
    raw
        .map(GeneratedHarmony360ScenarioCleanup::apply)
        .map(GeneratedHarmony360TextCleanup::apply)
        .map(Harmony360ContentRework::apply)
)
```

- [ ] **Step 4: Add archive/override primitives with tests**

Inside the object, use explicit `archivedIds: Set<String>` and `questionOverrides: Map<String, List<GenQuestion>>`. Test that a known fixture ID can be filtered and a known fixture override can replace questions while unrelated IDs remain identical. Production collections may start empty; the behavior itself must be testable through package-visible helper functions or internal constructor-free functions without adding production dummy IDs.

- [ ] **Step 5: Run focused tests GREEN**

Run: `./gradlew testDebugUnitTest --tests com.example.data.Harmony360RelationshipQualityReworkTest`
Expected: PASS.

- [ ] **Step 6: Commit and open a narrow PR**

Branch: `360-rework/25-1-stage-05-1a-curation-infra`
PR title: `[25.1] 360 Rework — Stage 05.1a curation infrastructure`
Expected Stage-05.1 progress after merge: `1/7 = 14.3%`.

---

### Task 2 / 05.1b / Work Package 25.2: Nähe & Zuneigung

**Files:**
- Modify: `app/src/main/java/com/example/data/Harmony360RelationshipQualityRework.kt`
- Modify: `app/src/test/java/com/example/data/Harmony360RelationshipQualityReworkTest.kt`

**Interfaces:** Extend `questionOverrides` and `archivedIds`; do not change the public transform signature.

- [ ] **Step 1: Add failing tests for Section-01 decisions**

Tests must assert concrete rewrites for at least these canonical packs:
- `h500_001_zuneigung_im_alltag_entweder_oder`
- `h500_003_kleine_gesten_skala`
- `h500_004_koerpernaehe_ranking`
- `h500_005_komplimente_prognose`
- `h500_006_vermissen_szenario`

The test for `Körpernähe` must reject the generic option set `Sofort ansprechen / Erst fühlen / Nähe suchen / Raum geben` and require touch-specific options such as `Hand halten`, `Umarmung`, `Kuscheln`, `Nähe beim Einschlafen` across its curated questions.

- [ ] **Step 2: Run focused test RED**

Run the same focused Gradle test; expected failures are missing curated prompts/options.

- [ ] **Step 3: Add manual Section-01 overrides**

Curate 6–8 strong questions per retained pack. Include public/private affection, initiation of touch, comfort vs desire for space, compliments that land, reunion/being missed, and small gestures. Preserve `defaultMine` where present.

- [ ] **Step 4: Archive only confirmed filler duplicates**

Archive a Section-01 pack only when its useful material is represented in a canonical retained pack. Tests must name every archived ID explicitly and prove the canonical pack remains.

- [ ] **Step 5: Run focused tests GREEN and commit**

Branch: `360-rework/25-2-stage-05-1b-nearness`
PR title: `[25.2] 360 Rework — Stage 05.1b Nähe & Zuneigung`
Expected progress after merge: `2/7 = 28.6%`.

---

### Task 3 / 05.1c / Work Package 25.3: Wie wir miteinander reden

**Files:** same curation production/test files.

- [ ] **Step 1: Add failing tests** for canonical communication packs including `h500_026_zuhoeren_szenario`, `h500_028_schwierige_gespraeche_memory`, `h500_029_ehrlichkeit_prioritaet`, `h500_030_direkte_worte_offene_runde`, `h500_031_zwischen_den_zeilen_entweder_oder`, and `h500_032_textnachrichten_wer_eher`.

The `Zuhören` test must require real listening needs (`nur zuhören`, `Fragen stellen`, `Lösung suchen`, `erst Ruhe`) rather than plan/event language. `Ehrlichkeit` must focus on timing, tact, completeness and difficult truths. `Direkte Worte` remains canonical unless a regression is found.

- [ ] **Step 2: Run RED.**
- [ ] **Step 3: Implement concrete overrides and merge duplicate communication packs into canonical IDs.**
- [ ] **Step 4: Add tests proving duplicate/archived IDs are absent and canonical IDs are present.**
- [ ] **Step 5: Run GREEN and commit.**

Branch: `360-rework/25-3-stage-05-1c-communication`
PR title: `[25.3] 360 Rework — Stage 05.1c Kommunikation`
Expected progress after merge: `3/7 = 42.9%`.

---

### Task 4 / 05.1d / Work Package 25.4: Unser echter Alltag

**Files:** same curation production/test files.

- [ ] **Step 1: Add failing tests** for concrete everyday themes: morning/evening routine, household/mental load, order standards, shopping, cooking/meal planning, sleeping, phone use/shared downtime.
- [ ] **Step 2: Run RED.**
- [ ] **Step 3: Rewrite retained Section-06 packs** so questions address who notices tasks, acceptable cleanliness, division of labor, budget/spontaneous shopping, meal responsibility, sleep contact/noise/bedtime, routine preferences and weekend planning.
- [ ] **Step 4: Merge/archive semantically redundant routine/household packs only through explicit IDs and test the final visible set.**
- [ ] **Step 5: Run GREEN and commit.**

Branch: `360-rework/25-4-stage-05-1d-everyday`
PR title: `[25.4] 360 Rework — Stage 05.1d Alltag & Zuhause`
Expected progress after merge: `4/7 = 57.1%`.

---

### Task 5 / 05.1e / Work Package 25.5: Streit & Wiederannäherung

**Files:** same curation production/test files.

- [ ] **Step 1: Add failing tests** for `Streitkultur`, `Entschuldigung`, `Schweigen`, `Kompromisse`, `Feedback`, `Missverständnisse` and at least one repair-focused canonical pack.
- [ ] **Step 2: Run RED.**
- [ ] **Step 3: Rewrite around concrete conflict behavior**: immediate reaction, distance vs closeness, apology language, repair attempts, receiving feedback, silence meaning, compromise boundaries, humor timing and what counts as resolved.
- [ ] **Step 4: Consolidate duplicate `Missverständnisse` presentation across Sections 02 and 12.** Keep one canonical topic and archive the redundant visible pack; test both decisions explicitly.
- [ ] **Step 5: Run GREEN and commit.**

Branch: `360-rework/25-5-stage-05-1e-conflict-repair`
PR title: `[25.5] 360 Rework — Stage 05.1e Streit & Wiederannäherung`
Expected progress after merge: `5/7 = 71.4%`.

---

### Task 6 / 05.1f / Work Package 25.6: Quick Game “Was brauchst du gerade?”

**Files:**
- Create: `app/src/main/java/com/example/data/Harmony360NeedNowQuickGame.kt`
- Modify: `app/src/main/java/com/example/data/Harmony360RelationshipQualityRework.kt`
- Create/modify: `app/src/test/java/com/example/data/Harmony360NeedNowQuickGameTest.kt`

- [ ] **Step 1: Write failing Quick-Game tests** asserting exactly one pack ID `h360_need_now_quick`, 8–12 questions, exactly two nonblank options per question, `topic = "beziehung"`, and no dependency on Experience classes.
- [ ] **Step 2: Run RED.**
- [ ] **Step 3: Implement 10 concrete two-choice situations** covering bad day, problem sharing, post-conflict reconnection, feeling overlooked, unusual silence, stress before plans, self-doubt, unspoken need for closeness, decision fatigue, and one playful hungry-vs-angry recognition moment.
- [ ] **Step 4: Append the pack exactly once from the curation transform.** If a pack with the same ID already exists, replace/retain deterministically rather than duplicating it.
- [ ] **Step 5: Run both Quick-Game and curation tests GREEN and commit.**

Branch: `360-rework/25-6-stage-05-1f-need-now`
PR title: `[25.6] 360 Rework — Stage 05.1f Was brauchst du gerade?`
Expected progress after merge: `6/7 = 85.7%`.

---

### Task 7 / 05.1g / Work Package 25.7: Cross-section audit, verification and tracker

**Files:**
- Modify tests as needed under `app/src/test/java/com/example/data/`
- Modify: `docs/360-rework/360_REWORK_MASTER_ROADMAP.md`
- Modify: `docs/360-rework/360_REWORK_CURRENT_STATE.md`
- Append: `docs/360-rework/360_REWORK_WORKLOG.md`

- [ ] **Step 1: Add final audit tests** asserting no duplicate pack IDs, archived IDs absent, canonical packs present, Quick Game exactly once, all Quick-Game questions valid, and non-05.1 packs unchanged through `Harmony360RelationshipQualityRework.apply`.
- [ ] **Step 2: Add a content-quality assertion for curated packs** rejecting known generic filler quartets where a pack has an explicit manual override.
- [ ] **Step 3: Run focused Stage-05.1 tests.**

Run:
`./gradlew testDebugUnitTest --tests com.example.data.Harmony360RelationshipQualityReworkTest --tests com.example.data.Harmony360NeedNowQuickGameTest`

Expected: PASS.

- [ ] **Step 4: Attempt broader relevant unit verification.**

Run:
`./gradlew testDebugUnitTest --tests 'com.example.data.*Harmony360*'`

Record exact result. If the repository environment cannot execute Gradle or Actions fail before step 1, document the infrastructure limitation without claiming green.

- [ ] **Step 5: Update tracker only with verified facts.**

After all seven slices are independently merged, set:
- Stage 05.1: `7/7 = 100%`
- Stage 05 overall: `1/5 = 20%`
- Next Stage-05 substage: `05.2 — Food / travel / leisure / culture`

Do not modify the active Stage-02 percentage/status except to preserve newer concurrent facts already on `main`.

- [ ] **Step 6: Commit and merge narrow final audit PR.**

Branch: `360-rework/25-7-stage-05-1g-final-audit`
PR title: `[25.7] 360 Rework — Stage 05.1 final quality audit`
Expected progress after merge: `7/7 = 100%`; Stage 05 = `1/5 = 20%`.

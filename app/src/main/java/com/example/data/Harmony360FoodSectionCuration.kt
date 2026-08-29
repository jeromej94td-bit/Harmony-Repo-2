package com.example.data

/** Explicit Stage 05.2 curation for Harmony-360 Section 05 — Essen & Genuss. */
object Harmony360FoodSectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – extrem")
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun scaleQ(text: String): GenQuestion = GenQuestion(q = text, options = scale)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_101_fruehstueck_entweder_oder" to CurationDecision.REWRITE,
        "h500_102_brunch_wer_eher" to CurationDecision.REWRITE,
        "h500_103_streetfood_skala" to CurationDecision.REWRITE,
        "h500_104_fine_dining_ranking" to CurationDecision.REWRITE,
        "h500_105_comfort_food_prognose" to CurationDecision.REWRITE,
        "h500_106_suesses_szenario" to CurationDecision.REWRITE,
        "h500_108_scharfes_essen_memory" to CurationDecision.REWRITE,
        "h500_109_pasta_prioritaet" to CurationDecision.REWRITE,
        "h500_110_pizza_offene_runde" to CurationDecision.REWRITE,
        "h500_111_sushi_entweder_oder" to CurationDecision.REWRITE,
        "h500_112_burger_wer_eher" to CurationDecision.REWRITE,
        "h500_113_desserts_skala" to CurationDecision.REWRITE,
        "h500_114_kaffee_ranking" to CurationDecision.REWRITE,
        "h500_117_kochen_zu_zweit_geheime_wahl" to CurationDecision.REWRITE,
        "h500_119_restaurantwahl_prioritaet" to CurationDecision.REWRITE,
        "h500_120_lieblingskuechen_offene_runde" to CurationDecision.REWRITE,
        "h500_122_mitternachtsessen_wer_eher" to CurationDecision.REWRITE,
        "h500_125_unser_perfektes_dinner_prognose" to CurationDecision.REWRITE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_101_fruehstueck_entweder_oder" to listOf(
            q("Was landet morgens eher auf deinem Teller?", "Süßes Frühstück", "Herzhaftes Frühstück"),
            q("Was wäre eher dein Frühstücks-Favorit?", "Frühstücksei", "Müsli oder Porridge"),
            q("Wie startest du lieber?", "Schnell etwas essen", "In Ruhe frühstücken"),
            q("Was passt eher zu dir?", "Kaffee oder Tee reicht fast", "Ich brauche richtiges Essen"),
            q("Am freien Tag: Was reizt dich mehr?", "Früh zuhause frühstücken", "Später ausgiebig brunchen"),
            q("Wenn ihr nur eins teilen könntet?", "Frisches Gebäck", "Obst und Joghurt")
        ),
        "h500_102_brunch_wer_eher" to listOf(
            whoQ("Wer schlägt eher spontan vor, am Wochenende brunchen zu gehen?"),
            whoQ("Wer bestellt beim Brunch eher noch etwas zusätzlich, obwohl der Tisch schon voll ist?"),
            whoQ("Wer probiert eher zuerst vom Teller des anderen?"),
            whoQ("Wer findet eher das Café, das ihr beide noch nicht kennt?"),
            whoQ("Wer bleibt beim Brunch eher so lange sitzen, dass daraus fast Mittagessen wird?"),
            whoQ("Wer würde eher zuhause einen großen Brunch für euch vorbereiten?")
        ),
        "h500_103_streetfood_skala" to listOf(
            scaleQ("Wie gern probierst du Streetfood, dessen Namen du vorher noch nie gehört hast?"),
            scaleQ("Wie wenig stört es dich, beim Essen zu stehen oder auf einer einfachen Bank zu sitzen?"),
            scaleQ("Wie wichtig ist dir beim Streetfood, direkt zu sehen, wie es zubereitet wird?"),
            scaleQ("Wie gern teilst du mehrere kleine Streetfood-Gerichte statt nur eines zu bestellen?"),
            scaleQ("Wie mutig bist du bei unbekannten Gewürzen und Texturen?"),
            scaleQ("Wie sehr gehört Streetfood für dich zu einer Reise oder einem neuen Viertel dazu?")
        ),
        "h500_104_fine_dining_ranking" to listOf(
            q("Was macht Fine Dining für dich zuerst besonders? Ordne.", "Geschmack", "Menüfolge", "Service", "Atmosphäre"),
            q("Wofür würdest du bei einem besonderen Dinner am ehesten mehr bezahlen? Ordne.", "Außergewöhnliche Zutaten", "Kreative Zubereitung", "Perfekter Service", "Besondere Location"),
            q("Was entscheidet, ob sich ein langes Menü lohnt? Ordne.", "Überraschende Gänge", "Passende Portionsgröße", "Gutes Tempo", "Zeit zum Reden"),
            q("Was wäre dir bei der Auswahl wichtiger? Ordne.", "Regional", "Saisonal", "Experimentell", "Klassisch perfektioniert"),
            q("Was sollte ein Fine-Dining-Abend am wenigsten sein? Ordne vom größten Störfaktor.", "Steif", "Zu laut", "Zu langsam", "Mehr Show als Geschmack"),
            q("Was würdest du vom Abend am liebsten mit nach Hause nehmen? Ordne.", "Ein neues Lieblingsgericht", "Eine überraschende Kombination", "Eine schöne Erinnerung", "Eine neue Geschmacksidee")
        ),
        "h500_105_comfort_food_prognose" to listOf(
            q("Was würde dein Partner an einem richtig schlechten Tag vermutlich als Comfort Food wählen?", "Pasta", "Pizza", "Suppe", "Etwas Süßes"),
            q("Welche Art Comfort Food passt wahrscheinlich besser zu deinem Partner?", "Wie früher zuhause", "Deftig und warm", "Süß und weich", "Bestellen statt kochen"),
            q("Was wäre deinem Partner dabei vermutlich wichtiger?", "Vertrauter Geschmack", "Große Portion", "Schnell verfügbar", "Gemeinsam essen"),
            q("Wenn dein Partner krank auf dem Sofa liegt: Was trifft eher?", "Brühe oder Suppe", "Toast oder etwas Einfaches", "Lieblingsessen bestellen", "Nur Tee und später schauen"),
            q("Welche Erinnerung steckt für deinen Partner wahrscheinlich am ehesten in Comfort Food?", "Familie", "Kindheit", "Urlaub", "Gemeinsame Zeit mit dir"),
            q("Wie würdest du deinen Partner mit Essen am ehesten aufmuntern?", "Lieblingsgericht kochen", "Lieblingssnack mitbringen", "Etwas Neues bestellen", "Gemeinsam etwas Einfaches machen")
        ),
        "h500_106_suesses_szenario" to listOf(
            q("Ihr wollt nur ein Dessert teilen, aber eure Favoriten sind komplett verschieden. Was macht ihr?", "Zwei kleine nehmen", "Heute entscheidet einer", "Etwas Drittes wählen", "Kein Teilen – jeder sein eigenes"),
            q("Im Café ist genau dein Lieblingskuchen ausverkauft. Was wählst du?", "Neuen Kuchen probieren", "Eis nehmen", "Nur Kaffee oder Tee", "Woanders weitersuchen"),
            q("Einer möchte weniger Zucker essen, der andere bringt ständig Süßes mit. Was wäre hilfreich?", "Vorher absprechen", "Kleine Portionen", "Süßes nur bewusst kaufen", "Getrennte Snacks"),
            q("Ihr bekommt eine große Pralinenschachtel geschenkt. Wie geht ihr ran?", "Alles halbieren", "Jeder sucht Favoriten", "Blind probieren", "Für besondere Abende aufheben"),
            q("Beim Backen wird das Dessert optisch ein Desaster, schmeckt aber gut. Was zählt?", "Sofort essen", "Noch retten und dekorieren", "Foto vom Chaos machen", "Neu anfangen"),
            q("Nach dem Essen seid ihr eigentlich satt, die Dessertkarte sieht aber gefährlich gut aus. Was macht ihr?", "Eins teilen", "Jeder bestellt", "Nur probieren, wenn etwas Besonderes dabei ist", "Heute vernünftig bleiben")
        ),
        "h500_108_scharfes_essen_memory" to listOf(
            GenQuestion("Wann hast du zum ersten Mal etwas gegessen, das wirklich zu scharf für dich war?"),
            GenQuestion("Welches scharfe Gericht hat dich positiv überrascht, obwohl du erst skeptisch warst?"),
            GenQuestion("Gab es einen Moment, in dem du Schärfe völlig überschätzt hast – was ist passiert?"),
            GenQuestion("Welche Person oder Reise hat deinen Geschmack für scharfes Essen am stärksten geprägt?"),
            GenQuestion("Welches scharfe Gericht würdest du deinem Partner unbedingt einmal zeigen?"),
            GenQuestion("Welche lustige Erinnerung mit Chili, scharfer Sauce oder einem viel zu mutigen Bissen ist hängen geblieben?")
        ),
        "h500_109_pasta_prioritaet" to listOf(
            q("Was entscheidet bei Pasta zuerst?", "Sauce", "Pasta-Sorte", "Gute Zutaten", "Perfekter Garpunkt"),
            q("Welche Pasta-Richtung gewinnt bei dir?", "Tomatig", "Cremig", "Ölig und würzig", "Gefüllt"),
            q("Was wäre dir bei selbst gekochter Pasta wichtiger?", "Einfach und richtig gut", "Aufwendig und besonders", "Schnell fertig", "Gemeinsam zubereitet"),
            q("Welche Zutat dürfte am wenigsten fehlen?", "Käse", "Kräuter", "Knoblauch", "Gemüse"),
            q("Wenn ihr nur ein Pasta-Erlebnis wählen könntet?", "Kleine Trattoria", "Selbstgemachte Pasta zuhause", "Modernes Restaurant", "Pasta direkt im Urlaub"),
            q("Was ist bei Pasta für dich am ehesten verhandelbar?", "Form", "Sauce", "Extras", "Portionsgröße")
        ),
        "h500_110_pizza_offene_runde" to listOf(
            GenQuestion("Welche Pizza würdest du bestellen, wenn du garantiert mit niemandem teilen müsstest?"),
            GenQuestion("Welche Pizza-Zutat ist für dich völlig überschätzt – und welche muss fast immer drauf?"),
            GenQuestion("Dünn und knusprig oder dicker, weicher Teig: Was macht für dich die bessere Pizza aus?"),
            GenQuestion("Was war die beste Pizza, an die du dich erinnern kannst – und wo war das?"),
            GenQuestion("Wie kompromissbereit bist du, wenn ihr nur eine Pizza gemeinsam bestellen wollt?"),
            GenQuestion("Welche ungewöhnliche Pizza-Kombination würdest du wenigstens einmal mit mir testen?")
        ),
        "h500_111_sushi_entweder_oder" to listOf(
            q("Was greifst du eher zuerst?", "Nigiri", "Maki"),
            q("Was passt eher zu dir?", "Klassisch mit Fisch", "Vegetarisch"),
            q("Wie isst du lieber Sushi?", "Viele Sorten teilen", "Meine Favoriten selbst bestellen"),
            q("Was reizt dich mehr?", "Bekannte Lieblingsrollen", "Etwas völlig Neues probieren"),
            q("Was wäre eher dein Abend?", "Sushi-Restaurant", "Sushi zuhause bestellen"),
            q("Was gewinnt bei dir?", "Schlicht und hochwertig", "Kreativ mit vielen Zutaten")
        ),
        "h500_112_burger_wer_eher" to listOf(
            whoQ("Wer bestellt beim Burger eher doppelt Käse oder eine Extra-Zutat?"),
            whoQ("Wer nimmt eher den ungewöhnlichsten Burger auf der Karte?"),
            whoQ("Wer klaut dem anderen eher Pommes vom Teller?"),
            whoQ("Wer baut zuhause eher einen Burger, der kaum noch in den Mund passt?"),
            whoQ("Wer besteht eher auf einer bestimmten Sauce?"),
            whoQ("Wer würde eher einen sehr guten vegetarischen Burger dem mittelmäßigen Fleischburger vorziehen?")
        ),
        "h500_113_desserts_skala" to listOf(
            scaleQ("Wie wichtig ist dir ein Dessert nach einem besonderen Essen?"),
            scaleQ("Wie gern teilst du Desserts, um mehrere Sorten zu probieren?"),
            scaleQ("Wie sehr ziehst du ein fruchtiges Dessert einem sehr schokoladigen vor?"),
            scaleQ("Wie neugierig bist du auf Desserts mit ungewöhnlichen Zutaten?"),
            scaleQ("Wie wahrscheinlich ist es, dass du nur wegen eines Desserts in ein bestimmtes Café gehst?"),
            scaleQ("Wie viel Platz lässt du bei einem guten Essen ganz bewusst noch fürs Dessert?")
        ),
        "h500_114_kaffee_ranking" to listOf(
            q("Welche Kaffeearten passen am besten zu dir? Ordne.", "Espresso", "Cappuccino", "Filterkaffee", "Eiskaffee"),
            q("Was ist bei Kaffee am wichtigsten? Ordne.", "Geschmack", "Stärke", "Temperatur", "Milch oder keine Milch"),
            q("Wann schmeckt Kaffee für dich am besten? Ordne.", "Direkt morgens", "Nach dem Frühstück", "Nach dem Essen", "Am Nachmittag"),
            q("Wo trinkst du Kaffee am liebsten? Ordne.", "Zuhause", "Kleines Café", "Unterwegs", "Im Urlaub draußen"),
            q("Was stört dich bei Kaffee am meisten? Ordne.", "Zu bitter", "Zu dünn", "Zu süß", "Zu kalt"),
            q("Was macht einen gemeinsamen Kaffee-Moment besonders? Ordne.", "Zeit zum Reden", "Guter Kaffee", "Schöner Ort", "Keine Eile")
        ),
        "h500_117_kochen_zu_zweit_geheime_wahl" to listOf(
            q("Wie würdest du beim gemeinsamen Kochen die Aufgaben am liebsten verteilen?", "Einer kocht, einer schnippelt", "Beide machen alles zusammen", "Jeder übernimmt ein Gericht", "Einer führt, der andere hilft"),
            q("Was würdest du heimlich lieber zusammen kochen?", "Pasta von Grund auf", "Curry", "Pizza", "Mehrere kleine Gerichte"),
            q("Wie viel Rezept braucht ihr?", "Genau befolgen", "Als grobe Orientierung", "Nur Zutatenliste", "Komplett improvisieren"),
            q("Was darf beim gemeinsamen Kochen eher passieren?", "Küche wird chaotisch", "Es dauert viel länger", "Ein Gericht misslingt", "Wir ändern spontan den Plan"),
            q("Was wäre dein Lieblings-Part?", "Vorbereiten", "Am Herd stehen", "Abschmecken", "Anrichten"),
            q("Was macht Kochen zu zweit für dich wirklich gut?", "Teamwork", "Reden nebenbei", "Gemeinsam etwas Neues lernen", "Das Essen danach")
        ),
        "h500_119_restaurantwahl_prioritaet" to listOf(
            q("Was entscheidet bei der Restaurantwahl zuerst?", "Küche", "Preis", "Entfernung", "Atmosphäre"),
            q("Wenn eure Geschmäcker auseinandergehen: Was ist wichtiger?", "Beide finden etwas", "Einer darf heute wählen", "Etwas Neues für beide", "Bewährter Kompromiss"),
            q("Wofür würdest du eher einen längeren Weg fahren?", "Besonderes Essen", "Sehr gute Bewertungen", "Schöne Aussicht", "Restaurant mit Erinnerung"),
            q("Was ist dir bei spontaner Restaurantwahl am wichtigsten?", "Ohne Reservierung möglich", "Kurze Wartezeit", "Interessante Karte", "Faire Preise"),
            q("Was wäre für dich der größte Grund, ein Restaurant nicht wieder zu besuchen?", "Essen enttäuscht", "Schlechter Service", "Zu laut", "Preis passt nicht"),
            q("Was darf bei einem Date-Restaurant am wenigsten fehlen?", "Gutes Gespräch möglich", "Besondere Atmosphäre", "Essen zum Teilen", "Ein Gericht, auf das ihr euch freut")
        ),
        "h500_120_lieblingskuechen_offene_runde" to listOf(
            GenQuestion("Welche Landesküche könntest du wochenlang essen, ohne dass sie dir langweilig wird?"),
            GenQuestion("Welche Küche liebst du, obwohl einzelne typische Zutaten eigentlich gar nicht dein Ding sind?"),
            GenQuestion("Welche Küche würdest du deinem Partner gern besser zeigen oder gemeinsam entdecken?"),
            GenQuestion("Bei welcher Landesküche bist du deutlich wählerischer als die meisten Menschen?"),
            GenQuestion("Welches Gericht repräsentiert für dich deine Lieblingsküche am besten?"),
            GenQuestion("Welche Küche hat dich erst spät überzeugt – und durch welches Essen?")
        ),
        "h500_122_mitternachtsessen_wer_eher" to listOf(
            whoQ("Wer bekommt eher kurz vor dem Schlafen plötzlich noch richtig Hunger?"),
            whoQ("Wer würde nachts eher noch Pasta oder etwas Warmes machen statt nur einen Snack zu nehmen?"),
            whoQ("Wer bestellt eher spätabends noch Essen?"),
            whoQ("Wer hat eher einen geheimen Lieblingssnack für nach Mitternacht?"),
            whoQ("Wer würde eher den letzten Rest aus dem Kühlschrank kreativ verwerten?"),
            whoQ("Wer sagt eher 'nur ein kleiner Snack' und macht daraus eine komplette Mahlzeit?")
        ),
        "h500_125_unser_perfektes_dinner_prognose" to listOf(
            q("Was würde dein Partner für euer perfektes Dinner vermutlich zuerst wählen?", "Selbst kochen", "Lieblingsrestaurant", "Neues Restaurant testen", "Mehrere kleine Gerichte zuhause"),
            q("Welche Küche würde dein Partner wahrscheinlich wählen?", "Italienisch", "Asiatisch", "Mediterran", "Etwas völlig Neues"),
            q("Was wäre deinem Partner für den Abend am wichtigsten?", "Das Essen", "Zeit zum Reden", "Schöne Atmosphäre", "Keine Planung und kein Stress"),
            q("Welcher Hauptgang würde deinem Partner am ehesten gefallen?", "Pasta oder Risotto", "Fisch oder Meeresfrüchte", "Fleischgericht", "Vegetarisches Highlight"),
            q("Wie würde dein Partner das Dinner am liebsten beenden?", "Dessert teilen", "Jeder sein Dessert", "Kaffee oder Tee", "Noch eine kleine Runde spazieren"),
            q("Was wäre für deinen Partner die schönste Überraschung?", "Lieblingsgericht ohne Nachfrage", "Ein Gericht aus einer gemeinsamen Erinnerung", "Etwas, das ihr noch nie gegessen habt", "Ein selbst gemachtes Dessert")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.mapNotNull { pack ->
        when {
            decisions[pack.id] == CurationDecision.ARCHIVE -> null
            pack.id in overrides -> pack.copy(questions = overrides.getValue(pack.id))
            else -> pack
        }
    }
}

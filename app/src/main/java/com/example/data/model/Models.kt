package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// --- ROOM ENTITIES ---

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Jerome",
    val partnerName: String = "Alex",
    val startDate: Long = System.currentTimeMillis() - (830L * 24 * 3600 * 1000), // ~2.28 years ago
    val simulatorEnabled: Boolean = true,
    val userAvatarPath: String? = null,
    val partnerAvatarPath: String? = null
)

@Entity(tableName = "answers", primaryKeys = ["packId", "questionIndex"])
data class AnswerEntity(
    val packId: String,
    val questionIndex: Int,
    val answerText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "me" or "them"
    val text: String,
    val imagePath: String? = null,
    val audioPath: String? = null,
    val audioDurationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "shared_pics")
data class SharedPicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val caption: String = "",
    val addedBy: String = "me",
    val target: String = "partner_home",
    val status: String = "local_ready",
    val selectedForWidget: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "moments")
data class MomentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val emoji: String = "💕",
    val timestamp: Long = System.currentTimeMillis(),
    val isMilestone: Boolean = false,
    val imagePathsJson: String = "[]"
)

@Entity(tableName = "couple_stats")
data class CoupleStatsEntity(
    @PrimaryKey val id: Int = 1,
    val visitedCities: Int = 7,
    val visitedCountries: Int = 3
)

// --- DOMAIN MODELS & PACK DEFINITIONS ---

data class Category(
    val id: String,
    val name: String,
    val emoji: String,
    val tagColorHex: Long
)

data class Topic(
    val id: String,
    val name: String,
    val emoji: String
)

data class Question(
    val q: String,
    val options: List<String> = emptyList(),
    val defaultMine: String? = null
)

data class QuestionPack(
    val id: String,
    val title: String,
    val tags: List<String>,
    val cat: String,
    val topic: String,
    val type: String, // "quiz", "tot", "disc"
    val questions: List<Question> = emptyList(),
    val pairs: List<Pair<String, String>> = emptyList(),
    val emoji: String = ""
)

object HarmonyPacksData {

    private val DEFAULT_CATEGORIES = listOf(
        Category("mischung", "Eure Mischung", "👶", 0xFFFF70A6),
        Category("unterbewusstsein", "Tauche ins Unterbewusstsein", "🧙‍♂️", 0xFF9D4EDD),
        Category("wer", "Wer würde eher?", "🤔", 0xFFFF2E63),
        Category("zeich", "Zeichnen", "🎨", 0xFF9E59BD),
        Category("tot", "Das oder das?", "⚖️", 0xFFFFC46B),
        Category("zust", "Zustimmen oder Ablehnen", "👍", 0xFF9DB2FF),
        Category("nie", "Ich habe noch nie", "🙈", 0xFFFF6B8F),
        Category("lieber", "Was magst du lieber?", "💫", 0xFFC89BE0),
        Category("foto", "Antwort mit einem Foto", "📷", 0xFF7BD8CB),
        Category("tief", "Tiefe Gespräche", "🌊", 0xFF9DB2FF),
        Category("reden", "Reden vor ...", "🗣️", 0xFFFFC46B)
    )

    private val dynamicCategories = mutableListOf<Category>()
    private val dynamicPacks = mutableListOf<QuestionPack>()

    fun setDynamicCategories(cats: List<Category>) {
        dynamicCategories.clear()
        dynamicCategories.addAll(cats)
    }

    fun setDynamicPacks(packs: List<QuestionPack>) {
        dynamicPacks.clear()
        dynamicPacks.addAll(packs)
    }

    val CATEGORIES: List<Category>
        get() {
            val result = DEFAULT_CATEGORIES.toMutableList()
            for (dc in dynamicCategories) {
                val idx = result.indexOfFirst { it.id == dc.id }
                if (idx >= 0) {
                    result[idx] = dc
                } else {
                    result.add(dc)
                }
            }
            return result
        }

    val TOPICS = listOf(
        Topic("aufwaermen", "Aufwärmen", "☀️"),
        Topic("beziehung", "Beziehung", "💗"),
        Topic("sex", "Sex & Liebe", "🔥"),
        Topic("moral", "Moralische Werte", "⚖️"),
        Topic("geld", "Geld & Finanzen", "💰"),
        Topic("kennen", "Einander kennenlernen", "🫶"),
        Topic("reisen", "Reisen", "✈️"),
        Topic("familie", "Familie", "👨‍👩‍👧"),
        Topic("hobbys", "Hobbys", "🎯"),
        Topic("filme_serien", "Filme/Serien", "📺"),
        Topic("essen", "Essen & Genuss", "🍽️")
    )

    val DEFAULT_PACKS = listOf(
        QuestionPack(
            id = "entweder_oder_panda",
            title = "Entweder oder",
            tags = listOf("dasoderdas", "fürpaare"),
            cat = "tot",
            topic = "aufwaermen",
            type = "tot",
            emoji = "🐼",
            pairs = listOf(
                "Frühstück im Bett 🥐" to "Mitternachtssnack 🌙",
                "Strandurlaub 🏖️" to "Bergabenteuer 🏔️",
                "Netflix-Marathon 📺" to "Party bis 4 Uhr 🎉",
                "Pizza 🍕" to "Sushi 🍣",
                "Hund 🐶" to "Katze 🐱",
                "Sommer ☀️" to "Winter ❄️",
                "Kaffee ☕" to "Tee 🍵",
                "Frühaufsteher 🌅" to "Nachteule 🦉",
                "Kino 🎬" to "Couch & Decke 🛋️",
                "Süß 🍫" to "Salzig 🍟",
                "Roadtrip 🚗" to "Flugreise ✈️",
                "Karaoke 🎤" to "Tanzen 💃",
                "Camping ⛺" to "5-Sterne-Hotel 🏨",
                "Kuscheln 🤗" to "Kitzeln 😆",
                "Stadt 🏙️" to "Land 🌾",
                "Textnachricht 💬" to "Sprachnachricht 🎙️",
                "Geburtstag groß feiern 🎂" to "Nur wir zwei 🥂",
                "Kochen 👩‍🍳" to "Bestellen 🛵",
                "Meer 🌊" to "Pool 🏊",
                "Achterbahn 🎢" to "Riesenrad 🎡",
                "Spontan ⚡" to "Durchgeplant 📋",
                "Duschen 🚿" to "Baden 🛁",
                "Buch 📖" to "Podcast 🎧",
                "Frühstücksdate 🍳" to "Dinnerdate 🍷",
                "Eis 🍦" to "Kuchen 🍰",
                "Regen am Fenster 🌧️" to "Sonne auf der Haut 🌞",
                "Brettspiel 🎲" to "Videospiel 🎮",
                "Blumen 💐" to "Schokolade 🍫",
                "Tattoo 🖋️" to "Piercing 💎",
                "Zug 🚆" to "Auto 🚗",
                "Silvester draußen 🎆" to "Silvester drinnen 🛋️",
                "Vergangenheit besuchen ⏪" to "Zukunft sehen ⏩",
                "Fliegen können 🕊️" to "Gedanken lesen 🧠",
                "Reich & gestresst 💰" to "Entspannt & genug 😌",
                "Immer Sommer ☀️" to "Alle 4 Jahreszeiten 🍂",
                "Handy weg für 1 Woche 📵" to "Kein Zucker für 1 Monat 🚫🍬",
                "Konzert 🎸" to "Festival 🎪",
                "Picknick 🧺" to "Rooftop-Bar 🍸",
                "Neue Stadt jede Woche 🧳" to "Für immer Traumhaus 🏡",
                "Erster Kuss nochmal 💋" to "Erstes Date nochmal 🌹",
                "Händchen halten 🤝" to "Arm um die Schulter 💪",
                "Lange Sprachnachricht 🎙️" to "Kurzer Anruf 📞",
                "Gemeinsam duschen 🚿" to "Gemeinsam kochen 🍳",
                "Frühstück ans Bett bringen 🥐" to "Frühstück ans Bett bekommen 😌",
                "Peinlich tanzen in der Öffentlichkeit 💃" to "Peinlich singen in der Öffentlichkeit 🎤",
                "Partner-Look tragen 👕👕" to "Niemals Partner-Look 🙅",
                "Streit sofort klären ⚡" to "Erstmal eine Nacht schlafen 😴",
                "Immer die Wahrheit hören 💯" to "Kleine Notlügen erlaubt 🤫",
                "Gedanken des Partners lesen 🧠" to "Eigene Gedanken verbergen 🔒",
                "Zusammen einschlafen 🌙" to "Zusammen aufwachen ☀️",
                "Doppeldate 👥" to "Nur wir zwei 💑",
                "Überraschungsparty 🎉" to "Ruhiges Dinner 🕯️",
                "Horrorfilm 😱" to "Liebeskomödie 🥰",
                "Wochenende ohne Pläne 🛋️" to "Wochenende voll verplant 📅",
                "Geschenk selbst gemacht 🎨" to "Geschenk gekauft 🎁",
                "Immer 10 Min zu früh ⏰" to "Immer 10 Min zu spät 🏃",
                "Fenster auf beim Schlafen 🌬️" to "Fenster zu 🔒",
                "Große Hochzeit 💒" to "Heimlich heiraten ✈️",
                "Serie zusammen suchten 📺" to "Jeder sein eigenes Ding 🎧",
                "Warme Decke teilen 🛏️" to "Jeder seine eigene Decke 😤",
                "Beim Essen teilen 🍴" to "Finger weg von meinem Teller! 🚫",
                "Karaoke-Duett 🎶" to "Tanz-Battle 🕺",
                "Altes Foto-Album 📔" to "Neues Fotoshooting 📸",
                "Geheimen Handshake 🤜" to "Geheimes Codewort 🗝️",
                "Zelten im Garten ⛺" to "Matratze im Wohnzimmer 🛋️",
                "Ohne Musik leben 🔇" to "Ohne Serien leben 📵",
                "Immer Winter-Kuscheln ❄️" to "Immer Sommer-Abende 🌅",
                "Zusammen Sport 🏋️" to "Zusammen faulenzen 🦥",
                "Liebesbrief bekommen 💌" to "Playlist bekommen 🎧",
                "Perfektes erstes Date nochmal 🌹" to "Sneak Peek auf uns in 20 Jahren 🔮"
            )
        ),

        // ★ Zuhause & Alltag
        QuestionPack(
            id = "zuhause",
            title = "Zuhause & Alltag",
            tags = listOf("unterhaltung"),
            cat = "tief",
            topic = "kennen",
            type = "quiz",
            questions = listOf(
                Question("Was gefällt dir an deinem Zuhause am besten?", listOf("Die Gemütlichkeit und Ruhe", "Dass alles meinen Stil hat", "Die Menschen, die darin wohnen", "Der Blick nach draußen")),
                Question("Welcher Raum sagt am meisten über dich aus?", listOf("Küche", "Schlafzimmer", "Wohnzimmer", "Mein Arbeitsplatz")),
                Question("Was würdest du sofort ändern, wenn Geld keine Rolle spielt?", listOf("Größere Küche", "Ein Balkon oder Garten", "Bessere Lage", "Nichts — es passt so")),
                Question("Wie sieht dein perfekter Sonntag zuhause aus?", listOf("Ausschlafen und nichts tun", "Kochen und Freunde einladen", "Serienmarathon auf dem Sofa", "Aufräumen und Projekte angehen")),
                Question("Was ist dir wichtiger in einer Wohnung?", listOf("Viel Platz", "Perfekte Lage", "Helligkeit", "Gute Aufteilung")),
                Question("Stört dich Unordnung?", listOf("Sehr", "Manchmal", "Kaum", "Gar nicht")),
                Question("Wofür gibst du zuhause am ehesten Geld aus?", listOf("Möbel", "Pflanzen", "Technik", "Deko")),
                Question("Was ist das erste, das du tust, wenn du heimkommst?", listOf("Schuhe aus", "Hände waschen", "Couch", "Kühlschrank öffnen"))
            )
        ),

        // ★ Der perfekte Heiratsantrag
        QuestionPack(
            id = "antrag",
            title = "Der perfekte Heiratsantrag",
            tags = listOf("hochzeit", "unterhaltung"),
            cat = "tief",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(
                Question("Welche Umgebung würdest du dir für einen Antrag wünschen?", listOf("Zu Hause, gemütlich und privat", "Draußen mit der Natur als Kulisse", "Schickes Restaurant oder Hotel")),
                Question("Magst du einen öffentlichen oder privaten Antrag lieber?", listOf("Nur wir beide", "Mit Familie und engen Freunden", "An einem öffentlichen Ort mit vielen Zuschauern"))
            )
        ),

        // ★ Kinderkriegen Diskussionspaket
        QuestionPack(
            id = "kinder",
            title = "Diskutiere vor dem Kinderkriegen",
            tags = listOf("kinder", "unterhaltung"),
            cat = "reden",
            topic = "familie",
            type = "quiz",
            questions = listOf(
                Question("Sind wir in der Lage, alle Kosten für ein Kind/mehrere Kinder zu decken? 💰", listOf("Ja, absolut", "Müssen sparen", "Eher knapp"), defaultMine = "Möglicherweise müssen wir Einsparungen vornehmen"),
                Question("Werden wir genug Zeit für das Kind / die Kinder haben? ⏳", listOf("Ja, genug", "Müssen es planen", "Wird stressig"), defaultMine = "Ja, wir werden sicherstellen, dass die Zeit mit der Familie Vorrang hat."),
                Question("Wie werden wir Zeit für unsere Beziehung finden, wenn das Baby da ist?", listOf("Feste Date-Nights", "Zeit, wenn das Baby schläft", "Spontan"), defaultMine = "Regelmäßige Rendezvous oder gemeinsame Zeit einplanen"),
                Question("Wie möchtest du, dass unsere Zukunft aussieht?", listOf("Große Familie", "Zu zweit reisen", "Karriere & Erfolg"), defaultMine = "Gemeinsam die Welt bereisen, neue Kulturen und Küchen erkunden"),
                Question("Welche Eigenschaft sollte unser Kind unbedingt von uns mitbekommen?", listOf("Humor", "Mut", "Mitgefühl", "Neugier")),
                Question("Was sollten wir als Eltern niemals aus den Augen verlieren?", listOf("Zeit füreinander", "Geduld", "Unsere eigenen Träume", "Leichtigkeit")),
                Question("Wie wichtig sind Großeltern bei der Erziehung?", listOf("Sehr wichtig", "Gelegentlich hilfreich", "Wir machen das alleine", "Eher unwichtig")),
                Question("Was ist der wichtigste Wert, den wir vermitteln wollen?", listOf("Ehrlichkeit", "Toleranz", "Respekt", "Selbstständigkeit")),
                Question("Werden wir eher strengere oder lockerere Eltern?", listOf("Eher streng", "Eher locker", "Eine gute Mischung", "Kommt auf die Situation an")),
                Question("Wie regeln wir die Kinderbetreuung im ersten Jahr?", listOf("Viel selbst machen", "Familie einbinden", "Tagesmutter/Kita", "Flexibel bleiben"))
            )
        ),

        QuestionPack(
            id = "haustier",
            title = "Vor der Anschaffung eines Haustiers besprechen",
            tags = listOf("reden", "unterhaltung"),
            cat = "reden",
            topic = "familie",
            type = "quiz",
            questions = listOf(
                Question("Wer übernimmt die tägliche Versorgung?", listOf("{user}", "{partner}", "Beide", "Niemand")),
                Question("Was passiert mit dem Tier, wenn wir verreisen?", listOf("Kommt mit", "Familie/Freunde", "Tierpension")),
                Question("Welches Budget planen wir für Futter und Tierarzt ein?", listOf("Unter 50€/Monat", "50-100€/Monat", "Über 100€/Monat")),
                Question("Passt ein Tier überhaupt zu unserem Alltag?", listOf("Ja, perfekt", "Mit Kompromissen", "Eher schwierig")),
                Question("Was für ein Haustier wollen wir am liebsten?", listOf("Hund", "Katze", "Kleintiere", "Exotisch")),
                Question("Was ist unsere größte Sorge bei einem Haustier?", listOf("Tierarztkosten", "Zeitmangel", "Schmutz in der Wohnung", "Die Verantwortung")),
                Question("Soll das Haustier ins Schlafzimmer dürfen?", listOf("Auf jeden Fall", "Nein", "Manchmal", "Nur wenn es klein ist")),
                Question("Sind wir bereit, bei Wind und Wetter Gassi zu gehen?", listOf("Klar", "Eher ungern", "Abwechselnd", "Lieber ein Stubentiger"))
            )
        ),

        QuestionPack(
            id = "reisevor",
            title = "Vor der gemeinsamen Reise besprechen",
            tags = listOf("reden", "unterhaltung"),
            cat = "reden",
            topic = "reisen",
            type = "quiz",
            questions = listOf(
                Question("Wie viel wollen wir insgesamt ausgeben?", listOf("Unter 1000", "1000 bis 3000", "3000 bis 5000", "Open End")),
                Question("Lieber durchgeplant oder spontan?", listOf("Durchgeplant", "Spontan", "Beides")),
                Question("Wie viel Zeit wollen wir getrennt verbringen?", listOf("Viel getrennt", "Ein paar Stunden", "Am liebsten alles zusammen")),
                Question("Was ist für jeden von uns das absolute Highlight?", listOf("Kultur & Sehenswürdigkeiten", "Entspannung", "Abenteuer", "Gutes Essen")),
                Question(
                    "Wie sieht deine Traumreise aus?",
                    listOf(
                        "Strand & Sonne – Relaxen, Meer & Cocktails",
                        "Städtetrip & Kultur – Museen, Architektur & Flanieren",
                        "Abenteuer & Trekking – Berge, Wandern & Grenzen testen",
                        "Safari & Wildtiere – Naturbeobachtungen, Abenteuer & Wildnis",
                        "Kultur & Geschichte – Vergangenheit entdecken, Ruinen & Wissen",
                        "Kreuzfahrt – Häfen entdecken, Meerblick & Entspannung pur",
                        "Roadtrip & Freiheit – Vanlife, Entdecken & Unabhängigkeit",
                        "Wellness & Entspannung – Spa, Erholung & Seele baumeln lassen",
                        "Inselhüpfen & Bootstrip – Viele Orte, Boot fahren & Schnorcheln",
                        "Camping & Natur – Natur erleben, Lagerfeuer & Sterne",
                        "Gourmet & Foodie – Kulinarische Entdeckungen, Restaurants & Genuss",
                        "Festival & Musik – Energie, Konzerte & Feiern"
                    )
                ),
                Question("Wie reisen wir am liebsten an?", listOf("Flugzeug", "Auto", "Zug", "Schiff")),
                Question("Sind wir bereit für ein Airbnb oder lieber Hotel?", listOf("Hotel", "Airbnb", "Camping", "Hostel")),
                Question("Wie gehen wir mit Reise-Stress um?", listOf("Wir bleiben ruhig", "Wir streiten kurz", "Einer muss die Führung übernehmen", "Kopfhörer rein")),
                Question("Was darf auf keinen Fall im Koffer fehlen?", listOf("Gutes Buch", "Kamera", "Snacks", "Reiseapotheke"))
            )
        ),

        QuestionPack(
            id = "hauskauf",
            title = "Vor dem Kauf eines Hauses besprechen",
            tags = listOf("reden", "unterhaltung"),
            cat = "reden",
            topic = "geld",
            type = "quiz",
            questions = listOf(
                Question("Wie viel Kredit ist für uns realistisch tragbar?", listOf("Unter 1000 im Monat", "1000 - 1500 im Monat", "1500 - 2000 im Monat", "Über 2000 im Monat")),
                Question("Stadt oder Land — was ist uns wichtiger?", listOf("Stadt", "Land", "Vorort")),
                Question("Wie lange wollen wir dort mindestens bleiben?", listOf("1-5 Jahre", "5-10 Jahre", "Für immer", "Weiß ich noch nicht")),
                Question("Wer kümmert sich um Renovierung und Instandhaltung?", listOf("{user}", "{partner}", "Beide", "Niemand")),
                Question("Ist uns ein großer Garten wichtig?", listOf("Sehr wichtig", "Nice to have", "Eher unwichtig", "Gar keinen Garten bitte")),
                Question("Sind wir bereit, selbst Hand anzulegen beim Bauen?", listOf("Klar, vieles", "Nur Kleinigkeiten", "Nein, alles machen lassen", "Mal schauen")),
                Question("Wie viel Eigenkapital können wir aufbringen?", listOf("Wenig", "Mittel", "Viel", "Wird gerade gespart")),
                Question("Gibt es ein Zimmer, auf das wir nicht verzichten können?", listOf("Büro", "Gästezimmer", "Hobbyraum", "Ankleidezimmer"))
            )
        ),

        // ★ Das oder Das - Reiseziele
        QuestionPack(
            id = "reiseziele",
            title = "Reiseziele",
            tags = listOf("dasoderdas"),
            cat = "tot",
            topic = "reisen",
            type = "tot",
            pairs = listOf(
                "Paris, Frankreich" to "Rom, Italien",
                "Bali, Indonesien" to "Santorini, Griechenland",
                "London, England" to "New York, USA",
                "Malediven" to "Seychellen",
                "Tokyo, Japan" to "Dubai, VAE",
                "Venedig, Italien" to "Amsterdam, Niederlande",
                "Lappland, Finnland" to "Island"
            )
        ),

        QuestionPack(
            id = "traumhaus",
            title = "Unser Traumhaus",
            tags = listOf("dasoderdas"),
            cat = "tot",
            topic = "geld",
            type = "tot",
            pairs = listOf(
                "Altbau mit Charme" to "Neubau mit Smart Home",
                "Offene Wohnküche" to "Separate Küche",
                "Prasselnder Kamin" to "Fußbodenheizung",
                "Großer Garten" to "Sonnige Dachterrasse",
                "Stadtvilla" to "Landhaus",
                "Glasfassade" to "Natursteinfassade",
                "Penthouse mit Ausblick" to "Haus am See",
                "Minimalistisches Interieur" to "Landhausstil",
                "Bibliothek" to "Heimkino",
                "Innenpool" to "Wellnessbad",
                "Große Fensterfront" to "Privater Innenhof",
                "Tiny House" to "Mehrgenerationenhaus"
            )
        ),

        QuestionPack(
            id = "aussen",
            title = "Traumhaus Außenbereich",
            tags = listOf("dasoderdas"),
            cat = "tot",
            topic = "geld",
            type = "tot",
            pairs = listOf(
                "Großer Außenpool" to "Outdoor-Whirlpool",
                "Moderne Grillstation" to "Gemütliche Feuerstelle",
                "Eigenes Gemüsebeet" to "Bunte Blumenwiese",
                "Entspannte Hängematte" to "Stilvolles Outdoor-Sofa",
                "Infinity-Pool" to "Naturteich",
                "Outdoor-Küche" to "Pizzaofen",
                "Pergola mit Lounge" to "Wintergarten",
                "Kräuterbeet" to "Obstgarten",
                "Dachgarten mit Lounge" to "Mediterraner Innenhof",
                "Feuerstelle" to "Außenkamin",
                "Spielbereich für Kinder" to "Sportplatz",
                "Gewächshaus" to "Saunahaus"
            )
        ),

        QuestionPack(
            id = "aktivitaeten",
            title = "Aktivitäten",
            tags = listOf("dasoderdas"),
            cat = "tot",
            topic = "hobbys",
            type = "tot",
            pairs = listOf(
                "Wandern" to "Strandtag",
                "Konzert" to "Kino",
                "Kochkurs" to "Restaurant",
                "Museum" to "Freizeitpark"
            )
        ),

        QuestionPack(
            id = "essen",
            title = "Essensvorlieben",
            tags = listOf("dasoderdas"),
            cat = "tot",
            topic = "kennen",
            type = "tot",
            pairs = listOf(
                "Pizza" to "Pasta",
                "Sushi" to "Burger",
                "Süß" to "Herzhaft",
                "Selbst kochen" to "Bestellen"
            )
        ),

        QuestionPack(
            id = "ringe",
            title = "Verlobungsringe",
            tags = listOf("hochzeit", "dasoderdas"),
            cat = "tot",
            topic = "beziehung",
            type = "tot",
            pairs = listOf(
                "Klassisch Solitär" to "Vintage verspielt",
                "Gelbgold" to "Weißgold",
                "Großer Stein" to "Filigran & schlicht",
                "Diamant" to "Farbedelstein",
                "Platin" to "Roségold",
                "Drei-Stein-Ring" to "Moderner Solitär",
                "Ovaler Diamant" to "Runder Diamant",
                "Schmal & zart" to "Markant & breit",
                "Moissanit" to "Saphir",
                "Vintage Art déco" to "Modern geometrisch",
                "Gravur innen" to "Diamanten im Band",
                "Ohne Stein" to "Statement-Ring"
            )
        ),

        QuestionPack(
            id = "straeusse",
            title = "Hochzeitssträuße",
            tags = listOf("hochzeit", "dasoderdas"),
            cat = "tot",
            topic = "beziehung",
            type = "tot",
            pairs = listOf(
                "Weiße Rosen" to "Pfingstrosen",
                "Wildblumen" to "Klassisch gebunden",
                "Groß & üppig" to "Klein & zart",
                "Pastell" to "Kräftige Farben"
            )
        ),

        QuestionPack(
            id = "traumhochzeit",
            title = "Traumhochzeit",
            tags = listOf("hochzeit", "dasoderdas"),
            cat = "tot",
            topic = "beziehung",
            type = "tot",
            pairs = listOf(
                "Große Feier" to "Kleine Runde",
                "Am Strand" to "In den Bergen",
                "Kirchlich" to "Standesamt & Party",
                "Sommerhochzeit" to "Winterhochzeit"
            )
        ),

        QuestionPack(
            id = "gelegenheit",
            title = "Fragen für jede Gelegenheit",
            tags = listOf("unterhaltung"),
            cat = "lieber",
            topic = "aufwaermen",
            type = "quiz",
            questions = listOf(
                Question("Was magst du lieber: früh aufstehen oder lange wach bleiben?", listOf("Früh aufstehen", "Lange wach bleiben", "Kommt auf den Tag an")),
                Question("Lieber ein ruhiger Abend zu zweit oder unter Leuten?", listOf("Ruhig zu zweit", "Unter Leuten", "Gemischt")),
                Question("Was entspannt dich mehr?", listOf("Musik", "Spazieren", "Serie schauen", "Gar nichts tun")),
                Question("Wobei lachst du am meisten?", listOf("Bei Insider-Witzen", "Bei Memes", "Wenn ich müde bin", "Über mich selbst")),
                Question("Bist du eher Team Kaffee oder Tee?", listOf("Kaffee", "Tee", "Nichts von beidem", "Hauptsache viel Zucker")),
                Question("Was würdest du wählen: Immer Sommer oder immer Winter?", listOf("Immer Sommer", "Immer Winter", "Mir egal", "Ich brauche den Wechsel")),
                Question("Lieber süß oder salzig?", listOf("Süß", "Salzig", "Am besten beides zusammen", "Kommt auf die Tageszeit an")),
                Question("Planst du gerne voraus oder entscheidest du spontan?", listOf("Alles durchplanen", "Spontan bleiben", "Grob planen, dann mal sehen")),
                Question("Was motiviert dich morgens am meisten?", listOf("Gutes Frühstück", "Ein konkretes Ziel", "Die Dusche", "Gar nichts, ich bin ein Morgenmuffel")),
                Question("Wie verbringst du eine lange Zugfahrt am liebsten?", listOf("Lesen", "Schlafen", "Aus dem Fenster schauen", "Podcast hören"))
            )
        ),

        QuestionPack(
            id = "schnapp",
            title = "Schnappschüsse aus unserer Liebesgeschichte",
            tags = listOf("unterhaltung"),
            cat = "foto",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(
                Question("Was war dein schönster Moment mit mir bisher?", listOf("Unser erstes Treffen", "Ein ganz normaler Alltagstag", "Eine gemeinsame Reise", "Ein schwerer Moment, den wir geschafft haben")),
                Question("Welches gemeinsame Foto ist dein Lieblingsfoto?", listOf("Das erste Selfie", "Ein Urlaubsfoto", "Ein zufälliger Schnappschuss", "Eins, das nur wir kennen"))
            )
        ),

        QuestionPack(
            id = "aufwaermen1",
            title = "Aufwärmen: Einander kennenlernen",
            tags = listOf("unterhaltung"),
            cat = "tief",
            topic = "aufwaermen",
            type = "quiz",
            questions = listOf(
                Question("Was würdest du an einem gemeinsamen Tag am liebsten machen?", listOf("Ausschlafen und faulenzen", "Etwas Neues ausprobieren", "Rausgehen in die Natur", "Freunde treffen")),
                Question("Wie fühlst du dich am meisten geliebt?", listOf("Durch Worte", "Durch Zeit zu zweit", "Durch Berührung", "Durch kleine Gesten")),
                Question("Worauf freust du dich bei uns am meisten?", listOf("Unsere nächste Reise", "Zusammenziehen", "Einfach mehr Alltag", "Alles, was noch kommt")),
                Question("Was war dein erster Eindruck von mir?", listOf("Sympathisch", "Interessant", "Attraktiv", "Ganz anders als jetzt")),
                Question("Wie zeigst du am liebsten Zuneigung?", listOf("Kleine Geschenke", "Körperliche Nähe", "Hilfsbereitschaft", "Komplimente")),
                Question("Welche Eigenschaft schätzt du an Menschen am meisten?", listOf("Ehrlichkeit", "Humor", "Loyalität", "Intelligenz")),
                Question("Was ist dein liebstes Thema für tiefe Gespräche?", listOf("Zukunft", "Philosophie", "Unsere Gefühle", "Gott und die Welt")),
                Question("Wie viel Freiraum brauchst du in einer Beziehung?", listOf("Viel", "Ein bisschen", "Kaum", "Wir können alles zusammen machen")),
                Question("Was ist für dich das schönste Gefühl?", listOf("Verstanden werden", "Geborgenheit", "Freude", "Leidenschaft")),
                Question("Welche kleine Macke von mir findest du süß?", listOf("Dein Lachen", "Wie du dich aufregst", "Deine Schusseligkeit", "Ich liebe alle deine Macken"))
            )
        ),

        QuestionPack(
            id = "wergehteher",
            title = "Wer würde eher?",
            tags = listOf("unterhaltung"),
            cat = "wer",
            topic = "aufwaermen",
            type = "quiz",
            questions = listOf(
                Question("Wer würde eher zu spät kommen?", listOf("Ich", "Mein Partner", "Beide gleich", "Keiner von uns")),
                Question("Wer würde eher bei einem Streit als Erstes einlenken?", listOf("Ich", "Mein Partner", "Kommt drauf an", "Wir treffen uns in der Mitte")),
                Question("Wer würde eher spontan eine Reise buchen?", listOf("Ich", "Mein Partner", "Beide zusammen", "Niemand ohne Plan")),
                Question("Wer vergisst eher einen Jahrestag?", listOf("Ich", "Mein Partner", "Keiner", "Beide, aber wir tun so als ob nicht")),
                Question("Wer würde eher im Lotto gewinnen und alles ausgeben?", listOf("Ich", "Mein Partner", "Beide", "Wir würden es klug anlegen")),
                Question("Wer würde eher eine Woche ohne Handy überleben?", listOf("Ich", "Mein Partner", "Beide", "Niemand von uns")),
                Question("Wer flucht öfter beim Autofahren?", listOf("Ich", "Mein Partner", "Beide", "Wir sind total entspannt")),
                Question("Wer würde eher fremde Leute auf der Straße ansprechen?", listOf("Ich", "Mein Partner", "Beide", "Auf keinen Fall")),
                Question("Wer braucht morgens länger im Bad?", listOf("Ich", "Mein Partner", "Beide gleich lang", "Wir sind beide sehr schnell")),
                Question("Wer würde eher einen Fallschirmsprung machen?", listOf("Ich", "Mein Partner", "Wir beide", "Keiner, zu gefährlich")),
                Question("Wer isst eher den letzten Keks?", listOf("Ich", "Mein Partner", "Wir teilen", "Wir lassen ihn ewig liegen")),
                Question("Wer lacht eher in einer völlig unpassenden Situation?", listOf("Ich", "Mein Partner", "Wir beide", "Wir haben uns gut im Griff"))
            )
        ),

        QuestionPack(
            id = "nienie",
            title = "Ich habe noch nie",
            tags = listOf("unterhaltung"),
            cat = "nie",
            topic = "kennen",
            type = "quiz",
            questions = listOf(
                Question("Ich habe noch nie … ein Date abgesagt, um zuhause zu bleiben.", listOf("Stimmt, noch nie", "Doch, schon mal", "Öfter als ich zugebe")),
                Question("Ich habe noch nie … heimlich das Handy meines Partners angeschaut.", listOf("Stimmt, noch nie", "Einmal", "Ich würde es nie tun")),
                Question("Ich habe noch nie … eine Nachricht 10x umformuliert.", listOf("Stimmt, noch nie", "Ständig", "Nur bei wichtigen Themen")),
                Question("Ich habe noch nie … den Namen des Partners in einem peinlichen Moment verwechselt.", listOf("Stimmt, noch nie", "Leider ja", "Fast")),
                Question("Ich habe noch nie … so getan, als würde ich schlafen, um nicht reden zu müssen.", listOf("Stimmt, noch nie", "Einmal", "Das ist mein Standardtrick")),
                Question("Ich habe noch nie … etwas geschenkt bekommen und so getan, als würde es mir gefallen.", listOf("Stimmt, noch nie", "Schon oft", "Ich bin immer ehrlich")),
                Question("Ich habe noch nie … einen Jahrestag völlig vergessen.", listOf("Stimmt, noch nie", "Einmal", "Ich bin schrecklich mit Daten")),
                Question("Ich habe noch nie … eine Serie weitergeschaut und behauptet, ich hätte gewartet.", listOf("Stimmt, noch nie", "Schuldig", "Ich habe sogar gespoilert")),
                Question("Ich habe noch nie … einen peinlichen Kosenamen in der Öffentlichkeit gerufen.", listOf("Stimmt, noch nie", "Ja, aus Versehen", "Mache ich absichtlich")),
                Question("Ich habe noch nie … einen Ex-Partner auf Social Media gestalkt.", listOf("Stimmt, noch nie", "Ein bisschen", "Wir sind befreundet")),
                Question("Ich habe noch nie … beim ersten Date gelogen, um besser dazustehen.", listOf("Stimmt, noch nie", "Eine kleine Notlüge", "Total geflunkert")),
                Question("Ich habe noch nie … heimlich die Schokolade des Partners gegessen.", listOf("Stimmt, noch nie", "Immer", "Wir teilen alles"))
            )
        ),

        QuestionPack(
            id = "tiefe",
            title = "Tiefe Gespräche",
            tags = listOf("unterhaltung"),
            cat = "tief",
            topic = "moral",
            type = "quiz",
            questions = listOf(
                Question("Was bedeutet Vertrauen für dich konkret?", listOf("Dass ich alles erzählen kann", "Dass ich mich nicht sorgen muss", "Dass Zusagen gehalten werden", "Alles davon")),
                Question("Was ist für dich ein absoluter Dealbreaker?", listOf("Lügen", "Respektlosigkeit", "Gleichgültigkeit", "Untreue")),
                Question("Wann fühlst du dich mir am nächsten?", listOf("Beim Reden", "In Stille nebeneinander", "Wenn wir zusammen lachen", "Wenn es schwierig ist")),
                Question("Wovor hast du in unserer Beziehung am meisten Angst?", listOf("Uns auseinanderzuleben", "Missverständnisse", "Die Distanz", "Vor nichts")),
                Question("Was glaubst du, ist unser größtes gemeinsames Potenzial?", listOf("Wir ergänzen uns gut", "Unsere offene Kommunikation", "Unsere gemeinsame Vision", "Unsere Leidenschaft")),
                Question("Gibt es etwas, das du mir schon immer sagen wolltest, aber dich nie getraut hast?", listOf("Ja, einiges", "Nur Kleinigkeiten", "Nein, ich bin immer offen", "Weiß nicht")),
                Question("Wie gehst du innerlich mit Kritik von mir um?", listOf("Ich nehme sie mir sehr zu Herzen", "Ich versuche sachlich zu bleiben", "Manchmal blocke ich ab", "Ich bin dankbar dafür")),
                Question("Was war der Moment, in dem du wusstest, dass du mich liebst?", listOf("Das war ein schleichender Prozess", "Ein ganz bestimmter Moment", "Weiß ich gar nicht mehr genau", "Ich wusste es sofort")),
                Question("Wie sehr darf ich dich bei Entscheidungen beeinflussen?", listOf("Sehr stark", "Ein bisschen", "Nur als Ratgeber", "Gar nicht")),
                Question("Was ist der Sinn unserer Beziehung für dich?", listOf("Zusammen wachsen", "Spaß haben und leben", "Eine Familie gründen", "Einfach nicht allein sein"))
            )
        ),

        QuestionPack(
            id = "geldpack",
            title = "Geld & Finanzen",
            tags = listOf("unterhaltung"),
            cat = "reden",
            topic = "geld",
            type = "quiz",
            questions = listOf(
                Question("Führen wir getrennte oder gemeinsame Konten?", listOf("Getrennte Konten", "Gemeinsame Konten", "Sowohl als auch")),
                Question("Wie gehen wir mit unterschiedlichen Einkommen um?", listOf("Jeder zahlt 50%", "Prozentual nach Einkommen", "Einer zahlt alles")),
                Question("Wofür sparen wir gemeinsam?", listOf("Urlaub", "Haus/Wohnung", "Auto", "Für die Zukunft")),
                Question("Ab welchem Betrag sprechen wir vor einer Anschaffung?", listOf("Ab 50€", "Ab 100€", "Ab 500€", "Erst bei sehr großen Summen")),
                Question("Wer hat den besseren Überblick über die Finanzen?", listOf("{user}", "{partner}", "Wir beide", "Niemand")),
                Question("Wie wichtig ist dir Reichtum?", listOf("Sehr wichtig", "Wichtig für Sicherheit", "Weniger wichtig", "Gar nicht wichtig")),
                Question("Bist du eher Sparer oder Ausgeber?", listOf("Sparer", "Ausgeber", "Kommt auf die Phase an", "Gute Balance")),
                Question("Würdest du einen Kredit aufnehmen für eine Reise?", listOf("Ja, auf jeden Fall", "Nein, niemals", "Nur im Notfall", "Kommt auf die Zinsen an")),
                Question("Wie stehst du zu Investitionen (Aktien, etc.)?", listOf("Mache ich gerne", "Ist mir zu riskant", "Möchte ich noch lernen", "Kein Interesse")),
                Question("Ist Geld oft ein Streitthema bei uns?", listOf("Ja, leider", "Selten", "Nie", "Wir reden kaum drüber"))
            )
        ),

        QuestionPack(
            id = "naehe",
            title = "Nähe & Intimität",
            tags = listOf("unterhaltung"),
            cat = "tief",
            topic = "sex",
            type = "quiz",
            questions = listOf(
                Question("Wann fühlst du dich mir körperlich am nächsten?", listOf("Beim Einschlafen nebeneinander", "Wenn wir uns lange umarmen", "Bei einer spontanen Berührung", "Wenn wir zusammen lachen")),
                Question("Was fehlt dir über die Distanz am meisten?", listOf("Körperliche Nähe", "Einfach nebeneinander sein", "Gemeinsame Nächte", "Alltägliche Berührungen")),
                Question("Wie leicht fällt es dir, über Wünsche zu sprechen?", listOf("Sehr leicht", "Geht so", "Eher schwer", "Ich übe noch")),
                Question("Was macht einen Moment für dich romantisch?", listOf("Aufmerksamkeit", "Überraschung", "Vertrautheit", "Dass wir ungestört sind")),
                Question("Wie wichtig ist dir Kuscheln ohne sexuellen Hintergrund?", listOf("Sehr wichtig", "Manchmal schön", "Eher unwichtig", "Gar nicht meins")),
                Question("Was ist deine liebste Art der Berührung im Alltag?", listOf("Kuss auf die Stirn", "Händchen halten", "Umarmung von hinten", "Kurze Streichler")),
                Question("Stört es dich, wenn wir beim Schlafen keinen Körperkontakt haben?", listOf("Ja, sehr", "Ein bisschen", "Nein, ich brauche Platz", "Wir wechseln uns ab")),
                Question("Zeigst du gerne Zuneigung in der Öffentlichkeit?", listOf("Ja, absolut", "Nur dezent", "Nein, ungern", "Kommt auf die Umgebung an")),
                Question("Wie reagierst du, wenn ich dich nach langer Zeit wiedersehe?", listOf("Umfallen vor Freude", "Lange Umarmung", "Eher cool", "Direkt küssen")),
                Question("Wie wichtig ist dir Augenkontakt beim Reden?", listOf("Sehr wichtig", "Manchmal überbewertet", "Ich schaue lieber weg", "Kommt aufs Thema an"))
            )
        ),

        QuestionPack(
            id = "zeichnen",
            title = "Zeichne für mich",
            tags = listOf("unterhaltung"),
            cat = "zeich",
            topic = "hobbys",
            type = "draw",
            questions = listOf(
                Question("Ein kleiner süßer Roboter 🤖"),
                Question("Ein glitzernder Zauberhut 🎩✨"),
                Question("Unser Traumschloss in den Wolken 🏰☁️"),
                Question("Ein lustiges Porträt deines Partners 🤭🎨"),
                Question("Ein Alien, der gerade ein Selfie macht 👽🤳")
            )
        ),

        QuestionPack(
            id = "zustimmen",
            title = "Zustimmen oder Ablehnen",
            tags = listOf("unterhaltung"),
            cat = "zust",
            topic = "moral",
            type = "quiz",
            questions = listOf(
                Question("In einer Beziehung sollte man alles voneinander wissen.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Man sollte nie streitend einschlafen.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Getrennte Urlaube tun einer Beziehung gut.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Eifersucht ist ein Zeichen von Liebe.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Freundschaften mit Ex-Partnern sind okay.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Man sollte finanzielle Geheimnisse vor dem Partner haben.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Soziale Medien schaden modernen Beziehungen.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Gegensätze ziehen sich wirklich dauerhaft an.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Heiraten ist heute nicht mehr zeitgemäß.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Partner sollten immer zusammenwohnen.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Ehrlichkeit ist immer wichtiger als Taktgefühl.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Der Partner sollte der beste Freund sein.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Ein schlechter Witz ist besser als peinliches Schweigen.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Fernsehen im Schlafzimmer ruiniert die Romantik.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab")),
                Question("Gemeinsame Hobbys sind ein Muss für lange Paare.", listOf("Stimme voll zu", "Eher zu", "Eher nicht", "Lehne ab"))
            )
        ),

        QuestionPack(
            id = "tagesfragen",
            title = "Tägliche Aktivität",
            tags = listOf("unterhaltung"),
            cat = "tief",
            topic = "aufwaermen",
            type = "quiz",
            questions = listOf(
                Question("Wie kann dein Partner ein noch besserer Partner für dich sein?", listOf("Mehr zuhören", "Mehr gemeinsame Zeit", "Mehr Unterstützung", "Ist schon perfekt")),
                Question("Kennst du die Essensvorlieben deines Partners?", listOf("Ja, in- und auswendig", "Das meiste weiß ich", "Bin manchmal unsicher"))
            )
        ),

        QuestionPack(
            id = "niealltag",
            title = "Das tägliche Leben",
            tags = listOf("unterhaltung"),
            cat = "nie",
            topic = "kennen",
            type = "quiz",
            questions = listOf(
                Question("Ich bin noch nie in einem Kino eingeschlafen.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich habe noch nie Pläne abgesagt, um zu Hause zu bleiben und eine Fernsehsendung zu sehen.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich habe noch nie in einer Universitätssportmannschaft mitgespielt.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich habe noch nie die nassen Klamotten für ein paar Tage in der Waschmaschine vergessen.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich habe noch nie ein Elektroauto gefahren.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich habe noch nie einen Nachtzug genommen.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich musste noch nie rennen, um einen Anschlussflug zu erwischen.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich habe noch nie ein Auto in einem fremden Land gefahren.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich habe noch nie einen Freund gehabt, der in ein anderes Land gezogen ist.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich habe noch nie einen Freund gefunden, der eine andere Muttersprache spricht.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich habe noch nie einen Social-Media-Beitrag gelöscht, weil er nicht genug Likes bekommen hat.", listOf("Ich habe noch nie!", "Ich habe!")),
                Question("Ich habe noch nie wegen des Finales einer Fernsehsendung geweint.", listOf("Ich habe noch nie!", "Ich habe!"))
            )
        ),

        QuestionPack(
            id = "intimleben",
            title = "Unser Intimleben",
            tags = listOf("reden"),
            cat = "reden",
            topic = "sex",
            type = "quiz",
            questions = listOf(
                Question("Wie kann dein Partner am besten Sex mit dir initiieren?", listOf("Direkt ansprechen", "Körperliche Annäherung", "Romantische Stimmung", "Überraschend")),
                Question("Wie stehst du zu schmutzigem Gerede beim Sex?", listOf("Ich liebe es", "Ist okay für mich", "Nicht mein Fall")),
                Question("Was ist das Wichtigste, das du bei einer sexuellen Begegnung suchst?", listOf("Leidenschaft", "Romantik", "Spaß & Abenteuer", "Verbindung")),
                Question("Was hältst du davon, gemeinsam erotische Inhalte anzuschauen?", listOf("Sehr gerne", "Ab und zu", "Lieber nicht")),
                Question("Wie zeigst du deine Zuneigung am liebsten außerhalb von Sex?", listOf("Kuscheln", "Worte/Komplimente", "Kleine Geschenke", "Hilfe im Alltag")),
                Question("Beschreibe unser Sexleben mit einem Emoji.", listOf("🔥", "❤️", "🎢", "🧸")),
                Question("Sind wir beim Sex eher laut oder leise?", listOf("Sehr laut", "Normal", "Sehr leise", "Kommt drauf an")),
                Question("Wie wichtig ist dir das Vorspiel?", listOf("Extrem wichtig", "Schön, aber nicht zwingend", "Lieber gleich zur Sache", "Kommt auf die Stimmung an")),
                Question("Probierst du gerne neue Dinge im Bett aus?", listOf("Ja, ständig", "Ab und zu", "Ich mag unsere Routine", "Eher nicht")),
                Question("Wie fühlst du dich nach dem Sex am liebsten?", listOf("Müde und entspannt", "Energisch", "Kuschelbedürftig", "Ich brauche kurz Platz"))
            )
        ),

        QuestionPack(
            id = "unbeliebt",
            title = "Unbeliebte Meinungen",
            tags = listOf("unterhaltung"),
            cat = "zust",
            topic = "moral",
            type = "quiz",
            questions = listOf(
                Question("Ananas auf der Pizza schmeckt köstlich.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Berühmte Touristenorte sind immer eine Enttäuschung.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Geister existieren wirklich.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Fernsehwerbung ist manchmal interessant anzusehen.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Eine neue Sprache zu lernen ist einfach.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Denselben Film zweimal zu schauen, ist Zeitverschwendung.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Socken in Sandalen sind eigentlich ganz bequem.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Der Winter ist die weitaus schönere Jahreszeit als der Sommer.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Süßes Popcorn ist salzigem Popcorn haushoch überlegen.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Koriander schmeckt einfach nur nach Seife.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Morgens kalt duschen ist der beste Start in den Tag.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Frühstück ist die unwichtigste Mahlzeit des Tages.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Bücher sind fast immer besser als ihre Verfilmungen.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Papierkram digitalisieren macht das Leben komplizierter.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Hunde sind weitaus treuere Begleiter als Katzen.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Kaffee braucht weder Milch noch Zucker, um gut zu sein.", listOf("Stimmt", "Stimmt nicht", "Teils teils"))
            )
        ),

        QuestionPack(
            id = "ehepaar",
            title = "Ehepaar Leben",
            tags = listOf("unterhaltung"),
            cat = "wer",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(
                Question("Wer ist romantischer?", listOf("{user}", "{partner}", "Beide", "Niemand")),
                Question("Wer ist der beste Tänzer?", listOf("{user}", "{partner}", "Beide", "Niemand")),
                Question("Wer merkt schneller, wenn es dem anderen nicht gut geht?", listOf("{user}", "{partner}", "Beide gleich", "Kommt darauf an")),
                Question("Wer hat den besseren Musikgeschmack?", listOf("{user}", "{partner}", "Beide", "Niemand")),
                Question("Wer findet die besten Restaurants?", listOf("{user}", "{partner}", "Beide", "Niemand")),
                Question("Wer hängt mehr an seinen Eltern?", listOf("{user}", "{partner}", "Beide", "Niemand")),
                Question("Wer ist besser organisiert?", listOf("{user}", "{partner}", "Beide", "Niemand")),
                Question("Wer sucht am Ende wirklich den Film aus?", listOf("{user}", "{partner}", "Wir wechseln uns ab", "Wir einigen uns nie")),
                Question("Wer kocht besser?", listOf("{user}", "{partner}", "Beide", "Niemand")),
                Question("Wer ist der Beste bei der Planung romantischer Dates?", listOf("{user}", "{partner}", "Beide", "Niemand"))
            )
        ),

        QuestionPack(
            id = "gespraechsanreger",
            title = "Gesprächsanreger",
            tags = listOf("reden"),
            cat = "reden",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(
                Question("Was möchtest du, dass dein Partner öfter tut?", listOf("Mich überraschen", "Zuhören", "Im Haushalt helfen", "Zärtlich sein")),
                Question("Was ist dein Lieblingsfoto von uns? 📸", listOf("Ein lustiges Bild", "Ein romantisches Bild", "Aus dem Urlaub")),
                Question("Welches Lied macht dich an? 🥵", listOf("RnB / Soul", "Pop", "Rock", "Keine Musik")),
                Question("Was magst du an deinem Partner am liebsten?", listOf("Humor", "Aussehen", "Intelligenz", "Fürsorglichkeit")),
                Question("Was ist deine größte Angst vor dem Zusammenleben?", listOf("Keine Privatsphäre", "Streit über Haushalt", "Alltagsroutine")),
                Question("Was hast du von deinem Partner gelernt?", listOf("Geduld", "Gelassenheit", "Neues Hobby", "Besser kommunizieren")),
                Question("Welcher gemeinsame Moment bringt dich immer zum Lächeln?", listOf("Unser erster Kuss", "Ein lustiger Fail", "Ein tiefer Blick", "Etwas Alltägliches")),
                Question("Gibt es ein Thema, über das wir zu wenig reden?", listOf("Unsere Zukunft", "Unsere Ängste", "Finanzen", "Nein, alles super")),
                Question("Was würdest du dir für unser nächstes Date wünschen?", listOf("Action", "Entspannung", "Gutes Essen", "Spontanität")),
                Question("Wie würdest du unsere Beziehung in 3 Worten beschreiben?", listOf("Liebevoll, ehrlich, wild", "Ruhig, sicher, warm", "Spannend, witzig, tief", "Ich brauche mehr Worte")),
                Question("Was ist das Schönste daran, mit mir zusammen zu sein?", listOf("Dass ich so bin, wie ich bin", "Die Geborgenheit", "Die gemeinsamen Abenteuer", "Alles")),
                Question("Wenn wir morgen ans andere Ende der Welt ziehen müssten, wohin?", listOf("Asien", "Südamerika", "Nordamerika", "Australien/Ozeanien"))
            )
        ),

        QuestionPack(
            id = "liebegleichgewicht",
            title = "Liebe im Gleichgewicht",
            tags = listOf("unterhaltung"),
            cat = "lieber",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(
                Question("Was fällt dir in unserer Beziehung leichter?", listOf("Geben", "Nehmen", "Beides gleich", "Es ist situationsabhängig")),
                Question("Wie triffst du am liebsten Entscheidungen für uns?", listOf("Ich schlage vor, du entscheidest", "Du schlägst vor, ich entscheide", "Gemeinsam stundenlang diskutieren", "Spontan abwechseln")),
                Question("Wer investiert gefühlt mehr Zeit in die Beziehungsarbeit?", listOf("{user}", "{partner}", "Absolut ausgeglichen", "Wir machen das unbewusst")),
                Question("Wie wichtig ist dir persönlicher Freiraum?", listOf("Extrem wichtig", "Wichtig, aber zu zweit ist besser", "Lieber fast alles zusammen machen", "Ein gesundes Mittelmaß")),
                Question("Wie gehen wir mit unterschiedlichen Meinungen um?", listOf("Wir finden immer einen Kompromiss", "Einer gibt meistens nach", "Wir akzeptieren, dass wir uneinig sind", "Wir diskutieren leidenschaftlich")),
                Question("Fühlst du dich in deinen Bedürfnissen voll gesehen?", listOf("Ja, immer", "Meistens", "Manchmal wünsche ich mir mehr Aufmerksamkeit", "Wir arbeiten daran")),
                Question("Wer von uns initiiert häufiger tiefe Gespräche?", listOf("{user}", "{partner}", "Beide gleich", "Das ergibt sich von selbst")),
                Question("Wie ausgeglichen ist unsere Aufgabenverteilung im Alltag?", listOf("Sehr fair", "Könnte besser sein", "Einer macht fast alles", "Wir haben keine feste Struktur")),
                Question("Wie gehen wir mit Fehlern des anderen um?", listOf("Schnell verzeihen", "Darüber reden, bis alles geklärt ist", "Erstmal schmollen", "Wir lachen es oft weg")),
                Question("Was stärkt das Gleichgewicht unserer Liebe am meisten?", listOf("Regelmäßige Dates", "Kleine Aufmerksamkeiten", "Ehrliches Feedback", "Gemeinsame Zukunftspläne"))
            )
        ),

        QuestionPack(
            id = "neueliebe",
            title = "Neue Liebe, neue Erfahrungen",
            tags = listOf("unterhaltung"),
            cat = "zust",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(
                Question("Ich lache viel mit meinem Partner.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich entdecke gerne neue Hobbys oder Aktivitäten mit meinem Partner.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich teile gerne Memes und Witze mit meinem Partner.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich priorisiere es, Zeit mit meinem Partner zu verbringen, auch wenn ich beschäftigt bin.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich schreibe meinem Partner jeden Tag.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich teile gerne alle Details meines Tages mit meinem Partner.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich lerne gerne die Hobbys und Interessen meines Partners kennen.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Es nervt mich, wenn mein Partner zu lange braucht, um auf Nachrichten zu antworten.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich plane gerne Dates mit meinem Partner.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich liebe es, Filme mit meinem Partner zu schauen, die keiner von uns zuvor gesehen hat.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich liebe es, meine Lieblingsmusik mit meinem Partner zu teilen.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Romantische Gesten wie Nachrichten, Zettelchen oder Geschenke machen mich glücklich.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich bevorzuge romantische Spaziergänge im Park gegenüber einem Kinobesuch.", listOf("Stimmt", "Stimmt nicht", "Teils teils")),
                Question("Ich genieße es, mit meinem Partner neue Restaurants auszuprobieren, anstatt immer an die gleichen Orte zu gehen.", listOf("Stimmt", "Stimmt nicht", "Teils teils"))
            )
        ),

        QuestionPack(
            id = "liebervideo",
            title = "Was magst du lieber?",
            tags = listOf("dasoderdas"),
            cat = "lieber",
            topic = "aufwaermen",
            type = "tot",
            pairs = listOf(
                "Ein Filmabend" to "Ein Spieleabend",
                "Ein aktives Abenteuer" to "Ein entspannender Spa-Tag",
                "Ein gemütliches Date drinnen" to "Eine Autoreise",
                "Einen Film anschauen" to "Gemeinsam ein Lego bauen",
                "Eine Weinverkostung" to "Eine Schokoladenverkostung",
                "Eine gemütliche Nacht zu Hause" to "Ein Abenteuer in einer neuen Stadt",
                "Zu einem Picknick gehen" to "Zu einem ausgefallenen Abendessen gehen",
                "Ein romantischer Abend" to "Eine Nacht in einem Club",
                "Eine Date-Nacht unter den Sternen" to "Ein romantisches Abendessen bei Kerzenlicht",
                "In einen Coffeeshop gehen" to "In eine Bar gehen",
                "Ein gemütlicher Abend im Haus während eines Gewitters" to "Ein tolles Date im Freien unter dem Mondlicht",
                "Einen Vergnügungspark erkunden" to "Ein Museum besuchen",
                "Ein Spieleabend mit Freunden" to "Ein romantisches Picknick an einem schönen Ort",
                "Gemeinsam in der Küche ein neues Rezept ausprobieren" to "In einem feinen Restaurant essen gehen",
                "Zelten gehen" to "Einen Wellness-Tag",
                "Einen Tanzkurs besuchen" to "Eine Wanderung mit Panoramablick machen",
                "Spiele spielen und Spaß haben" to "Tiefgründige Gespräche führen",
                "Ein Live-Musik-Konzert besuchen" to "Auf eine Bootsparty gehen",
                "Geh früh am Morgen, um den Sonnenaufgang zu sehen" to "In eine Strandbar gehen, um den Sonnenuntergang zu sehen"
            )
        ),

        // ★ Marken & Alltag (Das oder Das)
        QuestionPack(
            id = "markenalltag",
            title = "Marken & Alltag",
            tags = listOf("dasoderdas"),
            cat = "tot",
            topic = "aufwaermen",
            type = "tot",
            pairs = listOf(
                "McDonald’s" to "Burger King",
                "iPhone" to "Android",
                "Netflix" to "Kino",
                "Nike" to "Adidas",
                "Spotify" to "YouTube Music",
                "PlayStation" to "Xbox",
                "Coca-Cola" to "Pepsi",
                "IKEA" to "Möbelhaus",
                "Amazon" to "Lokal einkaufen",
                "Disney" to "Studio Ghibli"
            )
        ),

        // ★ Essen & Genuss
        QuestionPack(
            id = "essenreden",
            title = "Essen & Genuss",
            tags = listOf("essen", "reden", "unterhaltung"),
            cat = "reden",
            topic = "essen",
            type = "quiz",
            emoji = "🍽️",
            questions = listOf(
                Question(
                    "Was ist ein Essen, das du liebst, dein Partner aber nicht verstehen kann?",
                    listOf(
                        "Etwas sehr Scharfes",
                        "Ein ungewöhnliches Gericht",
                        "Ein Kindheitsessen",
                        "Etwas, das ich selbst gern erfinde"
                    )
                ),
                Question(
                    "Was wäre für dich schlimmer: nie wieder Pizza oder nie wieder Burger?",
                    listOf(
                        "Nie wieder Pizza",
                        "Nie wieder Burger",
                        "Beides wäre schlimm",
                        "Ich finde eine Alternative"
                    )
                ),
                Question(
                    "Wer entscheidet bei euch schneller, was bestellt wird?",
                    listOf(
                        "{user}",
                        "{partner}",
                        "Wir brauchen ewig",
                        "Wir bestellen einfach beides"
                    )
                ),
                Question(
                    "Wie möchtest du dein Ei am liebsten?",
                    listOf(
                        "4 Minuten – Sehr flüssig",
                        "5 Minuten – Flüssig",
                        "6 Minuten – Weich & cremig",
                        "7 Minuten – Weiches Eigelb",
                        "8 Minuten – Cremiges Eigelb",
                        "9 Minuten – Fast fest",
                        "10 Minuten – Vollständig fest",
                        "11 Minuten – Fest",
                        "12 Minuten – Sehr fest",
                        "13 Minuten – Trocken",
                        "14 Minuten – Sehr trocken",
                        "15 Minuten – Übergart"
                    )
                ),
                Question(
                    "Wie willst du dein Steak?",
                    listOf(
                        "Roh – Kaltes Herz, roh",
                        "Fast roh – Sehr kühler Kern, rot",
                        "Sehr blutig – Kühler Kern, rot",
                        "Blutig (Blue-Rare) – Kühler roter Kern",
                        "Sehr englisch (Rare) – Warmer roter Kern",
                        "Englisch (Rare) – Warmer Kern, zart",
                        "Halb rosa – Rosa Center, saftig",
                        "Rosa (Medium-Rare) – Rosa Kern, zart",
                        "Medium – Rosa Center, saftig",
                        "Halb durch (Medium-Well) – Teils rosa Center",
                        "Durch (Well-Done) – Kein Rosa, fest",
                        "Übergart – Ganz trocken & fest"
                    )
                ),
                Question("Bist du beim Essen eher probierfreudig oder isst du immer dasselbe?", listOf("Sehr probierfreudig", "Manchmal offen für Neues", "Meistens dasselbe", "Immer dasselbe")),
                Question("Was ist dein absolutes Comfort Food?", listOf("Pasta", "Schokolade", "Suppe", "Fast Food")),
                Question("Wenn du für den Rest deines Lebens nur noch aus einer Länderküche essen dürftest, welche wäre es?", listOf("Italienisch", "Asiatisch", "Mexikanisch", "Gutbürgerlich")),
                Question("Wie stehst du zu scharfem Essen?", listOf("Ich liebe es extrem scharf", "Ein bisschen Schärfe ist gut", "Gar nicht", "Ich weine schon bei Pfeffer")),
                Question("Nachtisch oder lieber eine Vorspeise?", listOf("Nachtisch", "Vorspeise", "Beides!", "Keins von beidem"))
            )
        )
    )

    val PACKS: List<QuestionPack>
        get() {
            val result = (DEFAULT_PACKS + HarmonyExpansionPacks.PACKS).toMutableList()
            for (dp in dynamicPacks) {
                val idx = result.indexOfFirst { it.id == dp.id }
                if (idx >= 0) {
                    result[idx] = dp
                } else {
                    result.add(dp)
                }
            }
            return result.map { com.example.data.Harmony360SectionTopicSorting.apply(it) }
        }
}

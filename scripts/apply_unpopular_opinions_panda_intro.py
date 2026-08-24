#!/usr/bin/env python3
from pathlib import Path

screen = Path("app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt")
text = screen.read_text(encoding="utf-8")

import_anchor = "import androidx.compose.ui.window.Dialog\n"
import_replacement = "import androidx.compose.ui.viewinterop.AndroidView\nimport androidx.compose.ui.window.Dialog\n"
if import_anchor not in text:
    raise SystemExit("AndroidView import anchor missing")
text = text.replace(import_anchor, import_replacement, 1)

function_anchor = "@Composable\nfun QuizRunnerScreen(\n"
intro_function = '''@Composable
private fun UnpopularOpinionsPandaIntro(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { viewContext ->
                android.widget.VideoView(viewContext).apply {
                    setBackgroundColor(android.graphics.Color.BLACK)
                    setVideoURI(
                        android.net.Uri.parse(
                            "android.resource://${viewContext.packageName}/${R.raw.unpopular_opinions_panda_intro}"
                        )
                    )
                    setOnPreparedListener { mediaPlayer ->
                        mediaPlayer.isLooping = false
                        mediaPlayer.setVolume(1f, 1f)
                        start()
                    }
                    setOnCompletionListener { onFinished() }
                    setOnErrorListener { _, _, _ ->
                        onFinished()
                        true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        TextButton(
            onClick = onFinished,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f))
        ) {
            Text(
                text = tr("Überspringen", "Skip"),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

'''
if function_anchor not in text:
    raise SystemExit("QuizRunnerScreen function anchor missing")
text = text.replace(function_anchor, intro_function + function_anchor, 1)

state_anchor = '''    val context = LocalContext.current
    val pack = activeRun.pack
    val totalLen = if (pack.type == "tot") pack.pairs.size else pack.questions.size
'''
state_replacement = '''    val context = LocalContext.current
    val pack = activeRun.pack
    var showUnpopularOpinionsIntro by remember(pack.id) {
        mutableStateOf(pack.id == "unbeliebt")
    }
    val totalLen = if (pack.type == "tot") pack.pairs.size else pack.questions.size
'''
if state_anchor not in text:
    raise SystemExit("QuizRunnerScreen state anchor missing")
text = text.replace(state_anchor, state_replacement, 1)

body_anchor = '''                    } else if (pack.type == "tot") {
                        // This or That Mode
'''
body_replacement = '''                    } else if (pack.id == "unbeliebt" && showUnpopularOpinionsIntro) {
                        UnpopularOpinionsPandaIntro(
                            onFinished = { showUnpopularOpinionsIntro = false },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (pack.type == "tot") {
                        // This or That Mode
'''
if body_anchor not in text:
    raise SystemExit("Runner body anchor missing")
text = text.replace(body_anchor, body_replacement, 1)

screen.write_text(text, encoding="utf-8")
print("Integrated Panda intro video into Unbeliebte Meinungen")

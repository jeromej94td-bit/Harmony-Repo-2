import re

with open("app/src/main/java/com/example/ui/screens/GamesScreen.kt", "r") as f:
    text = f.read()

# Let's fix the specific place
# It looks like:
#             item(key = "after_categories_spacer") {
#                 Spacer(modifier = Modifier.height(20.dp))
#             }
#                 }
#             }
#             item(key = "topics_header") {
# We should remove the extra "                }\n            }\n"

text = text.replace(
    """            item(key = "after_categories_spacer") {
                Spacer(modifier = Modifier.height(20.dp))
            }
                }
            }
            item(key = "topics_header") {""",
    """            item(key = "after_categories_spacer") {
                Spacer(modifier = Modifier.height(20.dp))
            }
            item(key = "topics_header") {"""
)

with open("app/src/main/java/com/example/ui/screens/GamesScreen.kt", "w") as f:
    f.write(text)

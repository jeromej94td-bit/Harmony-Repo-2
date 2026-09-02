#!/bin/bash
git checkout FETCH_HEAD -- \
app/.gitignore \
app/build.gradle.kts \
app/src/main/assets/introspection/merlin_theme_01.b64 \
app/src/main/assets/introspection/merlin_theme_02.b64 \
app/src/main/assets/introspection/merlin_theme_03.b64 \
app/src/main/assets/introspection/merlin_theme_04.b64 \
app/src/main/assets/introspection/merlin_theme_05.b64 \
app/src/main/assets/introspection/merlin_theme_06.b64 \
app/src/main/assets/introspection/merlin_theme_07.b64 \
app/src/main/assets/introspection/merlin_theme_08.b64 \
app/src/main/assets/introspection/merlin_theme_09.b64 \
app/src/main/assets/introspection/merlin_theme_10.b64 \
app/src/main/assets/introspection/merlin_theme_11.b64 \
app/src/main/assets/introspection/merlin_theme_12.b64 \
app/src/main/res/raw/introspection_animal.mp3 \
app/src/main/res/raw/introspection_color.mp3 \
app/src/main/res/raw/introspection_reveal.mp3 \
app/src/main/res/raw/introspection_water.mp3 \
scripts/verify_merlin_theme_assets.py

rm -f app/src/main/res/raw/introspection_animal.xml
rm -f app/src/main/res/raw/introspection_color.xml
rm -f app/src/main/res/raw/introspection_reveal.xml
rm -f app/src/main/res/raw/introspection_water.xml
rm -f app/src/main/res/raw/merlin_theme.xml
rm -f generate_clean_audio.py

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.TranslationCatalog
import com.example.ui.components.HarmonyCard
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyText
import com.example.ui.tr

@Composable
internal fun LanguageSelectorCard(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    var isLanguageExpanded by remember { mutableStateOf(false) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchFocusRequester = remember { FocusRequester() }
    val allAvailableLanguages = remember {
        AppLanguage.entries.filter(TranslationCatalog::hasCompletePack)
    }
    val availableLanguages = remember(allAvailableLanguages, searchQuery) {
        filterLanguages(allAvailableLanguages, searchQuery)
    }

    LaunchedEffect(isSearchVisible) {
        if (isSearchVisible) searchFocusRequester.requestFocus()
    }

    HarmonyCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tr("Sprache", "Language"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = HarmonyText
                    )
                    Spacer(modifier = Modifier.padding(top = 2.dp))
                    Text(
                        text = tr("App-Sprache auswählen", "Select app language"),
                        fontSize = 11.5.sp,
                        color = HarmonyMuted
                    )
                }

                IconButton(
                    onClick = {
                        if (isSearchVisible) {
                            searchQuery = ""
                            isSearchVisible = false
                        } else {
                            isSearchVisible = true
                            isLanguageExpanded = true
                        }
                    },
                    modifier = Modifier.testTag("language_search_toggle")
                ) {
                    Icon(
                        imageVector = if (isSearchVisible) Icons.Rounded.Close else Icons.Rounded.Search,
                        contentDescription = if (isSearchVisible) {
                            tr("Suche schließen", "Close search")
                        } else {
                            tr("Sprache suchen", "Search languages")
                        },
                        tint = HarmonyPink
                    )
                }
            }

            AnimatedVisibility(
                visible = isSearchVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        isLanguageExpanded = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .focusRequester(searchFocusRequester)
                        .testTag("language_search_input"),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = tr("Sprache suchen", "Search language"),
                            color = HarmonyMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = HarmonyMuted
                        )
                    },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = tr("Suche leeren", "Clear search"),
                                    tint = HarmonyMuted
                                )
                            }
                        }
                    } else null,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HarmonyPink,
                        unfocusedBorderColor = HarmonyLine,
                        focusedTextColor = HarmonyText,
                        unfocusedTextColor = HarmonyText,
                        cursorColor = HarmonyPink,
                        focusedContainerColor = Color.White.copy(alpha = 0.04f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.04f)
                    )
                )
            }

            Spacer(modifier = Modifier.padding(top = 10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(
                        1.dp,
                        if (isLanguageExpanded) HarmonyPink.copy(alpha = 0.7f) else HarmonyLine,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { isLanguageExpanded = !isLanguageExpanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("language_selector_trigger"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = language.flagEmoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.ENGLISH) language.englishName else language.nativeName,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isLanguageExpanded) {
                            tr("Tippen zum Zuklappen", "Tap to collapse")
                        } else {
                            tr("Tippen zum Auswählen & Scrollen", "Tap to select & scroll")
                        },
                        color = HarmonyMuted,
                        fontSize = 11.5.sp
                    )
                }
                Text(
                    text = if (isLanguageExpanded) "▲" else "▼",
                    color = HarmonyPink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(
                visible = isLanguageExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, HarmonyLine, RoundedCornerShape(16.dp))
                        .padding(6.dp)
                ) {
                    if (availableLanguages.isEmpty()) {
                        Text(
                            text = tr("Keine Sprache gefunden", "No language found"),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 16.dp),
                            color = HarmonyMuted,
                            fontSize = 13.sp
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableLanguages.forEach { option ->
                                val isSelected = language == option
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) HarmonyPink.copy(alpha = 0.85f)
                                            else Color.White.copy(alpha = 0.04f)
                                        )
                                        .clickable {
                                            onLanguageChange(option)
                                            searchQuery = ""
                                            isSearchVisible = false
                                            isLanguageExpanded = false
                                        }
                                        .padding(horizontal = 14.dp, vertical = 12.dp)
                                        .testTag("language_option_${option.name}"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = option.flagEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = if (language == AppLanguage.ENGLISH) {
                                            option.englishName
                                        } else {
                                            option.nativeName
                                        },
                                        modifier = Modifier.weight(1f),
                                        color = Color.White,
                                        fontSize = 14.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    if (isSelected) {
                                        Text(
                                            text = "✓",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

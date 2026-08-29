package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun Test() {
    Column {
        Box {
            this@Column.AnimatedVisibility(visible = true) {
                
            }
        }
    }
}

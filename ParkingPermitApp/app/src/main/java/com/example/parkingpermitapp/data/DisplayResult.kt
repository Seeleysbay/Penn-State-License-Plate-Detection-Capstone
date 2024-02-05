package com.example.parkingpermitapp.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp

@Composable
fun DisplayResult(ocrResultState: MutableState<String>, onClose: () -> Unit) {
    if (ocrResultState.value.isNotEmpty()) {
        Box(Modifier
            .fillMaxSize(fraction = 0.75f)
            .background(Color.LightGray)){
            Text(
                text = ocrResultState.value,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
            Button(
                onClick = onClose,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text("Close")
            }
        }
    }
}

package com.example.parkingpermitapp.cameraview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkingpermitapp.ui.theme.Navy

@Composable
fun RadioButtons(submitBatch: MutableState<Boolean>, clearBatch: MutableState<Boolean>, plateCount: MutableState<Int>,
                 radioOptions: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {

    val textDisplay = listOf("Plates Scanned: ", plateCount.value.toString() )
    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
       var i = 0
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                        }
                    )
                    .padding(horizontal = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = { onOptionSelected(text) }
                )
                Text(
                    modifier = Modifier.padding(start = 0.dp),
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(red = 17, green = 85, blue = 204, alpha = 255),
                    fontSize = 17.sp,
                    overflow = TextOverflow.Ellipsis

                )
                if(i==0) {Spacer(modifier = Modifier.fillMaxWidth(0.1f))}
                else{ Spacer(modifier = Modifier.fillMaxWidth(0.27f))}
                Text(
                    text = textDisplay[i],
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(red = 17, green = 85, blue = 204, alpha = 255),
                    fontSize = 17.sp
                )
            }
            i++
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = { clearBatch.value = true },
                    modifier = Modifier
                        .padding(10.dp)

                ) {
                    Text("Clear Batch")
                }
                Button(
                    onClick = { submitBatch.value = true },
                    modifier = Modifier
                        .padding(10.dp)

                ) {
                    Text("Batch Search")
                }
            }
        }
    }
}
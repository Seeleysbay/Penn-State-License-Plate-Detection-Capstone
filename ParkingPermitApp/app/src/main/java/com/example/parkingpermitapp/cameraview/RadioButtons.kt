package com.example.parkingpermitapp.cameraview

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
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkingpermitapp.ui.theme.Navy

@Composable
fun RadioButtons(submitBatch: MutableState<Boolean>, plateCount: MutableState<Int>,
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy,
                    fontSize = 15.sp,
                    overflow = TextOverflow.Ellipsis

                )
                if(i==0) {Spacer(modifier = Modifier.width(20.dp))}
                else{ Spacer(modifier = Modifier.width(60.dp))}
                Text(
                    text = textDisplay[i],
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy,
                    fontSize = 15.sp
                )
            }
            i++
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
            //count animation composable goes here or just text
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
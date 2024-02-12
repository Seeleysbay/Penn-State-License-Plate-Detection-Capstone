package com.example.parkingpermitapp.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
//connect android to a rest API
//https://www.digitalocean.com/community/tutorials/retrofit-android-example-tutorial


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayResult(ocrResultState: MutableState<String>, onClose: () -> Unit) {
    fun getPlate(ocrResultState: String): String{
        return ocrResultState.substringBefore('_', "")
    }
    fun getState(ocrResultState: String): String{
        return ocrResultState.substringAfter('_', "")
    }
    if (ocrResultState.value.isNotEmpty()) {
        val states = listOf(
            "",
            "AL", "AK", "AZ", "AR", "CA",
            "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA",
            "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO",
            "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH",
            "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT",
            "VA", "WA", "WV", "WI", "WY"
        )
        var plate = getPlate(ocrResultState.value)
        var state = getState(ocrResultState.value)
        var expanded = remember { mutableStateOf(false) }
        var PLATE by remember { mutableStateOf(plate) } //holds license plate code to be sent to API
        var STATE by remember { mutableStateOf(state) } //holds state abbreviation to be sent to API


        Box(
            Modifier
                .fillMaxSize(fraction = 0.75f)
                .background(Color.White)){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier.padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        modifier = Modifier.padding(16.dp, 16.dp),
                        value = PLATE,
                        onValueChange = { PLATE = it },
                        label = { Text("License Plate") },
                        placeholder = { Text(text = "License Plate") }
                    )
                }
                Row(
                    modifier = Modifier.padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    ExposedDropdownMenuBox(
                        expanded = expanded.value,
                        onExpandedChange = { expanded.value = it }) {
                        TextField(
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            value = STATE,
                            onValueChange = { },
                            label = { Text("State") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded.value
                                )
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = {
                                expanded.value = false
                            }
                        ) {
                            states.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(text = selectionOption) },
                                    onClick = {
                                        STATE = selectionOption
                                        expanded.value = false
                                    }
                                )
                            }
                        }
                    }

                }
            }
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

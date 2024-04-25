package com.example.parkingpermitapp.data

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkingpermitapp.network.DriverInfo
import com.example.parkingpermitapp.network.PlatesAPI
import com.example.parkingpermitapp.network.RetrofitClient
import com.example.parkingpermitapp.ui.theme.PennStatePlatesPrimaryColor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

//connect android to a rest API
//https://www.digitalocean.com/community/tutorials/retrofit-android-example-tutorial


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayResult(ocrResultState: MutableState<String>,platesApi: PlatesAPI, apiKey: String, onClose: () -> Unit) {
    var apiResponse by remember { mutableStateOf<String?>(null) } //Holds API query results

    fun getPlate(ocrResultState: String): String{
        return ocrResultState.substringBefore('_', "")
    }
    fun getState(ocrResultState: String): String{
        return ocrResultState.substringAfter('_', "")
    }
    fun searchPlate(state: String, plate: String) {
        val call = platesApi.queryLicensePlate(state, plate, "bc5300a645ed994e494e70e31fd11b91eb685ca139a1d50eab1e447d61da2be2")
        call.enqueue(object : Callback<DriverInfo> {
            override fun onResponse(call: Call<DriverInfo>, response: Response<DriverInfo>) {
                if (response.isSuccessful) {
                    if(response.body()?.plateNum != null) {
                        // Match found in database for query, toString of DriverInfo class is output
                        apiResponse = response.body()?.toString()
                    }
                    else{ // Match for query not found
                        apiResponse = "$plate is not registered."
                    }
                } else {
                    // Unsuccessful query to database
                    val errorString = response.errorBody()!!.string()
                    apiResponse = "Error."
                    Log.e("API Error", errorString)
                }
            }
            // Network failure
            override fun onFailure(call: Call<DriverInfo>, t: Throwable) {
                // Handle network failure
                apiResponse = "Failure: ${t.message}"
            }
        })
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
                // display OCR results
                if (apiResponse == null) {
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
                //Display API query results
                else{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(top = 0.dp, bottom = 50.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            // Display API query results here
                            Text(
                                text = apiResponse!!,
                                color = Color.Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center // Align text to center horizontally
                            )
                        }
                    }
                }
            }
            Button(
                onClick = { searchPlate(STATE, PLATE) },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PennStatePlatesPrimaryColor,
                    contentColor = Color.White
                )
            ) {
                Text("Search")
            }
            Button(
                onClick = onClose,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PennStatePlatesPrimaryColor,
                    contentColor = Color.White
                )
            ) {
                Text("Close")
            }


        }
    }
}

package com.example.parkingpermitapp.data

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkingpermitapp.network.DriverInfo
import com.example.parkingpermitapp.network.PlatesAPI
import com.example.parkingpermitapp.ui.theme.PennStatePlatesPrimaryColor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

//connect android to a rest API
//https://www.digitalocean.com/community/tutorials/retrofit-android-example-tutorial


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayBatchResult(plates: MutableList<String>, states: MutableList<String>, platesApi: PlatesAPI, apiKey: String, onClose: () -> Unit) {
    var apiResponse by remember { mutableStateOf<String?>(null) } //Holds API query results
    val notFoundPlates = remember { mutableStateListOf<String>() }
    val notFoundStates = remember { mutableStateListOf<String>() }
    val text = buildAnnotatedString {
        withStyle(style = SpanStyle(
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            textDecoration = TextDecoration.Underline
        )
        ) {
            append("Unregistered License Plates")
        }
    }

    fun searchPlate(state: String, plate: String) {
        val call = platesApi.queryLicensePlate(state, plate, apiKey)
        call.enqueue(object : Callback<DriverInfo> {
            override fun onResponse(call: Call<DriverInfo>, response: Response<DriverInfo>) {
                if (response.isSuccessful) {
                    if (response.body()?.plateNum != null) {
                        // Match found in database for query, toString of DriverInfo class is output
                        apiResponse = response.body()?.toString()
                    } else { // Match for query not found
                        apiResponse = "$plate is not registered."
                        notFoundPlates.add(plate)
                        notFoundStates.add(state)
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
    if (plates.isNotEmpty()) {
        for (i in plates.indices) {
            searchPlate(states[i], plates[i])
        }
        Box(
            Modifier
                .fillMaxSize(fraction = 0.75f)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                LazyColumn(modifier = Modifier.fillMaxSize().weight(1f).padding(16.dp)) {
                    itemsIndexed(notFoundPlates) { index, plate ->
                        val state = notFoundStates[index]
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = plate,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(2.dp)) // Adjust spacing between columns as needed
                            Text(
                                text = state,
                                color = Color.Black,
                                modifier = Modifier.width(IntrinsicSize.Min).align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .padding(16.dp),
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
}



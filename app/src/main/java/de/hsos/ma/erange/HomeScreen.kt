package de.hsos.ma.erange

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.hsos.ma.erange.ui.theme.ERangeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.text.MessageFormat

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScrollContentHome(
    isMenuExpanded: MutableState<Boolean>,
    innerPadding: PaddingValues,
    navController: NavController,
    context: Context,
    entryViewModel: EntryViewModel
) {

    OptionMenu(isMenuExpanded,navController)
    Column() {
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            Header(Modifier.padding(innerPadding))
        }
        Row() {
            ERangePreview(Modifier.padding(innerPadding), navController, context, entryViewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ERangeHome(
    navController: NavController,
    context: Context,
    entryViewModel: EntryViewModel
)
{
    screen = "home"
    Screen(navController, context,entryViewModel)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ERangePreview(
    modifier: Modifier,
    navController: NavController,
    context: Context,
    entryViewModel: EntryViewModel
) {
    val weight = rememberSaveable { mutableStateOf("80") }
    val capacity = rememberSaveable() { mutableStateOf("600")  }
    val isFlatTourProfile = rememberSaveable { mutableStateOf(true) }
    val isDropDownExpanded = rememberSaveable { mutableStateOf(false) }
    val itemPosition = rememberSaveable { mutableIntStateOf(0) }
    val capacities = listOf("600", "620", "640", "660")
    val modifier = Modifier
    ERangeTheme {
        weight.value = loadSharedPreference(context, "weight").toString()
        val modifier = Modifier
        ERange(modifier, weight, capacity, isFlatTourProfile, isDropDownExpanded, itemPosition, capacities, navController,context, entryViewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ERange(
    modifier: Modifier,
    weight: MutableState<String>,
    capacity: MutableState<String>,
    isFlatTourProfile: MutableState<Boolean>,
    isDropDownExpanded: MutableState<Boolean>,
    itemPosition: MutableIntState,
    capacities: List<String>,
    navController: NavController,
    context: Context,
    entryViewModel: EntryViewModel
) {
    var isLoading by remember { mutableStateOf(false) }

    Column()
    {
        InputBox("Your weight [kg] :", txt = weight)

        Spacer(Modifier.requiredHeight(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Battery capacity [wh] :"
            )
            DropDownSelection(isDropDownExpanded, itemPosition, capacity, capacities)
        }

        Spacer(Modifier.requiredHeight(10.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Flat Tour Profile? :"
            )
            Row(
                Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)
            ) {
                Switch(
                    checked = isFlatTourProfile.value,
                    onCheckedChange = {
                        isFlatTourProfile.value = it
                    }
                )
            }
        }

        Spacer(Modifier.requiredHeight(10.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val weight = weight.value.toDoubleOrNull() ?: 0.0
                    saveSharedPreference(context, "weight", weight)
                    val capacity = capacity.value.toDoubleOrNull() ?: 0.0
                    val range = range(weight, capacity, true)
                    output = MessageFormat.format("Range is: {0}.", range)
                    navController.navigate("results")

                    entryViewModel.addEntry(range,weight,capacity)

                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    "Calculate",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }

            Button(
                onClick = {
                    navController.navigate("info")
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    "Info",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }

        Spacer(Modifier.requiredHeight(10.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    isLoading = true
                    fetchBatteryCapacity { fetchedCapacity, error ->
                        isLoading = false
                        if (error != null) {
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                        } else {
                            capacity.value = fetchedCapacity ?: "Not Found"
                            Toast.makeText(
                                context,
                                "Battery Capacity Updated: $fetchedCapacity Wh",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier.padding(10.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Fetch Battery Capacity")
                }
            }
        }

        Spacer(Modifier.requiredHeight(10.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        fetchWeather("Osnabrück", "19c89c04520dce04ef037fe1534f9060")
                    }
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text("Fetch Weather")
            }
        }
        Text(temperature.value)
    }
}


@Composable
fun DropDownSelection(isDropDownExpanded: MutableState<Boolean>, itemPosition: MutableIntState, capacity: MutableState<String>, capacities: List<String>) {
    Column (
        Modifier
            .padding(20.dp, 0.dp, 0.dp, 0.dp)
    ) {
        Text(
            text = capacity.value,
            style = MaterialTheme.typography.titleLarge,
        )
    }
    Row (horizontalArrangement = Arrangement.End)  {
    DropdownMenu(
        expanded = isDropDownExpanded.value,
        onDismissRequest = {
            isDropDownExpanded.value = false
        }) {
        capacities.forEachIndexed { index, capacity ->
            DropdownMenuItem(text = {
                Text(text = capacity + " wh",
                    style = MaterialTheme.typography.titleLarge)
            },
                onClick = {
                    isDropDownExpanded.value = false
                    itemPosition.intValue = index
                })

        }
        capacity.value = capacities.get(itemPosition.intValue)
    }
    }
    Icon(
        Icons.Filled.KeyboardArrowDown, modifier = Modifier
            .size(36.dp)
            .clickable { isDropDownExpanded.value = true },
        contentDescription = "Show options")
}


@Composable
fun InputBox(title: String, txt: MutableState<String>) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    )
    {
        // 2/5 of horizontal length for label, right-aligned
        Column(
            Modifier
                .padding(0.dp, 2.dp, 4.dp, 0.dp)
                .weight(2F),
            horizontalAlignment = Alignment.End
        )
        {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        // 3/5 of horizontal length for input field
        OutlinedTextField(
            value = txt.value,
            label = {},
            modifier = Modifier
                .padding(4.dp, 0.dp, 12.dp, 0.dp)
                .weight(3F),
            textStyle = MaterialTheme.typography.headlineMedium,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ) ,
            onValueChange = { txt.value = it })
    }
}

/*
* Formula for range computation.
*/
fun range (weight: Double, capacity: Double, is_flat: Boolean) : Double {
    val consumption = 7.0f /* Wh per km */
    val normWeight = 80f /* kg */
    var range = (capacity / consumption * (normWeight / weight))
    range = range / 2.0f
    if (!is_flat) range *= 0.7.toFloat() /* penalty mountainous */
    return range
}
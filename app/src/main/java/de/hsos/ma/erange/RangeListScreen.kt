package de.hsos.ma.erange

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScrollContentList(
    isMenuExpanded: MutableState<Boolean>,
    innerPadding: PaddingValues,
    navController: NavController,
    context: Context,
    entryViewModel: EntryViewModel
) {
    OptionMenu(isMenuExpanded,navController)
    val modifier = Modifier.padding(innerPadding)
    Column {
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Header(modifier)
        }
                list_Input_fields(modifier,entryViewModel)
                EntryListPage(entryViewModel)

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ERangeList(
    navController: NavController,
    context: Context,
    entryViewModel: EntryViewModel
) {
    screen = "list"
    Screen(navController, context, entryViewModel)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun list_Input_fields(modifier: Modifier, entryViewModel: EntryViewModel
    ) {
    val weight = rememberSaveable { mutableStateOf("80") }
    val capacity = rememberSaveable { mutableStateOf("80") }
    val range = rememberSaveable { mutableStateOf("80") }
    Column(
        verticalArrangement = Arrangement.Center) {
        Row() {
            InputBox("Your weight [kg] :", txt = weight)
        }
        Row() {
            InputBox("Your Battery Capacity [wh] :", txt = capacity)
        }
        Row() {
            InputBox("Achieved Range :", txt = range)
        }
        Row(modifier = Modifier.padding(30.dp).align(Alignment.CenterHorizontally)) {
        Column() {
            Button(
                onClick = {
                    val weight = weight.value.toDoubleOrNull() ?: 0.0
                    val capacity = capacity.value.toDoubleOrNull() ?: 0.0
               //     val range = range(weight, capacity, true)
                    val range = range.value.toDoubleOrNull() ?: 0.0
                    entryViewModel.addEntry(range,weight,capacity)
                },
            ) {
                Text(
                    "Add Data",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }

        }

        Column(){
            Button(
                onClick = {
                    entryViewModel.deleteEntries()
                },
            ) {
                Text(
                    "Clear Data",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }

        }
    }
}
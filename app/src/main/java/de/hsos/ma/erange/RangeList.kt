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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScrollContentList(innerPadding: PaddingValues, navController: NavController, context: Context, entryViewModel: EntryViewModel) {
    OptionMenu(navController)
    val modifier = Modifier.padding(innerPadding)
    Column {
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Header(modifier)
        }
        Row(
            Modifier
                .padding(innerPadding)) {
            Column(modifier.padding()) {
                list_Input_fields(modifier,entryViewModel)
            }
            Column(modifier.padding()) {
                EntryListPage(entryViewModel)
            }
        }
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
fun list_Input_fields(modifier: Modifier, entryViewModel: EntryViewModel) {
    val weight = rememberSaveable { mutableStateOf("80") }
    val capacity = rememberSaveable { mutableStateOf("80") }
    val range = rememberSaveable { mutableStateOf("80") }
    Column(
        modifier,
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
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    val weight = weight.value.toDoubleOrNull() ?: 0.0
                    val capacity = capacity.value.toDoubleOrNull() ?: 0.0
                    val range = range(weight, capacity, true)
                    entryViewModel.addEntry(range,weight,capacity)
                },
                modifier = Modifier.padding(10.dp),
            ) {
                Text(
                    "Add Data",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }

        }
    }
}
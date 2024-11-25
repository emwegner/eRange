package de.hsos.ma.erange

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ERangeInfo(
    navController: NavController,
    context: Context,
    entryViewModel: EntryViewModel
) {
    screen = "info"
    Screen(navController, context,entryViewModel)
}

@Composable
fun ScrollContentInfo(
    isMenuExpanded: MutableState<Boolean>,
    innerPadding: PaddingValues,
    navController: NavController
) {
    val modifier = Modifier.padding(innerPadding)
    OptionMenu(isMenuExpanded,navController)

    Column {
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Header(modifier)
        }
        Column()
        {
            RoundedOutputWindow("With this app you can easily determine the range of your e-bike. Just fill out the form and press 'Calculate'.", modifier)
            Spacer(Modifier.requiredHeight(10.dp))
        }
    }
}
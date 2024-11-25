package de.hsos.ma.erange

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun ScrollContentMap(
    isMenuExpanded: MutableState<Boolean>,
    innerPadding: PaddingValues,
    navController: NavController,
    context: Context
) {
    OptionMenu(isMenuExpanded,navController)

    val location = rememberSaveable() { mutableStateOf("ort")  }
    location.value = loadSharedPreferenceString(context, "location")
    val modifier = Modifier.padding(innerPadding)
    Column() {
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Header(modifier)
        }
        Column()
        {
            InputBox("Location :", txt = location)
            Button(
                onClick = {
                    saveSharedPreferenceString(context,"location", location.value)
                    sendIntent(location.value, context)
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    "Search",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }
    }

}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ERangeMap(
    navController: NavController,
    context: Context,
    entryViewModel: EntryViewModel
)
{
    screen = "map"
    Screen(navController,context, entryViewModel)
}

fun sendIntent(location: String,context: Context) {
    val searchString: String = "Ebike Ladestationen in " + location
    // Construct the search URL
    val url = "https://www.google.com/search?q=$searchString"

    // Create an intent to open the URL in a browser
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}
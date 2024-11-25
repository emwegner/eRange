package de.hsos.ma.erange

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ERangeResults(navController: NavController, context: Context, entryViewModel: EntryViewModel) {
    screen = "results"
    Screen(navController, context,entryViewModel)
}



@Composable
fun ScrollContentResults(
    isMenuExpanded: MutableState<Boolean>,
    innerPadding: PaddingValues,
    navController: NavController
) {
    val modifier = Modifier.padding(innerPadding)
    OptionMenu(isMenuExpanded,navController)
    Column() {
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Header(modifier)
        }
        Column(modifier.padding(innerPadding))
        {
            OutlinedOutputWindow(output)
            Spacer(Modifier.requiredHeight(10.dp))
            // BackToHomeButton(navController)
        }
    }
}

@Composable
fun RoundedOutputWindow(output : String, modifier : Modifier) {
    Surface(Modifier.height(100.dp),color = MaterialTheme.colorScheme.inversePrimary) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = output,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
fun OutlinedOutputWindow(output : String ) {
    val border = BorderStroke(3.dp, MaterialTheme.colorScheme.outline)
    val size = CornerSize(40f)
    val shape : CornerBasedShape = RoundedCornerShape(size)
    Surface(Modifier.height(100.dp),color = MaterialTheme.colorScheme.inversePrimary, border = border, shape = shape
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = output,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }

}


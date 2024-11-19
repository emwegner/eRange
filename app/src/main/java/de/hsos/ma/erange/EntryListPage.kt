package de.hsos.ma.erange

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntryListPage(viewModel: EntryViewModel){
    val entryDao = MainDB.entryDatabase.getEntryDao()
    val entryList by viewModel.entryList.observeAsState()


    Column(
    ) {
        Row(

        ) {

        }

        entryList?.let {
            LazyColumn(
                content = {
                    itemsIndexed(entryList!!){ index: Int, item: Entry ->
                        EntryItem(item = item, onDelete = {
                            viewModel.deleteEntry(item)
                        })
                    }
                }
            )
        }?: Text(
            text = "No items yet",
        )


    }

}

@SuppressLint("DefaultLocale")
@Composable
fun EntryItem(item : Entry,onDelete : ()-> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
        ) {
            Text(
                text = item.id.toString() + ". ",
                fontSize = 12.sp,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier
                .padding(4.dp)
        ) {
            Text(
                text = item.weight.toString() + " kg",
                fontSize = 12.sp,
                color = Color.White
            )
        }
        Column( modifier = Modifier
            .padding(4.dp)
        ) {
            Text(
                text = item.capacity.toString() + " wh",
                fontSize = 12.sp,
                color = Color.White
            )
        }
        Column( modifier = Modifier
            .padding(4.dp)
        ) {
            Text(
                text = String.format("%.3f", item.range).toDouble().toString() + " Range",
                fontSize = 12.sp,
                color = Color.White
            )
        }

    }
}
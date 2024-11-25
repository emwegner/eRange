package de.hsos.ma.erange

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class EntryDB {
    //main thing

}

//OPTION B NEU
@Entity
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Date,
    val weight: Double,
    val capacity: Double,
    val range: Double
)

@Dao
interface EntryDao {

    @Insert
    fun addEntry(entry: Entry)

    @Delete
    fun deleteEntry(entry: Entry)

    @Query("DELETE FROM Entry")
    fun deleteEntries()

    @Query("SELECT * FROM Entry")
    fun getEntries(): LiveData<List<Entry>>

}

@Database(
    entities = [Entry::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class EntryDatabase: RoomDatabase() {
    companion object {
        const val NAME = "Entry_DB"
    }

    abstract fun getEntryDao(): EntryDao
}


class EntryViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    val entryDao = MainDB.entryDatabase.getEntryDao()

    @RequiresApi(Build.VERSION_CODES.O)
    val entryList: LiveData<List<Entry>> = entryDao.getEntries()

    @RequiresApi(Build.VERSION_CODES.O)
    fun addEntry(range: Double, weight: Double, capacity: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            entryDao.addEntry(
                Entry(
                    date = getDate(),
                    weight = weight,
                    capacity = capacity,
                    range = range
                )
            )
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteEntry(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            entryDao.deleteEntry(entry = entry)
        }
    }

    @SuppressLint("NewApi")
    fun deleteEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            entryDao.deleteEntries()
        }
    }
}

class Converters {
    @TypeConverter
    fun fromDate(date : Date) : Long {
        return date.time
    }
    @TypeConverter
    fun toDate(time : Long) : Date {
        return Date(time)
    }
}



@RequiresApi(Build.VERSION_CODES.O)
fun getDate() : Date {
    val localDate = LocalDate.now()
    val date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
    return date
}

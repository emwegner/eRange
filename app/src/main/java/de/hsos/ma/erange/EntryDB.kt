package de.hsos.ma.erange

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class EntryDB {
    //main thing

}

//OPTION B NEU
@Entity
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date : Date,
    val weight : Double,
    val capacity : Int,
    val range : Double
)

@Dao
interface EntryDao {

    @Insert
    fun addEntry(entry: Entry)

    @Delete
    fun deleteEntry(entry: Entry)


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

    val entryDao = MainActivity.entryDatabase.getEntryDao()

    val EntryList: LiveData<List<Entry>> = entryDao.getEntries()

    @RequiresApi(Build.VERSION_CODES.O)
    fun addEntry(range: Double, weight: Double, capacity: Int) {
        viewModelScope.launch (Dispatchers.IO){
            entryDao.addEntry(Entry(date = Date.from(Instant.now()), weight = weight, capacity = capacity, range = range))
        }
    }
    fun deleteEntry(entry: Entry) {
        viewModelScope.launch (Dispatchers.IO){
            entryDao.deleteEntry(entry = entry)
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
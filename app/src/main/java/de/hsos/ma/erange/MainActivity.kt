@file:OptIn(ExperimentalMaterial3Api::class)

package de.hsos.ma.erange

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hsos.ma.erange.ui.theme.ERangeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.text.MessageFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date


var temperature: MutableState<String> = mutableStateOf("n/a")
var output : String = ""
var isMenuExpanded : Boolean = false
var screen : String = "home"


private lateinit var fusedLocationClient: FusedLocationProviderClient
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val context : Context = this
        super.onCreate(savedInstanceState)
        val entryViewModel = ViewModelProvider(this)[EntryViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        enableEdgeToEdge()
        setContent {
            AppNavigator(Modifier,context, entryViewModel)

        }
    }

}


suspend fun fetchWeather(city: String, apikey: String) {
    val api = WeatherApi.create()
    val response = api.getWeather(city, apikey)
    val temp = response.main.temp
    val humidity = response.main.humidity
    val description = response.weather[0].description

    Log.d("Weather", "Temperature: $temp, Humidity: $humidity, Description: $description")
    temperature.value = "Temperature: $temp, Humidity: $humidity, Description: $description"
}

fun fetchBatteryCapacity(callback: (String?, String?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val doc = Jsoup.connect("https://www.fahrrad-xxl.de/beratung/e-bike/akku/").get()

            val capacityElement = doc.selectFirst(".battery-capacity-selector-class")
            val temp = doc.getElementById("Bosch")
            Log.d("FetchBatteryCapacity", doc.toString())

            val capacity = capacityElement?.text()

            withContext(Dispatchers.Main) {
                callback(capacity, null)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                callback(null, e.message)
            }
        }
    }
}


@Composable
fun Header(modifier: Modifier) {
    Column(modifier.padding(), verticalArrangement = Arrangement.Center) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
        }

       Row(horizontalArrangement = Arrangement.Center
        ) {
            val image: Painter = painterResource(id = R.drawable.bikeimg)
            Image(
                painter = image, contentDescription = "",
                Modifier.size(100.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigator(modifier: Modifier, context: Context, entryViewModel: EntryViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { ERangeHome(navController, context,entryViewModel) }
        composable ("info") { ERangeInfo (navController,context,entryViewModel) }
        composable ("results") { ERangeResults (navController,context,entryViewModel) }
        composable ("map") { ERangeMap (navController,context,entryViewModel) }
        composable ("list") { ERangeList (navController,context,entryViewModel) }
    }
}

@Composable
fun OptionMenu(navController: NavController) {
    val options = listOf("Home", "Calculate","Search","List","Info")

    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = {
            isMenuExpanded = false
        }) {
        options.forEachIndexed { index, option ->
            DropdownMenuItem(text = {
                Text(text = option,
                    style = MaterialTheme.typography.titleLarge)
            },
                onClick = {
                    isMenuExpanded = false
                    when (index) {
                        0 -> navController.navigate("home")
                        1 -> navController.navigate("results")
                        2 -> navController.navigate("map")
                        3 -> navController.navigate("list")
                        else -> { // Note the block
                            navController.navigate("info")
                        }
                    }
                })

        }
    }

    Icon(
        Icons.Filled.KeyboardArrowDown, modifier = Modifier
            .size(36.dp)
            .clickable { isMenuExpanded = true },
        contentDescription = "Show options")
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(navController: NavController, context : Context, entryViewModel: EntryViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "E-Range",
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton (onClick = { navController.popBackStack()
                    }) {
                        Icon (Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {  isMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick =
                    {
                        navController.navigate("info")
                    }) {
                        Icon(
                            Icons.Filled.Info,
                            modifier = Modifier.size(36.dp),
                            contentDescription = "App Info",
                        )
                    };
                    IconButton(onClick =
                    {
                        navController.navigate("map")
                    }) {
                        Icon(
                            Icons.Filled.LocationOn,
                            modifier = Modifier.size(36.dp),
                            contentDescription = "App Info",
                        )
                    }

                    IconButton(onClick =
                    {
                        navController.navigate("list")
                    }) {
                        Icon(
                            Icons.Filled.DateRange,
                            modifier = Modifier.size(36.dp),
                            contentDescription = "App Info",
                        )
                    }
                    IconButton(onClick =
                    {
                        navController.navigate("home")
                    }) {
                        Icon(
                            Icons.Filled.Home,
                            modifier = Modifier.size(36.dp),
                            contentDescription = "App Info",
                        )
                    }
                },
            )
        }
    ) {
            innerPadding ->
        ScrollContent(innerPadding,navController, context, entryViewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScrollContent(innerPadding: PaddingValues,navController: NavController, context : Context, entryViewModel: EntryViewModel) {
    when (screen) {
        "home" -> {
            screen = "home"
            ScrollContentHome(innerPadding,navController, context, entryViewModel)
        }
        "results" -> {
            screen = "results"
            ScrollContentResults(innerPadding,navController)
        }
        "map" -> {
            screen = "map"
            ScrollContentMap(innerPadding,navController, context)
        }
        "list" -> {
            screen = "list"
            ScrollContentList(innerPadding,navController, context, entryViewModel)
        }
        else -> { // Note the block
            screen = "info"
            ScrollContentInfo(innerPadding,navController)
        }
    }
}


@Composable
fun BackToHomeButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate("home")
        },
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            "Back",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.inversePrimary
        )
    }
}










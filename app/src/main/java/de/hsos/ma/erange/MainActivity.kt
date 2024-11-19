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

//Home
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
    Column(modifier.padding())
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
                        fetchWeather("Stuttgart", "19c89c04520dce04ef037fe1534f9060")
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




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ERangePreview(
    modifier: Modifier,
    navController: NavController,
    context: Context,
    entryViewModel: EntryViewModel
) {
    val weight = rememberSaveable { mutableStateOf("80") }
    val capacity = rememberSaveable() { mutableStateOf("670")  }
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



@Composable
fun RoundedOutputWindow(output: String, modifier: Modifier) {
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
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ERangeResults(navController: NavController,context: Context,entryViewModel: EntryViewModel) {
    screen = "results"
    Screen(navController, context,entryViewModel)
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

//SCROLL CONTENTS
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScrollContentList(innerPadding: PaddingValues,navController: NavController, context: Context, entryViewModel: EntryViewModel) {
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
        EntryListPage(entryViewModel)
    }
    }
    }
}

@Composable
fun ScrollContentResults(innerPadding: PaddingValues,navController: NavController) {
    val modifier = Modifier.padding(innerPadding)
    OptionMenu(navController)
    Column() {
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
        Header(modifier)
    }
        Column()
    {
        RoundedOutputWindow(output, modifier)
        Spacer(Modifier.requiredHeight(10.dp))
       // BackToHomeButton(navController)
    }
    }
}


@Composable
fun ScrollContentInfo(innerPadding: PaddingValues,navController: NavController) {
    val modifier = Modifier.padding(innerPadding)
    OptionMenu(navController)
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
      //  BackToHomeButton(navController)
    }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScrollContentHome(
    innerPadding: PaddingValues,
    navController: NavController,
    context: Context,
    entryViewModel: EntryViewModel
) {
    OptionMenu(navController)
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
fun ScrollContentMap(innerPadding: PaddingValues,navController: NavController, context: Context) {
    OptionMenu(navController)

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


//SHARED PREFERENCES
fun saveSharedPreference (context: Context, name: String, doubleVal: Double) {
    val sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    // Create JSON.
    val gson = Gson()
    val json = gson.toJson(doubleVal)
    // Save data toShared Prefs.
    editor.putString(name, json)
    editor.apply()
    val msg = String.format("'%s = %f' saved to preferences", name, doubleVal)
}

fun loadSharedPreference (context: Context, name: String): Double {
    val sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE)
    // Access JSON formatted preference data.
    val gson = Gson()
    val json = sharedPreferences.getString(name, null)
    // Read value (type-safe).
    val type: Type = object : TypeToken<Double?>() {}.type
    val readVal = gson.fromJson<Any>(json, type)
    val result: Double = if (readVal != null) readVal as Double else 70.0
    return result
}

fun saveSharedPreferenceString (context: Context, name: String, string: String) {
    val sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    // Create JSON.
    val gson = Gson()
    val json = gson.toJson(string)
    // Save data toShared Prefs.
    editor.putString(name, json)
    editor.apply()
   // val msg = String.format("'%s = %f' saved to preferences", name, string)
}

fun loadSharedPreferenceString (context: Context, name: String): String {
    val sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE)
    // Access JSON formatted preference data.
    val gson = Gson()
    val json = sharedPreferences.getString(name, null)
    // Read value (type-safe).
    val type: Type = object : TypeToken<String?>() {}.type
    var readVal = gson.fromJson<Any>(json, type)
    if(readVal == null) readVal = ""
    return readVal.toString()
}





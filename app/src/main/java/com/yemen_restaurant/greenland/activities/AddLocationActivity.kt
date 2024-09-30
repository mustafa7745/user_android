package com.yemen_restaurant.greenland.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.yemen_restaurant.greenland.MainCompose2
import com.yemen_restaurant.greenland.models.UserLocationModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.UserStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody


class AddLocationActivity : ComponentActivity() {
    val stateController = StateController()
    private val requestServer = RequestServer(this)

    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    var long = mutableStateOf("")
    var lat = mutableStateOf("")

    val street = mutableStateOf("")
    val nearto = mutableStateOf("")
    val contact = mutableStateOf("")

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

private fun add() {
    stateController.isErrorAUD.value = false
    stateController.isLoadingAUD.value = true
    stateController.errorAUD.value = ""
    val data3 = buildJsonObject {
        put("tag", "add")
        put("inputUserLocationCity", "صنعاء")
        put("inputUserLocationStreet", street.value)
        put("inputUserLocationLatLong", lat.value +","+ long.value)
        put("inputUserLocationNearTo", nearto.value)
        put("inputUserLocationContactPhone", contact.value)
    }
    val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("data1", requestServer.getData1().toString())
        .addFormDataPart("data2", requestServer.getData2())
        .addFormDataPart("data3", data3.toString())
        .build()

    requestServer.request2(body1, Urls.userLocationUrl, { code, it ->

        stateController.isLoadingAUD.value = false
        stateController.isErrorAUD.value = true
        stateController.errorAUD.value = it
    }) {
        try {
            stateController.isLoadingAUD.value = false
            MyJson.IgnoreUnknownKeys.decodeFromString<UserLocationModel>(it)
            runOnUiThread {
                Toast.makeText(this@AddLocationActivity,"تمت الاضافه بنجاح",Toast.LENGTH_SHORT).show()
            }
            val data1 = Intent()
            data1.putExtra("location",it)
            setResult(RESULT_OK,data1)
            finish()
        } catch (e: Exception) {
            stateController.isLoadingAUD.value = false
            stateController.isErrorAUD.value = true
            stateController.errorAUD.value = e.message.toString()

        }
    }


}
    val isGpsEnabled = mutableStateOf(false)
    val isGetLastLocation = mutableStateOf(false)
    val isPermissionAllow = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIsPermissionGranted()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGpsEnabled.value = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val lastLocationRequest = LastLocationRequest.Builder();


        setContent {
            GreenlandRestaurantTheme {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement= Arrangement.Center,
                horizontalAlignment= Alignment.CenterHorizontally,
                ) {
                    if (isPermissionAllow.value) {

                        if (!isGpsEnabled.value) {
                            Button(onClick = {
                                requestEnableGPS()
                            }) {
                                Text(text = "تفعيل الموقع")
                            }
                        } else {
                            LaunchedEffect(null){
                                getLocation()
                            }
                            if (!isGetLastLocation.value){
                                Button(onClick = {
                                    getLocation()
                                }) {
                                    Text(text = "تأكيد الموقع")
                                }
                            }else{
                                MainCompose2(padding = 0.dp, stateController =stateController , activity = this@AddLocationActivity ) {
                                    LazyColumn(content = {
                                        item {
                                            ContentAdd()
                                        }
                                        item{
                                            Spacer(modifier = Modifier.height(300.dp))
                                        }

                                    })

                                }

                            }


                        }
                    } else {
                        Text(text = "لم يتم منح صلاحية الوصول الى الموقع")
                        Button(onClick = {
                            checkIsPermissionGranted()
                        }) {
                            Text(text = "طلب الوصول الى الموقع")
                        }
                    }
                }
            }

        }
    }

    @Composable
    fun ContentAdd() {
        val userStorage = UserStorage()
        contact.value = userStorage.getUser().phone
        val fontSize = 10.sp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "إضافة تفاصيل الموقع",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE)
                )
            )




            OutlinedTextField(
                value = "صنعاء",
                onValueChange = {},
                label = { Text("المدينة") },
                modifier = Modifier.fillMaxWidth()
            )

            var isValidStreet by remember { mutableStateOf(true) }
            if (!isValidStreet) {
                Text(
                    text = "يحب الايزيد طول الحقل اكثر من 20 حرف",
                    color = Color.Red,
                    fontSize = fontSize,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            OutlinedTextField(
                value = street.value,
                onValueChange = { s->street.value = s
                    isValidStreet = street.value.length < 20
                                },
                label = { Text("الشارع") },
                modifier = Modifier.fillMaxWidth(),
                isError = !isValidStreet,
            )

            var isValidNearTo by remember { mutableStateOf(true) }
            if (!isValidNearTo) {
                Text(
                    text = "يحب الايزيد طول الحقل اكثر من 100 حرف",
                    color = Color.Red,
                    fontSize = fontSize,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            OutlinedTextField(
                value = nearto.value,
                onValueChange = { nearto.value = it
                    isValidNearTo = nearto.value.length < 100
                                },
                label = { Text("بالقرب من / مركز معروف / محل / منزل") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 5,

                singleLine = false,
                isError = !isValidNearTo,
            )


            var isValidPhone by remember { mutableStateOf(true) }
            if (!isValidPhone) {
                Text(
                    fontSize = fontSize,
                    text = "الرجاء إدخال رقم هاتف صحيح (يجب أن يتكون من 9 أرقام ويبدأ بـ 70, 71, 73, 77, أو 78)",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
            OutlinedTextField(
                value = contact.value,
                onValueChange = {
                    contact.value = it
                    isValidPhone = it.matches(Regex("^7[0|1|3|7|8][0-9]{7}$"))
                },
                label = { Text("رقم الهاتف للتواصل معه") },
                isError = !isValidPhone,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )



            Button(
                enabled = isValidPhone && contact.value.isNotEmpty() && nearto.value.isNotEmpty() && street.value.isNotEmpty() && isValidNearTo && isValidStreet,
                onClick = { add() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "حفظ", color = Color.White, fontSize = 18.sp)
            }
        }
    }

    private fun getLocation(){
        if (ActivityCompat.checkSelfPermission(
                this@AddLocationActivity,
                permissions[0]
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@AddLocationActivity,
                permissions[1]
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@AddLocationActivity,
                permissions[2]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
           checkIsPermissionGranted()
        }else{
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { l: Location?->
                if (l != null) {
                    isGetLastLocation.value = true
                    long.value = l.longitude.toString()
                    lat.value = l.latitude.toString()

                }else{
//                    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
//                        .setFastestInterval(5000)
//                        .build()
                    val locationRequest = LocationRequest.create().apply {
                        interval = 10000 // Update interval in milliseconds
                        fastestInterval = 5000 // Fastest update interval in milliseconds
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Use high accuracy
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, null)
                }
            }.addOnFailureListener {
            }
        }
    }
//
//    private val locationRequest = LocationRequest.create().apply {
//        interval = 5000 // Update interval in milliseconds (e.g., every 5 seconds)
//    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // Handle location updates here
            val locations = locationResult.locations

            for (location in locations) {
                // Process each location
                println("Received location: Lat=${location.latitude}, Long=${location.longitude}")
            }
        }
    }
    private fun checkIsPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(
                this@AddLocationActivity,
                permissions[0]
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@AddLocationActivity,
                permissions[1]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isPermissionAllow.value = false
            fineLocationActivityResultLauncher.launch(permissions[0])
            fineLocationActivityResultLauncher.launch(permissions[1])
        } else {
            isPermissionAllow.value = true
        }

    }
    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    private fun requestEnableGPS() {
        val locationRequest = LocationRequest.create()
        //
        //
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build())//Checksettings with building a request
        task.addOnSuccessListener { locationSettingsResponse ->
            getLocation()
            isGpsEnabled.value = true
            Log.d(
                "Location",
                "enableLocationRequest: LocationService Already Enabled"
            )
        }
        task.addOnFailureListener { exception ->
            isGpsEnabled.value = false
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution)
                            .build()//Create the request prompt
                    gpsActivityResultLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private val gpsActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        Log.e("result ", result.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            isGpsEnabled.value = true
            getLocation()
        } else {

        }
    }
    private val fineLocationActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isPermissionAllow.value = true

        } else {
            // Permission denied, handle accordingly (e.g., show an explanation or disable the feature)
        }
    }
}


package com.yemen_restaurant.greenland

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.yemen_restaurant.greenland.activities.DashboardActivity
import com.yemen_restaurant.greenland.activities.LoginActivity
import com.yemen_restaurant.greenland.models.EncryptedModel
import com.yemen_restaurant.greenland.models.Success2Model
import com.yemen_restaurant.greenland.models.SuccessModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.TokenVM
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.encodeToString
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    private val stateController = StateController()
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var appUpdateInfoTask: Task<AppUpdateInfo>
    private lateinit var updateLauncher: ActivityResultLauncher<IntentSenderRequest>
    val isThereUpdate = mutableStateOf(false)

    private val isSuccessToken = mutableStateOf(false)


    private val tokenVM: TokenVM by viewModels()
    val requestServer = RequestServer(this)
    private lateinit var body: MultipartBody

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen.
        val str1 = intent.getStringExtra("refreshToken")
        if (str1 != null) {
            stateController.isLoadingRead.value = true
            refreshLoginToken()
        } else {
            checkUpdate()
            getTokenFirebase()
        }



        setContent {
            GreenlandRestaurantTheme {
                MainCompose1(
                    padding = 0.dp,
                    stateController = stateController,
                    activity = this,
                    read = {
                        if (isSuccessToken.value) {

                            finalInit()
                        } else {
                            getTokenFirebase()
                        }
                    }) {

                }
            }
        }
    }

    //
    private fun checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateInfoTask = appUpdateManager.appUpdateInfo
        updateLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode != RESULT_OK) {
                    // Handle if the update failed or was canceled
                    Toast.makeText(this, "Update failed or canceled", Toast.LENGTH_SHORT).show()
                }
            }

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // an activity result launcher registered via registerForActivityResult
                    updateLauncher,
                    // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                    // flexible updates.
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
                isThereUpdate.value = true
            }
        }
    }


    private fun getTokenFirebase() {
            stateController.isLoadingRead.value = true
            tokenVM.check({
                errorState("اعادة المحاولة1")
            }, {
                isSuccessToken.value = true
                finalInit()
            })
        }


    private fun setServerKey() {

        requestServer.requestGet2(Urls.publicKeyUrl, {
            errorState(it)
        }) {
            when (it.code) {
                200 -> {
                    val data = requestServer.getResponse(it)
                    requestServer.login.setServerKey(MyJson.MyJson.decodeFromString(data))
                    init()
                }

                else -> {
                    errorState("خطا عند تحديث البيانات الخاصة")
                }
            }
        }
    }

    private fun initDeviceInServer() {
        body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data1", requestServer.getData1().toString()).build()
        Urls.initUrl
        requestServer.request2(body, Urls.initUrl, { _, it ->
            errorState(it)
        }) {
            val projectId =
                MyJson.MyJson.decodeFromString<Success2Model>(it)
            Firebase.messaging.subscribeToTopic(projectId.success)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        errorState("Error register FB")
                    } else {
                        requestServer.login.setProjectId(projectId.success)
                        val intent = Intent(
                            this@MainActivity,
                            LoginActivity::class.java
                        )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finalFinish()
                    }
                }
        }

    }

    private fun refreshLoginToken() {
        body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data1", requestServer.getData1().toString())
            .addFormDataPart("data2", requestServer.getData2())
            .build()

        requestServer.request2(body, Urls.refreshToken, { _, it ->
            errorState(it)
        }) {
            var decryptResult: String = "";
            try {
                val encryptedData = MyJson.MyJson.decodeFromString<EncryptedModel>(it)
                decryptResult = requestServer.decryptData(
                    Base64.decode(
                        encryptedData.encrypted_data,
                        Base64.DEFAULT
                    )
                )
            } catch (e: Exception) {
                errorState("حدث خطأ عند فك التشفير")
            }

            val token = MyJson.MyJson.decodeFromString<SuccessModel>(decryptResult)
            requestServer.login.setLoginToken(MyJson.MyJson.encodeToString(token))
            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finalFinish()
        }

    }

    private fun errorState(it: String) {
        stateController.isLoadingRead.value = false
        stateController.isErrorRead.value = true
        stateController.errorRead.value = it
    }

    private fun init() {
        if (!requestServer.login.isSetProjectId()) {
            initDeviceInServer()
        } else {
            if (requestServer.login.isSetLoginToken()) {
                val tokenInfo = requestServer.login.getLoginTokenWithDate();
                val tokenDateExpireAt =
                    (LocalDateTime.parse(tokenInfo.expire_at.replace("\\s".toRegex(), "T")))
                if (tokenDateExpireAt.isBefore(LocalDateTime.now())) {
                    refreshLoginToken()
                } else {
                    val intent =
                        Intent(this@MainActivity, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finalFinish()
                }
            } else {
                val intent =
                    Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finalFinish()
            }
        }
    }

    private fun finalFinish() {
        if (!isThereUpdate.value){
            finish()
        }
    }

    private fun finalInit() {
        stateController.isLoadingRead.value = true
        if (requestServer.login.isSetServerKey()) {
            init()
        } else {
            setServerKey()
        }
    }
}

@Composable
fun MainCompose1(
    padding: Dp,
    stateController: StateController,
    activity: Activity,
    read: () -> Unit,
    onSuccess: @Composable() (() -> Unit)
) {
    var verticalArrangement: Arrangement.Vertical by remember { mutableStateOf(Arrangement.Center) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = padding),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stateController.isLoadingAUD.value) {
            Dialog(onDismissRequest = { }) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    LoadingCompose()
                }
            }
        }
        if (stateController.isErrorAUD.value) {
            Toast.makeText(activity, stateController.errorAUD.value, Toast.LENGTH_SHORT).show()
            stateController.isErrorAUD.value = false
            stateController.errorAUD.value = ""
        }
        if (stateController.isSuccessRead.value) {
            verticalArrangement = Arrangement.Top

            onSuccess()
        }
        if (stateController.isLoadingRead.value) {
            LoadingCompose()


//            LoadingCompose()
        }
        if (stateController.isErrorRead.value) {
            Text(text = stateController.errorRead.value)
            Button(onClick = {
                stateController.errorRead.value = ""
                stateController.isErrorRead.value = false
                stateController.isLoadingRead.value = true
                read()
            }
            ) {
                Text(text = "جرب مرة اخرى")
            }
        }
    }
}

@Composable
fun MainCompose2(
    padding: Dp,
    stateController: StateController,
    activity: Activity,
    content: @Composable() (() -> Unit)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = padding),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stateController.isLoadingAUD.value) {
            Dialog(onDismissRequest = { }) {
                LoadingCompose()
            }
        }
        if (stateController.isErrorAUD.value) {
            Toast.makeText(activity, stateController.errorAUD.value, Toast.LENGTH_SHORT).show()
            stateController.isErrorAUD.value = false
            stateController.errorAUD.value = ""
        }
        content()
    }
}

@Composable
fun CustomImageView(
    context: Context,
    imageUrl: String,
    okHttpClient: OkHttpClient,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    // Create ImageLoader with OkHttpClient
//    val imageLoader = ImageLoader.Builder(context)
//        .okHttpClient(okHttpClient)
//        .build()

    // Display the image using AsyncImage
    SubcomposeAsyncImage(
        error = {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "خطأ في التحميل")
            }

        },
        loading = {
            LoadingCompose()
        },
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .build(),
//        imageLoader = imageLoader,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}




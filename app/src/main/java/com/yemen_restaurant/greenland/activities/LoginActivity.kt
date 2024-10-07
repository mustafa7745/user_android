package com.yemen_restaurant.greenland.activities
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.yemen_restaurant.greenland.LoadingCompose
import com.yemen_restaurant.greenland.MainActivity
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.models.EncryptedModel
import com.yemen_restaurant.greenland.models.SuccessModel
import com.yemen_restaurant.greenland.shared.CCompose
import com.yemen_restaurant.greenland.shared.Login
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.SharedInAppUpdate
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody


val cCompose = CCompose()

class LoginActivity : ComponentActivity() {
    val requestServer = RequestServer(this)
    val isLoading = mutableStateOf(false)
    val isError = mutableStateOf(false)
    val error = mutableStateOf("")
    val login = Login()

    //
    val phone = mutableStateOf("")
    val password = mutableStateOf("")
    val showDialog = mutableStateOf(false)


    private fun login() {
        error.value = ""
        isLoading.value = true
        GlobalScope.launch {

            val text = buildJsonObject {
                put("inputUserPhone",  phone.value.trim())
                put("inputUserPassword", password.value.trim())
            }
            var encryptedUserData = requestServer.encryptData(
                MyJson.MyJson.encodeToString(text),
                (login.getServerKey())
            )
            val data2 = encryptedUserData


            val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("data1", requestServer.getData1().toString())
                .addFormDataPart("data2", data2.toString())
                .build()

            requestServer.request2(body, Urls.loginUrl, { code, it ->
                Log.e("fff", code.toString())
                if (code == 1111) {

                    login.setServerKey("")
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                isLoading.value = false
                isError.value = true
                error.value = it
            }) { res ->
                Log.e("erreerooor123", res)
                var decryptResult: String = "";
                try {
                    val encryptedData = MyJson.IgnoreUnknownKeys.decodeFromString<EncryptedModel>(res)
                    Log.e("erreerooor", encryptedData.encrypted_data)
                    decryptResult = requestServer.decryptData(
                        Base64.decode(
                            encryptedData.encrypted_data,
                            Base64.DEFAULT
                        )
                    )
                    Log.e("erreerooor5555", decryptResult)
                } catch (e: Exception) {
                    isLoading.value = false
                    isError.value = true
                    error.value = "حدث خطأ عند فك التشفير"
                }
                Log.e("erreer", decryptResult)


                val token = MyJson.IgnoreUnknownKeys.decodeFromString<SuccessModel>(decryptResult)

                login.setLoginToken(MyJson.MyJson.encodeToString(token))

                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedInAppUpdate(this).checkUpdate()
        setContent {
            GreenlandRestaurantTheme {
                LoginScreen(
                    phone = phone,
                    password = password,
                    isLoading = isLoading,
                    error = error,
                    login = { login() })
                if (showDialog.value)
                    InfoDialog(showDialog = showDialog, title =
                        "كيفية الاشتراك", message = "عند الضغظ على موافق سيتم فتح الواتس اب على رسالة (السلام عليكم) قم بإرساله وسيتم ارسال رمز الدخول") {
                    }
            }
        }
    }


    @Composable
    fun LoginScreen(
        phone: MutableState<String>,
        password: MutableState<String>,
        isLoading: MutableState<Boolean>,
        error: MutableState<String>,
        login: () -> Unit
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Icon or Image
                    Image(
                        painter = rememberImagePainter(R.mipmap.ic_launcher_round),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp)
                    )


                    // Heading
                    Text(
                        text = "تسجيل الدخول",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )


                    var isValidPhone by remember { mutableStateOf(true) }
                    if (!isValidPhone && phone.value.length>8) {
                        Text(
                            fontSize = 10.sp,
                            text = "الرجاء إدخال رقم هاتف صحيح (يجب أن يتكون من 9 أرقام ويبدأ بـ 70, 71, 73, 77, أو 78)",
                            color = Color.Red,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                    // Phone Number Field
                    OutlinedTextField(
                        value = phone.value,
                        onValueChange = {
                            phone.value = it
                            isValidPhone = it.matches(Regex("^7[0|1|3|7|8][0-9]{7}$"))
                        },
                        label = { Text(text = "رقم الهاتف") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Row (
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){
                        OutlinedTextField(
                            enabled = !isLoading.value,
                            value = password.value,
                            onValueChange = { password.value = it },
                            label = { Text(text = "الرقم السري") },
                            modifier = Modifier
                                .width(150.dp)
                                .padding(bottom = 20.dp),
                            visualTransformation = PasswordVisualTransformation()
                        )
                        Column {
                            if (isLoading.value) {
                                Box(
                                    Modifier.size(100.dp)
                                ) {
                                    LoadingCompose()
                                }
                            }
                            else{
                                Column (

                                    horizontalAlignment = Alignment.CenterHorizontally,

                                    verticalArrangement = Arrangement.Center
                                ){
                                    if (error.value.isNotEmpty()) {
                                        Text(
                                            text = error.value,
                                            fontSize = 6.sp,
                                            color = Color.Red
                                        )
                                    }
                                    Button(
                                        onClick = login,
                                        enabled = isValidPhone  && password.value.length in 4..8,
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(text = "دخول",fontFamily = FontFamily(
                                            Font(R.font.bukra_bold))
                                        )
                                    }
                                    // Error Message


                                }

                            }

                        }
                    }
//                    Spacer(modifier = Modifier.height(32.dp))

                    // Sign Up Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ليس لدي حساب",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "اشتراك",
                            color = Color.Blue,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                showDialog.value = true
                            }
                        )
                    }






                    Spacer(modifier = Modifier.height(100.dp))

                    // Terms and Conditions
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row {
                            Text(
                                text = "من خلال تسجيل الدخول او الاشتراك فانك توافق على ",
                                fontSize = 9.sp
                            )
                        }
                        Row(
                            Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://greenland-rest.com/policies-terms.html")
                                }
                                startActivity(intent)
                            }
                        ) {
                            Text(

                                text = "سياسة الاستخدام", color = Color.Blue, fontSize = 9.sp
                            )
                            Text(text = " و ", fontSize = 9.sp)
                            Text(
                                text = "شروط الخدمة ", color = Color.Blue, fontSize = 9.sp
                            )

                        }
                    }
                }
            }
        }
    }

    private fun intentFunWhatsapp(): Boolean {
        val formattedNumber = "967781874077"
        val message = "السلام عليكم"

        // Create the URI for the WhatsApp link
        val uri =
            "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}"

        // Create an Intent to open the WhatsApp application
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uri)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        try {
            startActivity(intent)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "يجب تثبيت الواتس اولا", Toast.LENGTH_LONG).show()
            return false
        }
    }
    @Composable
    fun InfoDialog(
        showDialog: MutableState<Boolean>,
        title: String,
        message: String,
        onDismiss: () -> Unit
    ) {
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    // Handle dismiss action
                    showDialog.value = false
                    onDismiss()
                },
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Confirm button action
                            showDialog.value = false
                            intentFunWhatsapp()
                        }
                    ) {
                        Text("موافق")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            // Dismiss button action
                            showDialog.value = false
                            onDismiss()
                        }
                    ) {
                        Text("الغاء", color = Color.Red)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }



}


//package com.onemegasoft.greenlandrestaurant.activities
//
//
//import android.content.ContentValues.TAG
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.viewModels
//import androidx.compose.ui.unit.dp
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.messaging.ktx.messaging
//import com.onemegasoft.greenlandrestaurant.shared.RequestServer
//import com.onemegasoft.greenlandrestaurant.shared.TokenVM
//import com.onemegasoft.greenlandrestaurant.shared.UserMethod
//import java.lang.Exception
//
//class OnEnterActivity : ComponentActivity() {
//    private val tokenVM: TokenVM by viewModels()
////    private val sendRequestApi: SendRequestApi by viewModels()
//    val component1 = Component1()
//    private val userMethod = UserMethod()
//
//    private fun go1() {
//        val url = "admin/init.php";
//        val body = RequestServer().body
//        sendRequestApi.go(url, body) {
//            go2()
//        }
//    }
//
//    private fun go2() {
//        if (userMethod.isUserLogin()) {
//            val url = "admin/login/login_after_login.php"
//            val body = RequestServer().get_body()
//            runOnUiThread {
//                sendRequestApi.go(url, body) {
//                    try {
//                        val user = userMethod.toUser(it.data.toString())
//                        userMethod.setUser(user)
//                        val intent = Intent(this, com.onemegasoft.greenlandrestaurant.activities.DashboardActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                        startActivity(intent)
//                        finish()
//
//                    } catch (e: Exception) {
//                        Log.e("exepp", e.toString())
//                    }
//                }
//            }
//
//        } else {
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//            finish()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//
////        Log.e("sd",FirebaseMessaging.getInstance().subscribeToTopic("app_mustafa123").isSuccessful.toString())
//        Firebase.messaging.subscribeToTopic("mustafa")
//            .addOnCompleteListener { task ->
//                var msg = "Subscribed"
//                if (!task.isSuccessful) {
//                    msg = "Subscribe failed"
//                }
//                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//            }
//        println("mustafa ismail")
//        //INIT
//        tokenVM.check()
//        go1()
//
//        super.onCreate(savedInstanceState)
//        setContent {
//            AdminTheme {
//                component1.MyColumn(0.dp) {
//                    component1.GreetingImageView(model = R.drawable.admin_greeting_image)
//                    component1.MyBox(h = 100.dp) {
//
//                    }
//                    if (tokenVM.isLoading.value)
//                        component1.MyProgressBar()
//                    else if (tokenVM.isRetry.value)
//
//                        component1.MyButton(text = "اعادة المحاولة") { tokenVM.check() }
//                    else {
//                        if (sendRequestApi.isLoading.value)
//                            component1.MyProgressBar()
//                        else if (sendRequestApi.isRetry.value)
//                            component1.MyButton(text = "اعادة المحاولة") { go1() }
//                        if (sendRequestApi.isAlert.value)
//                            sendRequestApi.ShowAlert()
//                    }
//
//                }
//            }
//        }
//    }
//
//
//}
//

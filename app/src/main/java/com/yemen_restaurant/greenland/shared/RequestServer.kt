package com.yemen_restaurant.greenland.shared


import GetStorage
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.yemen_restaurant.greenland.MainActivity
import com.yemen_restaurant.greenland.models.ErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class RequestServer(private val activity: ComponentActivity) {

    val login = Login()
    private  val deviceInfoMethod = DeviceInfoMethod()
    private val AppInfoMethod = AppInfoMethod()

    fun requestGet2(url:String,onFail:(fail:String)->Unit,onSuccess:(data:Response)->Unit,) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val okHttpClient = createOkHttpClientWithCustomCert()
                try {
                    if (!isInternetAvailable()) {
                        onFail( "لايوجد اتصال بالانترنت")
                    }

                    val request = Request.Builder()
                        .url(url)
                        .build()
                    val response = okHttpClient.newCall(request).execute()
                    onSuccess(response)

                } catch (e:Exception){
                    onFail(e.message.toString())
                }
                finally {
                    okHttpClient.connectionPool.evictAll()
                }
            }
            }

    }
    fun request2(body: RequestBody,url:String,onFail:(code:Int,fail:String)->Unit,onSuccess:(data:String)->Unit,) {
        activity.lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val okHttpClient = createOkHttpClientWithCustomCert()
                try {
                    if (!isInternetAvailable()) {
                        onFail(0, "لايوجد اتصال بالانترنت")
                    }

                    val request = Request.Builder()
                        .url(url)
                        .post(body)
                        .build()
                    val response = okHttpClient.newCall(request).execute()
                    val data = response.body!!.string()
                    Log.e("dataa",data)
                    Log.e("dataaUrl",url)

                    when(response.code){
                        200->{
                            if (MyJson.isJson(data)){

                                onSuccess(data)
                            }
                            else{
                                onFail(response.code,"not json")
                            }
                        }
                        400->{
                            if (MyJson.isJson(data)){

                                val ero = MyJson.IgnoreUnknownKeys.decodeFromString<ErrorMessage>(data)
                                when (ero.code) {
                                    1111 -> {
                                        login.setServerKey("")
                                        val intent = Intent(activity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        activity.startActivity(intent)
                                        activity.finish()
                                    }
                                    5001 -> {
                                        val intent = Intent(activity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        intent.putExtra("refreshToken", 1)
                                        activity.startActivity(intent)
                                        activity.finish()
                                    }
                                    5002 -> {
                                        val intent = Intent(activity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        login.setLoginToken("")
                                        activity.startActivity(intent)
                                        activity.finish()
                                    }
                                }
                                onFail(ero.code,ero.message.ar)
                            }
                            else{
                                onFail(response.code,"not json E")
                            }
                        }
                        else->{
                            onFail(response.code,response.code.toString())
                        }
                    }
                } catch (e:Exception){
//                onFail(0,e.message.toString())
                    val errorMessage = when (e) {
                        is java.net.SocketTimeoutException -> "Request timed out"
                        is java.net.UnknownHostException -> "Unable to resolve host"
                        is java.net.ConnectException -> "Failed to connect to server"
                        else -> e.message ?: "Unknown error occurred"
                    }
                    onFail(0, "Request failed: $errorMessage")
                    Log.e("request2", "Exception: ", e)
                }
                finally {
                    okHttpClient.connectionPool.evictAll()
                }
            }
        }
    }

    fun createOkHttpClientWithCustomCert(): OkHttpClient {
//        // Load the certificate from raw resources
//        val certInputStream: InputStream = activity.resources.openRawResource(R.raw.isrgrootx1)
//
//        // Create a CertificateFactory
//        val certificateFactory = CertificateFactory.getInstance("X.509")
//
//        // Generate the certificate
//        val certificate = certificateFactory.generateCertificate(certInputStream) as X509Certificate
//
//        // Create a KeyStore and add the certificate
//        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
//            load(null, null)
//            setCertificateEntry("ca", certificate)
//        }
//
//        // Initialize TrustManagerFactory with the KeyStore
//        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
//        trustManagerFactory.init(keyStore)
//
//        // Create SSLContext with the custom TrustManager
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(null, trustManagerFactory.trustManagers, null)

        // Build OkHttpClient with the custom SSLContext




        return OkHttpClient.Builder()
//            .sslSocketFactory(sslContext.socketFactory, trustAllCertificates)
//            .sslSocketFactory(sslContext.socketFactory, trustManagerFactory.trustManagers.first() as X509TrustManager)
            .build()
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun getResponse(response: Response): String {
        return response.body!!.string()
    }

    fun getData1(): JsonObject {
        Log.e("app_package_name",AppInfoMethod.getAppPackageName())
        Log.e("sha",AppInfoMethod.getAppSha())
        Log.e("app_version",AppInfoMethod.getAppVersion())
        Log.e("device_type_name","android")
        Log.e("device_id",deviceInfoMethod.getDeviceId())
        Log.e("device_info",deviceInfoMethod.getDeviceInfo().toString())

//        Log.e("fgfgfg",generateKeyPair().first)
        return buildJsonObject {
           val  public_key = generateKeyPair().first.replace(Regex("""(\r\n)|\n"""), "")
//            Log.e("ooo",public_key)
//            Log.e("ooo",public_key.length.toString())

            put("packageName",AppInfoMethod.getAppPackageName())
//            put("appSha",AppInfoMethod.getAppSha())
                put("appSha", "41:C7:4D:A4:15:03:35:83:84:62:54:9A:22:E6:39:DA:07:F9:60:05:44:CC:4C:5E:A2:02:74:34:BD:3A:E2:73")

            put("appVersion",1)
            put("device_type_name","android")
            put("devicePublicKey",public_key)
            put("deviceId",deviceInfoMethod.getDeviceId())
            put("deviceInfo", deviceInfoMethod.getDeviceInfo().toString())
            put("appDeviceToken",AppInfoMethod.getAppToken())
        }
    }

    fun getData2(): String {
//        val text = buildJsonObject {
//            put("login_token",login.getLoginTokenWithDate().token)
//        }
//        val encryptedUserData  = encryptData(MyJson.MyJson.encodeToString(text),(login.getServerKey()))
//        val data2 = MyJson.MyJson.encodeToString(encryptedUserData)
//        return data2
        val text = buildJsonObject {
            put("inputLoginToken", login.getLoginTokenWithDate().token)
        }
        return encryptData(MyJson.IgnoreUnknownKeys.encodeToString(text),login.getServerKey())
    }

    fun generateKeyPair(): Pair<String, String> {
//        algorithim cecert RSA
       val s = GetStorage("al")
        val name = "c_r"
        val key = s.getData(name)
        if (key.isEmpty()){
            val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            generator.initialize(2048, SecureRandom())
            val keyPair = generator.genKeyPair()
            val publicKeyBase64 = Base64.encodeToString(keyPair.public.encoded, Base64.DEFAULT)
            val privateKeyBase64 = Base64.encodeToString(keyPair.private.encoded, Base64.DEFAULT)
            s.setData(name, "$publicKeyBase64;$privateKeyBase64")
            return Pair(publicKeyBase64, privateKeyBase64)
        }
        val keys = key.split(";")
        val publicKeyBase64 = keys[0]
        val privateKeyBase64 = keys[1]
        return Pair(publicKeyBase64, privateKeyBase64)
    }

    fun encryptData(data: String, publicKey: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, base64ToPublicKey(publicKey))
        return Base64.encodeToString(cipher.doFinal(data.toByteArray()),Base64.DEFAULT)
    }
    fun decryptData(encryptedData: ByteArray): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, base64ToPrivateKey(generateKeyPair().second))
        val decryptedBytes = cipher.doFinal(encryptedData)
        return String(decryptedBytes)
    }

    fun base64ToPrivateKey(base64Key: String): PrivateKey {
        val keyBytes = Base64.decode(base64Key,Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }
    fun base64ToPublicKey(base64Key: String): PublicKey {
        val keyBytes = Base64.decode(base64Key,Base64.DEFAULT)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }
}
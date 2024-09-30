package com.yemen_restaurant.greenland.application
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.pm.PackageInfoCompat
import com.yemen_restaurant.greenland.shared.AppInfoMethod
import com.yemen_restaurant.greenland.shared.DeviceInfoMethod
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MyApplication: Application() {
    private lateinit var appInfoMethod: AppInfoMethod
    private lateinit var deviceInfoMethod : DeviceInfoMethod
    override fun onCreate() {
        super.onCreate()
        AppContext = this
        appInfoMethod = AppInfoMethod();
        deviceInfoMethod = DeviceInfoMethod();
        setDeviceId()
        setAppPackageNameAndVersion()
//        Toast.makeText(AppContext,"Hello",Toast.LENGTH_SHORT).show()
    }
    companion object {
        lateinit var AppContext: Application
            private set

    }
    @SuppressLint("HardwareIds")
    private fun setDeviceId(){
      deviceInfoMethod.setDeviceId(Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID))
  }
    private fun setAppPackageNameAndVersion(){
         try {
            val packageManager = AppContext.packageManager
            val packageName = AppContext.packageName
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }
             val appPackageName = packageInfo.packageName
             appInfoMethod.setAppVersion(PackageInfoCompat.getLongVersionCode(packageInfo).toString())
             appInfoMethod.setAppPackageName(appPackageName)
             setKeyHash(appPackageName)


        } catch (_: Exception) {

        }
    }
    private fun setKeyHash(appPackageName:String):String {
        var sha = ""
                try {
        val info: PackageInfo = AppContext.packageManager.getPackageInfo(
                appPackageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA-256")
                md.update(signature.toByteArray())
                val digest = md.digest()
                val toRet = StringBuilder()
                for (i in digest.indices) {
                    if (i != 0) toRet.append(":")
                    val b = digest[i].toInt() and 0xff
                    val hex = Integer.toHexString(b)
                    if (hex.length == 1) toRet.append("0")
                    toRet.append(hex)
                }
                val s = toRet.toString()
                sha = s
//                Log.e("sig", s)
            }
            appInfoMethod.setAppSha(sha);
            return sha
        } catch (e1: PackageManager.NameNotFoundException) {
            return e1.toString()
        } catch (e: NoSuchAlgorithmException) {
            return e.toString()
        } catch (e: Exception) {
            return e.toString()
        }
    }

}
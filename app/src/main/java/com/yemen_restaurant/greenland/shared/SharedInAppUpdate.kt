package com.yemen_restaurant.greenland.shared

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class SharedInAppUpdate (val context:ComponentActivity){
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var appUpdateInfoTask: Task<AppUpdateInfo>
    private lateinit var updateLauncher: ActivityResultLauncher<IntentSenderRequest>

    fun checkUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(context)
        appUpdateInfoTask = appUpdateManager.appUpdateInfo
        updateLauncher =
            context.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode != Activity.RESULT_OK) {
                    // Handle if the update failed or was canceled
                    Toast.makeText(context, "لم يتم التحديث ", Toast.LENGTH_SHORT).show()
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
            }

        }
    }
}
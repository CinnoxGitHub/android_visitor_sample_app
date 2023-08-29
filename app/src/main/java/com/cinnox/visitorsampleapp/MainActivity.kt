package com.cinnox.visitorsampleapp

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cinnox.visitorsampleapp.push.genFcmIntentPushData
import com.cinnox.visitorsampleapp.push.genHuaweiIntentPushData
import com.cinnox.visitorsampleapp.push.genXiaomiIntentPushData
import com.m800.cinnox.visitor.CinnoxVisitorCore
import com.m800.sdk.core.noti.CinnoxPushType

class MainActivity : AppCompatActivity() {

    companion object{
        const val TAG = "MainActivity"
    }

    private lateinit var ssoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleClickedSystemNotification()
        ssoTextView = findViewById(R.id.tv_sso)
        ssoTextView.setOnClickListener {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.m800.liveconnect.mobile.agent.tb.hkmtr", // this is Cinnox app package name
                    "com.m800.liveconnect.mobile.agent.ui.MainActivity" // this is Cinnox app activity which used for login
                )
            }
//            val intent = packageManager.getLaunchIntentForPackage("com.m800.liveconnect.mobile.agent.tb.hkmtr")!!
            intent.putExtra("action", "login")
            intent.putExtra("StaffId", "userStaffId")
            intent.putExtra("StationCode", "userStationCode")
            intent.putExtra("DisplayName", "userDisplayName")
            startActivity(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleClickedSystemNotification()
    }

    private fun handleClickedSystemNotification() {
        when (MainApplication.pushType) {
            CinnoxPushType.FCM -> genFcmIntentPushData(intent)
            CinnoxPushType.XIAOMI -> genXiaomiIntentPushData(intent)
            CinnoxPushType.HUAWEI -> genHuaweiIntentPushData(intent)
        }?.let { data ->
            if (data.length() == 0) {
                Log.i(TAG, "handleClickedSystemNotification data is empty")
                return
            }
            Log.i(TAG, "handleClickedSystemNotification data: $data")
            CinnoxVisitorCore.getInstance().getPushManager().handleClickedSystemNotification(
                MainApplication.pushType,
                data
            )
        } ?: run {
            Log.i(TAG, "handleClickedSystemNotification data is null")
        }
    }
}
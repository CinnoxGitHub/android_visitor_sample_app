package com.cinnox.visitorsampleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
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
        private const val KEY_ACTION = "action"
        private const val ACTION_LOGIN = "login"
        private const val ACTION_LOGOUT = "logout"
        private const val KEY_STAFF_ID = "StaffId"
        private const val KEY_STATION_CODE = "StationCode"
        private const val KEY_DISPLAY_NAME = "DisplayName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleClickedSystemNotification()
        initLoginFlow()
        initLogoutFlow()
        initIncomingCallFlow()
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

    private fun initLoginFlow() {
        findViewById<TextView>(R.id.tv_login).setOnClickListener {
            genIntent().apply {
                putExtra(KEY_ACTION, ACTION_LOGIN)
                putExtra(KEY_STAFF_ID, getStaffId())
                putExtra(KEY_STATION_CODE, getStationCode())
                putExtra(KEY_DISPLAY_NAME, getDisplayName())
            }.run {
                startActivity(this)
            }
        }
    }

    private fun initLogoutFlow() {
        findViewById<TextView>(R.id.tv_logout).setOnClickListener {
            genIntent().apply {
                putExtra(KEY_ACTION, ACTION_LOGOUT)
                putExtra(KEY_STAFF_ID, getStaffId())
            }.run {
                startActivity(this)
            }
        }
    }

    private fun initIncomingCallFlow() {
        findViewById<TextView>(R.id.tv_incoming_call).setOnClickListener {
            genIntent().run {
                startActivity(this)
            }
        }
    }

    private fun genIntent(): Intent {
//        val intent = Intent().apply {
//            component = ComponentName(
//                "com.m800.liveconnect.mobile.agent.tb.hkmtr", // this is Cinnox app package name
//                "com.m800.liveconnect.mobile.agent.ui.MainActivity" // this is Cinnox app activity which used for login
//            )
//        }
        val intent = packageManager.getLaunchIntentForPackage("com.m800.liveconnect.mobile.agent.tb.hkmtr")!!
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        return intent
    }

    private fun getStaffId(): String {
        return findViewById<EditText>(R.id.et_staff_id).text.toString()
    }

    private fun getStationCode(): String {
        return findViewById<EditText>(R.id.et_station_code).text.toString()
    }

    private fun getDisplayName(): String {
        return findViewById<EditText>(R.id.et_display_name).text.toString()
    }
}
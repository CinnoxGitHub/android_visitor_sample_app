# **Android Quick Start Guide**
This guide will help you know how to use the CINNOX Visitor SDK on Android application through the sample app demonstration.
 
## **Step 1: Add Libraries and Dependencies**
1. Add the following code snippet inside the repositories block:
   
```kotlin

    repositories {
        maven { url 'https://jitpack.io' }
    }
```
This will include the JitPack repository in your project, allowing you to fetch dependencies from it.

2. Add the following code snippet inside the dependencies block:

```kotlin
implementation 'com.github.CinnoxGitHub:visitor_sdk:1.0.19'
```
This line specifies the dependency on the visitor_sdk library from the JitPack repository. 

Sync your project with the Gradle files by clicking on the "Sync Now" button or selecting File > Sync Project with Gradle Files.
Congratulations! You have successfully updated the dependencies using JitPack. The visitor_sdk library is now included in your Android project.

## **Step 2: Set Up and Initialisation**

1. Create a new class called MainApplication that extends Application & Add CinnoxVisitorCoreListener and register it when you need to know the initialisation end.
 ```kotlin
 class MainApplication : Application() {
     companion object {
         const val serviceName = "xxxx.cinnox.com" // Replace with your CINNOX subdomain
     }

     private val mCoreListener: CinnoxVisitorCoreListener = object : CinnoxVisitorCoreListener{
         override fun onInitializationEnd(success: Boolean, throwable: Throwable?) {
             Log.d(TAG, "onInitializationEnd, isSuccess: $success, throwable: $throwable")
         }
     }

     override fun onCreate() {
         super.onCreate()
         val core = CinnoxVisitorCore.initialize(this, serviceName)
         core.registerListener(mCoreListener)
     }
 }
 ```
 Remember to replace "xxxx.cinnox.com" with your actual CINNOX subdomain, which you can find in your CINNOX Dashboard under Administration > Widget > Installation.

 Now, with CinnoxVisitorCore properly initialized in the MainApplication class, the SDK will be ready to use throughout the entire lifecycle of your Android application. Make sure to update your AndroidManifest.xml to use the MainApplication class as the application name:
 
 ```kotlin
 <application
     android:name=".MainApplication"
     <!-- Other attributes -->
 >
     <!-- Activities, services, receivers, etc. -->

 </application>
 ```

## **Step 3: Show your CINNOX Widget to Users**
Add CinnoxVisitorWidget in the layout

```kotlin
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.m800.maaiiconnect.mobile.client.CinnoxVisitorWidget
        android:id="@+id/floating_button"
        android:layout_width="72dp"
        android:layout_height="72dp"
        android:gravity="center"
        android:scaleType="fitCenter"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

## **Step 4: Push Notification Setup Guide**

Now Supporting FCM, Xiaomi, and Huawei Push Notifications!

Follow the steps below to enable push notifications in your app:

1. If you have already implemented FCM service in your app, please follow these steps:

   1. Here's an example of the FcmPushService class:
   ```kotlin
   class FcmPushService : FirebaseMessagingService() {
       // ...
   
       override fun onNewToken(token: String) {
           updateToken(token)
       }
   
       override fun onMessageReceived(remoteMessage: RemoteMessage) {
           val data = genFcmRemoteMessagePushData(remoteMessage)
           data?.let {
               CinnoxVisitorCore.getInstance().getPushManager().handlePushNotification(
                   CinnoxPushType.FCM,
                   it
               )
           }
       }

       private fun updateToken(token: String) {
           CinnoxVisitorCore.getInstance().getPushManager().updateToken(
               CinnoxPushType.FCM,
               token
           )
       }
   
       // ...
   }
   ```
   2. You should pass the received token to the updateToken api of the CinnoxPushManager to update the token.
   3. To handle incoming push notifications. Extract the necessary data from the RemoteMessage object and pass it to the handlePushNotification api of the CinnoxPushManager.
   4. You can use the method genFcmRemoteMessagePushData in FcmPushHelper file for extract the JSONObject data for the handlePushNotification api.
 

2. If you have already implemented XIAOMI service in your app, please follow these steps:

   1. Here's an example of the XiaomiPushService class:
   ```kotlin
   class XiaomiPushService : PushMessageReceiver() {
       // ...
   
       override fun onCommandResult(context: Context, message: MiPushCommandMessage) {
           onNewRegId(message)
       }
   
       override fun onReceiveRegisterResult(context: Context, message: MiPushCommandMessage) {
           onNewRegId(message)
       }
   
       override fun onReceivePassThroughMessage(context: Context, message: MiPushMessage) {
           onPushMessage(message)
       }

       override fun onNotificationMessageArrived(context: Context, message: MiPushMessage) {
           onPushMessage(message)
       }

       private fun onNewRegId(message: MiPushCommandMessage) {
            val arguments = message.commandArguments
            val token = if (arguments != null && arguments.size > 0) arguments.get(0) else null
            if (message.resultCode.toInt() == ErrorCode.SUCCESS && message.command == MiPushClient.COMMAND_REGISTER) {
                if (!token.isNullOrEmpty()) {
                    updateToken(token)
                }
            }
        }
    
        private fun updateToken(token: String) {
            CinnoxVisitorCore.getInstance().getPushManager().updateToken(
                CinnoxPushType.XIAOMI,
                token
            )
        }
    
        private fun onPushMessage(message: MiPushMessage) {
            val data = genXiaomiRemoteMessagePushData(message)
            data?.let {
                CinnoxVisitorCore.getInstance().getPushManager().handlePushNotification(
                    CinnoxPushType.XIAOMI,
                    it
                )
            }
        }
   
       // ...
   }
   ```
   2. You should pass the received token to the updateToken api of the CinnoxPushManager to update the token.
   3. To handle incoming push notifications. Extract the necessary data from the MiPushMessage object and pass it to the handlePushNotification api of the CinnoxPushManager.
   4. You can use the method genXiaomiRemoteMessagePushData in XiaomiPushHelper file for extract the JSONObject data for the handlePushNotification api.
   

3. If you have already implemented HUAWEI service in your app, please follow these steps:

   1. Here's an example of the HuaweiPushService class:
   ```kotlin
   class HuaweiPushService : HmsMessageService() {
       // ...
  
       override fun onNewToken(token: String?) {
           super.onNewToken(token)
           if (!token.isNullOrEmpty()) {
               updateToken(token)
           }
       }
   
       override fun onMessageReceived(remoteMessage: RemoteMessage?) {
           super.onMessageReceived(remoteMessage)
           remoteMessage?.let { remoteMessage ->
               val data = genHuaweiRemoteMessagePushData(remoteMessage)
               data?.let {
                   CinnoxVisitorCore.getInstance().getPushManager().handlePushNotification(
                       CinnoxPushType.HUAWEI,
                       it
                   )
               }
           }
       }

       private fun updateToken(token: String) {
           CinnoxVisitorCore.getInstance().getPushManager().updateToken(
               CinnoxPushType.HUAWEI,
               token
           )
       }
   
       // ...
   }
   ```
   2. You should pass the received token to the updateToken api of the CinnoxPushManager to update the token.
   3. To handle incoming push notifications. Extract the necessary data from the RemoteMessage object and pass it to the handlePushNotification api of the CinnoxPushManager.
   4. You can use the method genHuaweiRemoteMessagePushData in HuaweiPushHelper file for extract the JSONObject data for the handlePushNotification api.
   
   
4. To handle system notifications when the user clicks on them, you can follow this example using the LAUNCHER MainActivity class:

   1. Here's an example of the MainActivity class:
   ```kotlin
   class MainActivity : AppCompatActivity() {
       // ...
   
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_main)
           handleClickedSystemNotification()
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
   
       // ...
   }
   ```
   2. In this example, when the user clicks on a system notification, the handleClickedSystemNotification method is called. Extract the necessary data from the intent object and pass it to the handleClickedSystemNotification api of the CinnoxPushManager for further processing.
   3. If the notification is not the type which handleClickedSystemNotification api can process, the api will return false to let your app switch to default click system notification flow.
   4. For FCM push, you can use the method genFcmIntentPushData in FcmPushHelper file for extract the JSONObject data for the handleClickedSystemNotification api.
   5. For XIAOMI push, you can use the method genXiaomiIntentPushData in XiaomiPushHelper file for extract the JSONObject data for the handleClickedSystemNotification api.
   6. For HUAWEI push, you can use the method genHuaweiIntentPushData in HuaweiPushHelper file for extract the JSONObject data for the handleClickedSystemNotification api.


## **Use CTA**
You can use its click-to-action (CTA) features (i.e., Click-to-call or Click-to-chat buttons) on your application.

Widget - a widget, i.e., a small application interface, located at the bottom of a UI page that lets your customers or visitors immediately interact with your business through call or chat.

Click-to-call - a CINNOX widget feature that automatically calls a specific Tag or Staff member whenever a customer.

Click-to-chat - a CINNOX widget that automatically launches a chat room for a specific Tag or Staff member whenever a customer.

```kotlin
   
        // create call Tag action
        val action = CinnoxAction.initTagAction("YOUR_TAG_ID", CinnoxVisitorContactMethod.CALL)

        // create call staff action
        val action = CinnoxAction.initStaffAction("YOUR_STAFF_EID", CinnoxVisitorContactMethod.IM)

        // create open directory action
        val action = CinnoxAction.initDirectory()

        CinnoxVisitorCore.getInstance()
            .callToAction(action, object : CallToActionListener {
                override fun onActionEnd(
                    success: Boolean,
                    throwable: CinnoxVisitorCoreErrorException?
                ) {

                }

            })
   ```

# **Compatibility**
Android 7 or later


# **API Documentation**

## [Api Documentation](https://cinnoxgithub.github.io/android_visitor_sample_app/)

package com.sibmentors.appointment

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    private var Version_Code = "versionCode"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    lateinit var Loginbtn: Button
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()
        //region RemoteConfig Update Checker
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        //Enable Debug mode for frequent fetches
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        mFirebaseRemoteConfig.setConfigSettings(configSettings)
        mFirebaseRemoteConfig!!.setDefaults(R.xml.firebasedefaults);
        getRemoteConfigValues()
        //val versionCode = BuildConfig.VERSION_CODE

        //endregion
        loginbtn.setOnClickListener {
            //region LoginButtonFunctionality
            try {

                val email = username.text.toString().trim()
                val passwords = password.text.toString().trim()

                if (email.isEmpty()) {
                    username.error = "Email Required"
                    username.requestFocus()
                    return@setOnClickListener
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    username.error = "Valid Email Required"
                    username.requestFocus()
                    return@setOnClickListener
                }

                if (passwords.isEmpty() || passwords.length < 6) {
                    password.error = "6 char password required"
                    password.requestFocus()
                    return@setOnClickListener
                }
                /*username.isEnabled=false
                password.isEnabled=false*/
                loginUser(email, passwords)

            } catch (e: Exception) {
                loginbtn.error = e.message
            }
//endregion

        }
        register.setOnClickListener(
            View.OnClickListener {
                var Intent = Intent(this, UserSignup::class.java)
                startActivity(Intent)
            }
        )
        mentorregister.setOnClickListener(
            View.OnClickListener {
                var Intent = Intent(this, MentorRegistration::class.java)
                startActivity(Intent)
            }
        )
        forget_link.setOnClickListener(View.OnClickListener {
            var Intent = Intent(this, passwordReset::class.java)
            startActivity(Intent)
        })
    }

    private fun getRemoteConfigValues() {

        var cacheExpiration: Long = 7200//2 hours

        // Allow fetch on every call for now - remove/comment on production builds
        if (mFirebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //   Toast.makeText(this, "Fetch Succeeded", Toast.LENGTH_SHORT).show()
                    mFirebaseRemoteConfig.activateFetched()
                } else {
                    //   Toast.makeText(this, "Fetch Failed", Toast.LENGTH_SHORT).show()
                }

                setRemoteConfigValues()
            }
    }

    private fun setRemoteConfigValues() {
        var remoteCodeVersion = mFirebaseRemoteConfig.getLong(Version_Code)
        if (remoteCodeVersion > 0) {
            val versionCode = BuildConfig.VERSION_CODE
            if (remoteCodeVersion > versionCode) {
                val dialogBuilder = AlertDialog.Builder(this)


                // set message of alert dialog
                dialogBuilder.setMessage("This App got new update with some new some new Features and stability \nUpdate Now")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Update", DialogInterface.OnClickListener { dialog, id ->
                        val uri = Uri.parse("market://details?id=" + this@MainActivity.packageName)
                        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY or
                                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        )
                        try {
                            startActivity(goToMarket)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + this@MainActivity.packageName)
                                )
                            )
                        }
                    })
                    // negative button text and action
                    .setNegativeButton("Not Now", // do something when the button is clicked
                        DialogInterface.OnClickListener { arg0, arg1 ->
                            finishAffinity()
                        })


                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("Update Alert!!")
                // show alert dialog
                alert.show()
            }
            //  main_layout!!.setBackgroundColor(Color.parseColor(remoteValueText))
        }

    }

    private fun loginUser(email: String, password: String) {

        loading.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {

                    task ->

                if (task.isSuccessful) {

                    // login()
                    //region LoginMethod
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    currentUser?.let { user ->
                        val rootRef = FirebaseDatabase.getInstance().reference
                        val userNameRef = rootRef.child("users").orderByChild("email").equalTo(user.email)
                        val eventListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (e in dataSnapshot.children) {
                                    val employee = e.getValue(Data::class.java)
                                    if (employee != null) {
                                        val u_type = employee.user_type
                                        loading.visibility = View.GONE
                                        if (u_type == "S") startActivity(
                                            Intent(
                                                this@MainActivity,
                                                UserHomeV2::class.java
                                            )
                                        )
                                        else if (u_type == "M") startActivity(
                                            Intent(
                                                this@MainActivity,
                                                Mentorhomev2::class.java
                                            )
                                        )

                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        }
                        userNameRef.addListenerForSingleValueEvent(eventListener)

                    }
                    //endregion


                } else if (task.isCanceled) {
                    loading.visibility = View.GONE
                    task.exception?.message?.let {
                        toast(it)
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    loading.visibility = View.GONE
                    task.exception?.message?.let {
                        toast(it)
                        //Toast.makeText( this, "Login Failed", Toast.LENGTH_SHORT ).show();
                    }
                }
            }
    }

    override fun onStart() {

        super.onStart()

        var remoteCodeVersion = mFirebaseRemoteConfig.getLong(Version_Code)
        val versionCode = BuildConfig.VERSION_CODE


            if (remoteCodeVersion > versionCode) {
                getRemoteConfigValues()
            }

         else {
            mAuth.currentUser?.let {

                login()

            }
        }
    }
}
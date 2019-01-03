package com.kowalczyk.michal.smack.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.kowalczyk.michal.smack.R
import com.kowalczyk.michal.smack.Services.AuthService
import com.kowalczyk.michal.smack.Services.UserDataService
import com.kowalczyk.michal.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


//to jest potrzebne(w deklaracji klasy) do zakomentowanych czesci kodu NavigationView.OnNavigationItemSelectedListener
class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        //floating action bar
        //snackbar to jest ten przycisk na dole po prawej tozowy ze jak sie go wcisnie to wyskakuje od dolu okienko pop up z jakas
        //wiadomoscia
        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/

        //to jest odpowiedzialne za to co sie dzieje jak sie kliknie na te trzy poziome kreski symbolizujace menu
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        //to jest odpowiedzialne za to co sie stanie jak klikniemy w jakas opcje z tego menu co sie wysuwa od lewej
        /*nav_view.setNavigationItemSelectedListener(this)*/

        //Odbiornik broadcasta
        //IntentFIlter bedzie mowil jakiego typu Intenta ma szukac/oczekiwac
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataCahngeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))

    }

    //BROADCAST RECEIVER
    private val userDataCahngeReceiver=object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            //co sie ma zadziac jak sygnal zostanie odebrany
            //AuthService to singleton ktorego stworzylismy a isLoggedIn jest jego czescia
            if(AuthService.isLoggedIn){
                //jesli jest zalogowany uzytkownik to podmieniamy jego logo nazwe itp
                userNameNavHeader.text=UserDataService.name
                userEmailNavHeader.text=UserDataService.email
                val resourceId=resources.getIdentifier(UserDataService.avatarName,"drawable",packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text="Logout"
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
//To jest odpowiedzialne za te menu opcji w prawym rogu
   /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }*/

   /* override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }*/

    fun loginBtnNavClicked(view: View){
        //Intent() 1 parametr context drugi parametr gdzie to wysyłamy nazwa::class.java
        //explicit intent

        if(AuthService.isLoggedIn){
            //logout
            UserDataService.logout()
            userNameNavHeader.text=""
            userEmailNavHeader.text=""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            loginBtnNavHeader.text="Login"
        }else{
            val loginIntent=Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelClicked(view:View){

    }

    fun sendMsgBtnClicked(view:View){

    }



}

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
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.kowalczyk.michal.smack.Model.Channel
import com.kowalczyk.michal.smack.R
import com.kowalczyk.michal.smack.Services.AuthService
import com.kowalczyk.michal.smack.Services.MessageService
import com.kowalczyk.michal.smack.Services.UserDataService
import com.kowalczyk.michal.smack.Utilities.BROADCAST_USER_DATA_CHANGE
import com.kowalczyk.michal.smack.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


//to jest potrzebne(w deklaracji klasy) do zakomentowanych czesci kodu NavigationView.OnNavigationItemSelectedListener
class MainActivity : AppCompatActivity(){

    val socket= IO.socket(SOCKET_URL)
    //adapter dla list view zwiazanego z channelami
    lateinit var channelAdapter:ArrayAdapter<Channel>
    var selectedChannel:Channel?=null

    //adapter dla list view zwiazanego z channelami
    private fun setupAdapters(){
        channelAdapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,MessageService.channels)
        channel_list.adapter=channelAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        socket.connect()
        //nazwa eventu wzieta z API
        socket.on("channelCreated",onNewChannel)
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
        setupAdapters()

        channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel=MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if(App.prefs.isLoggedIn){
            AuthService.findUserByEmail(this){}
        }

        //to jest odpowiedzialne za to co sie stanie jak klikniemy w jakas opcje z tego menu co sie wysuwa od lewej
        /*nav_view.setNavigationItemSelectedListener(this)*/

    }

    override fun onResume() {
        //Odbiornik broadcasta
        //IntentFIlter bedzie mowil jakiego typu Intenta ma szukac/oczekiwac
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataCahngeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))
        super.onResume()
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataCahngeReceiver)
        super.onDestroy()
    }

    //BROADCAST RECEIVER
    private val userDataCahngeReceiver=object:BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            //co sie ma zadziac jak sygnal zostanie odebrany
            //AuthService to singleton ktorego stworzylismy a isLoggedIn jest jego czescia
            if(App.prefs.isLoggedIn){
                //jesli jest zalogowany uzytkownik to podmieniamy jego logo nazwe itp
                userNameNavHeader.text=UserDataService.name
                userEmailNavHeader.text=UserDataService.email
                val resourceId=resources.getIdentifier(UserDataService.avatarName,"drawable",packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text="Logout"

                MessageService.getChannels{complete->
                    if(complete){
                        if(MessageService.channels.count()>0){
                            selectedChannel=MessageService.channels[0]
                            //przeładowanie danych bo na poczatku sa puste
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }

                    }
                }
            }
        }
    }

    fun updateWithChannel(){
        mainChannelName.text="#${selectedChannel?.name}"
        //download messages for channel
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

        if(App.prefs.isLoggedIn){
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
        if(App.prefs.isLoggedIn) {
            //nasza fabryka ktora stworzy AlertDialoga
            val builder=AlertDialog.Builder(this)
            //dajemy naszego layouta przez nas zrobionego
            val dialogView=layoutInflater.inflate(R.layout.add_channel_dialog,null)
            //nasz dialog ma miec layout taki jak podalim wczesniej
            builder.setView(dialogView)
                .setPositiveButton("Add"){_, _ ->
                    //Tutaj okreslimy co ma sie stac po nacisnieciu klawisza pozytywnego ze cos akceptujemy

                    //Nie ma bezposredniego dostepu do tych pol textowych w tym layoucie co zobilismy
                    //dlatego trezba zrobic uchwyty do trzymania pyty
                    val nameTextField=dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                    val descTextField=dialogView.findViewById<EditText>(R.id.addChannelDescTxt)
                    //za pomoca uchwytow wyjmujemy z nich to co jest w nich wpisane i dajemy to do zmiennych
                    val channelName=nameTextField.text.toString()

                    val channelDesc=descTextField.text.toString()

                    //Teraz tworzymy chanela z nazwa i opsiem ze zmiennych
                    //tworzymy emita(wysylamy informacje od klienta do serwera) ta nazwa newChannel jest wzieta z kodu API
                    //emit polega na wysylaniu info a socket.on na odbieraniu (nie wazne po ktorej stronie)
                    //czyli emity i ony sa wykorzystywane do komunikacji w socketach webowych
                    socket.emit("newChannel",channelName,channelDesc)

                }
                .setNegativeButton("Cancel"){_, _ ->
                    //tu okreslimy co ma sie dziac jak klikniemy na negatywny button

                }
                .show()
        }
    }

    private val onNewChannel=Emitter.Listener {args ->
        //trzeba rzutowac elementy tej tablicy args poniewaz sa one typu ogolnego jak object w c#
        //println(args[0] as String)
        //Uruchamiamy watek w tle
        //zeby apka nie mulila niech ciezkie polaczenia z baza i obliczenia dzieja sie w tle zeby nie zamrozić apki
        runOnUiThread {
            //tworzymy zmienne z atrybutami dla nowego kanalu
           val channelName=args[0] as String
            val channelDescription=args[1] as String
            val channelId=args[2] as String
            //tworzymy nowy kanal
            val newChannel=Channel(channelName,channelDescription,channelId)
            //wysylamy go do tablicy kanalow w messageService
            MessageService.channels.add(newChannel)
            //println(newChannel.name)
            //println(newChannel.description)
            //println(newChannel.id)
            channelAdapter.notifyDataSetChanged()
        }
    }

    fun sendMsgBtnClicked(view:View){
        hideKeyboard()
    }

    fun hideKeyboard(){
        val inputManager=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken,0)
        }
    }

}

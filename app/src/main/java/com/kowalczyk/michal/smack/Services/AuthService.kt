package com.kowalczyk.michal.smack.Services

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kowalczyk.michal.smack.Utilities.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    //zeby robic requesty trzeba w build.gradle dodac BIBLIOTEKE VOLLEY!!!!
    //zeby robic requesty trzeba tez zezwolic apce na dostep do neta i to trzeba zrobic w Android Manifest
    //to jest w android manifest jako uses permission i podajemy tam internet

    //zmienne do dzialania programu nedded
    var isLoggedIn=false
    var userEmail=""
    var authToken=""

    //ten argument complete to Question Handler i ten question handler musi byc lambdą
    //ten argument complete bedzie nam okreslał czy rejestracja sie udala czy nie
    fun registerUser(context: Context, email:String,password:String,complete:(Boolean)->Unit){

        //JSON BODY bo ten request jest typu POST i chcemy w jego body cos przeslac na serwer(dane rejestracji)
        //a wiec musimy utworzyc JSON BODY

        //tworzymy obiekt JSONOWY
        val jsonBody=JSONObject()
        //a teraz dodajemy do niego pary klucz wartosc
        //dodajemy email ktory chcemy wyslac jako pare klucz="email"(klucz zawsze jest stringiem)i wartosc email(zmienna typu String)
        jsonBody.put("email",email)
        //to samo z hasłem
        jsonBody.put("password",password)
        //oczywiscie jako wartosci moga byc stringi,liczby,booleany czy nawet inne obiekty jsona

        //JSON BODY trzeba na koniec przekonwertowac do stringa metoda toString()
        // bo pozniej bedziemy to przekonwertowywac na byteArray
        //dlatego ze obiekty przesylane JSONem sa byteArrayami
        val requestBody=jsonBody.toString()

        //tworzymy obiekt Requesta ktory bedzie zwracac stringa z odpowiedzia czy sie powiodlo czy nie
        //jako argumenty do tego reguesta podajemy kolejno:
        //1.rodzaj metody w naszym przypdaku POST(sa jeszcze GET,PUT,DELETE i moze jeszcze jakies ale nie wiem)
        //2.adres miejsca na serwie gdzie jest kod API ktory to obsluguje, w android studio robi sie kotlin file COnstants i tam umieszcza sie te wszystkie adresy
        //ten adres jest w tym pliku Constants
        //3.Response z biblioteki vollej zeby okreslic co bedzie jak sie uda ten request a co jak sie nie uda request
        //trzeba dodac Response.Listener zeby obsluzyl jak sie uda i Response.ErrorListener zeby obsluzyl jak sie nei uda
        //wybieramy ten rodzaj listeera z metoda lambda bo tak sobie przesylamy w naszej glownej funkcji
        val registerRequest=object :StringRequest(Method.POST, URL_REGISTER,Response.Listener {response ->
            //wszystko sie udalo i mozemy operowac na obiekcie ktory dostalismy w odpowiedzi od serwera
            //tutaj dzieja sie rzeczy ktora maja nastapic jak sie uda request
            println(response)
            //a do complete(nasza funkcja lambda) dajemy true bo się udal request
            complete(true)
        },Response.ErrorListener {error ->
            //teraz dodajemy do logów errora ktory nastapil bo sie nie udal request :(
            //funkcja d.(tag_errora,wiadomosc errora)
            Log.d("ERROR","Could not register user: $error")
            //nadajemy funkcji lambda wartosc false bo sie request nie udal
            complete(false)
        }){
            //specyfikujemy BODY JSONA m.inn ze jest kodowany utf-8
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            //to co wysylamy czyli requestBody przesylamy ale przekonwertowane na ByteArray bo tak Json to przesyla na serwa
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        //linijke wyzej specyfikujemy body jsona i dodajemy je

        //teraz dodajemy to do kolejki Volleya zeby to wyslal
        //tworzymy obiekt volley tworzymy nowa kolejke requestow i przesylamy jej context a na koniec dodajemy naszego requesta
        Volley.newRequestQueue(context).add(registerRequest)
        //teraz to podepniemy obsluzymy pod przyciskiem do rejestracji usera
    }

    fun loginUser(context: Context,email:String,password:String,complete: (Boolean) -> Unit){

        val jsonBody=JSONObject()
        jsonBody.put("email",email)
        jsonBody.put("password",password)

        val requestBody=jsonBody.toString()

        val loginRequest=object:JsonObjectRequest(Method.POST, URL_LOGIN,null,Response.Listener {response ->
            //this is where we parse json object
            //wszystko sie udalo i mozemy operowac na obiekcie ktory dostalismy w odpowiedzi od serwera
            //JESLI SIE UDAL REQUEST to z tego obiektu stworzonego [response] pobieramy dane typu email ten token itp
            //isLooged ustawiamy na true oraz complete ustawiamy na true bo sie request udal
            //metod getString rzuca wyjątki typu JSONException dlatego trzeba ja zrobic w bloku try catch
            //w getString nazwy tych parametrow name sa brane z odpowiedzi tej metody POST i w POSTMANIE zostalo sprawdzone
            //ze email sie tam nazywa user a token to token
            try {
                userEmail=response.getString("user")
                //pobranie wartosci tokena autentykacji
                authToken=response.getString("token")
                isLoggedIn=true
                complete(true)
            }catch (e:JSONException){
                Log.d("JSON","EXC:"+e.localizedMessage)
                complete(false)
            }
        },Response.ErrorListener {error->
            //this is where we deal with our error
            //BLEDY STATUS CODES
            //jesli sa 400 cos to znaczy ze zrabales cos w kodzie najpradopodobniej
            //jesli 500 cos to blad po stronie serwa czyli z kodem API(zazwyczaj wystarczy restart)

            //funkcja d.(tag_errora,wiadomosc errora)
            Log.d("ERROR","Could not login user: $error")
            //nadajemy funkcji lambda wartosc false bo sie request nie udal
            complete(false)

        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

        }

        //jak juz stowrzylismy jedna queve (kolejke) volleya to nie tworzmy znowu nowej tylko korzystajmy juz z tej stworzonej
        //bo inaczej mozemy doporowadzic do wyciekow pamieci
        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(context: Context,name:String,email:String,avatarName:String,avatarColor:String,complete: (Boolean) -> Unit){

        //to co jest potrzebne do ciała jsonowego ustalamy w api mozna to podejzec w postmanie
        //kolejnosc podawania tych argumentow do ciala jest wazna i musi byc taka sama jak ustalilsmy w api
        val jsonBody=JSONObject()
        jsonBody.put("name",name)
        jsonBody.put("email",email)
        jsonBody.put("avatarName",avatarName)
        jsonBody.put("avatarColor",avatarColor)
        val requestBody=jsonBody.toString()

        val createRequest=object :JsonObjectRequest(Method.POST, URL_CREATE_USER,null,Response.Listener {response ->
            //wszystko sie udalo i mozemy operowac na obiekcie ktory dostalismy w odpowiedzi od serwera

            //tutaj operujemy na tym obiekcie ktory dostalismy w odpowiedzi i zapisujemy sobie wartosci z niego na pozniej
            //obiekt odpowiedzi nazywa sie response
            try{

                UserDataService.name=response.getString("name")
                UserDataService.email=response.getString("email")
                UserDataService.avatarName=response.getString("avatarName")
                UserDataService.avatarColor=response.getString("avatarColor")
                UserDataService.id=response.getString("_id")
                complete(true)
            }catch (e:JSONException){
                Log.d("JSON","EXC "+e.localizedMessage)
                complete(false)
            }


        },Response.ErrorListener {error->
            //NIE UDALO SIE :<
            //funkcja d.(tag_errora,wiadomosc errora)
            Log.d("ERROR","Could not add user: $error")
            //nadajemy funkcji lambda wartosc false bo sie request nie udal
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            //w celu autoryzacji , zwiazane z tymi tokenami musimy przeciazyc metode getHeader
            override fun getHeaders(): MutableMap<String, String> {
                //tworzymy haszmape do ktorej wsadzimy pare z headera requesta a mianowice Authorization Value
               val headers=HashMap<String,String>()
                //to co przesylamy jest wziete z naglowka zapytania api CreateUser
                headers.put("Authorization","Bearer $authToken")

                return headers
            }
        }
        Volley.newRequestQueue(context).add(createRequest)

    }

    //To jest request typu GET nie potrzeba JSON BODY bo nic nie wysylamy na serwa tylko z niego pobieramy
    fun findUserByEmail(context: Context,complete: (Boolean) -> Unit){
        val findUserRequest=object:JsonObjectRequest(Method.GET,"$URL_GET_USER$userEmail",null,Response.Listener {response->

            try{
                UserDataService.name=response.getString("name")
                UserDataService.email=response.getString("email")
                UserDataService.avatarName=response.getString("avatarName")
                UserDataService.avatarColor=response.getString("avatarColor")
                UserDataService.id=response.getString("_id")

                //pobrane dane wysylamy broadcastem do innych zeby poinformowac ze ktos sie zalogowal w apce
                val userDataChange=Intent(BROADCAST_USER_DATA_CHANGE)
                LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                complete(true)

            }catch(e:JSONException){
                Log.d("JSON","EXC: "+ e.localizedMessage)
            }


        },Response.ErrorListener {error ->
            Log.d("ERROR","Could not find user")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            //w celu autoryzacji , zwiazane z tymi tokenami musimy przeciazyc metode getHeader
            override fun getHeaders(): MutableMap<String, String> {
                //tworzymy haszmape do ktorej wsadzimy pare z headera requesta a mianowice Authorization Value
                val headers=HashMap<String,String>()
                //to co przesylamy jest wziete z naglowka zapytania api CreateUser
                headers.put("Authorization","Bearer $authToken")

                return headers
            }
        }

        Volley.newRequestQueue(context).add(findUserRequest)
    }


}
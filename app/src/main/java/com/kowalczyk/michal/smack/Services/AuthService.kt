package com.kowalczyk.michal.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kowalczyk.michal.smack.Utilities.URL_REGISTER
import org.json.JSONObject

object AuthService {

    //zeby robic requesty trzeba w build.gradle dodac BIBLIOTEKE VOLLEY!!!!
    //zeby robic requesty trzeba tez zezwolic apce na dostep do neta i to trzeba zrobic w Android Manifest
    //to jest w android manifest jako uses permission i podajemy tam internet

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
}
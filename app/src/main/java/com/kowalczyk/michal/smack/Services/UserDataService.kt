package com.kowalczyk.michal.smack.Services

import android.graphics.Color
import com.kowalczyk.michal.smack.Controller.App
import java.util.*

object UserDataService {
    var id=""
    var avatarColor=""
    var avatarName=""
    var email=""
    var name=""


    fun logout(){
        id=""
        avatarColor=""
        avatarName=""
        email=""
        name=""
        App.prefs.authToken=""
        App.prefs.userEmail=""
        App.prefs.isLoggedIn=false
    }


    fun returnAvatarColor(components:String):Int{

        //[0.4980392156862745, 0.596078431372549, 0.8901960784313725, 1]

        //musimy pozbyc sie nawiasow i przecinkow

        // 0.4980392156862745 0.596078431372549 0.8901960784313725 1

        val strippedColor=components
            .replace("[","")
            .replace("]","")
            .replace(",","")

        var r=0
        var g=0
        var b=0

        //tworzymy skaner ktory posluzy do tego zeby w tym stringu znalazl i wycial liczby typu double
        val scanner=Scanner(strippedColor)

        if(scanner.hasNext()){
            //pobiermay wartosc ze stringa a nastepniemy mnozymy przez 255 zeby otrzymac liczbe z zakresu od 0 do 255 i rzutujemy na int
            r=(scanner.nextDouble()*255).toInt()
            g=(scanner.nextDouble()*255).toInt()
            b=(scanner.nextDouble()*255).toInt()
        }

        //Color jest w rzeczywistosci Intem
        return Color.rgb(r,g,b)
    }

}
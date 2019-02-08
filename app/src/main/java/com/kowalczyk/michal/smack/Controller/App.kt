package com.kowalczyk.michal.smack.Controller

import android.app.Application
import com.kowalczyk.michal.smack.Utilities.SharedPrefs

//ta klasa jest uruchamiana przed wszytskim innym!
class App:Application() {

    //companion object jest troche jak singleton tylko ze w srodku specyficznej klasy
    //bedzie tylko jedna instancja tych sharedPreferences
    companion object {
        lateinit var prefs:SharedPrefs
    }

    override fun onCreate() {
        prefs=SharedPrefs(applicationContext)
        super.onCreate()
    }
}
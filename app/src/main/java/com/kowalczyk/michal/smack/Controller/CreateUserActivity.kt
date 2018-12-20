package com.kowalczyk.michal.smack.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.kowalczyk.michal.smack.R
import kotlinx.android.synthetic.main.activity_create_user.*

class CreateUserActivity : AppCompatActivity() {

    //taka sama nazwa jak tej zmiennej jest nazwa tego defaultowego obrazka dla avatara
    //wiec jak ktos nie wybierze sobie innego obrazka to bedzie mial ten defaultowy
    var userAvatar="profileDefault"

    //apka z tego kursu jest tez zrobiona na ios i maca dlatego kolor jest podany w takim formacie
    //w androidze normalnie format dziala taki ze kolory sa od 0 do 255
    var avatarColor="[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateUserAvatar(view:View){
        val random=java.util.Random()
        //generuje randomowe liczby od 0 do 1
        val color=random.nextInt(2)
        //losuje liczbe od 0 do 27
        val avatar=random.nextInt(28)

        if(color==0){
            userAvatar="light$avatar"
        }else{
            userAvatar="dark$avatar"
        }
        //zebysmy mogli wyswietliv w imageview ten obrazek tworzymy resourceId
        //wywolujemy resources.getIdentifier(nazwa_tego_zasobu,jego_typ,nazwa_paczki)
        val resourceId=resources.getIdentifier(userAvatar,"drawable",packageName)
        //ustwaiamy dla tego imageView o id createAvatarImageView imageresource na ten resourceId ktory ustalilismy
        createAvatarImageView.setImageResource(resourceId)

    }

    fun generateColorClicked(view:View){
        val random=java.util.Random()
        //randomowe liczby dla poszczegolnych kolorow
        val r=random.nextInt(255)
        val g=random.nextInt(255)
        val b=random.nextInt(255)

        //ustawienie koloru tla dla tego imageView naszego
        createAvatarImageView.setBackgroundColor(Color.rgb(r,g,b))

        //tutaj przerabiamy to na ta skale od 0 do 1 co pasuje do Ios i maca
        val savedR=r.toDouble()/255
        val savedG=g.toDouble()/255
        val savedB=b.toDouble()/255

        //ustawiamy ta zmienne i w tej formie dane o kolorze bede przesylane dalej
        avatarColor="[$savedR, $savedG, $savedB, 1]"
    }

    fun createUserClicked(view:View){

    }
}

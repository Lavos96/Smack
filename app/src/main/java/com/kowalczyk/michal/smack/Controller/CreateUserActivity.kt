package com.kowalczyk.michal.smack.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.kowalczyk.michal.smack.R
import com.kowalczyk.michal.smack.Services.AuthService
import com.kowalczyk.michal.smack.Services.UserDataService
import com.kowalczyk.michal.smack.Utilities.BROADCAST_USER_DATA_CHANGE
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
        createSpinner.visibility=View.INVISIBLE
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
        //ostatni parametr complete ktory jest lambdą dodajemy poprzez dodanie na koncu tych nawiasow {}
        enableSpinner(true)
        val userName=createUserNameText.text.toString()
        val email=createEmailText.text.toString()
        val password=createPasswordText.text.toString()

        if(userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){

            AuthService.registerUser(email,password){regiserSucces->
                if(regiserSucces){
                    AuthService.loginUser(email,password){loginSucces->
                        if(loginSucces){
                            AuthService.createUser(userName,email,userAvatar,avatarColor){createSucces->
                                if(createSucces){
                                    //ustawiamy broadcasta ktory zacznie rozsylac sygnal (intent) i te activity ktore sa ustawiaone na nasluchiwanie
                                    //sie odezwa i odbiorą tego intenta [dziala to tak jak stacja radiowa ktory rozsyla sygnal i jesli radio jest ustawione na
                                    // ta stacje to zacznie odbierac od niej sygnal]
                                    val userDataChange=Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                    //koniec tworzenia broadcasta

                                    enableSpinner(false)
                                    finish()
                                }else{
                                    errorToast()
                                }
                            }
                        }else{
                            errorToast()
                        }
                    }
                }else{
                    errorToast()
                }
            }
        }else{
            Toast.makeText(this,"Make sure user name, email, password are filled in.",Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }
    }

    fun errorToast(){
        Toast.makeText(this,"Something went wrong, please try again.",Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean){
        if(enable){
            createSpinner.visibility=View.VISIBLE
            createUserBtn.isEnabled=false
            createAvatarImageView.isEnabled=false
            backgroundColorBtn.isEnabled=false
        }else{
            createSpinner.visibility=View.INVISIBLE
            createUserBtn.isEnabled=true
            createAvatarImageView.isEnabled=true
            backgroundColorBtn.isEnabled=true
        }
    }
}

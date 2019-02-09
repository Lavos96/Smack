package com.kowalczyk.michal.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.kowalczyk.michal.smack.Controller.App
import com.kowalczyk.michal.smack.Model.Channel
import com.kowalczyk.michal.smack.Model.Message
import com.kowalczyk.michal.smack.Utilities.URL_GET_CHANNELS
import com.kowalczyk.michal.smack.Utilities.URL_GET_MESSAGES
import okhttp3.Response
import org.json.JSONException

object MessageService {
    val channels=ArrayList<Channel>()
    val messages=ArrayList<Message>()

    fun getChannels(complete: (Boolean)->Unit){

        //zapytanie jsona o tablice kanałów
        val channelsRequest=object:JsonArrayRequest(Method.GET, URL_GET_CHANNELS,null,com.android.volley.Response.Listener{response->

            try{

                //petla ma zmienna x od zera do dlugosci odpowiedzi(czyli tej tablicy odpowiedzi bo typ JSONREQUESTA TO ARRAY)
                for(x in 0 until response.length()){
                    //bieremy pojedynczy obiekt z tej tablicy
                    val channel=response.getJSONObject(x)
                    val chanName=channel.getString("name")
                    val chanDesc=channel.getString("description")
                    val chanId=channel.getString("_id")

                    val newChannel=Channel(chanName,chanDesc,chanId)
                    this.channels.add(newChannel)
                }
                complete(true)
            }catch (e:JSONException){
                Log.d("JSON","EXC:"+e.localizedMessage)
                complete(false)
            }

        }, com.android.volley.Response.ErrorListener{error ->
            Log.d("ERROR","Could not retrieve channels")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>();
                headers.put("Authorization","Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelId:String,complete: (Boolean) -> Unit){
        val url="$URL_GET_MESSAGES$channelId"

        val messagesRequest=object:JsonArrayRequest(Method.GET,url,null,com.android.volley.Response.Listener {response ->
        clearMessages()
            try {
                for(x in 0 until response.length()){
                    val message=response.getJSONObject(x)
                    val messageBody=message.getString("messageBody")
                    val channelId=message.getString("channelId")
                    val id=message.getString("_id")
                    val userName=message.getString("userName")
                    val userAvatar=message.getString("userAvatar")
                    val userAvatarColor=message.getString("userAvatarColor")
                    val timeStamp=message.getString("timeStamp")

                    val newMessage=Message(messageBody,userName,channelId,userAvatar,userAvatarColor,id,timeStamp)
                    this.messages.add(newMessage)
                }
                complete(true)
            }catch (e:JSONException){
                Log.d("JSON","EXC:"+e.localizedMessage)
                complete(false)
            }

        },com.android.volley.Response.ErrorListener {
            Log.d("ERROR","Could not retrieve channels")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers=HashMap<String,String>();
                headers.put("Authorization","Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(messagesRequest)
    }

    fun clearMessages(){
        messages.clear()
    }

    fun clearChannels(){
        channels.clear()
    }

}
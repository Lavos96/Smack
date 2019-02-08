package com.kowalczyk.michal.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.kowalczyk.michal.smack.Model.Channel
import com.kowalczyk.michal.smack.Utilities.URL_GET_CHANNELS
import okhttp3.Response
import org.json.JSONException

object MessageService {
    val channels=ArrayList<Channel>()

    fun getChannels(context: Context,complete: (Boolean)->Unit){

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
                headers.put("Authorization","Bearer ${AuthService.authToken}")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(channelsRequest)
    }

}
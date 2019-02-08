package com.kowalczyk.michal.smack.Utilities

//to jest adres z heroku
const val BASE_URL="https://chattychatlavos.herokuapp.com/v1/"

const val SOCKET_URL="https://chattychatlavos.herokuapp.com/"
//jesli chcelibysmy pracowac lokalnie to ten adres ponizej
//const val BASE_URL="https//10.0.2.2:3005/v1/"
//a tutaj sa poszczegolne usługi
const val URL_REGISTER="${BASE_URL}account/register"
const val URL_LOGIN="${BASE_URL}account/login"
const val URL_CREATE_USER="${BASE_URL}user/add"
const val URL_GET_USER="${BASE_URL}user/byEmail/"
const val URL_GET_CHANNELS="${BASE_URL}channel/"
//BROADCAST constants
const val BROADCAST_USER_DATA_CHANGE="BROADCAST_USER_DATA_CHANGE"

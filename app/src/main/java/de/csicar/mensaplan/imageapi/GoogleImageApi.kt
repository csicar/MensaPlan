package de.csicar.mensaplan.imageapi

import java.net.URLEncoder

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import java.net.URL

class GoogleImageApi(val cx: String, val key: String) {


    fun getRequestUrl(searchText: String): String {
        val query = listOf(
                "q" to searchText,
                "type" to "image",
                "key" to key,
                "cx" to cx
        ).filter {
            it.second != ""
        }.map {
            URLEncoder.encode(it.first) to URLEncoder.encode(it.second)
        }.map { "${it.first}=${it.second}" }.joinToString("&")
        return "https://www.googleapis.com/customsearch/v1?$query"
    }

    fun fetch(context: Context, search: String, listener: Response.Listener<URL>, errorListener: Response.ErrorListener) {
        val queue = Volley.newRequestQueue(context)
        val shortenedSearch = search
        val stringRequest = GoogleImageRequest(Request.Method.GET, getRequestUrl(shortenedSearch),
                Response.Listener {
                    listener.onResponse(it)
                },
                errorListener)
        queue.add(stringRequest)
    }
}



package de.csicar.mensaplan.imageapi

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONObject
import java.net.URL

class GoogleImageRequest(method: Int, url: String, val listener: Response.Listener<URL>, errorListener: Response.ErrorListener) : Request<URL>(method, url, errorListener) {
    override fun parseNetworkResponse(response: NetworkResponse): Response<URL> {
        val parsed = String(response.data)
        val search = JSONObject(parsed)

        val url = extractUrl(search)
        if (url == null) {
            Log.v("asdasd url not found", search.toString())
            return Response.error(VolleyError(response))
        }
        return Response.success(url, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: URL?) {
        listener.onResponse(response)
    }

    fun extractUrl(json: JSONObject): URL? {
        val items = json.optJSONArray("items") ?: return null
        for (i in 0..items.length()) {
            val item = items.optJSONObject(i)
            if (item != null) {
                val srcCandidate = extractUrlFromItem(item)
                if (srcCandidate != null)
                    return srcCandidate
            }
        }
        return null
    }

    private fun extractUrlFromItem(item: JSONObject): URL? {
        val pagemap = item.optJSONObject("pagemap") ?: return null
        val cseImage = pagemap.optJSONArray("cse_image")
                ?: pagemap.optJSONArray("cse_thumbnail")
                ?: return null
        val firstImage = cseImage.optJSONObject(0)
                ?: return null
        val src = firstImage.optString("src") ?: null


        return URL(src)
    }
}
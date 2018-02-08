package de.csicar.mensaplan

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by csicar on 06.02.18.
 */
class CanteenRequest(method: Int, url: String?, val listener: Response.Listener<List<Canteen>>, errorListener: Response.ErrorListener?)
    : Request<List<Canteen>>(method, url, errorListener) {


    override fun getHeaders(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>("Authorization" to "Basic anNvbmFwaTpBaFZhaTZPb0NoM1F1b282amk=")
        return map
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<List<Canteen>> {
        val parsed = String(response.data)
        val canteens = JSONObject(parsed)
        val parsedCanteen = Canteen.canteensFromJson(canteens)
        return Response.success(parsedCanteen, HttpHeaderParser.parseCacheHeaders(response))
    }


    override fun deliverResponse(response: List<Canteen>?) {
        listener.onResponse(response)
    }
}
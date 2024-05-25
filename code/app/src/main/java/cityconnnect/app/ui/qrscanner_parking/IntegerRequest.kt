package cityconnnect.app.ui.qrscanner_parking
import com.android.volley.NetworkResponse

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException

class IntegerRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<Int>,
    errorListener: Response.ErrorListener
) : Request<Int>(method, url, errorListener) {

    override fun parseNetworkResponse(response: NetworkResponse?): Response<Int> {
        return try {
            val data = response?.data ?: byteArrayOf()
            val resultString = String(data, charset("UTF-8")) // Explicitly specify the charset
            val result = resultString.toIntOrNull()
            if (result != null) {
                Response.success(result, HttpHeaderParser.parseCacheHeaders(response))
            } else {
                Response.error(VolleyError("Response is not a valid integer"))
            }
        } catch (e: UnsupportedEncodingException) {
            Response.error(VolleyError(e))
        }
    }

    override fun deliverResponse(response: Int) {
        listener.onResponse(response)
    }
}

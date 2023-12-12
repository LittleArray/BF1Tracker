package api

import com.google.gson.Gson
import data.CurrentGamingServer
import data.GatewayPID
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Api {
    val okHttpClient = OkHttpClient()
    var serverUrl = "http://ipv6.ffshaozi.top:8080"
    val logger: Logger = LoggerFactory.getLogger("Api")


    fun getNowPlay(pid: Long): String? {
        val request = Request.Builder()
            .url("${serverUrl}/gateway/nowPlay/$pid")
            .get()
            .build()
        val response = try {
            okHttpClient.newCall(request).execute()
        } catch (e: Exception) {
            logger.error("网路异常 \n"+e.stackTraceToString())
            null
        }
        return if (response?.isSuccessful == true) {
            response.body.string()
        } else {
            null
        }
    }

    fun getUser(id: String): String? {
        val request = Request.Builder()
            .url("${serverUrl}/gateway/getUser/$id")
            .get()
            .build()
        val response = try {
            okHttpClient.newCall(request).execute()
        } catch (e: Exception) {
            logger.error("网路异常 \n"+e.stackTraceToString())
            null
        }
        return if (response?.isSuccessful == true) {
            response.body.string()
        } else {
            null
        }
    }

    inline fun <reified T : Any> fromJson(data: String): T? {
        return try {
            Gson().fromJson(data, T::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
package ro.pub.cs.systems.eim.practicaltest02v6.network

import kotlin.text.get

import android.util.Log

import java.net.Socket
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import ro.pub.cs.systems.eim.practicaltest02v6.general.Constants
import ro.pub.cs.systems.eim.practicaltest02v6.general.CurrencyInformation
import ro.pub.cs.systems.eim.practicaltest02v6.general.Utilities

import java.io.IOException

// Primesc ServerThread ca sa accesez constructorul!!
class CommunicationThread(private val serverThread: ServerThread, private val socket: Socket) : Thread() {
    override fun run() {
        try {
            // obtinem fluxurile de intrare/iesire
            val requestReader = Utilities.getReader(socket)
            val responseWriter = Utilities.getWriter(socket)

            // Citim cererea de la client (Orasul si tipul informatiei)
            val currency = requestReader.readLine()


            if (currency.isNullOrEmpty()) {
                Log.e(Constants.TAG, "Error receiving parameters from client")
                return
            }

            // Verificam cache ul (Hashmap din threadul ServerThread)
            var data = serverThread.getData()[currency]

            if (data == null) {
                Log.d(Constants.TAG, "Currency Not in cache")
            }

            // Descarc cu http daca nu am date, cu OkHttp
            val client = OkHttpClient()
            //var url : String
            var url : String
            if (currency == "BTC-USD") {
                 url = "https://data-api.coindesk.com/index/cc/v1/latest/tick?market=cadli&instruments=BTC-USD"
            } else {
                url = "https://data-api.coindesk.com/index/cc/v1/latest/tick?market=cadli&instruments=BTC-EUR"
            }


            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (responseBody != null) {
                // parsez json ul primit de la openweather
                Log.d(Constants.TAG, responseBody)
                val content = JSONObject(responseBody)

                val main = content.getJSONObject("Data")
                var field1 : JSONObject
                if (currency == "BTC-USD") {
                     field1 = main.getJSONObject("BTC-USD")
                } else {
                     field1 = main.getJSONObject("BTC-EUR")
                }
                val value = field1.getDouble("VALUE")





                // Creez obiectul cu informatiile pe care le am si salvez in Cache
                val currencyInfo = CurrencyInformation(value.toString())

                serverThread.setData(currency, currencyInfo)
                data = currencyInfo //variabila locala??


            } else {
                Log.d(Constants.TAG, "Data already found in cache")
            }

            // Trimit raspunsul inapoi cu tot cu eticheta
            if (data != null) {
                val result = data.toString()

                responseWriter.println(result)
            }


        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "Data processing error: " + ioException.message)
        } catch (jsonException: JSONException) {
            Log.e(Constants.TAG, "JSON parsing error: " + jsonException.message)
        } finally {
            try {
                socket.close()
            } catch (ioException: IOException) {
                Log.e(Constants.TAG, "Error closing socket: " + ioException.message)
            }
        }
    }
}
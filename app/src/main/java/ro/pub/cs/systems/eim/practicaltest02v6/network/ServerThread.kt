package ro.pub.cs.systems.eim.practicaltest02v6.network

import android.util.Log
import ro.pub.cs.systems.eim.practicaltest02v6.general.Constants
import ro.pub.cs.systems.eim.practicaltest02v6.general.CurrencyInformation
import java.io.IOException
import java.net.ServerSocket

class ServerThread(private val port: Int) : Thread() {
    private var serverSocket: ServerSocket? = null

    // AM NEVOIE DE CACHE LOCAL ( ca sa dau retrieve din el la nevoie)
    private val data = HashMap<String, CurrencyInformation>()

    fun startServer() {
        start()
        Log.d(Constants.TAG, "Serverul a pornit pe portul $port")
    }

    fun stopServer() {
        try {
            serverSocket?.close()
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: ${ioException.message}")
        }
    }

    @Synchronized
    fun getData(): HashMap<String, CurrencyInformation> {
        return this.data
    }
    @Synchronized
    fun setData(currency: String, currencyInfo: CurrencyInformation) {
        this.data[currency] = currencyInfo
    }

    override fun run() {
        try {
            // Deschid socket ul pe portul primit in constructor
            serverSocket = ServerSocket(port)

            while (!Thread.currentThread().isInterrupted) {
                Log.d(Constants.TAG, "Waiting for clients")
                val socket = serverSocket!!.accept()

                // Init la Communication thread, dau socket ul si o referinta inapoi la serverthread
                val communicationThread = CommunicationThread(this, socket)
                communicationThread.start()
            }
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.message)
        }
    }




}

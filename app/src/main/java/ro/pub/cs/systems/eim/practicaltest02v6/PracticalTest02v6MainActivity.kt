package ro.pub.cs.systems.eim.practicaltest02v6

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ro.pub.cs.systems.eim.practicaltest02v6.general.Constants
import ro.pub.cs.systems.eim.practicaltest02v6.network.ClientThread
import ro.pub.cs.systems.eim.practicaltest02v6.network.ServerThread

class PracticalTest02v6MainActivity : AppCompatActivity() {

    private lateinit var serverPortEditText: EditText
    private lateinit var serverConnectButton: Button
    private lateinit var clientAddressEditText: EditText
    private lateinit var clientPortEditText: EditText
    private lateinit var currencySpinner: Spinner
    private lateinit var clientGetCurrency: Button
    private lateinit var clientResultTextView: TextView

    private var serverThread: ServerThread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test02v6_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        serverPortEditText = findViewById(R.id.server_port_edit_text)
        serverConnectButton = findViewById(R.id.server_connect_button)

        clientAddressEditText = findViewById(R.id.client_address_edit_text)
        clientPortEditText = findViewById(R.id.client_port_edit_text)
        currencySpinner = findViewById(R.id.currency_spinner)
        clientGetCurrency = findViewById(R.id.client_get_currency)
        clientResultTextView = findViewById(R.id.client_result_text_view)

        // -----------------------------------------------------------------------
        // LOGICA PENTRU SERVER (Partea de sus a ecranului)
        // -----------------------------------------------------------------------
        serverConnectButton.setOnClickListener {
            val serverPort = serverPortEditText.text.toString()
            if (serverPort.isEmpty()) {
                Toast.makeText(this@PracticalTest02v6MainActivity, "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pornim serverul daca nu ruleaza deja
            if (serverThread == null || !serverThread!!.isAlive) {
                serverThread = ServerThread(serverPort.toInt())
                serverThread!!.startServer()
                Toast.makeText(this, "Server pornit pe portul $serverPort", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Serverul este deja pornit!", Toast.LENGTH_SHORT).show()
            }
        }
        // -----------------------------------------------------------------------
        // LOGICA PENTRU CLIENT (Partea de jos a ecranului)
        // -----------------------------------------------------------------------
        clientGetCurrency.setOnClickListener {
            val clientAddress = clientAddressEditText.text.toString()
            val clientPort = clientPortEditText.text.toString()
            val currency = currencySpinner.selectedItem.toString()

            // Sa nu crape aplicatia daca e gol ceva:

            if (clientAddress.isEmpty() || clientPort.isEmpty() ||currency .isEmpty()) {
                Toast.makeText(this, "Completeaza toate campurile de la Client!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            clientResultTextView.text = ""

            val clientThread = ClientThread(
                clientAddress,
                clientPort.toInt(),
                currency,

                clientResultTextView
            )
            clientThread.start()
        }
    }
    override fun onDestroy() {
        Log.d(Constants.TAG, "OPTIMS SERVERUL!")
        serverThread?.stopServer()
        super.onDestroy()

    }

}
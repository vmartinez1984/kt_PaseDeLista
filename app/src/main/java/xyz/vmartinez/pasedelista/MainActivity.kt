package xyz.vmartinez.pasedelista

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import xyz.vmartinez.pasedelista.activities.MenuActivity
import xyz.vmartinez.pasedelista.models.InicioDeSesionModel
import xyz.vmartinez.pasedelista.services.AsistenciaService
//Agregar una cliente web en la consola de ggogle

//Para la sacar la firma sha1 en la ventana de comandos den android studio ejecutar
//./gradlew signingReport
// Esto va en la consola de ggogle en la sección de clientes cliente android
class MainActivity : AppCompatActivity() {

    lateinit var editTextCorreo: EditText
    lateinit var editTextContrasenia: EditText

    lateinit var service: AsistenciaService

    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editTextCorreo = findViewById<EditText>(R.id.editTextCorreo)
        editTextContrasenia = findViewById<EditText>(R.id.editTextContrasenia)

        service = AsistenciaService()
        credentialManager = CredentialManager.create(this)
    }

    fun iniciarSesion(view: View){
        val correo = editTextCorreo.text.toString()
        val contrasenia = editTextContrasenia.text.toString()
        val token = service.iniciarSesion(InicioDeSesionModel(correo, contrasenia))
        if(token.token.isNullOrEmpty()){
            Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_LONG).show()
        }else{
            val intent = Intent(this, MenuActivity::class.java)
            //Pasar los datos del usuario
            startActivity(intent)
        }
    }

    fun iniciarSesionConGoogle(view: View){
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(
                //"979145343189-jqpnek26lsr0d706f47nlhvs5dd4ou6s.apps.googleusercontent.com"
                "748887728401-gn4hu66dn3nl6ap1d9dusbfm3a9tpp2k.apps.googleusercontent.com"
            )
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {

            try {

                val result = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = request
                )

                val credential = result.credential

                if (
                    credential is CustomCredential &&
                    credential.type ==
                    GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {

                    val googleCredential =
                        GoogleIdTokenCredential.createFrom(
                            credential.data
                        )

                    val idToken = googleCredential.idToken

                    Log.d("GOOGLE", idToken)

                    enviarTokenAlServidor(googleCredential)
                }

            } catch (e: Exception) {

                Log.e(
                    "GOOGLE",
                    "Error al iniciar sesión",
                    e
                )
            }
        }
    }

    fun enviarTokenAlServidor(googleCredential: GoogleIdTokenCredential) {
        var intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }
}
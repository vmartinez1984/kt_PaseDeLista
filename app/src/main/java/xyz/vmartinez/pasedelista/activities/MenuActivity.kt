package xyz.vmartinez.pasedelista.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import xyz.vmartinez.pasedelista.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MenuActivity : AppCompatActivity() {
    //Declaración de variables controles
    lateinit var buttonRegistraAsistencia: Button
    lateinit var textView: TextView
    lateinit var imageView: ImageView

    //Varaible donde se guardan las coordendas
    var gps = ""

    //gps
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //Para la foto
    var uri : Uri?  = null;
    private val tomarFotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ){
            isSuccess ->
        if(isSuccess){
            uri?.let{
                    uri -> imageView.setImageURI(uri)
            }
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }
    //Fin de foto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        buttonRegistraAsistencia = findViewById<Button>(R.id.buttonRegistrarAsistencia)
        textView = findViewById<TextView>(R.id.textViewIntruccion)
        imageView = findViewById<ImageView>(R.id.imageView)

        var nombre = "Pancho"
        Toast.makeText(this,"Bienvenido " + nombre, Toast.LENGTH_SHORT).show()

        obtenerUbicacion()
    }

    private fun obtenerUbicacion() {
        //gps
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val lat = it.latitude
                val lon = it.longitude
                textView.text =  "Latitud: $lat, Longitud: $lon"
                gps = "Latitud: $lat, Longitud: $lon"
            }
        }
    }

    //Para la foto
    fun crearArchivo(): File?{
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        var almacen = getExternalFilesDir(
            android.os.Environment.DIRECTORY_PICTURES
        )

        return try {
            File.createTempFile("${timeStamp}_",".jpg", almacen)
        }catch (e: Exception){
            Toast.makeText(this, "Error al crear archivo",Toast.LENGTH_SHORT).show()
            null
        }
    }

    fun tomarSelfie(view: View){
        val archivo = crearArchivo()

        archivo?.let { file ->
            var uri = FileProvider.getUriForFile(this,
                "${applicationContext.packageName}.fileprovider",
                file
            )
            this.uri = uri
            tomarFotoLauncher.launch(uri)
        }
        buttonRegistraAsistencia.isEnabled = true
        textView.text = "Ubicación obtenida "+ gps +". Ya puede registrar su asistencia"
    }
    //Fin de foto

    fun registrarAsistencia(view: View){
        Toast.makeText(this, "Asistencia registra", Toast.LENGTH_SHORT).show()
    }
}
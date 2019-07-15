package curso.carlos.indrivefordriver.gateway

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import curso.carlos.indrivefordriver.R
import curso.carlos.indrivefordriver.model.Driver
import curso.carlos.indrivefordriver.services.DriverService

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val TAG = "REGISTER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val loginBtn = findViewById<Button>(R.id.createAccountBtn)
        val usernameText = findViewById<EditText>(R.id.newUsernameEt)
        val passwordText = findViewById<EditText>(R.id.newPasswordEt)

        loginBtn.setOnClickListener {
            auth.createUserWithEmailAndPassword(usernameText.text.toString(), passwordText.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        Toast.makeText(baseContext, "User created successfully", Toast.LENGTH_SHORT).show()
                        val driverService = DriverService()
                        val driver = Driver()
                        val user = task.result?.user

                        driver.name = user?.email.toString()
                        driver.origin_lat = "37.422"
                        driver.origin_long = "-122.084"
                        driverService.createDriver(user?.uid.toString(), driver)
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}

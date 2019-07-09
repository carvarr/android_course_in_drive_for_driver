package curso.carlos.indrivefordriver

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import curso.carlos.indrivefordriver.helpers.VersionManager

class VersionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version)

        val intent = intent;
        val versionFromFirebase = intent.getStringExtra(VersionManager.VERSION_PARAM_NAME)
        val versionManager = VersionManager(versionFromFirebase, this)

        val button = findViewById<ImageButton>(R.id.update)
        button.setOnClickListener {
            versionManager.updateVersionWithFirebase()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

package curso.carlos.indrivefordriver

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import curso.carlos.indrivefordriver.adapters.DrivesAdapter
import curso.carlos.indrivefordriver.gateway.LoginActivity
import curso.carlos.indrivefordriver.helpers.DistanceManager
import curso.carlos.indrivefordriver.helpers.PriceCalculator
import curso.carlos.indrivefordriver.model.Drive
import curso.carlos.indrivefordriver.model.DriveItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val drives: ArrayList<DriveItem> = ArrayList()

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var childEventListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
            return
        }

        // Firebase reference
        database = FirebaseDatabase.getInstance().reference

        rv_drives.layoutManager = LinearLayoutManager(this)
        rv_drives.adapter = DrivesAdapter(drives, this)

        loadDrives()
    }

    // Menu configuration
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_profile -> {
            true
        }
        R.id.action_about -> {
            val intent = Intent(applicationContext, AboutActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.action_logout -> {
            auth.signOut()
            redirectToLogin()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        database.removeEventListener(childEventListener)
    }


    /**
     * redirigir a login
     */
    private fun redirectToLogin() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun loadDrives() {

        val query = database.child("addresess")
        childEventListener = query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val drive = dataSnapshot.getValue(Drive::class.java)
                val driveItem = DriveItem()
                driveItem.id = dataSnapshot.key.toString()
                driveItem.status = drive?.status!!
                val distance = DistanceManager.calculateDistance(drive?.origin_lat!!.toDouble(), drive?.origin_long.toDouble(), drive?.destination_lat.toDouble(), drive?.destination_long.toDouble())
                driveItem.distance = DistanceManager.distanceText(distance)
                driveItem.price =  "${"%.0f".format(PriceCalculator.calculateByDistance(distance))} $"

                drives.add(driveItem)

                rv_drives.adapter?.notifyDataSetChanged()

                sendDriveNotification(0, driveItem.distance, driveItem.price)
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val drive = dataSnapshot.getValue(Drive::class.java)
                val driveEditedInAdapter = drives.find { item -> item.id.equals(dataSnapshot.key) }
                val driveEditedInAdapterIndex = drives.indexOf(driveEditedInAdapter)
                driveEditedInAdapter?.status = drive?.status!!

                drives.set(driveEditedInAdapterIndex, driveEditedInAdapter!!)
                rv_drives.adapter?.notifyItemChanged(driveEditedInAdapterIndex)
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    /**
     * send notification when a new drive is placed
     */
    private fun sendDriveNotification(id: Int, distanceText: String, priceText: String) {
        var builder = NotificationCompat.Builder(this, "")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("A new drive is requested")
            .setContentText("Distance: $distanceText, Price: $priceText")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(id, builder.build())
        }
    }

    companion object {
        fun pickService(driveId: String) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val database = FirebaseDatabase.getInstance().reference

            database.child("addresess").child(driveId).child("status").setValue(true)
            database.child("addresess").child(driveId).child("drivername").setValue(currentUser?.email)
        }
    }
}

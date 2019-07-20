package curso.carlos.indrivefordriver

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import curso.carlos.indrivefordriver.adapters.DrivesAdapter
import curso.carlos.indrivefordriver.gateway.LoginActivity
import curso.carlos.indrivefordriver.helpers.DialogHelper
import curso.carlos.indrivefordriver.helpers.DistanceManager
import curso.carlos.indrivefordriver.helpers.PriceCalculator
import curso.carlos.indrivefordriver.helpers.VersionManager
import curso.carlos.indrivefordriver.model.Drive
import curso.carlos.indrivefordriver.model.DriveItem
import curso.carlos.indrivefordriver.model.Driver
import curso.carlos.indrivefordriver.model.History
import curso.carlos.indrivefordriver.services.DriverService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val drives: ArrayList<DriveItem> = ArrayList()

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var childEventListener: ChildEventListener
    private lateinit var driverService: DriverService
    private lateinit var checkAvailability: CheckBox
    private lateinit var locationManager: LocationManager

    val VERSION_KEY = "driver_version"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
            return
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        getUserLocation()

        driverService = DriverService()

        // Firebase reference
        database = FirebaseDatabase.getInstance().reference

        var versionFromFirebase = "-1"
        this.database.child("control_version")//.child("driver_version")
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    if (p0.key.equals(VERSION_KEY)) {
                        versionFromFirebase = p0.value.toString()
                        validateAppVersion(versionFromFirebase)
                    }
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    if (p0.key.equals(VERSION_KEY)) {
                        versionFromFirebase = p0.value.toString()
                        validateAppVersion(versionFromFirebase)

                    }
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })

        rv_drives.layoutManager = LinearLayoutManager(this)
        rv_drives.adapter = DrivesAdapter(drives, this)

        checkAvailability =  findViewById(R.id.check_availability)
        checkAvailability.setOnCheckedChangeListener { buttonView, isChecked ->
            changeAvailability(isChecked)
        }

        checkAvailability()
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


    private fun validateAppVersion(version: String) {
        val versionManager = VersionManager(version, this)
        if (!versionManager.isVersionUpdated()) {
            val intent = Intent(applicationContext, VersionActivity::class.java)
            intent.putExtra(VersionManager.VERSION_PARAM_NAME, version)
            startActivity(intent)
            return
        }
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
                val distance = DistanceManager.calculateDistance(
                    drive?.origin_lat!!.toDouble(),
                    drive?.origin_long.toDouble(),
                    drive?.destination_lat.toDouble(),
                    drive?.destination_long.toDouble()
                )
                driveItem.distance = DistanceManager.distanceText(distance)
                driveItem.price =
                    "${"%.0f".format(PriceCalculator.calculateByDistance(distance))} $"
                driveItem.demand = drive?.service_demand.service_mount.toString()
                driveItem.drivernameDemand = drive?.service_demand.getDriverName()
                driveItem.userId = drive.username
                driveItem.destination_lat = drive.destination_lat
                driveItem.destination_long = drive.destination_long
                driveItem.origin_lat = drive.origin_lat
                driveItem.origin_long = drive.origin_long

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

    private fun unloadDrives() {
        database.removeEventListener(childEventListener)
        drives.clear()
        rv_drives.adapter?.notifyDataSetChanged()
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


    private fun checkAvailability(){
        driverService.listenDriverInfo(auth.currentUser?.uid.toString(), object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val driver = dataSnapshot.getValue(Driver::class.java)
                checkAvailability.isChecked = driver!!.status

                if(checkAvailability.isChecked) {
                    loadDrives()
                } else {
                    unloadDrives()
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun changeAvailability(available: Boolean) {
        driverService.changeDriverAvailability(auth.currentUser?.uid.toString(), available)
    }

    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_ACCESS_PERMISSION
            )
        } else {
            requestUserLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun requestUserLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.1f, object: LocationListener {
            override fun onLocationChanged(location: Location?) {
                driverService.changeDriverLocation(auth.currentUser?.uid.toString(), location?.latitude.toString(), location?.longitude.toString())
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

            override fun onProviderEnabled(provider: String?) {}

            override fun onProviderDisabled(provider: String?) {}

        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ACCESS_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //requestUserLocation()
                }

                return
            }
        }
    }

    companion object {
        private const val REQUEST_ACCESS_PERMISSION = 1

        fun pickService(userId: String, driveId: String, destination_lat: String,
                        destination_lon: String, origin_lat: String, origin_lon: String,
                        mountDemand: Int) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val database = FirebaseDatabase.getInstance().reference

            database.child("addresess").child(driveId).child("status").setValue(true)
            database.child("addresess").child(driveId).child("drivername").setValue(currentUser?.uid)

            //almacenar historico
            val driverService = DriverService()
            val history = History()
            history.carfare = mountDemand
            history.destination_lat = destination_lat
            history.destination_lon = destination_lon
            history.origin_lat = origin_lat
            history.origin_lon = origin_lon
            history.service_id = "${userId}${driveId}"
            driverService.saveDriveHistory(userId, history)
        }

        fun offerService(driveId: String, context: Context) {
            DialogHelper.askForServiceMount(context, DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            }, DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
        }
    }
}

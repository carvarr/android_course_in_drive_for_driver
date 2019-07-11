package curso.carlos.indrivefordriver.adapters

import android.content.Context
import android.media.Image
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import curso.carlos.indrivefordriver.MainActivity
import curso.carlos.indrivefordriver.R
import curso.carlos.indrivefordriver.model.DriveItem
import kotlinx.android.synthetic.main.drives_list_item.view.*

class DrivesAdapter(val items: ArrayList<DriveItem>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.drives_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.tvDistance?.text = items[position].distance
        holder?.tvPrice?.text = items[position].price
        holder?.driverName.text = items[position].drivernameDemand
        holder?.mountDemand.text = items[position].demand
        holder?.driveId = items[position].id

        if(items[position].status) {
            holder?.setButtonInvisible()
        }
    }
}

class ViewHolder : RecyclerView.ViewHolder, View.OnClickListener {

    // Holds the TextView that will add each animal to
    val tvDistance: TextView
    val tvPrice: TextView
    val driverName: TextView
    val mountDemand: TextView
    val pickButton: ImageButton
    val offerButton: ImageButton

    var driveId: String = ""

    constructor(view: View) : super(view) {
        tvDistance = view.tv_distance
        tvPrice = view.tv_price
        pickButton = view.btn_pick_service
        driverName = view.driver_name
        mountDemand = view.service_mount
        offerButton = view.btn_offer

        pickButton.setOnClickListener(this)
        offerButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.btn_pick_service -> MainActivity.pickService(driveId)
            R.id.btn_offer -> MainActivity.offerService(driveId, v?.context)
        }

    }

    fun setButtonInvisible() {
        pickButton.visibility = View.INVISIBLE
    }
}
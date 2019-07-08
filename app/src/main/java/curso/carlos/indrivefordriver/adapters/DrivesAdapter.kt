package curso.carlos.indrivefordriver.adapters

import android.content.Context
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
    val pickButton: ImageButton

    var driveId: String = ""

    constructor(view: View) : super(view) {
        tvDistance = view.tv_distance
        tvPrice = view.tv_price
        pickButton = view.btn_pick_service

        pickButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        MainActivity.pickService(driveId)
    }

    fun setButtonInvisible() {
        pickButton.visibility = View.INVISIBLE
    }
}
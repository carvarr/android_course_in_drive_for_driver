package curso.carlos.indrivefordriver.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvDistance = view.tv_distance

    val tvPrice = view.tv_price
}
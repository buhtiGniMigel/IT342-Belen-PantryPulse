package edu.cit.belen.pantrypulse

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PantryItemAdapter(
    private var items: List<PantryItem>,
    private val onEditClick: (PantryItem) -> Unit
) : RecyclerView.Adapter<PantryItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val tvItemCategoryAndQty: TextView = view.findViewById(R.id.tvItemCategoryAndQty)
        val tvItemExpiry: TextView = view.findViewById(R.id.tvItemExpiry)
        val flStatusChip: FrameLayout = view.findViewById(R.id.flStatusChip)
        val tvStatusText: TextView = view.findViewById(R.id.tvStatusText)
        val btnEditItem: ImageView = view.findViewById(R.id.btnEditItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pantry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvItemName.text = item.itemName
        holder.tvItemCategoryAndQty.text = "${item.category} • ${item.quantity} units"
        holder.tvItemExpiry.text = "Expires: ${item.expiryDate}"

        // Set status and dynamic color code
        holder.tvStatusText.text = item.status

        when (item.status.lowercase()) {
            "fresh" -> {
                holder.flStatusChip.setBackgroundColor(Color.parseColor("#F0FDF4")) // Light Green
                holder.tvStatusText.setTextColor(Color.parseColor("#166534")) // Dark Green
            }
            "expiring" -> {
                holder.flStatusChip.setBackgroundColor(Color.parseColor("#FEF3C7")) // Light Orange/Yellow
                holder.tvStatusText.setTextColor(Color.parseColor("#92400E")) // Dark Orange
            }
            "expired" -> {
                holder.flStatusChip.setBackgroundColor(Color.parseColor("#FEF2F2")) // Light Red
                holder.tvStatusText.setTextColor(Color.parseColor("#991B1B")) // Dark Red
            }
            else -> {
                holder.flStatusChip.setBackgroundColor(Color.parseColor("#F3F4F6")) // Light Grey
                holder.tvStatusText.setTextColor(Color.parseColor("#374151")) // Dark Grey
            }
        }

        holder.btnEditItem.setOnClickListener { onEditClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<PantryItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}

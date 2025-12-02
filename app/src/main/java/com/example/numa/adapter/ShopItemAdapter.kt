package com.example.numa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.R
import com.example.numa.entity.Habit
import com.example.numa.entity.ShopItem
import com.example.numa.util.FixPixelArt
import com.google.android.material.card.MaterialCardView

class ShopItemAdapter(
    val shopItems: MutableList<ShopItem>,
    //private val onShopItemClick: (ShopItem) -> Unit
) : RecyclerView.Adapter<ShopItemAdapter.ShopItemViewHolder>() {

    class ShopItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.tvName)
        val price = itemView.findViewById<TextView>(R.id.tvPrice)
        val img = itemView.findViewById<ImageView>(R.id.imgItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_item, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val item = shopItems[position]
        holder.name.text = item.name
        holder.price.text = item.price.toString()

        holder.img.setImageResource(R.drawable.cat_banner_1)
        FixPixelArt.removeFilter(holder.img)


        holder.itemView.setOnClickListener {
            //onShopItemClick(item)
        }

    }

    override fun getItemCount() = shopItems.size
}

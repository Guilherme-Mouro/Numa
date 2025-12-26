package com.example.numa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.R
import com.example.numa.entity.ShopItem
import com.example.numa.util.FixPixelArt
import com.google.android.material.card.MaterialCardView

class ShopItemAdapter(
    val shopItems: MutableList<ShopItem>,
    private var ownedItemIds: Set<Int>,
    private var equippedItemId: Int? = null,
    private val onShopItemClick: (ShopItem) -> Unit
) : RecyclerView.Adapter<ShopItemAdapter.ShopItemViewHolder>() {

    class ShopItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvName)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val img: ImageView = itemView.findViewById(R.id.imgItem)
        val card: MaterialCardView = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_item, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val item = shopItems[position]
        val context = holder.itemView.context

        holder.name.text = item.name
        holder.price.text = item.price.toString()

        val imageResource = context.resources.getIdentifier(
            item.image,
            "drawable",
            context.packageName
        )
        holder.img.setImageResource(imageResource)

        FixPixelArt.removeFilter(holder.img)

        val strokeColorRes = when {
            item.id == equippedItemId -> R.color.green
            ownedItemIds.contains(item.id) -> R.color.yellow
            else -> R.color.white
        }

        holder.card.strokeColor = ContextCompat.getColor(context, strokeColorRes)

        holder.itemView.setOnClickListener {
            onShopItemClick(item)
        }
    }

    override fun getItemCount() = shopItems.size


    fun markItemAsOwned(itemId: Int) {
        val position = shopItems.indexOfFirst { it.id == itemId }
        if (position != -1) {
            ownedItemIds = ownedItemIds + itemId
            notifyItemChanged(position)
        }
    }

    fun updateEquippedItem(newEquippedId: Int) {
        val oldEquippedId = equippedItemId

        equippedItemId = newEquippedId

        if (oldEquippedId != null) {
            val oldPosition = shopItems.indexOfFirst { it.id == oldEquippedId }
            if (oldPosition != -1) notifyItemChanged(oldPosition)
        }

        val newPosition = shopItems.indexOfFirst { it.id == newEquippedId }
        if (newPosition != -1) notifyItemChanged(newPosition)
    }
}
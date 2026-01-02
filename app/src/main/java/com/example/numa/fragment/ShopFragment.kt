package com.example.numa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.DailyQuestRepository
import com.example.numa.R
import com.example.numa.util.UserRepository
import com.example.numa.adapter.ShopItemAdapter
import com.example.numa.databinding.FragmentShopBinding
import com.example.numa.entity.ShopItem
import com.example.numa.entity.UserItem
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ShopFragment : Fragment() {
    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!
    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }

    private lateinit var types: Map<String, RecyclerView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)

        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        if (userId == null) {
            Toast.makeText(requireContext(), "Error: user not found", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        types = mapOf(
            "skin" to binding.rvShopItemsSkins,
            "head" to binding.rvShopItemsHead,
            "torso" to binding.rvShopItemsTorso,
            "legs" to binding.rvShopItemsLegs,
            "feet" to binding.rvShopItemsFeet,
            "background" to binding.rvShopItemsBackground
        )

        loadShopItems(userId)

        return binding.root
    }

    private fun loadShopItems(userId: Int) {
        lifecycleScope.launch {
            val ownedItemIds = db.userItemDao()
                .getUserItemByUserId(userId)
                .map { it.itemId }
                .toSet()

            val pet = db.petDao().getPetByUser(userId)

            types.forEach { (type, recyclerView) ->
                val shopItems = db.shopItemDao().getShopItemByType(type)

                val currentEquippedValue = when(type) {
                    "skin" -> pet?.skin; "head" -> pet?.head; "torso" -> pet?.torso
                    "legs" -> pet?.legs; "feet" -> pet?.feet
                    else -> null
                }

                val equippedId = shopItems.find { it.item == currentEquippedValue }?.id

                setupCategoryList(recyclerView, shopItems, userId, ownedItemIds, equippedId)
            }
        }
    }

    private fun setupCategoryList(
        recyclerView: RecyclerView,
        shopItems: List<ShopItem>,
        userId: Int,
        ownedItemIds: Set<Int>,
        equippedId: Int?
    ) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = ShopItemAdapter(
                shopItems = shopItems.toMutableList(),
                ownedItemIds = ownedItemIds,
                equippedItemId = equippedId
            ) { shopItem ->
                showActionDialog(shopItem, userId)
            }
        }
    }

    private fun showActionDialog(shopItem: ShopItem, userId: Int) {
        lifecycleScope.launch {
            val isOwned = db.userItemDao().getUserItemByUserId(userId).any { it.itemId == shopItem.id }

            val dialogView = layoutInflater.inflate(R.layout.item_custom_dialog, null)
            val imgItem = dialogView.findViewById<ImageView>(R.id.imgDialogItem)
            val tvName = dialogView.findViewById<TextView>(R.id.tvDialogName)
            val tvPrice = dialogView.findViewById<TextView>(R.id.tvDialogPrice)

            tvName.text = shopItem.name
            tvPrice.text = if (isOwned) "Owned" else "${shopItem.price} Points"

            val imageResource = requireContext().resources.getIdentifier(
                shopItem.image, "drawable", requireContext().packageName
            )
            imgItem.setImageResource(imageResource)

            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton(if (isOwned) "Equip" else "Buy") { _, _ ->
                    if (isOwned) equipItem(shopItem, userId) else processPurchase(shopItem, userId)
                }
                .create()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
        }
    }

    private fun equipItem(shopItem: ShopItem, userId: Int) {
        lifecycleScope.launch {
            val pet = db.petDao().getPetByUser(userId) ?: return@launch

            when (shopItem.type) {
                "skin" -> db.petDao().updateSkin(pet.id, shopItem.item)
                "head" -> db.petDao().updateHead(pet.id, shopItem.item)
                "torso" -> db.petDao().updateTorso(pet.id, shopItem.item)
                "legs" -> db.petDao().updateLegs(pet.id, shopItem.item)
                "feet" -> db.petDao().updateFeet(pet.id, shopItem.item)
            }

            val recyclerView = types[shopItem.type]
            val adapter = recyclerView?.adapter as? ShopItemAdapter
            adapter?.updateEquippedItem(shopItem.id)

            Toast.makeText(requireContext(), "${shopItem.name} Equipped!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processPurchase(shopItem: ShopItem, userId: Int) {
        lifecycleScope.launch {
            val user = db.userDao().getUserById(userId) ?: return@launch

            if (user.points >= shopItem.price) {
                db.userItemDao().insertUserItem(UserItem(userId = userId, itemId = shopItem.id))

                UserRepository(db.userDao()).addXpAndPoints(userId, 0, -shopItem.price)
                val questRepo = DailyQuestRepository(db.dailyQuestDao())
                questRepo.incrementProgress(userId, DailyQuestRepository.TYPE_SHOP)

                val recyclerView = types[shopItem.type]
                (recyclerView?.adapter as? ShopItemAdapter)?.markItemAsOwned(shopItem.id)

                Toast.makeText(requireContext(), "Bought!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Not enough points!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
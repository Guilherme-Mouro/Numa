package com.example.numa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.numa.adapter.ShopItemAdapter
import com.example.numa.databinding.FragmentShopBinding
import com.example.numa.entity.ShopItem
import com.example.numa.entity.UserItem
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch

class ShopFragment : Fragment() {
    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!
    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)

        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        if (userId == null) {
            Toast.makeText(requireContext(), "Erro: utilizador não encontrado", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        val types = mapOf(
            "skin" to binding.rvShopItemsSkins,
            "head" to binding.rvShopItemsHead,
            "torso" to binding.rvShopItemsTorso,
            "legs" to binding.rvShopItemsLegs,
            "feet" to binding.rvShopItemsFeet,
            "background" to binding.rvShopItemsBackground
        )

        lifecycleScope.launch {

            val ownedItemIds = db.userItemDao()
                .getUserItemByUserId(userId)
                .map { it.itemId }
                .toSet()

            types.forEach { (type, recyclerView) ->
                setupCategoryList(type, recyclerView, userId, ownedItemIds)
            }

        }

        return binding.root
    }

    private suspend fun setupCategoryList(
        type: String,
        recyclerView: RecyclerView,
        userId: Int,
        ownedItemIds: Set<Int>
    ) {
        val shopItems = db.shopItemDao().getShopItemByType(type)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = ShopItemAdapter(
                shopItems = shopItems.toMutableList(),
                ownedItemIds = ownedItemIds,
            ) { shopItem ->
                buyShopItems(shopItem, userId)
            }
        }
    }

    private fun buyShopItems(shopItem: ShopItem, userId: Int) {
        lifecycleScope.launch {
            val user = db.userDao().getUserById(userId)

            if (user == null) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                return@launch
            }

            var userPoints = user.points
            val itemCost = shopItem.price

            if (userPoints < itemCost) {
                Toast.makeText(
                    requireContext(),
                    "Unable to buy this item",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            val userItem = UserItem(
                userId = userId,
                itemId = shopItem.id
            )

            val result = db.userItemDao().insertUserItem(userItem)

            if (result != -1L) {

                userPoints -= itemCost

                val updatedUser = user.copy(points = userPoints)
                db.userDao().updateUser(updatedUser)

                (binding.rvShopItemsSkins.adapter as? ShopItemAdapter)?.addOwnedItem(shopItem.id)

                Toast.makeText(
                    requireContext(),
                    "Item comprado! (-$itemCost pontos)",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(
                    requireContext(),
                    "Já tens este item",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
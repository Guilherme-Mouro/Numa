package com.example.numa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.numa.R
import com.example.numa.adapter.ShopItemAdapter
import com.example.numa.databinding.FragmentHomeBinding
import com.example.numa.databinding.FragmentShopBinding
import com.example.numa.entity.UserItem
import com.example.numa.util.DatabaseProvider
import com.example.numa.util.SessionManager
import kotlinx.coroutines.launch
import java.util.Date

class ShopFragment : Fragment() {
    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!
    private val db by lazy { DatabaseProvider.getDatabase(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShopBinding.inflate(inflater, container, false)

        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        if (userId == null) {
            Toast.makeText(requireContext(), "Erro: utilizador não encontrado", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        lifecycleScope.launch {

            val shopItems = db.shopItemDao().getAllShopItem()

            val user = db.userDao().getUserById(userId)
            if (user == null) {
                Toast.makeText(requireContext(), "Erro ao carregar utilizador", Toast.LENGTH_SHORT).show()
                return@launch
            }

            var userPoints = user.points

            val ownedItemIds = db.userItemDao()
                .getUserItemByUserId(userId)
                .map { it.itemId }
                .toSet()

            binding.rvShopItems.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = ShopItemAdapter(
                    shopItems = shopItems.toMutableList(),
                    ownedItemIds = ownedItemIds,
                ) { shopItem ->

                    lifecycleScope.launch {
                        val itemCost = shopItem.price

                        if (userPoints < itemCost) {
                            Toast.makeText(
                                requireContext(),
                                "Pontos insuficientes para comprar este item",
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

                            (adapter as ShopItemAdapter).addOwnedItem(shopItem.id)

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
            }
        }

        return binding.root
    }
}

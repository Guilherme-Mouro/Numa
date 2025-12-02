package com.example.numa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.numa.R
import com.example.numa.adapter.ShopItemAdapter
import com.example.numa.databinding.FragmentHomeBinding
import com.example.numa.databinding.FragmentShopBinding
import com.example.numa.util.DatabaseProvider
import kotlinx.coroutines.launch

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

        lifecycleScope.launch {
            val shopItems = db.shopItemDao().getAllShopItem()

            binding.rvShopItems.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = ShopItemAdapter(shopItems.toMutableList())
            }
        }

        return binding.root
    }

}
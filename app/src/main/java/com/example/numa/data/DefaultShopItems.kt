package com.example.numa.data

import com.example.numa.entity.ShopItem

object DefaultShopItems {

    val items = listOf(
        ShopItem(
            name = "Gato Laranja",
            description = "Uma gato laranja",
            price = 100.0,
            image = "cat_banner_1",
            item = "orange_cat_idle_animation",
            type = "skin"
        ),
    )
}

package com.example.numa.data

import com.example.numa.entity.ShopItem

object DefaultShopItems {

    val items = listOf(
        ShopItem(
            id = 1,
            name = "Orange Cat",
            description = "A orange cat",
            price = 100,
            image = "orange_cat_banner_1",
            item = "orange_cat_idle_animation",
            type = "skin"
        ),
        ShopItem(
            id = 2,
            name = "White Cat",
            description = "A white cat",
            price = 200,
            image = "white_cat_banner_1",
            item = "white_cat_idle_animation",
            type = "skin"
        ),
        ShopItem(
            id = 3,
            name = "Gray Cat",
            description = "A gray cat",
            price = 300,
            image = "white_cat_banner_1",
            item = "white_cat_idle_animation",
            type = "skin"
        ),
    )
}

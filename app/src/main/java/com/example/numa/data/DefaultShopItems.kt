package com.example.numa.data

import com.example.numa.entity.ShopItem

object DefaultShopItems {

    val items = listOf(
        // --- SKINS ---
        ShopItem(
            id = 1,
            name = "Orange Cat",
            description = "A friendly orange cat",
            price = 100,
            image = "orange_cat_banner_1",
            item = "orange_cat_",
            type = "skin"
        ),
        ShopItem(
            id = 2,
            name = "White Cat",
            description = "A polite white cat",
            price = 200,
            image = "white_cat_banner_1",
            item = "white_cat_",
            type = "skin"
        ),
        ShopItem(
            id = 3,
            name = "Gray Cat",
            description = "A mysterious gray cat",
            price = 300,
            image = "gray_cat_banner_1",
            item = "gray_cat_",
            type = "skin"
        ),

        // --- HEAD ---
        ShopItem(
            id = 4,
            name = "Red Cap",
            description = "A cool baseball cap",
            price = 150,
            image = "orange_cat_banner_1",
            item = "red_cap_asset",
            type = "head"
        ),
        ShopItem(
            id = 5,
            name = "Sunglasses",
            description = "Deal with it",
            price = 120,
            image = "orange_cat_banner_1",
            item = "sunglasses_asset",
            type = "head"
        ),
        ShopItem(
            id = 6,
            name = "Gold Crown",
            description = "Fit for a king",
            price = 500,
            image = "orange_cat_banner_1",
            item = "crown_asset",
            type = "head"
        ),

        // --- TORSO ---
        ShopItem(
            id = 7,
            name = "Red Scarf",
            description = "Keeps you warm",
            price = 100,
            image = "orange_cat_banner_1",
            item = "red_scarf_asset",
            type = "torso"
        ),
        ShopItem(
            id = 8,
            name = "Blue Bow Tie",
            description = "Very fancy",
            price = 150,
            image = "orange_cat_banner_1",
            item = "bow_tie_asset",
            type = "torso"
        ),
        ShopItem(
            id = 9,
            name = "Green Hoodie",
            description = "Casual style",
            price = 250,
            image = "orange_cat_banner_1",
            item = "green_hoodie_asset",
            type = "torso"
        ),

        // --- LEGS ---
        ShopItem(
            id = 10,
            name = "Blue Shorts",
            description = "Good for running",
            price = 180,
            image = "orange_cat_banner_1",
            item = "blue_shorts_asset",
            type = "legs"
        ),
        ShopItem(
            id = 11,
            name = "Black Pants",
            description = "Goes with everything",
            price = 200,
            image = "orange_cat_banner_1",
            item = "black_pants_asset",
            type = "legs"
        ),

        // --- FEET ---
        ShopItem(
            id = 12,
            name = "Red Sneakers",
            description = "Fast shoes",
            price = 150,
            image = "orange_cat_banner_1",
            item = "red_sneakers_asset",
            type = "feet"
        ),
        ShopItem(
            id = 13,
            name = "Brown Boots",
            description = "Made for walking",
            price = 160,
            image = "orange_cat_banner_1",
            item = "brown_boots_asset",
            type = "feet"
        )
    )
}
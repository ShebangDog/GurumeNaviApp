package com.oxymoron.ui.list.data;

import com.oxymoron.api.gson.data.Access;
import com.oxymoron.api.gson.data.Rest;
import com.oxymoron.util.Optional;

public class RestaurantThumbnail {
    private String name;
    private Access access;
    private Optional<String> imageUrl;

    private String restaurantId;

    public RestaurantThumbnail() {
    }

    public RestaurantThumbnail(Rest restaurant) {
        this.name = restaurant.getName();
        this.access = restaurant.getAccess();
        this.imageUrl = restaurant.getImageUrl().getShopImage();

        this.restaurantId = restaurant.getId();
    }

    public String getName() {
        return name;
    }

    public Optional<Access> getAccess() {
        return Optional.of(access);
    }

    public Optional<String> getImageUrl() {
        return imageUrl;
    }

    public String getRestaurantId() {
        return restaurantId;
    }
}
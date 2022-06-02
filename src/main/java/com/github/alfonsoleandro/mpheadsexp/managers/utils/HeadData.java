package com.github.alfonsoleandro.mpheadsexp.managers.utils;

import org.bukkit.inventory.ItemStack;

public abstract class HeadData {

    protected double price;
    protected double xp;
    protected ItemStack headItem;

    public HeadData(double price, double xp, ItemStack headItem) {
        this.price = price;
        this.xp = xp;
        this.headItem = headItem;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getXp() {
        return this.xp;
    }

    public void setXp(double xp) {
        this.xp = xp;
    }

    public ItemStack getHeadItem() {
        return this.headItem;
    }

}


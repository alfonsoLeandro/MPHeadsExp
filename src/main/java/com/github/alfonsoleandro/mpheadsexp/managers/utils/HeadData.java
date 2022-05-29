package com.github.alfonsoleandro.mpheadsexp.managers.utils;

import org.bukkit.inventory.ItemStack;

public abstract class HeadData {
//    /**
//     * The id of the head. the mob type in case it is a mob head, a player's name in other case.
//     */
//    protected final String name;
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


    public void load(){
        //todo
    }

}


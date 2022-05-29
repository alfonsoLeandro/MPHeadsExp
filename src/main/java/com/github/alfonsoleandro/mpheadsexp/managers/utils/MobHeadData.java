package com.github.alfonsoleandro.mpheadsexp.managers.utils;

import org.bukkit.inventory.ItemStack;
public class MobHeadData extends HeadData{

    private final String mobType;
    public MobHeadData(double price, double xp, String mobType, ItemStack headItem) {
        super(price, xp, headItem);
        this.mobType = mobType;
    }

    public String getMobType() {
        return this.mobType;
    }
}

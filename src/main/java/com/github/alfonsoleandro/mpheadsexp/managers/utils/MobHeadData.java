package com.github.alfonsoleandro.mpheadsexp.managers.utils;

import org.bukkit.inventory.ItemStack;
public class MobHeadData extends HeadData{

    private final String mobType;
    private final int requiredLevel;

    public MobHeadData(double price, double xp, String mobType, ItemStack headItem, int requiredLevel) {
        super(price, xp, headItem);
        this.mobType = mobType;
        this.requiredLevel = requiredLevel;
    }

    public String getMobType() {
        return this.mobType;
    }

    public int getRequiredLevel() {
        return this.requiredLevel;
    }
}

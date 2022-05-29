package com.github.alfonsoleandro.mpheadsexp.managers.utils;

import org.bukkit.inventory.ItemStack;

public class PlayerHeadData extends HeadData{

    private final String playerName;

    public PlayerHeadData(double price, double xp, String playerName, ItemStack headItem) {
        super(price, xp, headItem);
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return this.playerName;
    }
}

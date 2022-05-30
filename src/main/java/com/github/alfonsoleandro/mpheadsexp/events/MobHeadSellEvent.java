package com.github.alfonsoleandro.mpheadsexp.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MobHeadSellEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player seller;
    private final String mobType;
    private final int amount;
    private int xp;
    private double price;
    private boolean cancelled = false;

    public MobHeadSellEvent(Player seller, String mobType, int xp, double price, int amount) {
        this.seller = seller;
        this.mobType = mobType;
        this.xp = xp;
        this.price = price;
        this.amount = amount;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getSeller() {
        return this.seller;
    }

    public String getMobType() {
        return this.mobType;
    }

    public int getXp() {
        return this.xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

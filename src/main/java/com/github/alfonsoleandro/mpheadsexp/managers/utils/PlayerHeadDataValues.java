package com.github.alfonsoleandro.mpheadsexp.managers.utils;

public class PlayerHeadDataValues {

    private final double moneyPercentage;
    private final double xp;

    public PlayerHeadDataValues(double moneyPercentage, double xp) {
        this.moneyPercentage = moneyPercentage;
        this.xp = xp;
    }

    public double getMoneyPercentage() {
        return this.moneyPercentage;
    }

    public double getXp() {
        return this.xp;
    }
}

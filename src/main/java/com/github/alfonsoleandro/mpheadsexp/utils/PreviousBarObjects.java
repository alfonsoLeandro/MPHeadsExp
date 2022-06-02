package com.github.alfonsoleandro.mpheadsexp.utils;

import org.bukkit.boss.KeyedBossBar;
import org.bukkit.scheduler.BukkitTask;

public class PreviousBarObjects {

    private final KeyedBossBar bossbar;
    private final BukkitTask task;

    public PreviousBarObjects(KeyedBossBar bossbar, BukkitTask task) {
        this.bossbar = bossbar;
        this.task = task;
    }

    public KeyedBossBar getBossbar() {
        return this.bossbar;
    }

    public BukkitTask getTask() {
        return this.task;
    }
}

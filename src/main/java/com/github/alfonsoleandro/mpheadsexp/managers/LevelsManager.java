package com.github.alfonsoleandro.mpheadsexp.managers;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import com.github.alfonsoleandro.mpheadsexp.utils.PreviousBarObjects;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.string.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class LevelsManager {

    private final HeadsExp plugin;
    private final Settings settings;
    private final MessageSender<Message> messageSender;
    private final Map<Player, PreviousBarObjects> bossBars = new HashMap<>();
    private final Map<UUID, Integer> playerXP = new HashMap<>();


    public LevelsManager(HeadsExp plugin){
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.messageSender = plugin.getMessageSender();
        loadPlayerLevelsFromFile();
    }


    //XP & Levels
    public int getXP(UUID player){
        if(!this.playerXP.containsKey(player)) this.playerXP.put(player, 0);
        return this.playerXP.get(player);
    }

    public int getLevel(UUID player){
        if(!this.playerXP.containsKey(player)) this.playerXP.put(player, 0);
        return this.playerXP.get(player)/this.settings.getExpPerLevel();
    }

    public void addXP(UUID player, int xp){
        if(!this.playerXP.containsKey(player)) this.playerXP.put(player, 0);
        int previousLevel = getLevel(player);
        this.playerXP.put(player, this.playerXP.get(player)+xp);

        if(getLevel(player) > previousLevel) {
            levelUpBossBar(Objects.requireNonNull(Bukkit.getPlayer(player)), previousLevel);
        } else {
            addXPBossBar(Objects.requireNonNull(Bukkit.getPlayer(player)), xp);
        }
    }

    public void setXP(UUID player, int xp){
        if(!this.playerXP.containsKey(player)) this.playerXP.put(player, 0);
        int previousXP = getXP(player);
        this.playerXP.put(player, xp);
        setXPBossbar(Objects.requireNonNull(Bukkit.getPlayer(player)), previousXP);
    }

    //Boss bars
    private void addXPBossBar(Player player, int xp){
        removePreviousBossBars(player);
        NamespacedKey key = new NamespacedKey(this.plugin, "MPHeadsExp/add/"+player.getName());

        double extraXP = ((double) getXP(player.getUniqueId()) - getLevel(player.getUniqueId())* this.settings.getExpPerLevel());

        KeyedBossBar bar = Bukkit.createBossBar(key,
                StringUtils.colorizeString('&', this.messageSender.getString(Message.ADDED_XP_BOSSBAR_TITLE)
                        .replace("%xp%", String.valueOf(xp))
                        .replace("%next%", String.valueOf(this.settings.getExpPerLevel()-extraXP))),
                BarColor.GREEN,
                BarStyle.SEGMENTED_20);

        bar.setProgress(extraXP/(double)this.settings.getExpPerLevel());

        bar.addPlayer(player);
        player.playSound(player.getLocation(),
                this.settings.getAddedXPBossBarSound().getSound(),
                this.settings.getAddedXPBossBarSound().getVolume(),
                this.settings.getAddedXPBossBarSound().getPitch());

        this.bossBars.put(player, new PreviousBarObjects(bar, new BukkitRunnable() {
                    @Override
                    public void run(){
                        removeBossBar(player, LevelsManager.this.bossBars.get(player).getBossbar());
                    }
                }.runTaskLater(this.plugin, 80)
                )
        );
    }


    private void levelUpBossBar(Player player, int previousLevel){
        removePreviousBossBars(player);

        NamespacedKey key = new NamespacedKey(this.plugin, "MPHeadsExp/levelUp/"+player.getName());
        KeyedBossBar bar = Bukkit.createBossBar(key,
                StringUtils.colorizeString('&', this.messageSender.getString(Message.LEVEL_UP_BOSSBAR_TITLE)
                        .replace("%previous%", String.valueOf(previousLevel))
                        .replace("%new%", String.valueOf(getLevel(player.getUniqueId())))),
                BarColor.GREEN,
                BarStyle.SEGMENTED_20);

        double extraXP = ((double) getXP(player.getUniqueId()) - getLevel(player.getUniqueId())*this.settings.getExpPerLevel());
        bar.setProgress(extraXP / (double)this.settings.getExpPerLevel());

        bar.addPlayer(player);
        player.playSound(player.getLocation(),
                this.settings.getLevelUpXPBossBarSound().getSound(),
                this.settings.getLevelUpXPBossBarSound().getVolume(),
                this.settings.getLevelUpXPBossBarSound().getPitch());

        this.bossBars.put(player,
                new PreviousBarObjects(bar, new BukkitRunnable() {
                    @Override
                    public void run(){
                        removeBossBar(player, LevelsManager.this.bossBars.get(player).getBossbar());
                    }
                }.runTaskLater(this.plugin, 80))
        );

    }

    private void setXPBossbar(Player player, int previousXp){
        removePreviousBossBars(player);

        NamespacedKey key = new NamespacedKey(this.plugin, "MPHeadsExp/set/"+player.getName());
        KeyedBossBar bar = Bukkit.createBossBar(key,
                StringUtils.colorizeString('&', this.messageSender.getString(Message.SET_XP_BOSSBAR_TITLE)
                        .replace("%previous%", String.valueOf(previousXp))
                        .replace("%new%", String.valueOf(getXP(player.getUniqueId())))),
                BarColor.GREEN,
                BarStyle.SEGMENTED_20);

        double extraXP = ((double) getXP(player.getUniqueId()) - getLevel(player.getUniqueId())*this.settings.getExpPerLevel());
        bar.setProgress(extraXP / (double)this.settings.getExpPerLevel());

        bar.addPlayer(player);
        player.playSound(player.getLocation(),
                this.settings.getSetXPBossBarSound().getSound(),
                this.settings.getSetXPBossBarSound().getVolume(),
                this.settings.getSetXPBossBarSound().getPitch());

        this.bossBars.put(player,
                new PreviousBarObjects(bar, new BukkitRunnable() {
                    @Override
                    public void run(){
                        removeBossBar(player, LevelsManager.this.bossBars.get(player).getBossbar());
                    }
                }.runTaskLater(this.plugin, 80))
        );

    }


    private void removePreviousBossBars(Player player){
        if(this.bossBars.containsKey(player)){
            this.bossBars.get(player).getTask().cancel();
            removeBossBar(player, this.bossBars.get(player).getBossbar());
        }
    }

    private void removeBossBar(Player player, KeyedBossBar bossBar){
        bossBar.removeAll();
        Bukkit.removeBossBar(bossBar.getKey());
        this.bossBars.remove(player);
    }



    private void loadPlayerLevelsFromFile(){
        ConfigurationSection players = this.plugin.getPlayersYaml().getAccess().getConfigurationSection("players");
        if(players == null) return;

        for(String uuid : players.getKeys(false)){
            this.playerXP.put(UUID.fromString(uuid), players.getInt(uuid));
        }
        this.plugin.getPlayersYaml().getAccess().set("players", null);
        this.plugin.getPlayersYaml().save(true);
    }

    public void saveLevelsToFile(){
        FileConfiguration players = this.plugin.getPlayersYaml().getAccess();

        for(UUID player : this.playerXP.keySet()){
            players.set("players."+player, this.playerXP.get(player));
        }
        this.plugin.getPlayersYaml().save(false);
    }
}

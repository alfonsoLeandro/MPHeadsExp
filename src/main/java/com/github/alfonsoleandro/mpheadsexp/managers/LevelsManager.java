package com.github.alfonsoleandro.mpheadsexp.managers;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.utils.PreviousBarObjects;
import com.github.alfonsoleandro.mpheadsexp.utils.Reloadable;
import com.github.alfonsoleandro.mpheadsexp.utils.SoundSettings;
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

public class LevelsManager implements Reloadable {

    private final Map<Player, PreviousBarObjects> bossBars = new HashMap<>();
    private final Map<UUID, Integer> players = new HashMap<>();
    private final Map<String, Integer> heads = new HashMap<>();
    private final HeadsExp plugin;
    private int expPerLevel;
    private String addedXPBossbarTitle;
    private String levelUpBossbarTitle;
    private String setXPBossbarTitle;
    private SoundSettings addedXPBossbarSound;
    private SoundSettings levelUpXPBossbarSound;
    private SoundSettings setXPBossbarSound;


    public LevelsManager(HeadsExp plugin){
        this.plugin = plugin;
        loadLevelsFromFile();
        reload();
    }


    //XP & Levels
    public int getXP(UUID player){
        if(!players.containsKey(player)) players.put(player, 0);
        return players.get(player);
    }

    public int getLevel(UUID player){
        if(!players.containsKey(player)) players.put(player, 0);
        return players.get(player)/expPerLevel;
    }

    public void addXP(UUID player, int xp){
        if(!players.containsKey(player)) players.put(player, 0);
        int previousLevel = getLevel(player);
        players.put(player, players.get(player)+xp);

        if(getLevel(player) > previousLevel) {
            levelUpBossbar(Objects.requireNonNull(Bukkit.getPlayer(player)), previousLevel);
        } else {
            addXPBossbar(Objects.requireNonNull(Bukkit.getPlayer(player)), xp);
        }
    }

    public void setXP(UUID player, int xp){
        if(!players.containsKey(player)) players.put(player, 0);
        int previousXP = getXP(player);
        players.put(player, xp);
        setXPBossbar(Objects.requireNonNull(Bukkit.getPlayer(player)), previousXP);
    }


    //Mob heads
    public int getLevelRequiredForHead(String mobType){
        if(!heads.containsKey(mobType)) heads.put(mobType, 0);
        return heads.get(mobType);
    }

    public boolean containsType(String type){
        return heads.containsKey(type);
    }


    //Boss bars
    private void addXPBossbar(Player player, int xp){
        removePreviousBossBars(player);

        NamespacedKey key = new NamespacedKey(plugin, "MPHeadsExp/add/"+player.getName());

        double extraXP = ((double) getXP(player.getUniqueId()) - getLevel(player.getUniqueId())*expPerLevel);

        KeyedBossBar bar = Bukkit.createBossBar(key,
                StringUtils.colorizeString('&',addedXPBossbarTitle
                        .replace("%xp%", String.valueOf(xp))
                        .replace("%next%", String.valueOf(expPerLevel-extraXP))),
                BarColor.GREEN,
                BarStyle.SEGMENTED_20);

        bar.setProgress(
                extraXP
                        /
                        (double)expPerLevel);

        bar.addPlayer(player);
        player.playSound(player.getLocation(),
                addedXPBossbarSound.getSound(),
                addedXPBossbarSound.getVolume(),
                addedXPBossbarSound.getPitch());

        bossBars.put(player, new PreviousBarObjects(bar, new BukkitRunnable() {
            @Override
            public void run(){
                removeBossBar(player, bossBars.get(player).getBossbar());
            }
        }.runTaskLater(plugin, 80)
                )
        );
    }


    private void levelUpBossbar(Player player, int previousLevel){
        removePreviousBossBars(player);

        NamespacedKey key = new NamespacedKey(plugin, "MPHeadsExp/levelUp/"+player.getName());
        KeyedBossBar bar = Bukkit.createBossBar(key,
                StringUtils.colorizeString('&', levelUpBossbarTitle
                        .replace("%previous%", String.valueOf(previousLevel))
                        .replace("%new%", String.valueOf(getLevel(player.getUniqueId())))),
                BarColor.GREEN,
                BarStyle.SEGMENTED_20);

        double extraXP = ((double) getXP(player.getUniqueId()) - getLevel(player.getUniqueId())*expPerLevel);
        bar.setProgress(
                extraXP
                        /
                        (double)expPerLevel);

        bar.addPlayer(player);
        player.playSound(player.getLocation(),
                levelUpXPBossbarSound.getSound(),
                levelUpXPBossbarSound.getVolume(),
                levelUpXPBossbarSound.getPitch());

        bossBars.put(player, new PreviousBarObjects(bar, new BukkitRunnable() {
                    @Override
                    public void run(){
                        removeBossBar(player, bossBars.get(player).getBossbar());
                    }
                }.runTaskLater(plugin, 80)
                )
        );

    }

    private void setXPBossbar(Player player, int previousXp){
        removePreviousBossBars(player);

        NamespacedKey key = new NamespacedKey(plugin, "MPHeadsExp/set/"+player.getName());
        KeyedBossBar bar = Bukkit.createBossBar(key,
                StringUtils.colorizeString('&', setXPBossbarTitle
                        .replace("%previous%", String.valueOf(previousXp))
                        .replace("%new%", String.valueOf(getXP(player.getUniqueId())))),
                BarColor.GREEN,
                BarStyle.SEGMENTED_20);

        double extraXP = ((double) getXP(player.getUniqueId()) - getLevel(player.getUniqueId())*expPerLevel);
        bar.setProgress(
                extraXP
                        /
                        (double)expPerLevel);

        bar.addPlayer(player);
        player.playSound(player.getLocation(),
                setXPBossbarSound.getSound(),
                setXPBossbarSound.getVolume(),
                setXPBossbarSound.getPitch());

        bossBars.put(player, new PreviousBarObjects(bar, new BukkitRunnable() {
                    @Override
                    public void run(){
                        removeBossBar(player, bossBars.get(player).getBossbar());
                    }
                }.runTaskLater(plugin, 80)
                )
        );

    }


    private void removePreviousBossBars(Player player){
        if(bossBars.containsKey(player)){
            bossBars.get(player).getTask().cancel();
            removeBossBar(player, bossBars.get(player).getBossbar());
        }
    }

    private void removeBossBar(Player player, KeyedBossBar bossbar){
        bossbar.removeAll();
        Bukkit.removeBossBar(bossbar.getKey());
        bossBars.remove(player);
    }



    //Util functions
    public void reload(){
        FileConfiguration config = plugin.getConfigYaml().getAccess();
        FileConfiguration messages = plugin.getLanguageYaml().getAccess();

        this.expPerLevel = Math.max(config.getInt("exp per level"), 1);

        this.addedXPBossbarTitle = messages.getString("added XP bossbar title");
        this.levelUpBossbarTitle = messages.getString("level up bossbar title");
        this.setXPBossbarTitle = messages.getString("set XP bossbar title");

        this.addedXPBossbarSound = new SoundSettings(
                config.getString("boss bars.added.sound.name"),
                config.getDouble("boss bars.added.sound.volume"),
                config.getDouble("boss bars.added.sound.pitch"));
        this.levelUpXPBossbarSound = new SoundSettings(
                config.getString("boss bars.level up.sound.name"),
                config.getDouble("boss bars.level up.sound.volume"),
                config.getDouble("boss bars.level up.sound.pitch"));
        this.setXPBossbarSound = new SoundSettings(
                config.getString("boss bars.set.sound.name"),
                config.getDouble("boss bars.set.sound.volume"),
                config.getDouble("boss bars.set.sound.pitch"));

        loadRequiredLevelsFromFile();
    }


    public void loadRequiredLevelsFromFile(){
        ConfigurationSection heads = plugin.getConfigYaml().getAccess().getConfigurationSection("heads");
        if(heads == null) return;

        for(String mobType : heads.getKeys(false)){
            this.heads.put(mobType, heads.getInt(mobType+".required level"));
        }
    }


    private void loadLevelsFromFile(){
        ConfigurationSection players = plugin.getPlayersYaml().getAccess().getConfigurationSection("players");
        if(players == null) return;

        for(String uuid : players.getKeys(false)){
            this.players.put(UUID.fromString(uuid), players.getInt(uuid));
        }
        plugin.getPlayersYaml().getAccess().set("players", null);
        plugin.getPlayersYaml().save();
    }

    public void saveLevelsToFile(){
        FileConfiguration players = plugin.getPlayersYaml().getAccess();

        for(UUID player : this.players.keySet()){
            players.set("players."+player, this.players.get(player));
        }
        plugin.getPlayersYaml().save();
    }
}

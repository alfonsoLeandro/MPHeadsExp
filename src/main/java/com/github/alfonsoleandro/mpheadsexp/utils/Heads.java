package com.github.alfonsoleandro.mpheadsexp.utils;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mputils.string.StringUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.*;

public class Heads implements Reloadable{

    private final HeadsExp plugin;
    private Map<EntityType, ItemStack> heads;
    private Map<String, ItemStack> playerHeads;
    private List<String> availableTypes;

    public Heads(HeadsExp plugin){
        this.plugin = plugin;
        preLoadHeads();
        fillAvailableMobTypes();
    }

    public List<String> getAvailableTypes(){
        return this.availableTypes;
    }


    public void preLoadHeads(){
        heads = new HashMap<>();
        playerHeads = new HashMap<>();

        //Mob heads
        ConfigurationSection mobHeads = plugin.getConfigYaml().getAccess().getConfigurationSection("heads");
        if(mobHeads == null) return;

        for(String mobType : mobHeads.getKeys(false)){
            heads.put(EntityType.valueOf(mobType), createSkull(mobHeads.getString(mobType+".url"),
                    mobType,
                    mobHeads.getInt(mobType+".exp"),
                    mobHeads.getInt(mobType+".price")));
        }

        //Player heads
        ConfigurationSection playerHeads = plugin.getConfigYaml().getAccess().getConfigurationSection("player heads");
        if(playerHeads == null) return;

        for(String playerName : playerHeads.getKeys(false)){
            this.playerHeads.put(playerName, createPlayerHead(playerName,
                    playerHeads.getDouble(playerName+".exp"),
                    playerHeads.getDouble(playerName+".balance")));
        }

    }

    public ItemStack getMobHead(EntityType entityType){
        if(heads.containsKey(entityType)) return heads.get(entityType).clone();

        FileConfiguration config = plugin.getConfigYaml().getAccess();
        if(config.contains("heads."+entityType.toString())) {
            return createSkull(config.getString("heads." + entityType.toString() + ".url"),
                    entityType.toString(),
                    config.getInt("heads." + entityType.toString() + ".exp"),
                    config.getInt("heads." + entityType.toString() + ".price"));
        }
        return null;
    }

    public ItemStack getPlayerHead(String playerName){
        FileConfiguration config = plugin.getConfigYaml().getAccess();

        if(playerHeads.containsKey(playerName)) return addPercentage(playerHeads.get(playerName),
                playerName,
                config.getDouble("player heads."+playerName+".balance"));

        if(config.getBoolean("default head enabled")) {
            return addPercentage(createPlayerHead(playerName,
                    config.getDouble("player heads.default head.exp"),
                    config.getDouble("player heads.default head.balance")),
                        playerName,
                        config.getDouble("player heads.default head.balance"));
        }
        return null;
    }


    private ItemStack createSkull(String url, String mobType, int xp, int price){
        String skullUrl = "http://textures.minecraft.net/texture/"+ url;
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        GameProfile profile = new GameProfile(UUID.fromString("8561b610-ad5c-390d-ac31-1a1d8ca69fd7"), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", skullUrl).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField;
        try {
            assert skullMeta != null;
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        FileConfiguration config = plugin.getConfigYaml().getAccess();
        skullMeta.setDisplayName(StringUtils.colorizeString('&', config.getString("heads name and lore.name")
                .replace("%type%", mobType)
                .replace("%xp%", String.valueOf(xp))
                .replace("%money%", String.valueOf(price))));

        List<String> lore = new ArrayList<>();
        for(String line : config.getStringList("heads name and lore.lore")){
            lore.add(StringUtils.colorizeString('&', line
                    .replace("%type%", mobType)
                    .replace("%xp%", String.valueOf(xp))
                    .replace("%money%", String.valueOf(price))));
        }
        skullMeta.setLore(lore);

        skullMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "MPHeads"), PersistentDataType.STRING, "HEAD:"+mobType);

        head.setItemMeta(skullMeta);

        return head;

    }

    private ItemStack createPlayerHead(String playerName, double xp, double price){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));

        FileConfiguration config = plugin.getConfigYaml().getAccess();
        skullMeta.setDisplayName(StringUtils.colorizeString('&', config.getString("heads name and lore.player heads.name")
                .replace("%player%", playerName)
                .replace("%xp%", String.valueOf(xp))
                .replace("%balance%", String.valueOf(price))));

        List<String> lore = new ArrayList<>();
        for(String line : config.getStringList("heads name and lore.player heads.lore")){
            lore.add(StringUtils.colorizeString('&', line
                    .replace("%player%", playerName)
                    .replace("%xp%", String.valueOf(xp))
                    .replace("%balance%", String.valueOf(price))));
        }
        skullMeta.setLore(lore);

        head.setItemMeta(skullMeta);

        return head;

    }


    private ItemStack addPercentage(ItemStack skull, String playerName, double percentage){
        if(skull == null || !skull.hasItemMeta()) return null;
        skull = skull.clone();
        ItemMeta meta = skull.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) return skull;
        double balance = plugin.getEconomy().getBalance(player);
        double amount = balance * (Math.min(percentage, 100.0) / 100.0);

        data.set(new NamespacedKey(plugin, "MPHeads"), PersistentDataType.STRING, "PLAYERHEAD:"+playerName+":"+amount);

        skull.setItemMeta(meta);

        return skull;
    }


    private void fillAvailableMobTypes(){
        availableTypes = new ArrayList<>();
        heads.keySet().forEach(type -> availableTypes.add(type.toString()));
    }

    public void reload(){
        this.preLoadHeads();
        this.fillAvailableMobTypes();
    }


}

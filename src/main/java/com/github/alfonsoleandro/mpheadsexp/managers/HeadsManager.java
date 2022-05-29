package com.github.alfonsoleandro.mpheadsexp.managers;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.managers.utils.MobHeadData;
import com.github.alfonsoleandro.mpheadsexp.managers.utils.PlayerHeadData;
import com.github.alfonsoleandro.mpheadsexp.managers.utils.PlayerHeadDataValues;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import com.github.alfonsoleandro.mputils.string.StringUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Stores all available heads. Manage Heads' prices and xp values.
 */
public class HeadsManager extends Reloadable {

    private final HeadsExp plugin;
    private final Settings settings;
    private final Map<String, MobHeadData> mobHeads;
    private final Map<String, PlayerHeadData> playerHeads;
    private final Map<String, PlayerHeadDataValues> playerHeadData;
    private final List<String> availableTypes;

    public HeadsManager(HeadsExp plugin){
        super(plugin);
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.mobHeads = new HashMap<>();
        this.playerHeads = new HashMap<>();
        this.playerHeadData = new HashMap<>();
        this.availableTypes = new ArrayList<>();
        loadHeads();
        fillAvailableMobTypes();
        fillPlayerHeadDataValues();
    }


    public void loadHeads(){
        //Mob heads
        ConfigurationSection mobHeads = this.plugin.getConfigYaml().getAccess().getConfigurationSection("heads");
        if(mobHeads != null) {
            for (String mobType : mobHeads.getKeys(false)) {
                this.mobHeads.put(mobType, new MobHeadData(mobHeads.getDouble(mobType + ".price"),
                                mobHeads.getInt(mobType + ".exp"),
                                mobType,
                                createSkull(mobHeads.getString(mobType + ".url"),
                                        mobType,
                                        mobHeads.getInt(mobType + ".exp"),
                                        mobHeads.getInt(mobType + ".price")
                                )
                        )
                );
            }
        }

        //Player heads
        ConfigurationSection playerHeads = this.plugin.getConfigYaml().getAccess().getConfigurationSection("player heads");
        if(playerHeads != null) {
            for (String playerName : playerHeads.getKeys(false)) {
                this.playerHeads.put(playerName, new PlayerHeadData(
                                playerHeads.getDouble(playerName + ".balance"),
                                playerHeads.getDouble(playerName + ".exp"),
                                playerName,
                                createPlayerHead(
                                        playerName,
                                        playerHeads.getDouble(playerName + ".exp"),
                                        playerHeads.getDouble(playerName + ".balance"))
                        )
                );
            }
        }

    }

    public MobHeadData getMobHeadData(String entityType){
        if(this.mobHeads.containsKey(entityType)) return this.mobHeads.get(entityType);
        return null;
    }

    public ItemStack getPlayerHead(String playerName){
        if(this.playerHeads.containsKey(playerName))
            return addPercentage(this.playerHeads.get(playerName).getHeadItem(),
                    playerName,
                    this.playerHeadData.get(playerName).getMoneyPercentage()
            );

        if(this.settings.isDefaultPlayerHeadEnabled()) {
            return addPercentage(createPlayerHead(playerName,
                            this.settings.getDefaultPlayerHeadExp(),
                            this.settings.getDefaultPlayerHeadBalance()),
                    playerName,
                    this.settings.getDefaultPlayerHeadBalance());
        }
        return null;
    }


    private ItemStack createSkull(String url, String mobType, int xp, int price){
        String skullUrl = "http://textures.minecraft.net/texture/"+ url;
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();

        //SET SKIN
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

        //SET NAME
        skullMeta.setDisplayName(StringUtils.colorizeString('&',
                this.settings.getHeadsName()
                        .replace("%type%", mobType)
                        .replace("%xp%", String.valueOf(xp))
                        .replace("%money%", String.valueOf(price)))
        );

        //SET LORE
        List<String> lore = new ArrayList<>();
        for(String line : this.settings.getHeadsLore()){
            lore.add(StringUtils.colorizeString('&', line
                    .replace("%type%", mobType)
                    .replace("%xp%", String.valueOf(xp))
                    .replace("%money%", String.valueOf(price))));
        }
        skullMeta.setLore(lore);

        skullMeta.getPersistentDataContainer().set(new NamespacedKey(this.plugin, "MPHeads"),
                PersistentDataType.STRING, "HEAD:"+mobType);

        head.setItemMeta(skullMeta);

        return head;
    }

    private ItemStack createPlayerHead(String playerName, double xp, double price){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));

        skullMeta.setDisplayName(StringUtils.colorizeString('&',
                this.settings.getPlayerHeadsName()
                        .replace("%player%", playerName)
                        .replace("%xp%", String.valueOf(xp))
                        .replace("%balance%", String.valueOf(price))));

        List<String> lore = new ArrayList<>();
        for(String line : this.settings.getPlayerHeadsLore()){
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
        assert meta != null;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        Player player = Bukkit.getPlayer(playerName);

        if(player == null) return skull;
        double balance = this.plugin.getEconomy().getBalance(player);
        double amount = balance * (Math.min(percentage, 100.0) / 100.0);

        data.set(new NamespacedKey(this.plugin, "MPHeads"),
                PersistentDataType.STRING, "PLAYER-HEAD:"+playerName+":"+amount);

        skull.setItemMeta(meta);

        return skull;
    }


    private void fillAvailableMobTypes(){
        this.availableTypes.clear();
        this.mobHeads.values().forEach(mh -> this.availableTypes.add(mh.getMobType()));
    }

    private void fillPlayerHeadDataValues(){
        this.playerHeadData.clear();
        ConfigurationSection playerHeadsSection = this.plugin.getConfigYaml().getAccess().getConfigurationSection("player heads");
        if(playerHeadsSection == null) return;
        playerHeadsSection.getKeys(false).forEach(pn ->
                this.playerHeadData.put(pn,
                        new PlayerHeadDataValues(playerHeadsSection.getDouble(pn+".balance percentage"),
                                playerHeadsSection.getDouble(pn+".exp")
                        )
                )
        );
    }


    public List<String> getAvailableTypes(){
        return this.availableTypes;
    }

    public PlayerHeadDataValues getPlayerHeadDataValues(String playerName){
        return this.playerHeadData.get(playerName);
    }

    public void reload(boolean deep){
        this.mobHeads.clear();
        this.playerHeads.clear();
        loadHeads();
        fillAvailableMobTypes();
        fillPlayerHeadDataValues();
    }


}

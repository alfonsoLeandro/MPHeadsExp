package com.github.alfonsoleandro.mpheadsexp.managers;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mputils.guis.navigation.GUIButton;
import com.github.alfonsoleandro.mputils.guis.navigation.NavigationBar;
import com.github.alfonsoleandro.mputils.itemstacks.MPItemStacks;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Settings extends Reloadable {

    private final HeadsExp plugin;

    //<editor-fold desc="Fields" default-state="collapsed">
    private boolean defaultPlayerHeadEnabled;
    private boolean allowPlacingHeads;
    private boolean showBarrierForLockedHeadsGUI;

    private int infoGUISize;
    private int infoInfoGUIItemSlot;
    private int infoUnlockedHeadsGUIItemSlot;
    private int infoSoldHeadsGUIItemSlot;

    private double defaultPlayerHeadExp;
    private double defaultPlayerHeadBalance;

    private String headsName;
    private String playerHeadsName;
    private String infoGUITitle;
    private String unlockedHeadsGUILockedHead;
    private String unlockedHeadsGUIUnlockedHead;
    private String unlockedHeadsGUIItemName;
    private String unlockedHeadsGUITitle;

    private List<String> headsLore;
    private List<String> playerHeadsLore;
    private List<String> unlockedHeadsGUIItemLore;

    private ItemStack infoInfoGUIItem;
    private ItemStack infoUnlockedHeadsGUIItem;
    private ItemStack infoSoldHeadsGUIItem;

    private NavigationBar navBar;
    //</editor-fold>

    public Settings(HeadsExp plugin) {
        super(plugin);
        this.plugin = plugin;
        loadFields();
    }

    private void loadFields() {
        FileConfiguration config = this.plugin.getConfigYaml().getAccess();

        this.defaultPlayerHeadEnabled = config.getBoolean("default head enabled");
        this.allowPlacingHeads = config.getBoolean("allow placing");
        this.showBarrierForLockedHeadsGUI = config.getBoolean("unlocked heads gui.show barrier for locked");

        this.infoGUISize = config.getInt("info gui.size");
        this.infoInfoGUIItemSlot = config.getInt("info gui.info item.slot");
        this.infoUnlockedHeadsGUIItemSlot = config.getInt("info gui.unlocked heads item.slot");
        this.infoSoldHeadsGUIItemSlot = config.getInt("info gui.sold heads item.slot");

        this.defaultPlayerHeadExp = config.getDouble("player heads.default head.exp");
        this.defaultPlayerHeadBalance = config.getDouble("player heads.default head.balance");

        this.headsName = config.getString("heads name and lore.name");
        this.playerHeadsName = config.getString("heads name and lore.player heads.name");
        this.infoGUITitle = config.getString("info gui.title");
        this.unlockedHeadsGUILockedHead = config.getString("unlocked heads gui.locked");
        this.unlockedHeadsGUIUnlockedHead = config.getString("unlocked heads gui.unlocked");
        this.unlockedHeadsGUIItemName = config.getString("unlocked heads gui.heads.name");
        this.unlockedHeadsGUITitle = config.getString("unlocked heads gui.title");

        this.headsLore = config.getStringList("heads name and lore.lore");
        this.playerHeadsLore = config.getStringList("heads name and lore.player heads.lore");
        this.unlockedHeadsGUIItemLore = config.getStringList("unlocked heads gui.heads.lore");

        this.infoInfoGUIItem = MPItemStacks.newItemStack(Material.PLAYER_HEAD, 1,
                config.getString("info gui.info item.name"),
                config.getStringList("info gui.info item.lore"));
        this.infoUnlockedHeadsGUIItem = MPItemStacks.newItemStack(Material.PLAYER_HEAD, 1,
                config.getString("info gui.unlocked heads item.name"),
                config.getStringList("info gui.unlocked heads item.lore"));
        this.infoSoldHeadsGUIItem = MPItemStacks.newItemStack(Material.PLAYER_HEAD, 1,
                config.getString("info gui.sold heads item.name"),
                config.getStringList("info gui.sold heads item.lore"));

        ItemStack previousPageItem = MPItemStacks.newItemStack(
                Material.valueOf(config.getString("gui navigation bar items.previous page.item")),
                1,
                config.getString("gui navigation bar items.previous page.name"),
                config.getStringList("gui navigation bar items.previous page.lore"));
        ItemStack currentPageItem = MPItemStacks.newItemStack(
                Material.valueOf(config.getString("gui navigation bar items.current page.item")),
                1,
                config.getString("gui navigation bar items.current page.name"),
                config.getStringList("gui navigation bar items.current page.lore"));
        ItemStack nextPageItem = MPItemStacks.newItemStack(
                Material.valueOf(config.getString("gui navigation bar items.next page.item")),
                1,
                config.getString("gui navigation bar items.next page.name"),
                config.getStringList("gui navigation bar items.next page.lore"));
        ItemStack navBarItem = MPItemStacks.newItemStack(
                Material.valueOf(config.getString("gui navigation bar items.navBar item.item")),
                1,
                config.getString("gui navigation bar items.navBar item.name"),
                config.getStringList("gui navigation bar items.navBar item.lore"));


        this.navBar = new NavigationBar();
        this.navBar.setButtonAt(0, new GUIButton("MPHeads:previousPage",
                previousPageItem,
                GUIButton.GUIButtonCondition.HAS_PREVIOUS_PAGE,
                navBarItem));
        this.navBar.setButtonAt(4, new GUIButton("MPHeads:currentPage",
                currentPageItem,
                GUIButton.GUIButtonCondition.ALWAYS,
                null));
        this.navBar.setButtonAt(8, new GUIButton("MPHeads:nextPage",
                nextPageItem,
                GUIButton.GUIButtonCondition.HAS_NEXT_PAGE,
                navBarItem));
        for (int i : new int[]{1,2,3,5,6,7}) {
            this.navBar.setButtonAt(i, new GUIButton("MPHeads:navBar",
                    navBarItem,
                    GUIButton.GUIButtonCondition.ALWAYS,
                    null));
        }
    }


    //<editor-fold desc="Getters" default-state="collapsed">
    public boolean isDefaultPlayerHeadEnabled() {
        return this.defaultPlayerHeadEnabled;
    }

    public boolean isAllowPlacingHeads() {
        return this.allowPlacingHeads;
    }

    public boolean isShowBarrierForLockedHeadsGUI() {
        return this.showBarrierForLockedHeadsGUI;
    }


    public int getInfoGUISize() {
        return this.infoGUISize;
    }

    public int getInfoInfoGUIItemSlot() {
        return this.infoInfoGUIItemSlot;
    }

    public int getInfoUnlockedHeadsGUIItemSlot() {
        return this.infoUnlockedHeadsGUIItemSlot;
    }

    public int getInfoSoldHeadsGUIItemSlot() {
        return this.infoSoldHeadsGUIItemSlot;
    }


    public double getDefaultPlayerHeadExp() {
        return this.defaultPlayerHeadExp;
    }

    public double getDefaultPlayerHeadBalance() {
        return this.defaultPlayerHeadBalance;
    }


    public String getHeadsName() {
        return this.headsName;
    }

    public String getPlayerHeadsName() {
        return this.playerHeadsName;
    }

    public String getInfoGUITitle() {
        return this.infoGUITitle;
    }

    public String getUnlockedHeadsGUILockedHead() {
        return this.unlockedHeadsGUILockedHead;
    }

    public String getUnlockedHeadsGUIUnlockedHead() {
        return this.unlockedHeadsGUIUnlockedHead;
    }

    public String getUnlockedHeadsGUIItemName() {
        return this.unlockedHeadsGUIItemName;
    }

    public String getUnlockedHeadsGUITitle() {
        return this.unlockedHeadsGUITitle;
    }


    public List<String> getHeadsLore() {
        return this.headsLore;
    }

    public List<String> getPlayerHeadsLore() {
        return this.playerHeadsLore;
    }

    public List<String> getUnlockedHeadsGUIItemLore() {
        return this.unlockedHeadsGUIItemLore;
    }


    public ItemStack getInfoInfoGUIItem() {
        return this.infoInfoGUIItem.clone();
    }

    public ItemStack getInfoUnlockedHeadsGUIItem() {
        return this.infoUnlockedHeadsGUIItem.clone();
    }

    public ItemStack getInfoSoldHeadsGUIItem() {
        return this.infoSoldHeadsGUIItem.clone();
    }


    public NavigationBar getNavBar() {
        return this.navBar;
    }
    //</editor-fold>

    @Override
    public void reload(boolean deep) {
        this.loadFields();
    }
}
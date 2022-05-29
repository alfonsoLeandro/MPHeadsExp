package com.github.alfonsoleandro.mpheadsexp.managers;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import jdk.javadoc.internal.doclets.formats.html.markup.Head;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Settings extends Reloadable {

    private final HeadsExp plugin;
    private String headsName;
    private List<String> headsLore;
    private String playerHeadsName;
    private List<String> playerHeadsLore;

    public Settings(HeadsExp plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    private void loadFields() {
        FileConfiguration config = this.plugin.getConfigYaml().getAccess();
        //TODO
        this.headsName = config.getString("heads name and lore.name");
        this.headsLore = config.getStringList("heads name and lore.lore");
        this.playerHeadsName = config.getString("heads name and lore.player heads.name");
        this.playerHeadsLore = config.getStringList("heads name and lore.player heads.lore");

    }


    //<editor-fold desc="Getters" default-state="collapsed">
    public String getHeadsName() {
        return this.headsName;
    }

    public String getPlayerHeadsName() {
        return this.playerHeadsName;
    }

    public List<String> getHeadsLore() {
        return this.headsLore;
    }

    public List<String> getPlayerHeadsLore() {
        return this.playerHeadsLore;
    }

    //</editor-fold>

    @Override
    public void reload(boolean deep) {
        this.loadFields();
    }
}
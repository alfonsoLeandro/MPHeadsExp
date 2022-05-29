package com.github.alfonsoleandro.mpheadsexp.managers;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings extends Reloadable {

    private final HeadsExp plugin;

    public Settings(HeadsExp plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    private void loadFields() {
        FileConfiguration config = this.plugin.getConfigYaml().getAccess();
        //TODO

    }


    @Override
    public void reload(boolean deep) {
        this.loadFields();
    }
}
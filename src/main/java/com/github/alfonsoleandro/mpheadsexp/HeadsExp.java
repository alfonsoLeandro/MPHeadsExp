package com.github.alfonsoleandro.mpheadsexp;

import com.github.alfonsoleandro.mpheadsexp.commands.HeadsCommand;
import com.github.alfonsoleandro.mpheadsexp.commands.tabcompleters.HeadsCommandTabAutoCompleter;
import com.github.alfonsoleandro.mpheadsexp.commands.MainCommand;
import com.github.alfonsoleandro.mpheadsexp.commands.tabcompleters.MainCommandTabAutoCompleter;
import com.github.alfonsoleandro.mpheadsexp.listeners.HeadBreakListener;
import com.github.alfonsoleandro.mpheadsexp.listeners.HeadPlaceListener;
import com.github.alfonsoleandro.mpheadsexp.listeners.InfoGUIClickListener;
import com.github.alfonsoleandro.mpheadsexp.listeners.PlayerKillMobListener;
import com.github.alfonsoleandro.mpheadsexp.managers.HeadsManager;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.managers.Settings;
import com.github.alfonsoleandro.mpheadsexp.utils.*;
import com.github.alfonsoleandro.mputils.files.YamlFile;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.reloadable.ReloaderPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public final class HeadsExp extends ReloaderPlugin {

    private final String version = getDescription().getVersion();
    private final char color = 'e';
    //Managers
    private MessageSender<Message> messageSender;
    private LevelsManager levelsManager;
    private HeadsManager headsManager;
    private Settings settings;
    //Hooks
    private Economy economy;
    private PAPIPlaceholder papiExpansion;
    //YAML files
    private YamlFile configYaml;
    private YamlFile languageYaml;
    private YamlFile playersYaml;
    private YamlFile recordsYaml;


    /**
     * Plugin enable logic.
     */
    @Override
    public void onEnable() {
        registerFiles();
        this.settings = new Settings(this);
        this.messageSender = new MessageSender<>(this, Message.values(), this.languageYaml, "prefix");
        this.messageSender.send("&aEnabled&f. Version: &e" + this.version);
        this.messageSender.send("&fThank you for using my plugin! &" + this.color + getDescription().getName() + "&f By " + getDescription().getAuthors().get(0));
        this.messageSender.send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        this.messageSender.send("Please consider subscribing to my yt channel: &c" + getDescription().getWebsite());
        this.headsManager = new HeadsManager(this);
        this.levelsManager = new LevelsManager(this);
        registerPAPIExpansion();
        if(setupEconomy()){
            this.messageSender.send("&aPlugin Vault and economy found, economy hooked");
        }else {
            this.messageSender.send("&cPlugin Vault or an economy plugin not found, disabling LoveEXP");
            setEnabled(false);
            return;
        }
        registerEvents();
        registerCommands();
    }

    /**
     * Plugin disable logic.
     */
    @Override
    public void onDisable() {
        this.messageSender.send("&cDisabled&f. Version: &e" + this.version);
        this.messageSender.send("&fThank you for using my plugin! &" + this.color + getDescription().getName() + "&f By " + getDescription().getAuthors().get(0));
        this.messageSender.send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        this.messageSender.send("Please consider subscribing to my yt channel: &c" + getDescription().getWebsite());
        this.levelsManager.saveLevelsToFile();
        this.unRegisterPAPIExpansion();
    }


    public boolean setupEconomy() {
        if(!getServer().getPluginManager().isPluginEnabled("Vault")) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null) return false;
        this.economy = rsp.getProvider();
        return true;
    }


    /**
     * Gets the plugins current version.
     * @return The version string.
     */
    public String getVersion() {
        return this.version;
    }



    /**
     * Registers plugin files.
     */
    public void registerFiles() {
        this.configYaml = new YamlFile(this, "config.yml");
        this.languageYaml = new YamlFile(this, "language.yml");
        this.playersYaml = new YamlFile(this, "players.yml");
        this.recordsYaml = new YamlFile(this, "selling records.yml");
    }

    /**
     * Reloads plugin files.
     */
    public void reloadFiles() {
        this.configYaml.loadFileConfiguration();
        this.languageYaml.loadFileConfiguration();
        this.playersYaml.loadFileConfiguration();
        this.recordsYaml.loadFileConfiguration();
    }


    /**
     * Registers the event listeners.
     */
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerKillMobListener(this), this);
        pm.registerEvents(new HeadPlaceListener(this), this);
        pm.registerEvents(new InfoGUIClickListener(this), this);
        pm.registerEvents(new HeadBreakListener(this), this);
    }


    /**
     * Registers commands and command classes.
     */
    private void registerCommands() {
        PluginCommand mainCommand = getCommand("headsExp");
        PluginCommand headsCommand = getCommand("heads");

        if(mainCommand == null || headsCommand == null){
            this.messageSender.send("&cCommands were not registered properly.");
            this.messageSender.send("&cPlease check your plugin.yml is valid. Disabling LoveExp.");
            setEnabled(false);
            return;
        }

        mainCommand.setExecutor(new MainCommand(this));
        mainCommand.setTabCompleter(new MainCommandTabAutoCompleter(this));
        headsCommand.setExecutor(new HeadsCommand(this));
        headsCommand.setTabCompleter(new HeadsCommandTabAutoCompleter());
    }

    public void reload(boolean deep){
        reloadFiles();
        this.settings.reload(deep); //Settings need to be reloaded before HeadsManager, maybe assign priorities to reloadables?
        super.reload(deep);
    }


    public void registerPAPIExpansion(){
        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            this.messageSender.send("&aPlaceholderAPI found, the placeholder has been registered successfully");
            this.papiExpansion = new PAPIPlaceholder(this);
            this.papiExpansion.register();
        }else{
            this.messageSender.send("&cPlaceholderAPI not found, the placeholder was not registered");
        }
    }

    public void unRegisterPAPIExpansion(){
        if(this.papiExpansion != null) this.papiExpansion.unregister();
    }

    public MessageSender<Message> getMessageSender(){
        return this.messageSender;
    }

    public LevelsManager getLevelsManager(){
        return this.levelsManager;
    }


    public HeadsManager getHeadsManager(){
        return this.headsManager;
    }

    public Settings getSettings() {
        return this.settings;
    }

    public Economy getEconomy(){
        return this.economy;
    }

    @Override
    public @NotNull FileConfiguration getConfig(){
        return getConfigYaml().getAccess();
    }

    /**
     * Get the config YamlFile.
     * @return The YamlFile containing the config file.
     */
    public YamlFile getConfigYaml(){
        return this.configYaml;
    }

    /**
     * Get the language YamlFile.
     * @return The YamlFile containing the language file.
     */
    public YamlFile getLanguageYaml(){
        return this.languageYaml;
    }

    /**
     * Get the players YamlFile.
     * @return The YamlFile containing the players file.
     */
    public YamlFile getPlayersYaml(){
        return this.playersYaml;
    }

    /**
     * Get the selling records YamlFile.
     * @return The YamlFile containing the selling records file.
     */
    public YamlFile getRecordsYaml(){
        return this.recordsYaml;
    }



}

package com.github.alfonsoleandro.mpheadsexp;

import com.github.alfonsoleandro.mpheadsexp.commands.HeadsCommand;
import com.github.alfonsoleandro.mpheadsexp.commands.HeadsCommandTabAutoCompleter;
import com.github.alfonsoleandro.mpheadsexp.commands.MainCommand;
import com.github.alfonsoleandro.mpheadsexp.commands.MainCommandTabAutoCompleter;
import com.github.alfonsoleandro.mpheadsexp.events.HeadPlaceEvent;
import com.github.alfonsoleandro.mpheadsexp.events.InfoGUIClickEvent;
import com.github.alfonsoleandro.mpheadsexp.events.PlayerKillMobEvent;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.utils.Heads;
import com.github.alfonsoleandro.mpheadsexp.utils.Logger;
import com.github.alfonsoleandro.mpheadsexp.utils.PAPIPlaceholder;
import com.github.alfonsoleandro.mpheadsexp.utils.Reloadable;
import com.github.alfonsoleandro.mputils.files.YamlFile;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class HeadsExp extends JavaPlugin {

    final private Set<Reloadable> reloadables = new HashSet<>();
    final private PluginDescriptionFile pdfFile = getDescription();
    final private String version = pdfFile.getVersion();
    final private char color = 'e';
    private Logger logger;
    private LevelsManager levelsManager;
    private Heads heads;
    private Economy economy;
    private PAPIPlaceholder papiExpansion;
    private YamlFile configYaml;
    private YamlFile messagesYaml;
    private YamlFile playersYaml;
    private YamlFile recordsYaml;


    /**
     * Plugin enable logic.
     */
    @Override
    public void onEnable() {
        reloadFiles();
        this.logger = new Logger(this);
        this.logger.send("&aEnabled&f. Version: &e" + version);
        this.logger.send("&fThank you for using my plugin! &" + color + pdfFile.getName() + "&f By " + pdfFile.getAuthors().get(0));
        this.logger.send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        this.logger.send("Please consider subscribing to my yt channel: &c" + pdfFile.getWebsite());
        this.levelsManager = new LevelsManager(this);
        this.heads = new Heads(this);
        this.registerPAPIExpansion();
        if(setupEconomy()){
            this.logger.send("&aPlugin Vault and economy found, economy hooked");
        }else {
            this.logger.send("&cPlugin Vault or an economy plugin not found, disabling LoveEXP");
            this.setEnabled(false);
            return;
        }
        registerEvents();
        registerCommands();
        reloadables.addAll(Arrays.asList(logger, levelsManager, heads));
    }

    /**
     * Plugin disable logic.
     */
    @Override
    public void onDisable() {
        this.logger.send("&cDisabled&f. Version: &e" + version);
        this.logger.send("&fThank you for using my plugin! &" + color + pdfFile.getName() + "&f By " + pdfFile.getAuthors().get(0));
        this.logger.send("&fJoin my discord server at &chttps://discordapp.com/invite/ZznhQud");
        this.logger.send("Please consider subscribing to my yt channel: &c" + pdfFile.getWebsite());
        this.levelsManager.saveLevelsToFile();
        this.unRegisterPAPIExpansion();
    }


    public boolean setupEconomy() {
        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if(vault == null || !vault.isEnabled()) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp==null) {
            return false;
        }
        economy = rsp.getProvider();
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
     * Registers and reloads plugin files.
     */
    public void reloadFiles() {
        configYaml = new YamlFile(this, "config.yml");
        messagesYaml = new YamlFile(this, "messages.yml");
        playersYaml = new YamlFile(this, "players.yml");
        recordsYaml = new YamlFile(this, "selling records.yml");
    }


    /**
     * Registers the event listeners.
     */
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerKillMobEvent(this), this);
        pm.registerEvents(new HeadPlaceEvent(this), this);
        pm.registerEvents(new InfoGUIClickEvent(this), this);
    }


    /**
     * Registers commands and command classes.
     */
    private void registerCommands() {
        PluginCommand mainCommand = getCommand("headsExp");
        PluginCommand headsCommand = getCommand("heads");

        if(mainCommand == null || headsCommand == null){
            this.logger.send("&cCommands were not registered properly.");
            this.logger.send("&cPlease check your plugin.yml is valid. Disabling LoveExp.");
            this.setEnabled(false);
            return;
        }

        MainCommand mainCommandExecutor = new MainCommand(this);
        HeadsCommand headsCommandExecutor = new HeadsCommand(this);
        reloadables.add(mainCommandExecutor);
        reloadables.add(headsCommandExecutor);


        mainCommand.setExecutor(mainCommandExecutor);
        mainCommand.setTabCompleter(new MainCommandTabAutoCompleter(this));
        headsCommand.setExecutor(headsCommandExecutor);
        headsCommand.setTabCompleter(new HeadsCommandTabAutoCompleter());
    }

    public void reload(){
        this.reloadFiles();
        for(Reloadable reloadable : reloadables){
            reloadable.reload();
        }
    }

    public Logger getConsoleLogger(){
        return this.logger;
    }

    public LevelsManager getLevelsManager(){
        return this.levelsManager;
    }


    public Heads getHeads(){
        return this.heads;
    }


    public Economy getEconomy(){
        return this.economy;
    }

    public void registerPAPIExpansion(){
        Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if(papi != null && papi.isEnabled()){
            logger.send("&aPlaceholderAPI found, the placeholder has been registered successfully");
            papiExpansion = new PAPIPlaceholder(this);
            papiExpansion.register();
        }else{
            logger.send("&cPlaceholderAPI not found, the placeholder was not registered");
        }
    }

    public void unRegisterPAPIExpansion(){
        try{
            papiExpansion.unregister();
        }catch (Exception ignored){
            assert true;
        }
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
     * Get the messages YamlFile.
     * @return The YamlFile containing the messages file.
     */
    public YamlFile getMessagesYaml(){
        return this.messagesYaml;
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

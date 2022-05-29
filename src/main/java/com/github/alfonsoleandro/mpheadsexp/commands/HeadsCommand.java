package com.github.alfonsoleandro.mpheadsexp.commands;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandHelpHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandInfoHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandSellHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandWorthHandler;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import com.github.alfonsoleandro.mputils.guis.SimpleGUI;
import com.github.alfonsoleandro.mputils.itemstacks.MPItemStacks;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import com.github.alfonsoleandro.mputils.string.StringUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

//TODO Move to command handlers only
public class HeadsCommand extends Reloadable implements CommandExecutor {

    final private HeadsExp plugin;
    final private MessageSender<Message> messageSender;
    private final AbstractHandler commandHandler;
    //Messages
    private String noPerm;
    private String unknown;
    private String mstHoldHead;
    private String headSold;
    private String playerHeadSold;
    private String headWorth;

    /**
     * MainCommand class constructor.
     * @param plugin The main class instance.
     */
    public HeadsCommand(HeadsExp plugin){
        super(plugin);
        this.plugin = plugin;
        this.messageSender = plugin.getMessageSender();
        this.commandHandler = new HeadsCommandHelpHandler(plugin,
                new HeadsCommandWorthHandler(plugin,
                        new HeadsCommandSellHandler(plugin,
                                new HeadsCommandInfoHandler(plugin, null)
                        )
                )
        );
        loadMessages();
    }

    private void loadMessages(){
        FileConfiguration messages = plugin.getLanguageYaml().getAccess();

        noPerm = messages.getString("no permission");
        unknown = messages.getString("unknown command");
        mstHoldHead = messages.getString("must be holding head");
        headSold = messages.getString("head sold");
        playerHeadSold = messages.getString("player head sold");
        headWorth = messages.getString("head worth");
    }



    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        this.commandHandler.handle(sender, label, args);
        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {

        }else if(args[0].equalsIgnoreCase("sell")) {
            if(sender instanceof ConsoleCommandSender) {
                messageSender.send(sender, "&cThat command can only be sent by a player.");
                return true;
            }
            if(notHasPerm(sender, "headsExp.sell")) return true;
            Player player = (Player) sender;
            ItemStack inHand = player.getInventory().getItemInMainHand();

            if(!inHand.getType().equals(Material.PLAYER_HEAD) ||
                    !inHand.hasItemMeta()) {
                messageSender.send(sender, mstHoldHead);
                return true;
            }
            PersistentDataContainer data = inHand.getItemMeta().getPersistentDataContainer();
            boolean isNotHead = true;
            boolean isPlayerHead = false;
            String mobType = "";
            double price = 0;

            for (NamespacedKey key : data.getKeys()) {
                if(data.has(key, PersistentDataType.STRING)) {
                    String string = data.get(key, PersistentDataType.STRING);
                    if(string.startsWith("HEAD") || string.startsWith("PLAYERHEAD")) {
                        isNotHead = false;
                        if(string.startsWith("PLAYERHEAD")){
                            isPlayerHead = true;
                            price = Double.parseDouble(string.split(":")[2]);
                        }
                        mobType = string.split(":")[1];
                        break;
                    }
                }
            }

            if(isNotHead) {
                messageSender.send(sender, mstHoldHead);
                return true;
            }
            if(isPlayerHead){
                FileConfiguration config = plugin.getConfigYaml().getAccess();
                Economy economy = plugin.getEconomy();
                LevelsManager manager = plugin.getLevelsManager();

                int xp = config.getInt("player heads."+ (config.contains("player heads."+mobType) ?
                        mobType : "default head") + ".exp");
                int amount = inHand.getAmount();
                price *= amount;

                inHand.setAmount(0);

                economy.depositPlayer(player, price);
                manager.addXP(player.getUniqueId(), xp);

                messageSender.send(player, playerHeadSold
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%player%", mobType)
                        .replace("%price%", String.valueOf(price))
                        .replace("%xp%", String.valueOf(xp))
                        .replace("%totalxp%", String.valueOf(manager.getXP(player.getUniqueId())))
                );


                FileConfiguration records = plugin.getRecordsYaml().getAccess();
                records.set("records."+player.getName()+".players."+mobType, records.getInt("records."+player.getName()+"."+mobType)+amount);
                plugin.getRecordsYaml().save();

                return true;
            }


            FileConfiguration config = plugin.getConfigYaml().getAccess();
            Economy economy = plugin.getEconomy();
            LevelsManager manager = plugin.getLevelsManager();

            int amount = inHand.getAmount();
            price = config.getDouble("heads." + mobType + ".price") * amount;
            int xp = config.getInt("heads." + mobType + ".exp") * amount;

            inHand.setAmount(0);

            economy.depositPlayer(player, price);
            manager.addXP(player.getUniqueId(), xp);

            messageSender.send(player, headSold
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%type%", mobType)
                    .replace("%price%", String.valueOf(price))
                    .replace("%xp%", String.valueOf(xp))
                    .replace("%totalxp%", String.valueOf(manager.getXP(player.getUniqueId())))
            );

            FileConfiguration records = plugin.getRecordsYaml().getAccess();
            records.set("records."+player.getName()+"."+mobType, records.getInt("records."+player.getName()+"."+mobType)+amount);
            plugin.getRecordsYaml().save();





        }else if(args[0].equalsIgnoreCase("info")){
            if(sender instanceof ConsoleCommandSender) {
                messageSender.send(sender, "&cThat command can only be sent by a player.");
                return true;
            }
            if(notHasPerm(sender, "headsExp.info")) return true;
            Player player = (Player) sender;
            openInfoGUI(player);







            //unknown command
        }else {
            messageSender.send(sender, unknown.replace("%command%", label));
        }



        return true;
    }


    private void openInfoGUI(Player player){
        FileConfiguration config = plugin.getConfigYaml().getAccess();
        String title = config.getString("info gui.title");
        int size = config.getInt("info gui.size");

        SimpleGUI infoGUI = new SimpleGUI(StringUtils.colorizeString('&', title), size, "MPHeadsExp:info");

        LevelsManager manager = plugin.getLevelsManager();
        Map<String,String> placeHolders = new HashMap<>();
        placeHolders.put("%player%", player.getName());
        placeHolders.put("%xp%", String.valueOf(manager.getXP(player.getUniqueId())));
        placeHolders.put("%level%", String.valueOf(manager.getLevel(player.getUniqueId())));

        ItemStack info = MPItemStacks.replacePlaceholders(
                MPItemStacks.newItemStack(
                        Material.PLAYER_HEAD,
                        1,
                        StringUtils.colorizeString('&', config.getString("info gui.info item.name")),
                        config.getStringList("info gui.info item.lore")), placeHolders);

        ItemStack unlocked = MPItemStacks.replacePlaceholders(
                MPItemStacks.newItemStack(
                        Material.BARRIER,
                        1,
                        StringUtils.colorizeString('&', config.getString("info gui.unlocked heads item.name")),
                        config.getStringList("info gui.unlocked heads item.lore")), placeHolders);

        ItemStack sold = MPItemStacks.replacePlaceholders(
                MPItemStacks.newItemStack(
                        Material.GOLD_INGOT,
                        1,
                        StringUtils.colorizeString('&', config.getString("info gui.sold heads item.name")),
                        config.getStringList("info gui.sold heads item.lore")), placeHolders);

        infoGUI.setItem(config.getInt("info gui.info item.slot"), info);
        infoGUI.setItem(config.getInt("info gui.unlocked heads item.slot"), unlocked);
        infoGUI.setItem(config.getInt("info gui.sold heads item.slot"), sold);


        infoGUI.openGUI(player);
    }


    private boolean notHasPerm(CommandSender sender, String permission){
        if(!sender.hasPermission(permission)){
            messageSender.send(sender, noPerm);
            return true;
        }
        return false;
    }

    public void reload(boolean deep){
        this.loadMessages();
    }

}

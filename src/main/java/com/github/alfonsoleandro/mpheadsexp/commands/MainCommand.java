package com.github.alfonsoleandro.mpheadsexp.commands;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.MCHelpHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.MCReloadHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.MCVersionHandler;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class MainCommand extends Reloadable implements CommandExecutor {

    final private HeadsExp plugin;
    final private MessageSender<Message> messageSender;
    private final AbstractHandler commandHandler;
    //Messages
    private String noPerm;
    private String reloaded;
    private String unknown;
    private String playerNotFound;
    private String errorNumber;
    private String addedXP;
    private String seeXP;
    private String setXP;
    private String invalidNumber;
    private String invalidType;
    private String selfAdded;
    private String addedToSmn;
    private String smnAdded;

    /**
     * MainCommand class constructor.
     * @param plugin The main class instance.
     */
    public MainCommand(HeadsExp plugin){
        super(plugin);
        this.plugin = plugin;
        this.messageSender = plugin.getMessageSender();
        this.commandHandler = new MCHelpHandler(plugin,
                new MCVersionHandler(plugin,
                        new MCReloadHandler(plugin, null)
                )
        );
        loadMessages();
    }

    private void loadMessages(){
        FileConfiguration messages = plugin.getLanguageYaml().getAccess();

        noPerm = messages.getString("no permission");
        reloaded = messages.getString("reloaded");
        unknown = messages.getString("unknown command");
        playerNotFound = messages.getString("player not found");
        errorNumber = messages.getString("error amount");
        addedXP = messages.getString("added xp");
        seeXP = messages.getString("see xp");
        setXP = messages.getString("set xp");
        invalidNumber = messages.getString("invalid number");
        invalidType = messages.getString("invalid type");
        selfAdded = messages.getString("self added");
        addedToSmn = messages.getString("added to someone");
        smnAdded = messages.getString("someone added");
    }



    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        this.commandHandler.handle(sender, label, args);

        if(args.length == 0 || args[0].equalsIgnoreCase("help")) {

        }else if(args[0].equalsIgnoreCase("xp")) {
            if(args.length < 3 || (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("see") && !args[1].equalsIgnoreCase("set"))) {
                messageSender.send(sender, "&cUse: &f/" + label + " xp (add/set/see) (player) (amount)");
                return true;
            }

            if(args[1].equalsIgnoreCase("add")) {
                if(args.length < 4) {
                    messageSender.send(sender, "&cUse: &f/" + label + " xp add (player) (amount)");
                    return true;
                }
                if(notHasPerm(sender, "headsExp.xp.add")) return true;
                Player player = Bukkit.getPlayer(args[2]);
                if(player == null) {
                    messageSender.send(sender, playerNotFound.replace("%name%", args[2]));
                    return true;
                }
                int xp;
                try {
                    xp = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    messageSender.send(sender, errorNumber.replace("%input%", args[3]));
                    return true;
                }
                LevelsManager manager = plugin.getLevelsManager();

                manager.addXP(player.getUniqueId(), xp);
                messageSender.send(sender, addedXP
                        .replace("%xp%", String.valueOf(xp))
                        .replace("%player%", args[2])
                        .replace("%total%", String.valueOf(manager.getXP(player.getUniqueId())))
                        .replace("%level%", String.valueOf(manager.getLevel(player.getUniqueId()))));


            } else if(args[1].equalsIgnoreCase("see")) {
                if(notHasPerm(sender, "headsExp.xp.see")) return true;
                Player player = Bukkit.getPlayer(args[2]);
                if(player == null) {
                    messageSender.send(sender, playerNotFound.replace("%name%", args[2]));
                    return true;
                }
                LevelsManager manager = plugin.getLevelsManager();

                messageSender.send(sender, seeXP
                        .replace("%player%", args[2])
                        .replace("%xp%", String.valueOf(manager.getXP(player.getUniqueId())))
                        .replace("%level%", String.valueOf(manager.getLevel(player.getUniqueId()))));


            } else {
                if(args.length < 4) {
                    messageSender.send(sender, "&cUse: &f/" + label + " xp set (player) (amount)");
                    return true;
                }
                if(notHasPerm(sender, "headsExp.xp.set")) return true;
                Player player = Bukkit.getPlayer(args[2]);
                if(player == null) {
                    messageSender.send(sender, playerNotFound.replace("%name%", args[2]));
                    return true;
                }
                int xp;
                try {
                    xp = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    messageSender.send(sender, errorNumber.replace("%input%", args[3]));
                    return true;
                }
                LevelsManager manager = plugin.getLevelsManager();

                manager.setXP(player.getUniqueId(), xp);
                messageSender.send(sender, setXP
                        .replace("%player%", args[2])
                        .replace("%xp%", String.valueOf(manager.getXP(player.getUniqueId())))
                        .replace("%level%", String.valueOf(manager.getLevel(player.getUniqueId()))));

            }


        }else if(args[0].equalsIgnoreCase("giveHead")) {
            if(args.length < 3) {
                messageSender.send(sender, "&cUse: &f/" + label + " giveHead (player) (type) <amount>");
                return true;
            }
            if(notHasPerm(sender, "headsExp.giveHead")) return true;

            int amount = 1;
            if(args.length > 3) {
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    messageSender.send(sender, invalidNumber.replace("%input%", args[3]));
                    return true;
                }
            }

            Player player = Bukkit.getPlayer(args[1]);
            if(player == null) {
                messageSender.send(sender, playerNotFound.replace("%name%", args[1]));
                return true;
            }
            String headType = args[2].toUpperCase(Locale.ROOT);
            if(!plugin.getLevelsManager().containsType(headType)) {
                messageSender.send(sender, invalidType.replace("%input%", headType));
                return true;
            }

            ItemStack item = plugin.getHeadsManager().getMobHead(EntityType.valueOf(headType));
            item.setAmount(amount);
            player.getInventory().addItem(item);

            if(player.equals(sender)) {
                messageSender.send(sender, selfAdded
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", headType));
            } else {
                messageSender.send(sender, addedToSmn
                        .replace("%player%", args[1])
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", headType));
                messageSender.send(player, smnAdded
                        .replace("%player%", sender.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%type%", headType));
            }


            //unknown command
        }else {
            messageSender.send(sender, unknown.replace("%command%", label));
        }



        return true;
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

package com.github.alfonsoleandro.mpheadsexp.commands;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.MCHelpHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.MCReloadHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.MCVersionHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.MCXPHandler;
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
    private String unknown;
    private String playerNotFound;
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
                        new MCReloadHandler(plugin,
                                new MCXPHandler(plugin, null)
                        )
                )
        );
        loadMessages();
    }

    private void loadMessages(){
        FileConfiguration messages = plugin.getLanguageYaml().getAccess();

        noPerm = messages.getString("no permission");
        unknown = messages.getString("unknown command");
        playerNotFound = messages.getString("player not found");
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

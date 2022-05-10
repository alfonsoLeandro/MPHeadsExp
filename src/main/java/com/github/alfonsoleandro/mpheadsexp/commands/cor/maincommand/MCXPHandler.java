package com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MCXPHandler extends AbstractHandler {

    public MCXPHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("xp") || args[0].equalsIgnoreCase("experience");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(args.length < 3 ||
                (!args[1].equalsIgnoreCase("add") &&
                        !args[1].equalsIgnoreCase("see") &&
                        !args[1].equalsIgnoreCase("set"))) {
            this.messageSender.send(sender, "&cUse: &f/" + label + " xp (add/set/see) (player) (amount)");
            return;
        }

        if(args[1].equalsIgnoreCase("add")) {
            if(args.length < 4) {
                this.messageSender.send(sender, "&cUse: &f/" + label + " xp add (player) (amount)");
                return;
            }
            if(!sender.hasPermission("HeadsExp.xp.add")){
                this.messageSender.send(sender, Message.NO_PERMISSION);
                return;
            }
            Player toAdd = Bukkit.getPlayerExact(args[2]);
            if(toAdd == null){
                this.messageSender.send(sender, Message.PLAYER_NOT_FOUND,
                        "%name%", args[2]);
                return;
            }
            int xp;
            try{
                xp = Integer.parseInt(args[3]);
            }catch (NumberFormatException e){
                this.messageSender.send(sender, "NOT NUMBER"); //TODO
                return;
            }

            LevelsManager lvlManager = this.plugin.getLevelsManager();

            lvlManager.addXP(toAdd.getUniqueId(), xp);
            this.messageSender.send(sender, "ADDED"); //TODO

            return;
        }

        if(args[1].equalsIgnoreCase("set")){
            Bukkit.broadcastMessage("a");
        }


    }

//    if(args.length < 4) {
//                    messageSender.send(sender, "&cUse: &f/" + label + " xp add (player) (amount)");
//                    return true;
//                }
//                if(notHasPerm(sender, "headsExp.xp.add")) return true;
//                Player player = Bukkit.getPlayer(args[2]);
//                if(player == null) {
//                    messageSender.send(sender, playerNotFound.replace("%name%", args[2]));
//                    return true;
//                }
//                int xp;
//                try {
//                    xp = Integer.parseInt(args[3]);
//                } catch (NumberFormatException e) {
//                    messageSender.send(sender, errorNumber.replace("%input%", args[3]));
//                    return true;
//                }
//                LevelsManager manager = plugin.getLevelsManager();
//
//                manager.addXP(player.getUniqueId(), xp);
//                messageSender.send(sender, addedXP
//                        .replace("%xp%", String.valueOf(xp))
//                        .replace("%player%", args[2])
//                        .replace("%total%", String.valueOf(manager.getXP(player.getUniqueId())))
//                        .replace("%level%", String.valueOf(manager.getLevel(player.getUniqueId()))));
//
//
//            } else if(args[1].equalsIgnoreCase("see")) {
//                if(notHasPerm(sender, "headsExp.xp.see")) return true;
//                Player player = Bukkit.getPlayer(args[2]);
//                if(player == null) {
//                    messageSender.send(sender, playerNotFound.replace("%name%", args[2]));
//                    return true;
//                }
//                LevelsManager manager = plugin.getLevelsManager();
//
//                messageSender.send(sender, seeXP
//                        .replace("%player%", args[2])
//                        .replace("%xp%", String.valueOf(manager.getXP(player.getUniqueId())))
//                        .replace("%level%", String.valueOf(manager.getLevel(player.getUniqueId()))));
//
//
//            } else {
//                if(args.length < 4) {
//                    messageSender.send(sender, "&cUse: &f/" + label + " xp set (player) (amount)");
//                    return true;
//                }
//                if(notHasPerm(sender, "headsExp.xp.set")) return true;
//                Player player = Bukkit.getPlayer(args[2]);
//                if(player == null) {
//                    messageSender.send(sender, playerNotFound.replace("%name%", args[2]));
//                    return true;
//                }
//                int xp;
//                try {
//                    xp = Integer.parseInt(args[3]);
//                } catch (NumberFormatException e) {
//                    messageSender.send(sender, errorNumber.replace("%input%", args[3]));
//                    return true;
//                }
//                LevelsManager manager = plugin.getLevelsManager();
//
//                manager.setXP(player.getUniqueId(), xp);
//                messageSender.send(sender, setXP
//                        .replace("%player%", args[2])
//                        .replace("%xp%", String.valueOf(manager.getXP(player.getUniqueId())))
//                        .replace("%level%", String.valueOf(manager.getLevel(player.getUniqueId()))));
//
//            }
}

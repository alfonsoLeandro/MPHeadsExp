package com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommandXPHandler extends AbstractHandler {

    public MainCommandXPHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("xp") ||
                args[0].equalsIgnoreCase("experience");
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
                this.messageSender.send(sender, Message.ERROR_AMOUNT,
                        "%input%", args[3]);
                return;
            }

            LevelsManager lvlManager = this.plugin.getLevelsManager();

            lvlManager.addXP(toAdd.getUniqueId(), xp);
            this.messageSender.send(sender, Message.ADDED_XP,
                    "%xp%", args[3],
                    "%player%", args[2],
                    "%total%", String.valueOf(lvlManager.getXP(toAdd.getUniqueId())),
                    "%level%", String.valueOf(lvlManager.getLevel(toAdd.getUniqueId())));


        } else if(args[1].equalsIgnoreCase("set")){
            if(args.length < 4) {
                this.messageSender.send(sender, "&cUse: &f/" + label + " xp set (player) (amount)");
                return;
            }
            if(!sender.hasPermission("HeadsExp.xp.set")){
                this.messageSender.send(sender, Message.NO_PERMISSION);
                return;
            }
            Player toSet = Bukkit.getPlayerExact(args[2]);
            if(toSet == null){
                this.messageSender.send(sender, Message.PLAYER_NOT_FOUND,
                        "%name%", args[2]);
                return;
            }
            int xp;
            try{
                xp = Integer.parseInt(args[3]);
            }catch (NumberFormatException e){
                this.messageSender.send(sender, Message.ERROR_AMOUNT,
                        "%input%", args[3]);
                return;
            }

            LevelsManager lvlManager = this.plugin.getLevelsManager();

            lvlManager.setXP(toSet.getUniqueId(), xp);
            this.messageSender.send(sender, Message.SET_XP,
                    "%player%", args[2],
                    "%xp%", String.valueOf(lvlManager.getXP(toSet.getUniqueId())),
                    "%level%", String.valueOf(lvlManager.getLevel(toSet.getUniqueId())));


        }else {
            //See
            if(!sender.hasPermission("HeadsExp.xp.see")) {
                this.messageSender.send(sender, Message.NO_PERMISSION);
                return;
            }
            Player toSee = Bukkit.getPlayerExact(args[2]);
            if(toSee == null) {
                this.messageSender.send(sender, Message.PLAYER_NOT_FOUND,
                        "%name%", args[2]);
                return;
            }

            LevelsManager lvlManager = this.plugin.getLevelsManager();

            this.messageSender.send(sender, Message.SEE_XP,
                    "%player%", args[2],
                    "%xp%", String.valueOf(lvlManager.getXP(toSee.getUniqueId())),
                    "%level%", String.valueOf(lvlManager.getLevel(toSee.getUniqueId())));

        }


    }
}

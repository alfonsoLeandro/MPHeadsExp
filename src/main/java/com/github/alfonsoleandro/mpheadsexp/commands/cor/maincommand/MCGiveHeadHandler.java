package com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class MCGiveHeadHandler extends AbstractHandler {

    public MCGiveHeadHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("giveHead") ||
                args[0].equalsIgnoreCase("getHead");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(args.length < 3) {
            this.messageSender.send(sender, "&cUse: &f/" + label + " giveHead (player) (type) <amount>");
            return;
        }
        if(!sender.hasPermission("HeadsExp.giveHead")){
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }
        int amount = 1;
        if(args.length > 3) {
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                this.messageSender.send(sender, Message.ERROR_AMOUNT,
                        "%input%", args[3]);
                return;
            }
        }

        Player toGive = Bukkit.getPlayerExact(args[1]);
        if(toGive == null) {
            this.messageSender.send(sender, Message.PLAYER_NOT_FOUND,
                    "%name%", args[1]);
            return;
        }
        String headType = args[2].toUpperCase(Locale.ROOT);
        if(!this.plugin.getLevelsManager().containsType(headType)) { //TODO head types in levels manager???
            this.messageSender.send(sender, Message.INVALID_TYPE,
                    "%input%", headType);
            return;
        }
        ItemStack item = this.plugin.getHeadsManager().getMobHead(EntityType.valueOf(headType));
        item.setAmount(amount);
        toGive.getInventory().addItem(item);

        if(toGive.equals(sender)) {
            this.messageSender.send(sender, Message.SELF_ADDED_HEAD,
                    "%amount%", String.valueOf(amount),
                    "%type%", headType);
        } else {
            this.messageSender.send(sender, Message.ADDED_HEAD_TO_SOMEONE,
                    "%player%", args[1],
                    "%amount%", String.valueOf(amount),
                    "%type%", headType);
            this.messageSender.send(toGive, Message.SOMEONE_ADDED_HEAD_TO_YOU,
                    "%player%", sender.getName(),
                    "%amount%", String.valueOf(amount),
                    "%type%", headType);
        }

    }



}

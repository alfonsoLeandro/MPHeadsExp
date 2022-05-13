package com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class HeadsCommandWorthHandler extends AbstractHandler {

    public HeadsCommandWorthHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("worth") ||
                args[0].equalsIgnoreCase("price");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)) {
            this.messageSender.send(sender, Message.CANNOT_SEND_CONSOLE);
            return;
        }
        if(!sender.hasPermission("headsExp.heads.worth")){
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }
        Player player = (Player) sender;
        ItemStack inHand = player.getInventory().getItemInMainHand();

        if(!inHand.getType().equals(Material.PLAYER_HEAD) ||
                !inHand.hasItemMeta()) {
            this.messageSender.send(sender, Message.MUST_BE_HOLDING_HEAD);
            return;
        }
        PersistentDataContainer data = Objects.requireNonNull(inHand.getItemMeta()).getPersistentDataContainer();

        boolean isNotHead = true;
        boolean isPlayerHead = false;
        String mobType = "";
        double price = 0;
        int xp;

        for (NamespacedKey key : data.getKeys()) {
            if(data.has(key, PersistentDataType.STRING)) {
                String string = data.get(key, PersistentDataType.STRING);
                assert string != null;
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
            this.messageSender.send(sender, Message.MUST_BE_HOLDING_HEAD);
            return;
        }


//        if(isPlayerHead){
//            xp = config.getInt("player heads."+ (config.contains("player heads."+mobType) ? mobType : "default head") + ".exp");
//        }else {
//            price = config.getDouble("heads." + mobType + ".price");
//            xp = config.getInt("heads." + mobType + ".exp");
//        }
//
//        messageSender.send(sender, headWorth
//                .replace("%head%", mobType)
//                .replace("%money%", String.valueOf(price))
//                .replace("%xp%", String.valueOf(xp)));
//
//


    }
}

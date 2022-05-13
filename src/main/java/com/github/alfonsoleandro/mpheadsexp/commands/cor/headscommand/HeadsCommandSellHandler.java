package com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
//TODO
public class HeadsCommandSellHandler extends AbstractHandler {

    public HeadsCommandSellHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("sell");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)) {
            this.messageSender.send(sender, Message.CANNOT_SEND_CONSOLE);
            return;
        }
        if(!sender.hasPermission("headsExp.heads.sell")){
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
//            FileConfiguration config = plugin.getConfigYaml().getAccess();
//            Economy economy = plugin.getEconomy();
//            LevelsManager manager = plugin.getLevelsManager();
//
//            int xp = config.getInt("player heads."+ (config.contains("player heads."+mobType) ? mobType : "default head") + ".exp");
//            int amount = inHand.getAmount();
//            price *= amount;
//
//            inHand.setAmount(0);
//
//            economy.depositPlayer(player, price);
//            manager.addXP(player.getUniqueId(), xp);
//
//            messageSender.send(player, playerHeadSold
//                    .replace("%amount%", String.valueOf(amount))
//                    .replace("%player%", mobType)
//                    .replace("%price%", String.valueOf(price))
//                    .replace("%xp%", String.valueOf(xp))
//                    .replace("%totalxp%", String.valueOf(manager.getXP(player.getUniqueId())))
//            );
//
//
//            FileConfiguration records = plugin.getRecordsYaml().getAccess();
//            records.set("records."+player.getName()+".players."+mobType, records.getInt("records."+player.getName()+"."+mobType)+amount);
//            plugin.getRecordsYaml().save();
//
//            return;
//        }
//
//
//        FileConfiguration config = plugin.getConfigYaml().getAccess();
//        Economy economy = plugin.getEconomy();
//        LevelsManager manager = plugin.getLevelsManager();
//
//        int amount = inHand.getAmount();
//        price = config.getDouble("heads." + mobType + ".price") * amount;
//        int xp = config.getInt("heads." + mobType + ".exp") * amount;
//
//        inHand.setAmount(0);
//
//        economy.depositPlayer(player, price);
//        manager.addXP(player.getUniqueId(), xp);
//
//        messageSender.send(player, headSold
//                .replace("%amount%", String.valueOf(amount))
//                .replace("%type%", mobType)
//                .replace("%price%", String.valueOf(price))
//                .replace("%xp%", String.valueOf(xp))
//                .replace("%totalxp%", String.valueOf(manager.getXP(player.getUniqueId())))
//        );
//
//        FileConfiguration records = plugin.getRecordsYaml().getAccess();
//        records.set("records."+player.getName()+"."+mobType, records.getInt("records."+player.getName()+"."+mobType)+amount);
//        plugin.getRecordsYaml().save();
//
//

    }
}

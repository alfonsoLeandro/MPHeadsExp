package com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.events.PlayerHeadSellEvent;
import com.github.alfonsoleandro.mpheadsexp.managers.HeadsManager;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.managers.Settings;
import com.github.alfonsoleandro.mpheadsexp.managers.utils.PlayerHeadDataValues;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
public class HeadsCommandSellHandler extends AbstractHandler {

    private final Settings settings;
    private final HeadsManager headsManager;

    public HeadsCommandSellHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
        this.settings = plugin.getSettings();
        this.headsManager = plugin.getHeadsManager();
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
        double price = 0;
        double xp;
        String mobType = "";

        for (NamespacedKey key : data.getKeys()) {
            if(data.has(key, PersistentDataType.STRING)) {
                String string = data.get(key, PersistentDataType.STRING);
                assert string != null;
                if(string.startsWith("HEAD") || string.startsWith("PLAYER-HEAD")) {
                    isNotHead = false;
                    if(string.startsWith("PLAYER-HEAD")){
                        isPlayerHead = true;
                        price = Double.parseDouble(string.split(":")[2]); //"PLAYER-HEAD:%player_name%:%price%"
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


        if(isPlayerHead){
            PlayerHeadDataValues playerHeadDataValues = this.headsManager.getPlayerHeadDataValues(mobType);
            xp = playerHeadDataValues == null ? this.settings.getDefaultPlayerHeadExp() : playerHeadDataValues.getXp();
        }else {
            price = this.headsManager.getMobHeadData(mobType).getPrice();
            xp = this.headsManager.getMobHeadData(mobType).getXp();
        }

        if(isPlayerHead){
            Economy economy = this.plugin.getEconomy();
            LevelsManager manager = this.plugin.getLevelsManager();

            int amount = inHand.getAmount();
            price *= amount;

            //TRIGGER EVENT
            PlayerHeadSellEvent event = new PlayerHeadSellEvent(player, mobType, (int) xp, price, amount);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) return;

            inHand.setAmount(0);
            economy.depositPlayer(player, event.getPrice());
            manager.addXP(player.getUniqueId(), event.getXp());

            this.messageSender.send(player, Message.PLAYER_HEAD_SOLD,
                    "%amount%", String.valueOf(amount),
                    "%player%", mobType,
                    "%price%", String.valueOf(event.getPrice()),
                    "%xp%", String.valueOf(event.getXp()),
                    "%totalxp%", String.valueOf(manager.getXP(player.getUniqueId()))
            );


            FileConfiguration records = this.plugin.getRecordsYaml().getAccess();
            records.set("records."+player.getName()+".players."+mobType,
                    records.getInt("records."+player.getName()+"."+mobType)+amount);
            this.plugin.getRecordsYaml().save(true);

            return;
        }

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

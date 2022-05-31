package com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.events.MobHeadSellEvent;
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


        NamespacedKey key = new NamespacedKey(this.plugin, "MPHeads");
        if(!data.has(key, PersistentDataType.STRING)) {
            this.messageSender.send(sender, Message.MUST_BE_HOLDING_HEAD);
            return;
        }
        String string = data.get(key, PersistentDataType.STRING);
        assert string != null;
        if(!string.startsWith("HEAD") && !string.startsWith("PLAYER-HEAD")) {
            this.messageSender.send(sender, Message.MUST_BE_HOLDING_HEAD);
            return;
        }

        if(string.startsWith("PLAYER-HEAD")){
            String playerName = string.split(":")[1];
            PlayerHeadDataValues playerHeadDataValues = this.headsManager.getPlayerHeadDataValues(playerName);
            double price = Double.parseDouble(string.split(":")[2]); //"PLAYER-HEAD:%player_name%:%price%"
            double xp = playerHeadDataValues == null ? this.settings.getDefaultPlayerHeadExp() : playerHeadDataValues.getXp();
            Economy economy = this.plugin.getEconomy();
            LevelsManager manager = this.plugin.getLevelsManager();


            int amount = inHand.getAmount();
            price *= amount;
            xp *= amount;

            //TRIGGER EVENT
            PlayerHeadSellEvent event = new PlayerHeadSellEvent(player, playerName, (int) xp, price, amount);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) return;

            inHand.setAmount(0);
            economy.depositPlayer(player, event.getPrice());
            manager.addXP(player.getUniqueId(), event.getXp());

            this.messageSender.send(player, Message.PLAYER_HEAD_SOLD,
                    "%amount%", String.valueOf(amount),
                    "%player%", playerName,
                    "%price%", String.valueOf(event.getPrice()),
                    "%xp%", String.valueOf(event.getXp()),
                    "%totalxp%", String.valueOf(manager.getXP(player.getUniqueId()))
            );


            FileConfiguration records = this.plugin.getRecordsYaml().getAccess();
            records.set("records."+player.getName()+".players."+playerName,
                    records.getInt("records."+player.getName()+"."+playerName)+amount);
            this.plugin.getRecordsYaml().save(true);


        }else{
            Economy economy = this.plugin.getEconomy();
            LevelsManager manager = this.plugin.getLevelsManager();
            String mobType = string.split(":")[1];
            double price = this.headsManager.getMobHeadData(mobType).getPrice();
            double xp = this.headsManager.getMobHeadData(mobType).getXp();

            int amount = inHand.getAmount();
            price *= amount;
            xp *= amount;

            //TRIGGER EVENT
            MobHeadSellEvent event = new MobHeadSellEvent(player, mobType, (int) xp, price, amount);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) return;

            inHand.setAmount(0);

            economy.depositPlayer(player, event.getPrice());
            manager.addXP(player.getUniqueId(), event.getXp());

            this.messageSender.send(player, Message.HEAD_SOLD,
                    "%amount%", String.valueOf(amount),
                    "%type%", mobType,
                    "%price%", String.valueOf(event.getPrice()),
                    "%xp%", String.valueOf(event.getXp()),
                    "%totalxp%", String.valueOf(manager.getXP(player.getUniqueId()))
            );

            FileConfiguration records = this.plugin.getRecordsYaml().getAccess();
            records.set("records." + player.getName() + "." + mobType,
                    records.getInt("records." + player.getName() + "." + mobType) + amount);
            this.plugin.getRecordsYaml().save(false);

        }


    }
}

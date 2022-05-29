package com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.managers.HeadsManager;
import com.github.alfonsoleandro.mpheadsexp.managers.Settings;
import com.github.alfonsoleandro.mpheadsexp.managers.utils.PlayerHeadDataValues;
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

    private final Settings settings;
    private final HeadsManager headsManager;

    public HeadsCommandWorthHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
        this.headsManager = plugin.getHeadsManager();
        this.settings = plugin.getSettings();
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

        this.messageSender.send(sender, Message.HEAD_WORTH,
                "%head%", mobType,
                "%money%", String.valueOf(price),
                "%xp%", String.valueOf(xp));




    }
}

package com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import com.github.alfonsoleandro.mputils.guis.SimpleGUI;
import com.github.alfonsoleandro.mputils.itemstacks.MPItemStacks;
import com.github.alfonsoleandro.mputils.string.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HeadsCommandInfoHandler extends AbstractHandler {

    public HeadsCommandInfoHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("info") ||
                args[0].equalsIgnoreCase("information");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)) {
            this.messageSender.send(sender, Message.CANNOT_SEND_CONSOLE);
            return;
        }
        if(!sender.hasPermission("headsExp.heads.info")){
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }

        Player player = (Player) sender;
        openInfoGUI(player);
    }


    private void openInfoGUI(Player player){
        FileConfiguration config = plugin.getConfigYaml().getAccess();
        String title = config.getString("info gui.title");
        int size = config.getInt("info gui.size");

        SimpleGUI infoGUI = new SimpleGUI(StringUtils.colorizeString('&', title), size, "MPHeadsExp:info");

        LevelsManager manager = plugin.getLevelsManager();
        Map<String,String> placeHolders = new HashMap<>();
        placeHolders.put("%player%", player.getName());
        placeHolders.put("%xp%", String.valueOf(manager.getXP(player.getUniqueId())));
        placeHolders.put("%level%", String.valueOf(manager.getLevel(player.getUniqueId())));

        ItemStack info = MPItemStacks.replacePlaceholders(
                MPItemStacks.newItemStack(
                        Material.PLAYER_HEAD,
                        1,
                        StringUtils.colorizeString('&', config.getString("info gui.info item.name")),
                        config.getStringList("info gui.info item.lore")), placeHolders);

        ItemStack unlocked = MPItemStacks.replacePlaceholders(
                MPItemStacks.newItemStack(
                        Material.BARRIER,
                        1,
                        StringUtils.colorizeString('&', config.getString("info gui.unlocked heads item.name")),
                        config.getStringList("info gui.unlocked heads item.lore")), placeHolders);

        ItemStack sold = MPItemStacks.replacePlaceholders(
                MPItemStacks.newItemStack(
                        Material.GOLD_INGOT,
                        1,
                        StringUtils.colorizeString('&', config.getString("info gui.sold heads item.name")),
                        config.getStringList("info gui.sold heads item.lore")), placeHolders);

        infoGUI.setItem(config.getInt("info gui.info item.slot"), info);
        infoGUI.setItem(config.getInt("info gui.unlocked heads item.slot"), unlocked);
        infoGUI.setItem(config.getInt("info gui.sold heads item.slot"), sold);


        infoGUI.openGUI(player);
    }
}

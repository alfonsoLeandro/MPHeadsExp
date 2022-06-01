package com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.managers.Settings;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import com.github.alfonsoleandro.mputils.guis.SimpleGUI;
import com.github.alfonsoleandro.mputils.itemstacks.MPItemStacks;
import com.github.alfonsoleandro.mputils.string.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

//TODO: fix GUI
public class HeadsCommandInfoHandler extends AbstractHandler {

    private final LevelsManager levelsManager;
    private final Settings settings;

    public HeadsCommandInfoHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
        this.levelsManager = plugin.getLevelsManager();
        this.settings = plugin.getSettings();
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
        SimpleGUI infoGUI = new SimpleGUI(StringUtils.colorizeString('&', this.settings.getInfoGUITitle()),
                this.settings.getInfoGUISize(),
                "MPHeadsExp:info");

        Map<String,String> placeHolders = new HashMap<>();
        placeHolders.put("%player%", player.getName());
        placeHolders.put("%xp%", String.valueOf(this.levelsManager.getXP(player.getUniqueId())));
        placeHolders.put("%level%", String.valueOf(this.levelsManager.getLevel(player.getUniqueId())));

        infoGUI.setItem(this.settings.getInfoInfoGUIItemSlot(),
                MPItemStacks.replacePlaceholders(this.settings.getInfoInfoGUIItem(), placeHolders));
        infoGUI.setItem(this.settings.getInfoUnlockedHeadsGUIItemSlot(),
                MPItemStacks.replacePlaceholders(this.settings.getInfoUnlockedHeadsGUIItem(), placeHolders));
        infoGUI.setItem(this.settings.getInfoSoldHeadsGUIItemSlot(),
                MPItemStacks.replacePlaceholders(this.settings.getInfoSoldHeadsGUIItem(), placeHolders));


        infoGUI.openGUI(player);
    }
}

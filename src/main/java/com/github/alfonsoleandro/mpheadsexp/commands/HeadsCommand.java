package com.github.alfonsoleandro.mpheadsexp.commands;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandHelpHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandInfoHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandSellHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandWorthHandler;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import com.github.alfonsoleandro.mputils.guis.SimpleGUI;
import com.github.alfonsoleandro.mputils.itemstacks.MPItemStacks;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import com.github.alfonsoleandro.mputils.reloadable.Reloadable;
import com.github.alfonsoleandro.mputils.string.StringUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HeadsCommand implements CommandExecutor {

    private final AbstractHandler commandHandler;

    /**
     * MainCommand class constructor.
     * @param plugin The main class instance.
     */
    public HeadsCommand(HeadsExp plugin){
        this.commandHandler = new HeadsCommandHelpHandler(plugin,
                new HeadsCommandWorthHandler(plugin,
                        new HeadsCommandSellHandler(plugin,
                                new HeadsCommandInfoHandler(plugin, null)
                        )
                )
        );
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        this.commandHandler.handle(sender, label, args);
        return true;
    }



}

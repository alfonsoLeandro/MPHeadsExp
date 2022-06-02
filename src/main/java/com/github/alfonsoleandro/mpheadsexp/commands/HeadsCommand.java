package com.github.alfonsoleandro.mpheadsexp.commands;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandHelpHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandInfoHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandSellHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.headscommand.HeadsCommandWorthHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class HeadsCommand implements CommandExecutor {

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

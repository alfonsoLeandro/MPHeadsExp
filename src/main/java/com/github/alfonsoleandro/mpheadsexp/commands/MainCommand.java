package com.github.alfonsoleandro.mpheadsexp.commands;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;


public final class MainCommand implements CommandExecutor {

    private final AbstractHandler commandHandler;

    /**
     * MainCommand class constructor.
     * @param plugin The main class instance.
     */
    public MainCommand(HeadsExp plugin){
        this.commandHandler = new MainCommandHelpHandler(plugin,
                new MainCommandVersionHandler(plugin,
                        new MainCommandReloadHandler(plugin,
                                new MainCommandXPHandler(plugin,
                                        new MainCommandGiveHeadHandler(plugin, null)
                                )
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

package com.github.alfonsoleandro.mpheadsexp.commands;

import com.github.alfonsoleandro.chestrestock.ChestRestockPlus;
import com.github.alfonsoleandro.chestrestock.commands.COR.*;
import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.MainCommandHelpHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.MainCommandReloadHandler;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand.MainCommandVersionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class MainCommandNew implements CommandExecutor {

    /**
     * The handlers' succession in charge of the handling of commands using the COR pattern.
     */
    private final AbstractHandler COR;

    /**
     * MainCommand class constructor.
     * @param plugin The main class instance.
     */
    public MainCommandNew(HeadsExp plugin){
        this.COR = new MainCommandHelpHandler(plugin,
                 new MainCommandVersionHandler(plugin,
                         new MainCommandReloadHandler(plugin,
                                         null))
        );
    }
    

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        this.COR.handle(sender, label, args);
        return true;
    }

}

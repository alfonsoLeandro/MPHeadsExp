package com.github.alfonsoleandro.mpheadsexp.commands.cor;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import org.bukkit.command.CommandSender;

public class ReloadHandler extends AbstractHandler{

    public ReloadHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("reload");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(!sender.hasPermission("ChestRestock.reload")) {
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }
        this.plugin.reload(false);
        this.messageSender.send(sender, Message.RELOADED);
    }
}

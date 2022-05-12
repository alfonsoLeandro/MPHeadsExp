package com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import org.bukkit.command.CommandSender;

public class MainCommandVersionHandler extends AbstractHandler {

    public MainCommandVersionHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args[0].equalsIgnoreCase("version");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        if(!sender.hasPermission("HeadsExp.version")) {
            this.messageSender.send(sender, Message.NO_PERMISSION);
            return;
        }
        this.messageSender.send(sender, "&fVersion: &e"+ this.plugin.getVersion()+"&f. &aUp to date!");
    }
}

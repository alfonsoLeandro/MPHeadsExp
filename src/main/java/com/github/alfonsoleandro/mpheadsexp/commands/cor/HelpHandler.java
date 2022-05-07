package com.github.alfonsoleandro.mpheadsexp.commands.cor;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import org.bukkit.command.CommandSender;

public class HelpHandler extends AbstractHandler{

    public HelpHandler(HeadsExp plugin, AbstractHandler successor) {
        super(plugin, successor);
    }

    @Override
    protected boolean meetsCondition(CommandSender sender, String label, String[] args) {
        return args.length == 0 || args[0].equalsIgnoreCase("help");
    }

    @Override
    protected void internalHandle(CommandSender sender, String label, String[] args) {
        this.messageSender.send(sender, "&6List of commands");
        this.messageSender.send(sender, "&f/"+label+" help");
        this.messageSender.send(sender, "&f/"+label+" version");
        this.messageSender.send(sender, "&f/"+label+" reload");
        this.messageSender.send(sender, "&f/"+label+" chests");
//        this.messageSender.send(sender, "&f/"+label+" loadChests");
//        this.messageSender.send(sender, "&f/"+label+" get (chest name)");
//        this.messageSender.send(sender, "&f/"+label+" settings");
//        this.messageSender.send(sender, "&f/"+label+" create (chest name)");
//        this.messageSender.send(sender, "&f/"+label+" resetCooldowns");
    }
}

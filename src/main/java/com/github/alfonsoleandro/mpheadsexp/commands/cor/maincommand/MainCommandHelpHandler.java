package com.github.alfonsoleandro.mpheadsexp.commands.cor.maincommand;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.commands.cor.AbstractHandler;
import org.bukkit.command.CommandSender;

public class MainCommandHelpHandler extends AbstractHandler {

    public MainCommandHelpHandler(HeadsExp plugin, AbstractHandler successor) {
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
        this.messageSender.send(sender, "&f/"+label+" xp (add/set/see) (player) (amount)");
        this.messageSender.send(sender, "&f/"+label+" giveHead (player) (type) <amount>");

    }
}

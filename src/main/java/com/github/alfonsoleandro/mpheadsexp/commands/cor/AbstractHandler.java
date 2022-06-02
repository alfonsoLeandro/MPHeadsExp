package com.github.alfonsoleandro.mpheadsexp.commands.cor;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import org.bukkit.command.CommandSender;

public abstract class AbstractHandler {

    protected final HeadsExp plugin;
    protected final MessageSender<Message> messageSender;
    protected final AbstractHandler successor;
    
    public AbstractHandler(HeadsExp plugin, AbstractHandler successor){
        this.plugin = plugin;
        this.messageSender = plugin.getMessageSender();
        this.successor = successor;
    }
    
    public void handle(CommandSender sender, String label, String[] args){
        if(meetsCondition(sender, label, args)){
            this.internalHandle(sender, label, args);
        }else{
            if(this.successor != null) this.successor.handle(sender, label, args);
            else this.messageSender.send(sender, Message.UNKNOWN_COMMAND,
                    "%command%", label);
        }
    }
    
    protected abstract boolean meetsCondition(CommandSender sender, String label, String[] args);

    protected abstract void internalHandle(CommandSender sender, String label, String[] args);
    
}

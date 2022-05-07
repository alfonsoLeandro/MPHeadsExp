package com.github.alfonsoleandro.mpheadsexp.utils;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mputils.string.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Logger implements Reloadable{

    private final HeadsExp plugin;
    private String prefix;

    public Logger(HeadsExp plugin){
        this.plugin = plugin;
        this.prefix = plugin.getConfigYaml().getAccess().getString("prefix");
    }


    public void send(CommandSender sender, String msg){
        sender.sendMessage(StringUtils.colorizeString('&', prefix+" "+msg));
    }


    public void send(String msg){
        send(Bukkit.getConsoleSender(), msg);
    }


    public void reload(){
        this.prefix = plugin.getConfigYaml().getAccess().getString("prefix");
    }


}

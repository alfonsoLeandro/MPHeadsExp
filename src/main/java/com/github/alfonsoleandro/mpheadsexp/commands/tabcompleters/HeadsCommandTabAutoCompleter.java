package com.github.alfonsoleandro.mpheadsexp.commands.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HeadsCommandTabAutoCompleter implements TabCompleter {

    public boolean equalsToStringUnCompleted(String input, String string){
        for(int i = 0; i < string.length(); i++){
            if(input.equalsIgnoreCase(string.substring(0,i))){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final List<String> lista = new ArrayList<>();

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("")) {
                lista.add("help");
                lista.add("sell");
                lista.add("info");

            } else if (equalsToStringUnCompleted(args[0], "help")) {
                lista.add("help");

            } else if(equalsToStringUnCompleted(args[0], "sell")) {
                lista.add("sell");

            } else if(equalsToStringUnCompleted(args[0], "info")) {
                lista.add("info");
            }
        }

        return lista;
    }
}

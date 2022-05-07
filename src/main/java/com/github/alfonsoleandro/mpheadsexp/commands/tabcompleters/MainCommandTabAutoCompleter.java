package com.github.alfonsoleandro.mpheadsexp.commands.tabcompleters;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainCommandTabAutoCompleter implements TabCompleter {

    private final HeadsExp plugin;

    public MainCommandTabAutoCompleter(HeadsExp plugin){
        this.plugin = plugin;
    }


    public boolean equalsToStringUnCompleted(String input, String string){
        for(int i = 0; i < string.length(); i++){
            if(input.equalsIgnoreCase(string.substring(0,i))){
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> lista = new ArrayList<>();

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("")) {
                lista.add("help");
                lista.add("version");
                lista.add("reload");
                lista.add("xp");
                lista.add("giveHead");

            } else if(equalsToStringUnCompleted(args[0], "help")) {
                lista.add("help");

            } else if(equalsToStringUnCompleted(args[0], "version")) {
                lista.add("version");

            } else if(equalsToStringUnCompleted(args[0], "reload")) {
                lista.add("reload");

            } else if(equalsToStringUnCompleted(args[0], "xp")) {
                lista.add("xp");

            } else if(equalsToStringUnCompleted(args[0], "giveHead")) {
                lista.add("giveHead");
            }

        } else if(args.length == 2){
            if(args[0].equalsIgnoreCase("xp")) {
                if(args[1].equalsIgnoreCase("")) {
                    lista.add("add");
                    lista.add("set");
                    lista.add("see");

                } else if(equalsToStringUnCompleted(args[1], "add")) {
                    lista.add("add");

                } else if(equalsToStringUnCompleted(args[1], "se")) {
                    lista.add("set");
                    lista.add("see");

                } else if(args[1].equalsIgnoreCase("see")) {
                    lista.add("see");

                } else if(args[1].equalsIgnoreCase("set")) {
                    lista.add("set");
                }
            }else if(args[0].equalsIgnoreCase("giveHead")){
                return null; //Returns list of players
            }


        }else if(args.length == 3){
            if(args[0].equalsIgnoreCase("xp")) {
                return null;

            }else if(args[0].equalsIgnoreCase("giveHead")){
                return plugin.getHeadsManager().getAvailableTypes();
            }

        }else if(args.length == 4){
            if(args[0].equalsIgnoreCase("xp")) {
                lista.add("1");
                lista.add("10");
                lista.add("50");
                lista.add("100");
                lista.add("1000");
                lista.add("10000");
                lista.add("20000");

            }else if(args[0].equalsIgnoreCase("giveHead")){
                lista.add("1");
                lista.add("2");
                lista.add("3");
                lista.add("4");
                lista.add("5");
                lista.add("10");
                lista.add("32");
                lista.add("64");
            }

        }

        return lista;
    }
}

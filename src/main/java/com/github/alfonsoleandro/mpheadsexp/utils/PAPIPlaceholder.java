package com.github.alfonsoleandro.mpheadsexp.utils;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIPlaceholder extends PlaceholderExpansion {

    private final HeadsExp plugin;

    public PAPIPlaceholder(HeadsExp plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public @NotNull String getAuthor(){
        return this.plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion(){
        return this.plugin.getDescription().getVersion();
    }

    /*
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public @NotNull String getIdentifier(){
        return "MPHeadsExp";
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier){
        if(player == null){
            return "";
        }

        // %MPHeadsExp_level%
        if(identifier.equals("level")){
            return String.valueOf(this.plugin.getLevelsManager().getLevel(player.getUniqueId()));
        }
        // %MPHeadsExp_xp% / %MPHeadsExp_exp%
        if(identifier.equals("xp") || identifier.equals("exp")){
            return String.valueOf(this.plugin.getLevelsManager().getXP(player.getUniqueId()));
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }
}
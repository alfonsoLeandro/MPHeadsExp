package com.github.alfonsoleandro.mpheadsexp.utils;

import com.github.alfonsoleandro.mputils.misc.MessageEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum Message implements MessageEnum {
    NO_PERMISSION("&cNo permission"),
    UNKNOWN_COMMAND("&cUnknown command. &fTry /%command% help"),
    RELOADED("&aPlugin reloaded!"),
    PLAYER_NOT_FOUND("&cPlayer with name %name% not found."),
    ERROR_AMOUNT("&c%input% &eis not a valid number"),
    ADDED_XP("&aSuccessfully added %xp% XP to %player% (total: %total%) (level: %level%)"),
    SET_XP("&aSuccessfully set the XP of %player% to %xp% (level: %level%)"),
    SEE_XP("&fXP of player %player%: %xp% (level: %level%)"),
    ;

    private final String path;
    private final String dflt;

    Message(String path, String dflt) {
        this.path = path;
        this.dflt = dflt;
    }

    Message(String dflt){
        this.path = this.toString().toLowerCase(Locale.ROOT).replace("_", " ");
        this.dflt = dflt;
    }

    @NotNull
    @Override
    public String getPath() {
        return this.path;
    }

    @NotNull
    @Override
    public String getDefault() {
        return this.dflt;
    }
}

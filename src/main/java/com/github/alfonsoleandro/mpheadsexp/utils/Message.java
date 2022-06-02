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
    INVALID_TYPE("&c\"&e%input%&c\" is not a valid type."),
    SELF_ADDED_HEAD("self added", "&aSuccessfully added %amount%X %type% heads to your inventory"),
    SOMEONE_ADDED_HEAD_TO_YOU("someone added", "&a%player% just sent you %amount%X %type% heads"),
    ADDED_HEAD_TO_SOMEONE("added to someone", "&aYou just added %amount%X %type% heads to %player%"),
    PLAYER_INVENTORY_FULL("&cCould not add items to %player%''s inventory"),
    YOUR_INVENTORY_FULL( "&cCould not add items to your inventory"),
    CANNOT_SEND_CONSOLE("&cThat command can only be sent by a player"),
    MUST_BE_HOLDING_HEAD("&cYou must be holding a valid head in your hand."),
    HEAD_WORTH("&c%head%&f''s head worth is: &e%money%$ &fand &e%xp% &fexperience"),
    PLAYER_HEAD_SOLD("&aSuccessfully sold %amount% heads of player %player% for %price%$ and %xp%XP (%totalxp% total xp)"),
    HEAD_SOLD("&aSuccessfully sold %amount% %type% heads for %price%$ and %xp%XP (%totalxp% total xp)"),
    CANNOT_PLACE_HEAD("cannot place","&cYou shouldn't place that head on the floor, it is highly valuable!"),
    ADDED_XP_BOSSBAR_TITLE("&a&l%xp%XP added! &f(%next%XP needed for the next level)"),
    LEVEL_UP_BOSSBAR_TITLE("&a&lLevel UP! &e(&c%previous% &e-> &a%new%)"),
    SET_XP_BOSSBAR_TITLE("&f&lXP changed &e(&c%previous% &e-> &a%new%)"),
    PLAYER_HEAD_DROPPED("&aYou just received %player%'s head!"),
    YOUR_HEAD_DROPPED("&cYou just dropped your head and lost %balance%% of your balance (%amount%)"),
    NOT_ENOUGH_LEVEL("&cYou need to be at least level %level% to get a %type% head, you are currently level %current_level%"),
    MOB_HEAD_DROPPED("&aThe %type% you just killed dropped their head!"),
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

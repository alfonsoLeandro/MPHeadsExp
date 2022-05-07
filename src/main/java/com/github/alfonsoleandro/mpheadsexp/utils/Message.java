package com.github.alfonsoleandro.mpheadsexp.utils;

import com.github.alfonsoleandro.mputils.misc.MessageEnum;
import org.jetbrains.annotations.NotNull;

public enum Message implements MessageEnum {
    //TODO
    NO_PERMISSION(),
    UNKNOWN_COMMAND(),
    RELOADED();

    @NotNull
    @Override
    public String getPath() {
        return null;
    }

    @NotNull
    @Override
    public String getDefault() {
        return null;
    }
}

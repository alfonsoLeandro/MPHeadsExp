package com.github.alfonsoleandro.mpheadsexp.utils;

import org.bukkit.Sound;

public class SoundSettings {

    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundSettings(String sound, double volume, double pitch) {
        this.sound = Sound.valueOf(sound);
        this.volume = (float)volume;
        this.pitch = (float)pitch;
    }


    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}

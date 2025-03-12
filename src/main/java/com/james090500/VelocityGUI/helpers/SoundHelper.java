package com.james090500.VelocityGUI.helpers;

import com.james090500.VelocityGUI.VelocityGUI;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.Sound;

public class SoundHelper {

    public static void playSound(ProtocolizePlayer protocolizePlayer, String sound){
        if(sound == null || sound.isEmpty())
            return;
        String[] soundString = sound.split(":");
        Sound soundEnum;
        float volume = 1;
        float pitch = 1;
        try {
            soundEnum = Sound.valueOf(soundString[0]);
            if(soundString.length == 3){
                volume = Float.parseFloat(soundString[1]);
                pitch = Float.parseFloat(soundString[2]);
            }
            protocolizePlayer.playSound(soundEnum, SoundCategory.MASTER, volume, pitch);
        } catch (IllegalArgumentException exc) {
            VelocityGUI.getInstance().getLogger().warn("Invalid sound: {}", sound);
        }
    }

}

package com.james090500.VelocityGUI.helpers;

import com.velocitypowered.api.proxy.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ChatControlHelper {

    static {
        try {

            Class<?> syncedCache = Class.forName("org.mineacademy.chatcontrol.SyncedCache");

            Method fromPlayerName = syncedCache.getMethod("fromPlayerName", String.class);
            fromPlayerName.setAccessible(true);
            ChatControlHelper.fromPlayerName = fromPlayerName;

            Method getNick = syncedCache.getMethod("getNameOrNickColorless");
            getNick.setAccessible(true);
            ChatControlHelper.getNick = getNick;

            loaded = true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            loaded = false;
        }
    }

    private static Method fromPlayerName;
    private static Method getNick;
    private static boolean loaded;

    public static String getNick(Player player) {
        if(!loaded)
            return null;
        try {
            Object syncedCache = fromPlayerName.invoke(null, player.getUsername());
            String nickName = (String) getNick.invoke(syncedCache);
            return nickName != null ? nickName : player.getUsername();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}

package com.james090500.VelocityGUI.helpers;

import com.james090500.VelocityGUI.VelocityGUI;
import com.james090500.VelocityGUI.helpers.vanish.VanishHelper;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderParser {

    private static ProxyServer proxyServer = VelocityGUI.getInstance().getServer();

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component of(Player player, String rawString) {
        //Username
        if(rawString.contains("%username%")) {
            rawString = rawString.replaceAll("%username%", player.getUsername());
        }

        //Server Name
        if(rawString.contains("%server_name%")) {
            ServerConnection server = player.getCurrentServer().orElse(null);
            if(server != null) {
                rawString = rawString.replaceAll("%server_name%", server.getServerInfo().getName());
            }
        }

        Matcher matcher;

        //Players Server
        matcher = Pattern.compile("%players_server_(\\w+)%").matcher(rawString);
        while (matcher.find()) {
            String serverName = matcher.group(1);
            Optional<RegisteredServer> server = proxyServer.getServer(serverName);
            int online = server.map(s -> s.getPlayersConnected().size()).orElse(0);
            rawString = rawString.replace("%players_server_" + serverName + "%", String.valueOf(online));
        }

        //Players Server Unvanished
        matcher = Pattern.compile("%players_server_(\\w+)_unvanished%").matcher(rawString);
        while (matcher.find()) {
            String serverName = matcher.group(1);
            Optional<RegisteredServer> server = proxyServer.getServer(serverName);
            long online = server.map(s -> s.getPlayersConnected().stream().filter(p->!VanishHelper.isVanished(p)).count()).orElse(0L);
            rawString = rawString.replace("%players_server_" + serverName + "_unvanished%", String.valueOf(online));
        }

        //Players Proxy
        if(rawString.contains("%players_proxy%")) {
            rawString = rawString.replaceAll("%players_proxy%", String.valueOf(proxyServer.getAllPlayers().size()));
        }

        //Players Proxy Unvanished
        if(rawString.contains("%players_proxy_unvanished%")) {
            rawString = rawString.replaceAll("%players_proxy_unvanished%", String.valueOf(proxyServer.getAllPlayers().stream().filter(p->!VanishHelper.isVanished(p)).count()));
        }

        //Server status
        matcher = Pattern.compile("%status_(\\w+)%").matcher(rawString);
        while (matcher.find()) {
            String serverName = matcher.group(1);
            Optional<RegisteredServer> server = proxyServer.getServer(serverName);
            String status = server.isPresent() ? "online" : "offline";
            rawString = rawString.replace("%status_" + serverName + "%", status);
        }

        //ChatControlRed
        if(rawString.contains("%chatcontrolred_nick%")) {
            String nickname = ChatControlHelper.getNick(player);
            if(nickname != null) {
                rawString = rawString.replaceAll("%chatcontrolred_nick%", nickname);
            }
        }

        //LuckPerms Meta
        if(rawString.startsWith("%luckperms_meta")) {
            String queryOption = rawString.replaceAll("%", "").replaceAll("luckperms_meta_", "");
            LuckPermsHelper.getMeta(player, queryOption);
        }

        //cursed way to parse legacy into minimessage
        rawString = convertLegacyToMiniMessage(rawString);
        return miniMessage.deserialize(rawString);
    }

    // Regex for `&#rrggbb` hex codes
    private static final Pattern HEX_PATTERN = Pattern.compile("(?i)&#([a-f0-9]{6})");
    // Mapping of Minecraft legacy color codes to MiniMessage
    private static final String[] LEGACY_COLORS = {
            "0", "black", "1", "dark_blue", "2", "dark_green", "3", "dark_aqua",
            "4", "dark_red", "5", "dark_purple", "6", "gold", "7", "gray",
            "8", "dark_gray", "9", "blue", "a", "green", "b", "aqua",
            "c", "red", "d", "light_purple", "e", "yellow", "f", "white",
            "k", "obf", "l", "bold", "m", "st", "n", "u", "o", "i", "r", "reset"
    };
    public static String convertLegacyToMiniMessage(String text) {
        // Convert legacy color codes (&c, &6, etc.) to MiniMessage
        for (int i = 0; i < LEGACY_COLORS.length; i += 2) {
            text = text.replaceAll("&" + LEGACY_COLORS[i], "<" + LEGACY_COLORS[i + 1] + ">");
        }

        // Convert `&#rrggbb` hex codes to MiniMessage format
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1);
            matcher.appendReplacement(sb, "<#" + hex + ">");
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}

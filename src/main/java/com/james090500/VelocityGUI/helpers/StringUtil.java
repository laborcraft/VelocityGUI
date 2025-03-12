package com.james090500.VelocityGUI.helpers;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Simplified org.bukkit.util.StringUtil
 */
public class StringUtil {

    @NotNull
    public static <T extends Collection<? super String>> T copyPartialMatches(@NotNull final String token, @NotNull final Iterable<String> originals, @NotNull final T collection) {
        for (String string : originals) {
            if (startsWithIgnoreCase(string, token)) {
                collection.add(string);
            }
        }
        return collection;
    }

    public static boolean startsWithIgnoreCase(@NotNull final String string, @NotNull final String prefix) {
        if (string.length() < prefix.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}

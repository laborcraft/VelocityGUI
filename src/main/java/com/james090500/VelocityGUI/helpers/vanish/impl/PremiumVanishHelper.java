package com.james090500.VelocityGUI.helpers.vanish.impl;

import com.james090500.VelocityGUI.helpers.vanish.VanishHelperImplementation;
import com.velocitypowered.api.proxy.Player;
import de.myzelyam.api.vanish.VelocityVanishAPI;

public class PremiumVanishHelper implements VanishHelperImplementation {

    @Override
    public boolean isVanished(Player p) {
        return VelocityVanishAPI.getInvisiblePlayers().contains(p.getUniqueId());
    }

}

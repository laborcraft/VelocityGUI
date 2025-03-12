package com.james090500.VelocityGUI.helpers.vanish.impl;

import com.james090500.VelocityGUI.helpers.vanish.VanishHelperImplementation;
import com.velocitypowered.api.proxy.Player;

public class DummyVanishHelper implements VanishHelperImplementation {
    @Override
    public boolean isVanished(Player p) {
        return false;
    }
}

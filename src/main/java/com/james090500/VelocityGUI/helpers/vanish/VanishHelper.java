package com.james090500.VelocityGUI.helpers.vanish;

import com.james090500.VelocityGUI.VelocityGUI;
import com.james090500.VelocityGUI.helpers.vanish.impl.DummyVanishHelper;
import com.james090500.VelocityGUI.helpers.vanish.impl.PremiumVanishHelper;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

public class VanishHelper {

    static {
        ProxyServer server = VelocityGUI.getInstance().getServer();
        if(server.getPluginManager().isLoaded("PremiumVanish")){
            vanishHelperImplementation = new PremiumVanishHelper();
        } else {
            vanishHelperImplementation = new DummyVanishHelper();
        }
    }

    private static final VanishHelperImplementation vanishHelperImplementation;

    public static boolean isVanished(Player p) {
        return vanishHelperImplementation.isVanished(p);
    }

}

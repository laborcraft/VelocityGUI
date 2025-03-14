package com.james090500.VelocityGUI.helpers;

import com.james090500.VelocityGUI.VelocityGUI;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;

public class InventoryClickHandler {

    private VelocityGUI velocityGUI;

    /**
     * Constructor
     * @param velocityGUI
     */
    public InventoryClickHandler(VelocityGUI velocityGUI) {
        this.velocityGUI = velocityGUI;
    }

    /**
     * Handle the gui commands
     * @param commands
     * @param click
     */
    public void execute(String[] commands, InventoryClick click) {
        Player player = velocityGUI.getServer().getPlayer(click.player().uniqueId()).get();
        for(String command : commands) {
            String[] splitCommand = command.split("=");
            switch(splitCommand[0]) {
                case "open":
                    new InventoryLauncher(velocityGUI).execute(splitCommand[1], player);
                    break;
                case "close":
                    click.player().closeInventory();
                    break;
                case "sudo":
                    player.spoofChatInput(splitCommand[1]);
                    break;
                case "vsudo":
                    velocityGUI.getServer().getCommandManager().executeAsync(player, splitCommand[1]);
                    break;
                case "server":
                    player.createConnectionRequest(velocityGUI.getServer().getServer(splitCommand[1]).get()).connect();
                    break;
                case "sound":
                    ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(player.getUniqueId());
                    SoundHelper.playSound(protocolizePlayer, splitCommand[1]);
            }
        }
    }

}

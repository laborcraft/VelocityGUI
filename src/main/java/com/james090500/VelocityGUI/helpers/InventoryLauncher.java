package com.james090500.VelocityGUI.helpers;

import com.james090500.VelocityGUI.VelocityGUI;
import com.james090500.VelocityGUI.config.Configs;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;

public class InventoryLauncher {

    private VelocityGUI velocityGUI;

    public InventoryLauncher(VelocityGUI velocityGUI) {
        this.velocityGUI = velocityGUI;
    }

    /**
     * Launches an inventory instance from a panel
     * @param panelName
     * @param player
     */
    public void execute(String panelName, Player player) {
        ProtocolizePlayer protocolizePlayer = Protocolize.playerProvider().player(player.getUniqueId());

        Configs.Panel panel = Configs.getPanels().get(panelName);
        if(panel == null) {
            protocolizePlayer.closeInventory();
            player.sendMessage(Configs.getLang().getPanelNotFound());
            return;
        }

        //Stop players with no permissions
        if(!panel.getPerm().equalsIgnoreCase("default") && !player.hasPermission(panel.getPerm())) {
            protocolizePlayer.closeInventory();
            player.sendMessage(Configs.getLang().getPanelNotFound());
            return;
        }

        protocolizePlayer.registeredInventories().clear();

        InventoryBuilder inventoryBuilder = new InventoryBuilder(velocityGUI, player);
        inventoryBuilder.setRows(panel.getRows());
        inventoryBuilder.setTitle(panel.getTitle());
        inventoryBuilder.setEmpty(panel.getEmpty());
        inventoryBuilder.setItems(panel.getItems());
        Inventory inventory = inventoryBuilder.build();
        inventory.onClick(click -> {
            try {
                Configs.Item item = panel.getItems().get(click.slot());
                if(item != null && item.getCommands() != null) {
                    new InventoryClickHandler(velocityGUI).execute(item.getCommands(), click);
                } else if(click.slot() >= 0 && click.slot() < 9*inventoryBuilder.getRowsNumber() && panel.getEmptysound() != null){
                    //play sound if config is set to
                    SoundHelper.playSound(protocolizePlayer, panel.getEmptysound());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //stop the player from keeping the item even on exceptions
                click.cancelled(true);
            }
        });

        protocolizePlayer.openInventory(inventory);

        if(panel.getSound() != null) {
            SoundHelper.playSound(protocolizePlayer, panel.getEmptysound());
        }
    }
}

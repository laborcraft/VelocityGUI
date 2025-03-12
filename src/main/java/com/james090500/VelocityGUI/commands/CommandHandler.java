package com.james090500.VelocityGUI.commands;

import com.james090500.VelocityGUI.VelocityGUI;
import com.james090500.VelocityGUI.config.Configs;
import com.james090500.VelocityGUI.helpers.InventoryLauncher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedArgument;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Iterator;
import java.util.Map;

public class CommandHandler {

    private VelocityGUI velocityGUI;

    public CommandHandler(VelocityGUI velocityGUI) {
        this.velocityGUI = velocityGUI;
    }

    /**
     *
     */
    public int panelList(CommandContext<CommandSource> commandSourceCommandContext){
        CommandSource source = commandSourceCommandContext.getSource();
        ComponentBuilder<TextComponent, TextComponent.Builder> msg = Component.text();
        msg.append(Configs.getLang().getAvailablePanels());
        msg.append(Component.space());
        Iterator<Map.Entry<String, Configs.Panel>> it = Configs.getPanels().entrySet().iterator();
        if(!it.hasNext()){
            msg.append(Component.text("-", NamedTextColor.GRAY));
        }
        boolean first = true;
        for(Map.Entry<String, Configs.Panel> entry : Configs.getPanels().entrySet()){
            String title = entry.getKey();
            Configs.Panel panel = entry.getValue();
            //Hide panels with no permissions
            if(panel.getPerm().equalsIgnoreCase("default") || source.hasPermission(panel.getPerm())) {
                if(!first){
                    msg.append(Component.text(", ", NamedTextColor.WHITE));
                } else {
                    first = false;
                }
                msg.append(Component.text(title, NamedTextColor.GRAY));
            }
        }
        source.sendMessage(msg);
        return 0;
    }

    /**
     * The command for /vgui panel
     * Handles listing panel and passes a valid argument to the InventoryLauncher
     * @param commandSourceCommandContext
     * @return
     */
    public int panel(CommandContext<CommandSource> commandSourceCommandContext) {

        CommandSource source = commandSourceCommandContext.getSource();

        //compute target
        Player target;
        if(commandSourceCommandContext.getArguments().size() == 1){
            //check that sender is a player
            if(!(source instanceof Player)) {
                source.sendMessage(Configs.getLang().getOnlyPlayersCanRunThis());
                return 0;
            }
            target = (Player) source;

        } else if(commandSourceCommandContext.getArguments().size() == 2){
            String arg = commandSourceCommandContext.getArgument("player", String.class);
            target = velocityGUI.getServer().getAllPlayers().stream().filter(s->s.getUsername().equalsIgnoreCase(arg)).findAny().orElse(null);
            if(target == null){
                source.sendMessage(Configs.getLang().getPlayerNotFound());
                return 0;
            }
        } else {
            source.sendMessage(Configs.getLang().getUnknownArgs());
            return 0;
        }

        ParsedArgument<CommandSource, ?> nameArgument = commandSourceCommandContext.getArguments().get("name");
        if(nameArgument == null) {
            return panelList(commandSourceCommandContext);
        }

        new InventoryLauncher(velocityGUI).execute((String) nameArgument.getResult(), target);
        return 1;
    }

    /**
     * Reloads the configs
     * @param commandSourceCommandContext
     * @return
     */
    public int reload(CommandContext<CommandSource> commandSourceCommandContext) {
        Configs.loadConfigs(velocityGUI);
        CommandSource source = commandSourceCommandContext.getSource();
        source.sendMessage(Configs.getLang().getPluginReloaded());
        velocityGUI.getLogger().info("VelocityGUI Reloaded");
        return 1;
    }

    /**
     * A bit of basic about information
     * @param commandSourceCommandContext
     * @return
     */
    public int about(CommandContext<CommandSource> commandSourceCommandContext) {
        CommandSource source = commandSourceCommandContext.getSource();
        source.sendMessage(Component.text("VelocityGUI by james090500", NamedTextColor.GREEN));
        return 1;
    }
}

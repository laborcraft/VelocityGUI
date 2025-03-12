package com.james090500.VelocityGUI.commands;

import com.james090500.VelocityGUI.VelocityGUI;
import com.james090500.VelocityGUI.config.Configs;
import com.james090500.VelocityGUI.helpers.InventoryLauncher;
import com.james090500.VelocityGUI.helpers.StringUtil;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PanelCommand implements SimpleCommand {

    ProxyServer proxyServer = VelocityGUI.getInstance().getServer();

    /**
     * Handles the execution of a panel plugin, eventually adding server connection arguments.
     */
    public PanelCommand(VelocityGUI velocityGUI, Configs.Panel panel) {
        this.velocityGUI = velocityGUI;
        this.panel = panel;
        this.panelServers = Configs.getPanels().get(panel.getName()).getServers();;
    }

    private final VelocityGUI velocityGUI;
    private final Configs.Panel panel;
    // if this is a "hub" command this array specifies which servers this command supports
    private final String[] panelServers;

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if(!(source instanceof Player)) {
            source.sendMessage(Configs.getLang().getOnlyPlayersCanRunThis());
            return;
        }

        //handle panel
        if(invocation.arguments().length == 0 || panelServers == null) {
            new InventoryLauncher(velocityGUI).execute(panel.getName(), (Player) source);

        //handle servers
        } else if(invocation.arguments().length == 1) {
            String arg = invocation.arguments()[0];
            RegisteredServer server = proxyServer.getServer(arg).orElse(null);
            if(Arrays.asList(panelServers).contains(arg) && server != null){
                //is player connected to this server?
                if(server.getPlayersConnected().stream().anyMatch(p->p.equals(source))){
                    source.sendMessage(Configs.getLang().getAlreadyConnected());
                } else {
                    ((Player)source).createConnectionRequest(server);
                }
            } else {
                source.sendMessage(Configs.getLang().getUnknownServer());
                velocityGUI.getLogger().warn(((Player) source).getUsername() + " tried to connect to an unknown server: " + arg);
            }

        } else {
            source.sendMessage(Configs.getLang().getUnknownArgs());
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        if (panelServers != null) {
            if(invocation.arguments().length == 0) return Arrays.asList(panelServers);
            if(invocation.arguments().length == 1) {
                return StringUtil.copyPartialMatches(invocation.arguments()[0], Arrays.asList(panelServers), new ArrayList<>());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return panel.getPerm().equalsIgnoreCase("default") || invocation.source().hasPermission(panel.getPerm());
    }
}

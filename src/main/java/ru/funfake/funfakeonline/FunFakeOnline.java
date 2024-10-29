package ru.funfake.funfakeonline;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import ru.funfake.funfakeonline.listeners.PlayerListener;
import ru.funfake.funfakeonline.placeholders.FFOnlineExpansion;

import java.util.List;

public final class FunFakeOnline extends JavaPlugin {
    private static FunFakeOnline instance;
    public static FunFakeOnline getInstance() {
        return instance;
    }
    private static PlayersInteraction pInteraction;
    public static PlayersInteraction getPlayersInteractionInstance() {
        return pInteraction;
    }

    public static ProtocolManager protocolManager;


    @Override
    public void onEnable() {
        pInteraction = new PlayersInteraction(this);
        instance = this;

        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            protocolManager = ProtocolLibrary.getProtocolManager();
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCould not find ProtocolLib dependency!"));
            getServer().getPluginManager().disablePlugin(this);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FFOnlineExpansion().register();
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCould not find PlaceHolderAPI dependency. Launch without it"));
        }

        pInteraction.generatePlayers();
        pInteraction.resendPlayers();
        setupListener();
        registerEvents();
        loadConfig();
        pInteraction.initialisePlayerBehaviour();

    }

    @Override
    public void onDisable() {
    }

    private void loadConfig() {
        saveDefaultConfig();
    }

    public void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(pInteraction), this);
    }

    public void setupListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, List.of(PacketType.Status.Server.SERVER_INFO), ListenerOptions.ASYNC) {
            public void onPacketSending(PacketEvent e) {
                if (getConfig().getBoolean("fake_MOTD_enable")) {
                    WrappedServerPing ping = e.getPacket().getServerPings().read(0);
                    ping.setPlayersOnline(Bukkit.getOnlinePlayers().size() + pInteraction.getPlayers().size());
                    if (ping.getPlayersMaximum() < ping.getPlayersOnline()) {
                        ping.setPlayersMaximum(Bukkit.getOnlinePlayers().size() + pInteraction.getPlayers().size() + 1);
                    }

                    ping.setPlayers(pInteraction.getProfiles());
                }

            }
        });
    }

}

package ru.funfake.funfakeonline;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.funfake.funfakeonline.generators.NameGenerator;
import ru.funfake.funfakeonline.generators.SkinPicker;
import ru.funfake.funfakeonline.runnables.UpdatePing;

import java.util.*;

public class PlayersInteraction {
    private static FunFakeOnline pluginInstance;
    private static final List<PlayerInfoData> players = new ArrayList<>();
    public static List<String> fakePlayersName;

    public static int pingMaximum;
    private static long pingInterval;
    public static int mode;
    public static int factor;
    private static int count;

    public PlayersInteraction(FunFakeOnline plugin) {
        pluginInstance = plugin;
        fakePlayersName = pluginInstance.getConfig().getStringList("players_names");
        pingMaximum = pluginInstance.getConfig().getConfigurationSection("ping").getInt("maximum");
        pingInterval = pluginInstance.getConfig().getConfigurationSection("ping").getInt("interval");

        mode = pluginInstance.getConfig().getConfigurationSection("add_players").getInt("mode");
        factor = pluginInstance.getConfig().getConfigurationSection("add_players").getInt("factor");
        count = pluginInstance.getConfig().getConfigurationSection("add_players").getInt("count");

    }

    public void resendPlayers() {
        Bukkit.getOnlinePlayers().forEach((player) -> {
            PacketContainer pi = FunFakeOnline.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            pi.getModifier().writeDefaults();
            pi.getPlayerInfoDataLists().write(0, getPlayers());

            FunFakeOnline.protocolManager.sendServerPacket(player, pi);

        });

    }

    public void unsendPlayers() {
        PacketContainer pc = FunFakeOnline.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        pc.getModifier().writeDefaults();
        pc.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        pc.getPlayerInfoDataLists().write(0, getPlayers());
        Bukkit.getOnlinePlayers().forEach((player) -> {
            FunFakeOnline.protocolManager.sendServerPacket(player, pc);

        });

    }

    public void sendPlayers(Player player) {
        PacketContainer pc = FunFakeOnline.protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        pc.getModifier().writeDefaults();
        pc.getPlayerInfoDataLists().write(0, getPlayers());

        FunFakeOnline.protocolManager.sendServerPacket(player, pc);

    }

    public void addPlayer(String player) {
        PlayerInfoData info = new PlayerInfoData(
                new WrappedGameProfile(UUID.randomUUID(), player),
                new Random().nextInt(pingMaximum),
                EnumWrappers.NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', player)));
        players.add(info);
        unsendPlayers();
        resendPlayers();
    }

    public void addPlayer(PlayerInfoData player) {
        players.add(player);
    }

    public void removePlayer(String player) {
        PlayerInfoData info = new PlayerInfoData(
                new WrappedGameProfile(UUID.randomUUID(), player),
                new Random().nextInt(pingMaximum),
                EnumWrappers.NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', player)));
        players.remove(info);
    }

    public void removePlayer(int index) {
        players.remove(index);
    }

    public List<WrappedGameProfile> getProfiles() {
        List<WrappedGameProfile> profiles = new ArrayList<>();
        getPlayers().forEach((player) -> profiles.add(player.getProfile()));
        return profiles;
    }

    public void generatePlayers() {
        if (mode == 1) {
            for (int i = 0; i < count; i++) {

                String playerx;

                if (i > fakePlayersName.size() - 1) {
                    playerx = (new NameGenerator()).generateName();
                    PlayerInfoData info = new PlayerInfoData(
                            new WrappedGameProfile(UUID.randomUUID(), playerx),
                            new Random().nextInt(pingMaximum),
                            EnumWrappers.NativeGameMode.SURVIVAL,
                            WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', playerx)));

                    AbstractMap.SimpleEntry<String, String> skin = (new SkinPicker()).getSkin();
                    String value = skin.getKey();
                    String signature = skin.getValue();
                    info.getProfile().getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));

                    players.add(info);
                    continue;
                }
                playerx = fakePlayersName.get(i);

                if (playerx.length() <= 16) {
                    PlayerInfoData info = new PlayerInfoData(
                            new WrappedGameProfile(UUID.randomUUID(), playerx),
                            new Random().nextInt(pingMaximum),
                            EnumWrappers.NativeGameMode.SURVIVAL,
                            WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', playerx)));

                    AbstractMap.SimpleEntry<String, String> skin = (new SkinPicker()).getSkin();
                    String value = skin.getKey();
                    String signature = skin.getValue();
                    info.getProfile().getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));

                    players.add(info);

                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8Fake player &c" + playerx + " &8ignored, name cannot be greater than 16 characters including colour codes"));
                }
            }

        }

    }



    public void setPing(UUID uuid) {
        for (PlayerInfoData pid : getPlayers()) {
            if (pid.getProfile().getUUID().equals(uuid)) {
                PlayerInfoData info = new PlayerInfoData(
                        new WrappedGameProfile(
                                pid.getProfile().getUUID(),
                                pid.getProfile().getName()
                        ),
                        new Random().nextInt(pingMaximum),
                        EnumWrappers.NativeGameMode.SURVIVAL,
                        pid.getDisplayName());
                this.unsendPlayers();
                this.getPlayers().remove(pid);
                this.getPlayers().add(info);
                this.resendPlayers();
                return;
            }
        }


    }

    public void initialisePlayerBehaviour() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                pluginInstance,
                new UpdatePing(this),
                0L,
                pingInterval * 20L);

    }

    public List<PlayerInfoData> getPlayers() {
        return players;
    }


}

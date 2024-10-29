package ru.funfake.funfakeonline.listeners;

import com.comphenix.protocol.wrappers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.funfake.funfakeonline.PlayersInteraction;
import ru.funfake.funfakeonline.generators.NameGenerator;
import ru.funfake.funfakeonline.generators.SkinPicker;

import java.util.AbstractMap;
import java.util.Random;
import java.util.UUID;

public class PlayerListener implements Listener {
    PlayersInteraction pInteraction;

    public PlayerListener(PlayersInteraction pi) {
        pInteraction = pi;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (PlayersInteraction.mode == 0) {
            pInteraction.unsendPlayers();
            for (int i = 0; i < Bukkit.getOnlinePlayers().size() * PlayersInteraction.factor; i++) {

                String playerx;

                if (i > PlayersInteraction.fakePlayersName.size() - 1) {
                    playerx = (new NameGenerator()).generateName();
                    PlayerInfoData info = new PlayerInfoData(
                            new WrappedGameProfile(UUID.randomUUID(), playerx),
                            new Random().nextInt(PlayersInteraction.pingMaximum),
                            EnumWrappers.NativeGameMode.SURVIVAL,
                            WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', playerx)));

                    AbstractMap.SimpleEntry<String, String> skin = (new SkinPicker()).getSkin();
                    String value = skin.getKey();
                    String signature = skin.getValue();
                    info.getProfile().getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));

                    pInteraction.addPlayer(info);
                    continue;
                }
                playerx = PlayersInteraction.fakePlayersName.get(i);

                if (playerx.length() <= 16) {
                    PlayerInfoData info = new PlayerInfoData(
                            new WrappedGameProfile(UUID.randomUUID(), playerx),
                            new Random().nextInt(PlayersInteraction.pingMaximum),
                            EnumWrappers.NativeGameMode.SURVIVAL,
                            WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', playerx)));

                    AbstractMap.SimpleEntry<String, String> skin = (new SkinPicker()).getSkin();
                    String value = skin.getKey();
                    String signature = skin.getValue();
                    info.getProfile().getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));

                    pInteraction.addPlayer(info);

                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8Fake player &c" + playerx + " &8ignored, name cannot be greater than 16 characters including colour codes"));
                }
            }
            pInteraction.resendPlayers();
        }
        else pInteraction.sendPlayers(p);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent e) {
        System.out.println(Bukkit.getOnlinePlayers().size());
        if ((Bukkit.getOnlinePlayers().size()-1) + (Bukkit.getOnlinePlayers().size()-1)*PlayersInteraction.factor < pInteraction.getPlayers().size()) {
            int size = pInteraction.getPlayers().size() - (Bukkit.getOnlinePlayers().size()-1) + (Bukkit.getOnlinePlayers().size()-1)*PlayersInteraction.factor;

            int removed = 0; // Setup the variable for removal counting
            while (removed < size) { // While we still haven't removed 5 entries OR second list size
                pInteraction.removePlayer(pInteraction.getPlayers().size() - 1); // Remove the last entry of the list
                removed++; // Increases 'removed' count
            }

        }
    }


}

package ru.funfake.funfakeonline.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.funfake.funfakeonline.FunFakeOnline;

public class FFOnlineExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "ffonline";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MordaSobaki";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        switch (params) {
            case "online":
                return Integer.toString(Bukkit.getOnlinePlayers().size() + FunFakeOnline.getPlayersInteractionInstance().getPlayers().size());
        }
        return super.onRequest(player, params);
    }
}

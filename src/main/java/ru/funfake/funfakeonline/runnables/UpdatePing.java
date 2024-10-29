package ru.funfake.funfakeonline.runnables;

import ru.funfake.funfakeonline.PlayersInteraction;

import java.util.Random;

public class UpdatePing implements Runnable {
    PlayersInteraction pInteraction;

    public UpdatePing(PlayersInteraction pi) {
        pInteraction = pi;
    }

    public void run() {
        if (pInteraction.getPlayers().isEmpty()) return;
        pInteraction.setPing(
                pInteraction.getPlayers().get(
                        new Random().nextInt(pInteraction.getPlayers().size())
                ).getProfile().getUUID());
    }
}

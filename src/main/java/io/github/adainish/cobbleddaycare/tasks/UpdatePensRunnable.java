package io.github.adainish.cobbleddaycare.tasks;

import io.github.adainish.cobbleddaycare.CobbledDayCare;

public class UpdatePensRunnable implements Runnable {
    @Override
    public void run() {
        if (CobbledDayCare.manager != null)
        {
            CobbledDayCare.manager.generateEggs();
            CobbledDayCare.manager.playerData.values().forEach(player -> {
                player.eggBox.removeAndSendHatchedEggs(player.uuid);
            });
        }
    }
}

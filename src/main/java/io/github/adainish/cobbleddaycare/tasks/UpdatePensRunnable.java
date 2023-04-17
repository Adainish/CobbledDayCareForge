package io.github.adainish.cobbleddaycare.tasks;

import io.github.adainish.cobbleddaycare.CobbledDayCare;
import io.github.adainish.cobbleddaycare.util.Util;

public class UpdatePensRunnable implements Runnable {
    @Override
    public void run() {
        if (CobbledDayCare.manager != null)
        {
            CobbledDayCare.manager.generateEggs();
            CobbledDayCare.manager.playerData.values().forEach(player -> {
                if (Util.isOnline(player.uuid)) {
                    player.eggBox.removeAndSendHatchedEggs(player.uuid);
                }
            });
        }
    }
}

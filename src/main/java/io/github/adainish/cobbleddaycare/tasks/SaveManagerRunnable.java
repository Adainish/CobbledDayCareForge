package io.github.adainish.cobbleddaycare.tasks;

import io.github.adainish.cobbleddaycare.CobbledDayCare;

public class SaveManagerRunnable implements Runnable {
    @Override
    public void run() {
        if (CobbledDayCare.manager != null)
        {
            CobbledDayCare.getLog().warn("Saving Day Care data...");
            CobbledDayCare.dayCareStorage.save();
        }
    }
}

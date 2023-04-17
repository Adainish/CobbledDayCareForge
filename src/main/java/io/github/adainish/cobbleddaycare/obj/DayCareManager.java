package io.github.adainish.cobbleddaycare.obj;

import io.github.adainish.cobbleddaycare.CobbledDayCare;

import java.util.HashMap;
import java.util.UUID;

public class DayCareManager
{
    public HashMap<UUID, Player> playerData = new HashMap<>();

    public transient HashMap<String, ConfigurableDayCarePen> penDataMap = new HashMap<>();

    public DayCareManager()
    {

    }

    public void loadPenData()
    {
        this.penDataMap = CobbledDayCare.penConfig.configurablePens;
    }

    public void verifyAndUpdate(Player player)
    {
        //loop through player data and lock/unlock pens that aren't available/don't eixst and/or that don't match or requirements
        player.dayCarePens.forEach((idString, dayCarePen) -> {
            ConfigurableDayCarePen configurableDayCarePen = null;
            if (penDataMap.containsKey(idString))
                configurableDayCarePen = penDataMap.get(idString);
            if (configurableDayCarePen != null)
            {
                dayCarePen.coolDownMinutes = configurableDayCarePen.coolDownMinutes;
                dayCarePen.eggCoolDown = configurableDayCarePen.eggCoolDown;
                dayCarePen.unlockCost = configurableDayCarePen.unlockCost;
                dayCarePen.permissionID = configurableDayCarePen.permissionID;
                dayCarePen.enabled = configurableDayCarePen.enabled;
            } else {
                dayCarePen.enabled = false;
            }
        });
        //add new pens if missing
        penDataMap.forEach((stringID, configurableDayCarePen) -> {
            if (player.dayCarePens.containsKey(stringID)) {
                return;
            }
            DayCarePen dayCarePen = new DayCarePen();
            dayCarePen.dayCareID = configurableDayCarePen.dayCareID;
            dayCarePen.unlockCost = configurableDayCarePen.unlockCost;
            dayCarePen.eggCoolDown = configurableDayCarePen.eggCoolDown;
            dayCarePen.permissionID = configurableDayCarePen.permissionID;
            dayCarePen.coolDownMinutes = configurableDayCarePen.coolDownMinutes;
            player.dayCarePens.put(stringID, dayCarePen);
            this.playerData.put(player.uuid, player);
        });
    }

    public void generateEggs()
    {
        playerData.values().forEach(Player::generateEggs);
    }
}

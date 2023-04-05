package io.github.adainish.cobbleddaycare.obj;

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

    }

    public void verifyAndUpdate(Player player)
    {
        //loop through player data and lock/unlock pens that aren't available/don't eixst and/or that don't match or requirements
    }

    public void generateEggs()
    {
        playerData.values().forEach(Player::generateEggs);
    }
}

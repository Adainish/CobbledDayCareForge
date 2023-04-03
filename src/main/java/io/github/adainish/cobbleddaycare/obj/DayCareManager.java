package io.github.adainish.cobbleddaycare.obj;

import java.util.HashMap;
import java.util.UUID;

public class DayCareManager
{
    public HashMap<UUID, Player> playerData = new HashMap<>();


    public DayCareManager()
    {

    }

    public void generateEggs()
    {
        playerData.values().forEach(Player::generateEggs);
    }
}

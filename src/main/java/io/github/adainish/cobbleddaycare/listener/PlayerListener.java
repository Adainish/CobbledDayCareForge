package io.github.adainish.cobbleddaycare.listener;

import io.github.adainish.cobbleddaycare.CobbledDayCare;
import io.github.adainish.cobbleddaycare.obj.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerListener
{
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() != null)
        {
            if (!CobbledDayCare.manager.playerData.containsKey(event.getEntity().getUUID()))
            {
                Player player = new Player();
                player.uuid = event.getEntity().getUUID();
                CobbledDayCare.manager.playerData.put(event.getEntity().getUUID(), player);
            }
            CobbledDayCare.manager.verifyAndUpdate(CobbledDayCare.manager.playerData.get(event.getEntity().getUUID()));
        }
    }
}

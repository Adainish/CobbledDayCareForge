package io.github.adainish.cobbleddaycare.cmd;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.cobbleddaycare.CobbledDayCare;
import io.github.adainish.cobbleddaycare.obj.Player;
import io.github.adainish.cobbleddaycare.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class DayCareCommand
{
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("daycare")
                .executes(cc -> {
                    try {
                        Player player = CobbledDayCare.manager.playerData.get(cc.getSource().getPlayerOrException().getUUID());
                        if (player != null)
                            player.openUI(cc.getSource().getPlayer());
                        else
                            cc.getSource().sendFailure(Component.literal(Util.formattedString("&cSomething went wrong while retrieving your player data...")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return 1;
                })
                .then(Commands.literal("unlock")
                        .executes(cc -> {

                            return 1;
                        })
                )
//                .then(Commands.literal("reload")
//                        .requires(cs -> PermissionUtil.checkPermAsPlayer(cs, PermissionWrapper.adminPermission))
//                        .executes(cc -> {
//                            cc.getSource().sendFeedback(new StringTextComponent(Util.formattedString("&aReloading Hunts")), true);
//                            ReturnHunts.instance.reload();
//                            return 1;
//                        })
//                )
                ;
    }
}

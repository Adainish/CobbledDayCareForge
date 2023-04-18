package io.github.adainish.cobbleddaycare.cmd;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.cobbleddaycare.CobbledDayCare;
import io.github.adainish.cobbleddaycare.obj.Player;
import io.github.adainish.cobbleddaycare.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

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
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&4Please provide a target player");
                            return 1;
                        })
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(cc -> {
                                    ServerPlayer serverPlayer = EntityArgument.getPlayer(cc, "player");

                                    Player player = CobbledDayCare.manager.playerData.get(serverPlayer.getUUID());
                                    if (player != null)
                                    {
                                        Util.send(cc.getSource(), "&4Please provide a valid pen name");
                                    }
                                    return 1;
                                })
                                .then(Commands.argument("pen", StringArgumentType.string())
                                        .executes(cc -> {
                                            ServerPlayer serverPlayer = EntityArgument.getPlayer(cc, "player");
                                            String penName = StringArgumentType.getString(cc, "pen");
                                            if (CobbledDayCare.manager.penDataMap.containsKey(penName)) {
                                                Player player = CobbledDayCare.manager.playerData.get(serverPlayer.getUUID());
                                                if (player != null) {
                                                    player.updatePenStatus(penName, true);
                                                }
                                            } else {
                                                Util.send(cc.getSource(), "&4That was not a valid pen name!");
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("lock")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&4Please provide a target player");
                            return 1;
                        })
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(cc -> {
                                    ServerPlayer serverPlayer = EntityArgument.getPlayer(cc, "player");

                                    Player player = CobbledDayCare.manager.playerData.get(serverPlayer.getUUID());
                                    if (player != null)
                                    {
                                        Util.send(cc.getSource(), "&4Please provide a valid pen name");
                                    }
                                    return 1;
                                })
                                .then(Commands.argument("pen", StringArgumentType.string())
                                        .executes(cc -> {
                                            ServerPlayer serverPlayer = EntityArgument.getPlayer(cc, "player");
                                            String penName = StringArgumentType.getString(cc, "pen");
                                            if (CobbledDayCare.manager.penDataMap.containsKey(penName)) {
                                                Player player = CobbledDayCare.manager.playerData.get(serverPlayer.getUUID());
                                                if (player != null) {
                                                    player.updatePenStatus(penName, false);
                                                }
                                            } else {
                                                Util.send(cc.getSource(), "&4That was not a valid pen name!");
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("reload")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                        .executes(cc -> {
                            cc.getSource().sendSystemMessage(Component.literal(Util.formattedString("&aReloading DayCare")));
                            CobbledDayCare.instance.reload();
                            return 1;
                        })
                )
                ;
    }
}

package io.github.adainish.cobbleddaycare.cmd;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.cobbleddaycare.CobbledDayCare;
import io.github.adainish.cobbleddaycare.obj.Player;
import io.github.adainish.cobbleddaycare.property.BreedCapabilityProperty;
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
                .then(Commands.literal("unbreedable")
                        .executes(cc -> {
                                Util.send(cc.getSource(), "&7Please provide a valid slot");
                            return 1;
                        })
                        .then(Commands.argument("partyslot", IntegerArgumentType.integer(1, 6))
                                .executes(cc -> {
                                    try {
                                        int slot = IntegerArgumentType.getInteger(cc, "partyslot");
                                        PartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(cc.getSource().getPlayerOrException().getUUID());
                                        if (partyStore.get(slot - 1) != null)
                                        {
                                            Pokemon pokemon = partyStore.get(slot - 1);
                                            if (pokemon != null) {
                                                if (pokemon.getOwnerPlayer() != null) {
                                                    ServerPlayer serverPlayer = cc.getSource().getPlayer();
                                                    if (serverPlayer != null) {
                                                        if (pokemon.getOwnerPlayer().getUUID().equals(cc.getSource().getPlayer().getUUID())) {
                                                            BreedCapabilityProperty breedCapabilityProperty = new BreedCapabilityProperty();
                                                            String status = "&aBreedable";
                                                            //if breedable set unbreedable
                                                            if (breedCapabilityProperty.isBreedAble(pokemon))
                                                            {
                                                                breedCapabilityProperty.unBreedAble().apply(pokemon);
                                                                status = "&cUnbreedable";
                                                            } else {
                                                                //else make breedable
                                                                breedCapabilityProperty.breedAble().apply(pokemon);
                                                            }
                                                            //send message
                                                            Util.send(serverPlayer.getUUID(), "&7Your pokemon is now %status%".replace("%status%", status));
                                                        } else {
                                                            Util.send(cc.getSource(), "&4&lOnly the Pokemon's OG Trainer may use this tool on them!");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } catch (NoPokemonStoreException e) {
                                        Util.send(cc.getSource(), "&eSomething went wrong while running the command");
                                    }

                                    return 1;
                                })
                        )
                )
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

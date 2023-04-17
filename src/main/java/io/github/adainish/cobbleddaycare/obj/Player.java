package io.github.adainish.cobbleddaycare.obj;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.google.gson.JsonObject;
import io.github.adainish.cobbleddaycare.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Player
{
    public UUID uuid;
    public EggBox eggBox;

    public HashMap<String, DayCarePen> dayCarePens = new HashMap<>();


    public Player()
    {
        eggBox = new EggBox();
    }

    public void generateEggs()
    {
        if (Util.isOnline(uuid)) {
            for (DayCarePen pen : dayCarePens.values()) {
                if (eggBox.isFull())
                    break;
                if (pen.getParentOne() == null || pen.getParentTwo() == null)
                    continue;
                if (pen.shouldGenerateEgg()) {
                    Egg egg = pen.generateEgg();
                    if (egg != null) {
                        eggBox.eggList.add(egg);
                    }
                }
            }
        }
    }


    public void openUI(ServerPlayer serverPlayer)
    {
        UIManager.openUIForcefully(serverPlayer, mainPenUI());
    }

    public GooeyButton filler() {
        return GooeyButton.builder()
                .display(new ItemStack(Items.GRAY_STAINED_GLASS_PANE))
                .build();
    }

    public List<Button> eggButtons()
    {
        List<Button> gooeyButtons = new ArrayList<>();
        eggBox.eggList.forEach(egg -> {
            ItemStack stack = new ItemStack(Items.EGG);
            if (egg.hasHatched())
                stack = Util.returnIcon(egg.getGeneratedPokemon());
            GooeyButton button = GooeyButton.builder()
                    .title(Util.formattedString("&bPokemon Egg"))
                    .lore(Util.formattedArrayList(Arrays.asList("%status%".replace("%status%", egg.hatchedStatus()))))
                    .display(stack)
                    .build();
            gooeyButtons.add(button);
        });
        return gooeyButtons;
    }

    public List<Button> penButtons()
    {
        List<Button> gooeyButtons = new ArrayList<>();
        dayCarePens.forEach((s, dayCarePen) -> {
            if (dayCarePen.enabled) {
                GooeyButton button = GooeyButton.builder()
                        .title(Util.formattedString("&b%penname%".replace("%penname%", dayCarePen.dayCareID)))
                        .lore(Util.formattedArrayList(Arrays.asList("" + dayCarePen.unlockStatus())))
                        .display(new ItemStack(CobblemonItems.LINK_CABLE.get().asItem()))
                        .onClick(b -> {
                            if (dayCarePen.unlocked) {
                                UIManager.openUIForcefully(b.getPlayer(), viewPenUI(dayCarePen));
                            }
                        })
                        .build();

                gooeyButtons.add(button);
            }
        });
        return gooeyButtons;
    }

    public GooeyPage selectParentUI(DayCarePen dayCarePen, boolean parentOne)
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(4);
        builder.fill(filler());

        GooeyButton goBack = GooeyButton.builder()
                .title(Util.formattedString("Go Back"))
                .display(new ItemStack(Items.ARROW))
                .onClick(b ->
                        UIManager.openUIForcefully(b.getPlayer(), mainPenUI()))
                .build();

        builder.set(0, 0, goBack);
        PartyStore partyStore = null;
        try {
            AtomicInteger i = new AtomicInteger();
            partyStore = Cobblemon.INSTANCE.getStorage().getParty(uuid);
            PartyStore finalPartyStore1 = partyStore;
            partyStore.forEach(pokemon -> {
                if (pokemon != null) {
                    GooeyButton button = GooeyButton.builder()
                            .title(Util.formattedString("&b" + pokemon.getSpecies().getName()))
                            .lore(Util.formattedArrayList(Util.pokemonLore(pokemon)))
                            .display(Util.returnIcon(pokemon))
                            .onClick(b -> {
                                if (parentOne) {
                                    if (dayCarePen.pokemonOne != null)
                                    {
                                        //send to player storage
                                        finalPartyStore1.add(dayCarePen.getParentOne());
                                    }
                                    dayCarePen.pokemonOne = pokemon.saveToJSON(new JsonObject());
                                    finalPartyStore1.remove(pokemon);
                                }  else {
                                    if (dayCarePen.pokemonTwo != null)
                                    {
                                        //send to player storage
                                        finalPartyStore1.add(dayCarePen.getParentTwo());
                                    }
                                    dayCarePen.pokemonTwo = pokemon.saveToJSON(new JsonObject());
                                    finalPartyStore1.remove(pokemon);
                                }
                                UIManager.openUIForcefully(b.getPlayer(), viewPenUI(dayCarePen));
                            })
                            .build();
                    builder.set(1, i.get() + 1, button);
                    i.getAndIncrement();
                }
            });
        } catch (NoPokemonStoreException e) {

        }

        PartyStore finalPartyStore = partyStore;
        GooeyButton removeParent = GooeyButton.builder()
                .title(Util.formattedString("&4&lRemove Parent"))
                .onClick(buttonAction -> {
                    if (finalPartyStore != null) {
                        if (parentOne) {
                            if (dayCarePen.pokemonOne != null) {
                                //send to player storage
                                finalPartyStore.add(dayCarePen.getParentOne());
                                dayCarePen.pokemonOne = null;
                            }
                        } else {
                            if (dayCarePen.pokemonTwo != null)
                            {
                                //send to player storage
                                finalPartyStore.add(dayCarePen.getParentTwo());
                                dayCarePen.pokemonTwo = null;
                            }
                        }
                    }
                    UIManager.openUIForcefully(buttonAction.getPlayer(), viewPenUI(dayCarePen));
                })
                .display(new ItemStack(Items.ENDER_CHEST))
                .build();

        builder.set(1, 0, removeParent);

        return GooeyPage.builder().template(builder.build()).build();
    }

    public GooeyPage viewPenUI(DayCarePen dayCarePen)
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(4);
        builder.fill(filler());

        GooeyButton goBack = GooeyButton.builder()
                .title(Util.formattedString("Go Back"))
                .display(new ItemStack(Items.ARROW))
                .onClick(b ->
                        UIManager.openUIForcefully(b.getPlayer(), mainPenUI()))
                .build();

        String parentOneTitle = "&cSelect a parent";
        ItemStack parentOneStack = new ItemStack(Items.BARRIER);
        if (dayCarePen.pokemonOne != null) {
            parentOneStack = Util.returnIcon(dayCarePen.getParentOne());
            parentOneTitle = "&b" + dayCarePen.getParentOne().getSpecies().getName();
        }
        String parentTwoTitle = "&cSelect a parent";
        ItemStack parentTwoStack = new ItemStack(Items.BARRIER);
        if (dayCarePen.pokemonTwo != null) {
            parentTwoStack = Util.returnIcon(dayCarePen.getParentTwo());
            parentTwoTitle = "&b" + dayCarePen.getParentTwo().getSpecies().getName();
        }
        GooeyButton parentOne = GooeyButton.builder()
                .title(Util.formattedString(parentOneTitle))
                .display(parentOneStack)
                .onClick(buttonAction -> {
                    UIManager.openUIForcefully(buttonAction.getPlayer(), selectParentUI(dayCarePen, true));
                })
                .build();

        GooeyButton parentTwo = GooeyButton.builder()
                .title(Util.formattedString(parentTwoTitle))
                .display(parentTwoStack)
                .onClick(buttonAction -> {
                    UIManager.openUIForcefully(buttonAction.getPlayer(), selectParentUI(dayCarePen, false));
                })
                .build();
        builder.set(0, 0, goBack);
        builder.set(1, 3, parentOne);
        builder.set(1, 5, parentTwo);

        return GooeyPage.builder().template(builder.build()).build();
    }

    public LinkedPage eggBoxUI()
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());
        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Previous Page"))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Next Page"))
                .linkType(LinkType.Next)
                .build();

        GooeyButton goBack = GooeyButton.builder()
                .title(Util.formattedString("Go Back"))
                .display(new ItemStack(Items.ARROW))
                .onClick(b ->
                        UIManager.openUIForcefully(b.getPlayer(), mainPenUI()))
                .build();

        builder.set(0, 0, goBack);
        builder.set(0, 3, previous)
                .set(0, 5, next)
                .rectangle(1, 1, 3, 7, placeHolderButton);

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), eggButtons(), LinkedPage.builder().template(builder.build()));
    }

    public LinkedPage mainPenUI()
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());
        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Previous Page"))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Next Page"))
                .linkType(LinkType.Next)
                .build();

        GooeyButton viewEggBox = GooeyButton.builder()
                .title(Util.formattedString("&bEgg Box"))
                .display(new ItemStack(Items.EGG))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), eggBoxUI());
                })
                .build();

        builder.set(0, 3, previous)
                .set(0, 5, next)
                .set(0, 4, viewEggBox)
                .rectangle(1, 1, 3, 7, placeHolderButton);

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), penButtons(), LinkedPage.builder().template(builder.build()));
    }




}

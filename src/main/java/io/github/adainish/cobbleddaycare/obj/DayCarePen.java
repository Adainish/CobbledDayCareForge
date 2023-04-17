package io.github.adainish.cobbleddaycare.obj;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.JsonObject;
import io.github.adainish.cobbleddaycare.CobbledDayCare;
import io.github.adainish.cobbleddaycare.config.SpeciesConfig;
import io.github.adainish.cobbleddaycare.util.RandomHelper;
import io.github.adainish.cobbleddaycare.util.Util;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DayCarePen
{
    public String dayCareID;
    public JsonObject pokemonOne;
    public JsonObject pokemonTwo;

    public int coolDownMinutes = 5;
    public int eggCoolDown = 5;
    public long lastStart = 0;

    public long lastEggAttempt = 0;

    public double unlockCost = 0;

    public String permissionID = "";

    public boolean unlocked = false;

    public boolean enabled = true;


    public DayCarePen()
    {

    }

    public int getTimerFromSpecies(Pokemon parentOne, Pokemon parentTwo)
    {
        int timer = 1;

        return timer;
    }

    public String unlockStatus()
    {
        if (unlocked)
            return "&aThis pen has been unlocked";
        return "&cYou're yet to unlock this pen...";
    }

    public boolean shouldGenerateEgg()
    {
        return System.currentTimeMillis() > (lastEggAttempt + TimeUnit.MINUTES.toMillis(getTimerFromSpecies(getParentOne(), getParentTwo())));
    }

    public boolean hasMove(Pokemon pokemon, String name)
    {
        AtomicBoolean val = new AtomicBoolean(false);
        if (pokemon.getMoveSet().getMoves().stream().anyMatch(move -> move.getName().equalsIgnoreCase(name))) {
            val.set(true);
        }
        return val.get();
    }

    public Egg generateEgg()
    {
        lastEggAttempt = System.currentTimeMillis();
        Pokemon femaleParent = null;
        Pokemon maleParent = null;

        Pokemon parentOne = getParentOne();
        Pokemon parentTwo = getParentTwo();

        if (parentOne.getGender().equals(Gender.MALE) && parentTwo.getGender().equals(Gender.MALE))
            return null;
        if (parentTwo.getGender().equals(Gender.FEMALE) && parentOne.getGender().equals(Gender.FEMALE))
            return null;

        switch (parentOne.getGender()) {
            case FEMALE -> femaleParent = parentOne;
            case MALE -> maleParent = parentOne;
        }
        switch (parentTwo.getGender()) {
            case MALE -> maleParent = getParentTwo();
            case FEMALE -> femaleParent = getParentTwo();
        }

        if (parentOne.isLegendary() || parentOne.isUltraBeast())
            return null;
        if (parentTwo.isLegendary() || parentTwo.isUltraBeast())
            return null;

        Species decidedSpecies = null;
        if (parentOne.getGender().equals(parentTwo.getGender()))
        {
            return null;
        }
        if (parentOne.getSpecies().equals(parentTwo.getSpecies()))
        {
            decidedSpecies = parentOne.getSpecies();
        } else if(parentOne.getSpecies().equals(Util.getSpeciesFromString("ditto")))
        {
            if (parentTwo.getSpecies().equals(parentOne.getSpecies()))
            {
                //check if ditto breeding is enabled
                decidedSpecies = RandomHelper.getRandomElementFromCollection(Util.pokemonList());
            } else
                decidedSpecies = parentTwo.getSpecies();
        } else if(parentTwo.getSpecies().equals(Util.getSpeciesFromString("ditto")))
        {
            if (parentOne.getSpecies().equals(parentTwo.getSpecies()))
            {
                //check if ditto breeding is enabled
                decidedSpecies = RandomHelper.getRandomElementFromCollection(Util.pokemonList());
            } else
                decidedSpecies = parentOne.getSpecies();
        } else {
              //check egg group compatibilities
            for (EggGroup eggGroup:parentOne.getSpecies().getEggGroups()) {
                if (parentTwo.getSpecies().getEggGroups().contains(eggGroup))
                {
                    //decide female parent
                    if (femaleParent == null || maleParent == null)
                        return null;
                    //set species to female parent
                    decidedSpecies = femaleParent.getSpecies();
                }
            }
        }
        if (decidedSpecies == null)
            return null;
        SpeciesConfig speciesConfig = CobbledDayCare.speciesConfig;
        if (speciesConfig.speciesData.get(maleParent.getSpecies().getName()) != null || speciesConfig.speciesData.get(femaleParent.getSpecies().getName()) != null) {
            BreedableSpecies father = speciesConfig.speciesData.get(maleParent.getSpecies().getName());
            BreedableSpecies mother = speciesConfig.speciesData.get(femaleParent.getSpecies().getName());
            int lvl = 1;
            //ivs data
            /**
             * No destiny knots yet, so ivs passing can't yet be implemented
             */
//        parentOne.heldItem().getItem().equals(CobblemonItems.DESTI)
            boolean shouldBeShiny = RandomHelper.getRandomChance(mother.shinyChance) || RandomHelper.getRandomChance(father.shinyChance);
            //ball;
            PokeBall pokeBall = femaleParent.getCaughtBall();
            //? no is hidden check, can't parse down HA checking

            //nature
            /**
             * Ever stones still need to be implemented, nature generation will be random
             */
            Nature nature = RandomHelper.getRandomElementFromCollection(Natures.INSTANCE.all());
            if (RandomHelper.getRandomChance(mother.natureChanceFemale)) {
                nature = femaleParent.getNature();
            } else if (RandomHelper.getRandomChance(father.natureChanceMale))
                nature = maleParent.getNature();

//        if (parentOne.heldItem().getItem().equals(CobblemonItems.EVER_STONE))

            //size?
            //form generation
            //

            Pokemon generated = decidedSpecies.create(lvl);
            if (nature != null) {
                generated.setNature(nature);
            }

            //moves
            Pokemon finalFemaleParent = femaleParent;
            Pokemon finalMaleParent = maleParent;
            decidedSpecies.getMoves().getEggMoves().forEach(moveTemplate -> {
                finalFemaleParent.getMoveSet().getMoves().forEach(move -> {
                    if (move.getName().equalsIgnoreCase(moveTemplate.create().getName()))
                    {
                        //do chance roll
                        if (RandomHelper.getRandomChance(mother.natureChanceFemale))
                        {
                            //add to moveset if not in moveset
                            if (hasMove(generated, move.getName()))
                                return;
                            if (generated.getMoveSet().hasSpace()) {
                                generated.getMoveSet().add(move);
                            }
                        }
                    }
                });

                finalMaleParent.getMoveSet().getMoves().forEach(move -> {
                    if (move.getName().equalsIgnoreCase(moveTemplate.create().getName()))
                    {
                        //do chance roll
                        if (RandomHelper.getRandomChance(father.natureChanceFemale))
                        {
                            //add to moveset if not in moveset
                            if (hasMove(generated, move.getName()))
                                return;
                            if (generated.getMoveSet().hasSpace()) {
                                generated.getMoveSet().add(move);
                            }
                        }
                    }
                });
            });
            generated.setShiny(shouldBeShiny);
            generated.setCaughtBall(pokeBall);
            return new Egg(generated);
        } else
            return null;
    }

    public boolean breedingOnCoolDown()
    {

        return false;
    }

    public void startBreeding()
    {
        this.lastStart = System.currentTimeMillis();
    }

    public Pokemon getParentOne()
    {
        if (pokemonOne == null)
            return null;
        return new Pokemon().loadFromJSON(pokemonOne);
    }

    public Pokemon getParentTwo()
    {
        if (pokemonTwo == null)
            return null;
        return new Pokemon().loadFromJSON(pokemonTwo);
    }

}

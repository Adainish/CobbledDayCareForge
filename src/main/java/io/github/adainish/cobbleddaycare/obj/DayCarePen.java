package io.github.adainish.cobbleddaycare.obj;

import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DayCarePen
{
    public String dayCareID;
    public JsonObject pokemonOne;
    public JsonObject pokemonTwo;
    public long lastStart = 0;

    public long lastEggAttempt = 0;

    public double unlockCost = 0;

    public String permissionID = "";

    public boolean unlocked = false;

    public boolean enabled = true;


    public DayCarePen()
    {

    }

    public int getOrder() {
        if (CobbledDayCare.dayCareStorage.dayCareManager.penDataMap.containsKey(dayCareID))
            return CobbledDayCare.dayCareStorage.dayCareManager.penDataMap.get(dayCareID).order;
        else return 0;
    }

    public String getPrettyDisplay()
    {
        String s = "";
        if (CobbledDayCare.dayCareStorage.dayCareManager.penDataMap.containsKey(dayCareID))
            s = CobbledDayCare.dayCareStorage.dayCareManager.penDataMap.get(dayCareID).prettyDisplay;
        return s;
    }

    public int getTimerFromSpecies(BreedableSpecies mother)
    {
        int timer = 1;
        if (mother != null) {
            timer = (int) mother.breedingTimeMinutes;
        } else {
            CobbledDayCare.getLog().error("Failed to verify mother status, unable to decide breed timer.");
        }
        return timer;
    }

    public BreedableSpecies decideFather(Pokemon parentOne, Pokemon parentTwo)
    {
        Pokemon femaleParent = null;
        Pokemon maleParent = null;

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

        if (parentOne.getGender().equals(Gender.GENDERLESS) && parentTwo.getGender().equals(Gender.GENDERLESS))
        {
            maleParent = parentOne;
            femaleParent = parentTwo;
        } else if (parentOne.getGender().equals(Gender.GENDERLESS))
        {
            if (femaleParent == null)
                femaleParent = parentOne;
            if (maleParent == null)
                maleParent = parentOne;
        } else if (parentTwo.getGender().equals(Gender.GENDERLESS))
        {
            if (femaleParent == null)
                femaleParent = parentTwo;
            if (maleParent == null)
                maleParent = parentTwo;
        }

        if (parentOne.isLegendary() || parentOne.isUltraBeast())
            return null;
        if (parentTwo.isLegendary() || parentTwo.isUltraBeast())
            return null;

        SpeciesConfig speciesConfig = CobbledDayCare.speciesConfig;
        if (speciesConfig.speciesData.get(maleParent.getSpecies().getName()) != null || speciesConfig.speciesData.get(femaleParent.getSpecies().getName()) != null) {
            return speciesConfig.speciesData.get(maleParent.getSpecies().getName());
        }
        return null;
    }

    public BreedableSpecies decideMother(Pokemon parentOne, Pokemon parentTwo)
    {
        Pokemon femaleParent = null;
        Pokemon maleParent = null;

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

        if (parentOne.getGender().equals(Gender.GENDERLESS) && parentTwo.getGender().equals(Gender.GENDERLESS))
        {
            maleParent = parentOne;
            femaleParent = parentTwo;
        } else if (parentOne.getGender().equals(Gender.GENDERLESS))
        {
            if (femaleParent == null)
                femaleParent = parentOne;
            if (maleParent == null)
                maleParent = parentOne;
        } else if (parentTwo.getGender().equals(Gender.GENDERLESS))
        {
            if (femaleParent == null)
                femaleParent = parentTwo;
            if (maleParent == null)
                maleParent = parentTwo;
        }

        if (parentOne.isLegendary() || parentOne.isUltraBeast())
            return null;
        if (parentTwo.isLegendary() || parentTwo.isUltraBeast())
            return null;

        SpeciesConfig speciesConfig = CobbledDayCare.speciesConfig;
        if (speciesConfig.speciesData.get(maleParent.getSpecies().getName()) != null || speciesConfig.speciesData.get(femaleParent.getSpecies().getName()) != null) {
            return speciesConfig.speciesData.get(femaleParent.getSpecies().getName());
        }
        return null;
    }

    public String unlockStatus()
    {
        if (unlocked)
            return "&aThis pen has been unlocked";
        return "&cYou're yet to unlock this pen...";
    }

    public boolean shouldGenerateEgg()
    {
        return System.currentTimeMillis() > (lastEggAttempt + TimeUnit.MINUTES.toMillis(getTimerFromSpecies(decideMother(getParentOne(), getParentTwo()))));
    }

    public void updateLockStatus(boolean unlocked, UUID uuid)
    {
        if (Util.isOnline(uuid))
        {
            String status = "&aunlocked";
            if (!unlocked)
                status = "&4locked";
            Util.send(uuid, "&7The daycare pen %pen% &7has been %status%"
                    .replace("%pen%", getPrettyDisplay())
                    .replace("%status%", status)
            );
        }
        this.unlocked = unlocked;
    }

    public boolean hasMove(Pokemon pokemon, String name)
    {
        AtomicBoolean val = new AtomicBoolean(false);
        if (pokemon.getMoveSet().getMoves().stream().anyMatch(move -> move.getName().equalsIgnoreCase(name))) {
            val.set(true);
        }
        return val.get();
    }

    public Species getEarliestSpecies(Species species)
    {
        if (species == null)
            return null;
        Species clonedDecision = species;
        if (species.create(1).getPreEvolution() != null)
        {
            clonedDecision = species.create(1).getPreEvolution().getSpecies();
            if (clonedDecision.create(1).getPreEvolution() != null)
                clonedDecision = clonedDecision.create(1).getPreEvolution().getSpecies();
        }
        return clonedDecision;
    }

    public Species getDecidedSpecies(Pokemon parentOne, Pokemon parentTwo, Pokemon femaleParent, Pokemon maleParent)
    {
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
            } else {
                if (parentTwo.getSpecies().create(1).getPreEvolution() != null) {
                    decidedSpecies = parentTwo.getSpecies().create(1).getPreEvolution().getSpecies();
                }
                else decidedSpecies = parentTwo.getSpecies();
            }
        } else if(parentTwo.getSpecies().equals(Util.getSpeciesFromString("ditto")))
        {
            if (parentOne.getSpecies().equals(parentTwo.getSpecies()))
            {
                //check if ditto breeding is enabled
                decidedSpecies = RandomHelper.getRandomElementFromCollection(Util.pokemonList());
            } else {
                if (parentOne.getSpecies().create(1).getPreEvolution() != null) {
                    decidedSpecies = parentOne.getSpecies().create(1).getPreEvolution().getSpecies();
                }
                else decidedSpecies = parentOne.getSpecies();
            }
        } else {
            //check egg group compatibilities
            for (EggGroup eggGroup:parentOne.getSpecies().getEggGroups()) {
                if (parentTwo.getSpecies().getEggGroups().contains(eggGroup)) {
                    //decide female parent
                    if (femaleParent == null || maleParent == null)
                        return null;
                    //set species to female parent
                    if (femaleParent.getPreEvolution() != null)
                        decidedSpecies = femaleParent.getSpecies().create(1).getPreEvolution().getSpecies();
                    else decidedSpecies = femaleParent.getSpecies();
                }
            }
        }
        return getEarliestSpecies(decidedSpecies);
    }

    public Pokemon decideParent(Pokemon parentOne, Pokemon parentTwo, boolean mother)
    {
        Pokemon femaleParent = null;
        Pokemon maleParent = null;
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

        if (parentOne.getGender().equals(Gender.GENDERLESS) && parentTwo.getGender().equals(Gender.GENDERLESS))
        {
            maleParent = parentOne;
            femaleParent = parentTwo;
        } else if (parentOne.getGender().equals(Gender.GENDERLESS))
        {
            if (femaleParent == null)
                femaleParent = parentOne;
            if (maleParent == null)
                maleParent = parentOne;
        } else if (parentTwo.getGender().equals(Gender.GENDERLESS))
        {
            if (femaleParent == null)
                femaleParent = parentTwo;
            if (maleParent == null)
                maleParent = parentTwo;
        }
        if (mother)
            return femaleParent;
        else return maleParent;
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



        femaleParent = decideParent(parentOne, parentTwo, true);
        maleParent = decideParent(parentOne, parentTwo, false);

        if (femaleParent == null)
            return null;
        if (maleParent == null)
            return null;

        Species decidedSpecies = getDecidedSpecies(parentOne, parentTwo, femaleParent, maleParent);

        if (decidedSpecies == null)
            return null;
        SpeciesConfig speciesConfig = CobbledDayCare.speciesConfig;
        if (speciesConfig.speciesData.get(maleParent.getSpecies().getName()) != null || speciesConfig.speciesData.get(femaleParent.getSpecies().getName()) != null) {
            BreedableSpecies father = speciesConfig.speciesData.get(maleParent.getSpecies().getName());
            BreedableSpecies mother = speciesConfig.speciesData.get(femaleParent.getSpecies().getName());
            if (father == null)
                return null;
            if (mother == null)
                return null;
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
            HashMap<String, Integer> breedableStats = generatedStats(finalMaleParent, finalFemaleParent, father, mother);
            if (!breedableStats.isEmpty())
            {
                Arrays.stream(Stats.values()).filter(stat -> !stat.equals(Stats.EVASION) && !stat.equals(Stats.ACCURACY)).filter(stat -> breedableStats.containsKey(stat.getIdentifier().toString())).forEach(stat -> generated.getIvs().set(stat, breedableStats.get(stat.getIdentifier().toString())));
            }
            generated.setShiny(shouldBeShiny);
            generated.setCaughtBall(pokeBall);
            Egg egg = new Egg(generated);
            egg.hatchTime = Math.toIntExact(mother.hatchTimeMinutes);
            return egg;
        } else
            return null;
    }

    public HashMap<String, Integer> generatedStats(Pokemon dad, Pokemon mom, BreedableSpecies father, BreedableSpecies mother)
    {
        HashMap<String, Integer> breedableStatHashMap = new HashMap<>();
        if (dad == null || mom == null || father == null || mother == null)
            return breedableStatHashMap;

        Arrays.stream(Stats.values()).filter(stat -> !stat.equals(Stats.EVASION) && !stat.equals(Stats.ACCURACY)).forEach(stat -> {
            if (RandomHelper.getRandomChance(father.breedableStats.get(stat.getIdentifier().toString()).chance)) {
                breedableStatHashMap.put(stat.getIdentifier().toString(), dad.getIvs().getOrDefault(stat));
            } else if (RandomHelper.getRandomChance(mother.breedableStats.get(stat.getIdentifier().toString()).chance)) {
                breedableStatHashMap.put(stat.getIdentifier().toString(), mom.getIvs().getOrDefault(stat));
            }
        });
        return breedableStatHashMap;
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

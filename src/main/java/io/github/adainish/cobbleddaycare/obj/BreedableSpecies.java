package io.github.adainish.cobbleddaycare.obj;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;

import java.util.HashMap;

public class BreedableSpecies
{
    public String species;
    public double shinyChance;

    public double natureChanceFemale;
    public double natureChanceMale;
    public double eggMoveChanceMale;
    public double eggMoveChanceFemale;
    public double haChanceFemale;

    public long hatchTimeMinutes;
    public long breedingTimeMinutes;

    public HashMap<String, BreedableStat> breedableStats;

    public BreedableSpecies(String species)
    {
        this.species = species;
        this.shinyChance = 5D;
        this.natureChanceFemale = 10D;
        this.natureChanceMale = 5D;
        this.eggMoveChanceMale = 15D;
        this.eggMoveChanceFemale = 10D;
        this.haChanceFemale = 5D;
        this.hatchTimeMinutes = 5;
        this.breedingTimeMinutes = 20;
        this.breedableStats = new HashMap<>();
        for (Stat st: Stats.values()) {
            if (st.equals(Stats.ACCURACY) || st.equals(Stats.EVASION))
                continue;
            BreedableStat breedableStat = new BreedableStat();
            breedableStat.statName = st.getIdentifier().toString();
            breedableStat.chance = 0.5;
            breedableStats.put(st.getIdentifier().toString(), breedableStat);
        }
    }

}

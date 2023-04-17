package io.github.adainish.cobbleddaycare.obj;

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
    }

}

package io.github.adainish.cobbleddaycare.obj;

public class ConfigurableDayCarePen
{
    public String dayCareID = "pen";
    public int coolDownMinutes = 5;
    public int eggCoolDown = 5;

    public double unlockCost = 100;

    public String permissionID = "";

    public boolean enabled = true;

    public ConfigurableDayCarePen(int i)
    {
        this.dayCareID = "pen_%i%".replace("%i%", String.valueOf(i));
    }
}

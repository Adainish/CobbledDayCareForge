package io.github.adainish.cobbleddaycare.obj;

public class ConfigurableDayCarePen
{
    public String dayCareID = "pen";

    public String prettyDisplay = "&cPen";

    public int order = 0;

    public double unlockCost = 100;

    public String permissionID = "";

    public boolean enabled = true;

    public ConfigurableDayCarePen(int i)
    {
        this.dayCareID = "pen_%i%".replace("%i%", String.valueOf(i));
        this.prettyDisplay = "&bPen %i%".replace("%i%", String.valueOf(i));
        this.order = i;
    }
}

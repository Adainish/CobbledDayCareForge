package io.github.adainish.cobbleddaycare.obj;

import java.util.HashMap;
import java.util.UUID;

public class Player
{
    public UUID uuid;
    public String username = "";

    public EggBox eggBox;

    public HashMap<String, DayCarePen> dayCarePens = new HashMap<>();


    public Player()
    {

    }

    public void generateEggs()
    {
        for (DayCarePen pen:dayCarePens.values()) {
            if (eggBox.isFull())
                break;
            if (pen.shouldGenerateEgg())
            {
                Egg egg = pen.generateEgg();
                eggBox.eggList.add(egg);
            }
        }
    }
}

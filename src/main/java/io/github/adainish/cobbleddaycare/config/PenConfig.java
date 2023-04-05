package io.github.adainish.cobbleddaycare.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobbleddaycare.CobbledDayCare;
import io.github.adainish.cobbleddaycare.obj.ConfigurableDayCarePen;
import io.github.adainish.cobbleddaycare.util.Adapters;

import java.io.*;
import java.util.HashMap;

public class PenConfig
{

    public HashMap<String, ConfigurableDayCarePen> configurablePens = new HashMap<>();

    public static void writeConfig()
    {
        File dir = CobbledDayCare.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        PenConfig config = new PenConfig();
        try {
            File file = new File(dir, "pens.json");
            if (file.exists())
                return;
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            String json = gson.toJson(config);
            writer.write(json);
            writer.close();
        } catch (IOException e)
        {
            CobbledDayCare.getLog().warn(e);
        }
    }

    public static PenConfig getConfig()
    {
        File dir = CobbledDayCare.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        File file = new File(dir, "pens.json");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            CobbledDayCare.getLog().error("Something went wrong attempting to read the Config");
            return null;
        }

        return gson.fromJson(reader, PenConfig.class);
    }
}

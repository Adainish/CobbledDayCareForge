package io.github.adainish.cobbleddaycare.storage;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobbleddaycare.CobbledDayCare;
import io.github.adainish.cobbleddaycare.obj.DayCareManager;
import io.github.adainish.cobbleddaycare.util.Adapters;

import java.io.*;

public class DayCareStorage
{
    public DayCareManager dayCareManager;

    public DayCareStorage()
    {
        this.dayCareManager = new DayCareManager();
    }
    public static void writeStorage()
    {
        File dir = CobbledDayCare.getStorage();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;

        try {
            File file = new File(dir, "storage.json");
            if (file.exists())
                return;
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            DayCareStorage storage = new DayCareStorage();
            String json = gson.toJson(storage);
            writer.write(json);
            writer.close();
        } catch (IOException e)
        {
            CobbledDayCare.getLog().warn(e);
        }
    }

    public static DayCareStorage getStorage()
    {
        File dir = CobbledDayCare.getStorage();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        File file = new File(dir, "storage.json");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            CobbledDayCare.getLog().error("Something went wrong attempting to read the daycare storage file");
            return null;
        }

        return gson.fromJson(reader, DayCareStorage.class);
    }

    public void save()
    {
        File dir = CobbledDayCare.getStorage();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        File file = new File(dir, "storage.json");
        if (file.exists()) {
            JsonReader reader = null;
            try {
                reader = new JsonReader(new FileReader(file));
            } catch (FileNotFoundException e) {

            }

            if (reader == null)
            {
                CobbledDayCare.getLog().warn("Failed to save the daycare data, something went wrong please contact the dev!");
                return;
            }
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(gson.toJson(this));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

package io.github.adainish.cobbleddaycare.config;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.cobbleddaycare.CobbledDayCare;
import io.github.adainish.cobbleddaycare.obj.BreedableSpecies;
import io.github.adainish.cobbleddaycare.util.Adapters;

import java.io.*;
import java.util.HashMap;

public class SpeciesConfig
{
    public HashMap<String, BreedableSpecies> speciesData = new HashMap<>();

    public SpeciesConfig()
    {
            initDefault();
    }

    public void initDefault()
    {
        for (Species species: PokemonSpecies.INSTANCE.getImplemented()) {
            BreedableSpecies breedableSpecies = new BreedableSpecies(species.getName());
            speciesData.put(species.getName(), breedableSpecies);
        }
    }

    public static void writeConfig()
    {
        File dir = CobbledDayCare.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        SpeciesConfig config = new SpeciesConfig();
        try {
            File file = new File(dir, "species.json");
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

    public static SpeciesConfig getConfig()
    {
        File dir = CobbledDayCare.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        File file = new File(dir, "species.json");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            CobbledDayCare.getLog().error("Something went wrong attempting to read the Config");
            return null;
        }

        return gson.fromJson(reader, SpeciesConfig.class);
    }
}

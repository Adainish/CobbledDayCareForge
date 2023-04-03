package io.github.adainish.cobbleddaycare.util;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.adapters.PokemonPropertiesAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

public class Adapters
{
    public static Gson PRETTY_MAIN_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()

            .registerTypeAdapter(PokemonProperties.class, new PokemonPropertiesAdapter(true))
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();
}

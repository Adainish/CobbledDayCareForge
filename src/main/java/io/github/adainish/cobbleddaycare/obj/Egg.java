package io.github.adainish.cobbleddaycare.obj;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.PokemonStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonObject;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Egg
{
    public long hatchStartedAt;
    public JsonObject generatedPokemon;
    public int hatchTime = 5;

    public Egg(Pokemon generated)
    {
        this.generatedPokemon = generated.saveToJSON(new JsonObject());
        this.hatchStartedAt = System.currentTimeMillis();
    }

    public boolean hasHatched()
    {
        return System.currentTimeMillis() >= (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(hatchTime));
    }

    public void send(UUID uuid)
    {
        if (generatedPokemon == null)
            return;

        try {
            PokemonStore pokemonStore = Cobblemon.INSTANCE.getStorage().getParty(uuid);
            Pokemon p = new Pokemon().loadFromJSON(generatedPokemon);
            pokemonStore.add(p);
        } catch (NoPokemonStoreException e) {
            throw new RuntimeException(e);
        }
    }
}

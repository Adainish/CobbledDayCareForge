package io.github.adainish.cobbleddaycare.obj;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.PokemonStore;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.JsonObject;
import io.github.adainish.cobbleddaycare.util.Util;

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

    public String hatchedStatus()
    {
        if (hasHatched())
            return "&aThis egg has hatched!";
        return "&cThis egg hasn't hatched yet...";
    }

    public boolean hasHatched()
    {
        return System.currentTimeMillis() >= (hatchStartedAt + TimeUnit.MINUTES.toMillis(hatchTime));
    }

    public Pokemon getGeneratedPokemon()
    {
        return new Pokemon().loadFromJSON(generatedPokemon);
    }

    public void send(PlayerPartyStore store, UUID uuid)
    {
        if (generatedPokemon == null)
            return;
        Util.send(uuid, "&aOne of your Day Care eggs has hatched and the Pokemon was sent over!");
        Pokemon p = getGeneratedPokemon();
        store.add(p);
    }
}

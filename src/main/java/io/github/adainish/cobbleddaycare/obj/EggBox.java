package io.github.adainish.cobbleddaycare.obj;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EggBox
{
    public int maxEggs = 5;
    public List<Egg> eggList = new ArrayList<>();

    public EggBox()
    {

    }

    public boolean isFull()
    {
        return eggList.size() >= maxEggs;
    }

    public void removeAndSendHatchedEggs(UUID uuid)
    {
        PlayerPartyStore store = null;
        try {
            store = Cobblemon.INSTANCE.getStorage().getParty(uuid);
        } catch (NoPokemonStoreException e) {
            return;
        }
        List<Egg> toRemove = new ArrayList<>();
        if (store != null) {
            for (Egg egg : eggList) {
                if (egg.hasHatched()) {
                    toRemove.add(egg);
                    store.add(new Pokemon().loadFromJSON(egg.generatedPokemon));
                }
            }
        }
        if (!toRemove.isEmpty()) {
            eggList.removeAll(toRemove);
        }
    }
}

package io.github.adainish.cobbleddaycare.property;

import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.FlagProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BreedCapabilityProperty implements CustomPokemonPropertyType<FlagProperty> {

    /**
     * @Author Winglet
     * @Description Asserts and verifies the validity of a pokemons breeding status
     * @Since 01/05/2023
     */

    public Set<String> keys = Set.of("unbreedable");

    @NotNull
    @Override
    public Iterable<String> getKeys() {
        return keys;
    }

    @Override
    public boolean getNeedsKey() {
        return true;
    }

    @NotNull
    @Override
    public Collection<String> examples() {
        return new ArrayList<>(Arrays.asList("yes", "no"));
    }

    public FlagProperty breedAble() {
        return new FlagProperty(keys.stream().findFirst().get(), true);
    }

    public FlagProperty unBreedAble() {
        return new FlagProperty(keys.stream().findFirst().get(), false);
    }

    public boolean isBreedAble(Pokemon pokemon) {
        AtomicBoolean toReturn = new AtomicBoolean(true);
        if (pokemon.getCustomProperties().isEmpty())
            return toReturn.get();
        pokemon.getCustomProperties().forEach(customPokemonProperty -> {
            if (customPokemonProperty instanceof FlagProperty) {
                if (keys.contains(((FlagProperty) customPokemonProperty).getKey())) {
                    toReturn.set(false);
                }
            }
        });
        return toReturn.get();
    }

    @Nullable
    @Override
    public FlagProperty fromString(@Nullable String s) {
        FlagProperty flagProperty = null;
        if (s == null || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("true")) {
            flagProperty = unBreedAble();
            return flagProperty;
        }
        if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("no"))
            flagProperty = breedAble();

        return flagProperty;
    }
}

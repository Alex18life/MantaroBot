/*
 * Copyright (C) 2016-2020 David Rubio Escares / Kodehawa
 *  
 *  Mantaro is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  Mantaro is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Mantaro. If not, see http://www.gnu.org/licenses/
 */

package net.kodehawa.mantarobot.commands.currency.item.special;

import net.kodehawa.mantarobot.commands.currency.item.Item;
import net.kodehawa.mantarobot.commands.currency.item.ItemType;
import net.kodehawa.mantarobot.commands.currency.item.special.helpers.Breakable;
import net.kodehawa.mantarobot.commands.currency.item.special.helpers.Castable;
import net.kodehawa.mantarobot.commands.currency.item.special.helpers.Salvageable;

import java.util.List;

public class FishRod extends Item implements Castable, Breakable, Salvageable {
    private int level;
    private int castLevelRequired;
    private int maximumCastAmount;
    private int maxDurability;
    private List<Integer> salvageReturns;

    public FishRod(ItemType type, int level, int castLevelRequired, int maximumCastAmount, String emoji, String name,
                   String translatedName, String desc, long value, String recipe, List<Integer> salvageReturns,
                   int maxDurability, int... recipeTypes) {
        super(type, emoji, name, translatedName, desc, value, true, false, recipe, recipeTypes);
        this.level = level;
        this.castLevelRequired = castLevelRequired;
        this.maximumCastAmount = maximumCastAmount;
        this.maxDurability = maxDurability;
        this.salvageReturns = salvageReturns;
    }

    public FishRod(ItemType type, int level, int castLevelRequired, int maximumCastAmount, String emoji, String name,
                   String alias, String translatedName, String desc, long value, String recipe, List<Integer> salvageReturns,
                   int maxDurability, int... recipeTypes) {
        super(type, emoji, name, alias, translatedName, desc, value, true, false, recipe, recipeTypes);
        this.level = level;
        this.castLevelRequired = castLevelRequired;
        this.maximumCastAmount = maximumCastAmount;
        this.maxDurability = maxDurability;
        this.salvageReturns = salvageReturns;
    }

    public FishRod(ItemType type, int level, int castLevelRequired, int maximumCastAmount, String emoji, String name,
                   String translatedName, String desc, long value, boolean buyable, String recipe, List<Integer> salvageReturns,
                   int maxDurability, int... recipeTypes) {
        super(type, emoji, name, translatedName, desc, value, true, buyable, recipe, recipeTypes);
        this.level = level;
        this.castLevelRequired = castLevelRequired;
        this.maximumCastAmount = maximumCastAmount;
        this.maxDurability = maxDurability;
        this.salvageReturns = salvageReturns;
    }

    public FishRod(ItemType type, int level, int castLevelRequired, int maximumCastAmount, String emoji, String name,
                   String alias, String translatedName, String desc, long value, boolean buyable, String recipe, List<Integer> salvageReturns,
                   int maxDurability, int... recipeTypes) {
        super(type, emoji, name, alias, translatedName, desc, value, true, buyable, recipe, recipeTypes);
        this.level = level;
        this.castLevelRequired = castLevelRequired;
        this.maximumCastAmount = maximumCastAmount;
        this.maxDurability = maxDurability;
        this.salvageReturns = salvageReturns;
    }

    @Override
    public int getMaxDurability() {
        return maxDurability;
    }

    public int getLevel() {
        return this.level;
    }

    public int getCastLevelRequired() {
        return this.castLevelRequired;
    }

    public int getMaximumCastAmount() {
        return this.maximumCastAmount;
    }

    @Override
    public List<Integer> getReturns() {
        return salvageReturns;
    }
}

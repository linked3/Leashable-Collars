package com.dipilodopilasaurus.leashablecollars;

import net.minecraft.server.level.ServerLevel;
//? if >=1.21.11 {
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
//? if fabric {
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
//?} else {
/*import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
*///?}
//?} else {
/*import net.minecraft.world.level.GameRules;
*///?}
//? if <1.21.11 && fabric {
/*import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
*///?}

/**
 * A boolean game rule, and the seam hiding how one is declared. 1.21.11 made them registry objects
 * with no alias, so a world upgraded past it reverts every rule here to its default.
 */
public final class BoolGameRule {
    //? if >=1.21.11 {
    private final GameRule<Boolean> rule;

    private BoolGameRule(GameRule<Boolean> rule) {
        this.rule = rule;
    }

    /** @param legacyName the pre-1.21.11 spelling; unreachable from here, see the class note. */
    @SuppressWarnings("java:S1172")
    public static BoolGameRule register(String path, String legacyName, boolean defaultValue) {
        //? if fabric {
        return new BoolGameRule(GameRuleBuilder.forBoolean(defaultValue)
                .category(GameRuleCategory.PLAYER)
                .buildAndRegister(Ids.of(path)));
        //?} else {
        /*GameRule<Boolean> rule = new GameRule<>(
                GameRuleCategory.PLAYER,
                GameRuleType.BOOL,
                BoolArgumentType.bool(),
                GameRuleTypeVisitor::visitBoolean,
                Codec.BOOL,
                value -> value ? 1 : 0,
                defaultValue,
                FeatureFlagSet.of());
        return new BoolGameRule(Registration.register(BuiltInRegistries.GAME_RULE, path, rule));
        *///?}
    }

    public boolean get(ServerLevel level) {
        return level.getGameRules().get(rule);
    }
    //?} else {
    /*private final GameRules.Key<GameRules.BooleanValue> rule;

    private BoolGameRule(GameRules.Key<GameRules.BooleanValue> rule) {
        this.rule = rule;
    }

    public boolean get(ServerLevel level) {
        return level.getGameRules().getBoolean(rule);
    }
    *///?}

    // A guard can't nest in an inactive branch, so the two loaders' pre-1.21.11 registration are siblings.
    //? if <1.21.11 && fabric {
    /*public static BoolGameRule register(String path, String legacyName, boolean defaultValue) {
        return new BoolGameRule(GameRuleRegistry.register(
                legacyName, GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(defaultValue)));
    }
    *///?}

    //? if <1.21.11 && !fabric {
    /*public static BoolGameRule register(String path, String legacyName, boolean defaultValue) {
        return new BoolGameRule(GameRules.register(
                legacyName, GameRules.Category.PLAYER, GameRules.BooleanValue.create(defaultValue)));
    }
    *///?}
}

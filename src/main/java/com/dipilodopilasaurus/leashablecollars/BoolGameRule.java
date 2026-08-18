package com.dipilodopilasaurus.leashablecollars;

import net.minecraft.server.level.ServerLevel;
//? if >=1.21.9 {
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

/**
 * A boolean game rule, and the seam hiding how one is declared. 1.21.9 made them registry objects under a
 * namespaced id; declarations carry the old bare name too, since that is what existing worlds already
 * wrote into {@code level.dat}.
 */
public final class BoolGameRule {
    //? if >=1.21.9 {
    private final GameRule<Boolean> rule;

    private BoolGameRule(GameRule<Boolean> rule) {
        this.rule = rule;
    }

    /** @param legacyName unused here; kept so both spellings stay together at the call site. */
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

    public static BoolGameRule register(String path, String legacyName, boolean defaultValue) {
        return new BoolGameRule(GameRules.register(
                legacyName, GameRules.Category.PLAYER, GameRules.BooleanValue.create(defaultValue)));
    }

    public boolean get(ServerLevel level) {
        return level.getGameRules().getBoolean(rule);
    }
    *///?}
}

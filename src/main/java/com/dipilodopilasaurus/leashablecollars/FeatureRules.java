package com.dipilodopilasaurus.leashablecollars;

import com.dipilodopilasaurus.leashablecollars.network.Net;
import com.dipilodopilasaurus.leashablecollars.network.PacketGameRules;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public enum FeatureRules {
    SIGN_NEW_DEEDS("sign_new_deeds", "signNewDeeds", true),
    CAN_LEASH_PLAYERS("can_leash_players", "canLeashPlayers", true),
    LEASHES_PERSIST_ON_LOGOUT("leashes_persist_on_logout", "leashesPersistOnLogout", true),
    CAN_ACCESS_OWNED_INVENTORY("can_access_owned_inventory", "canAccessOwnedInventory", true),
    CAN_USE_PAWS("can_use_paws", "canUsePaws", true),
    CAN_USE_FOOT_PAWS("can_use_foot_paws", "canUseFootPaws", false),
    CAN_USE_INVISIBLE_FENCES("can_use_invisible_fences", "canUseInvisibleFences", true),
    CAN_USE_CLICKERS("can_use_clickers", "canUseClickers", true),
    CAN_USE_DOG_BEDS("can_use_dog_beds", "canUseDogBeds", true),
    CAN_USE_DOG_BOWLS("can_use_dog_bowls", "canUseDogBowls", true);

    private static final FeatureRules[] RULES = values();
    private static final int DEFAULT_MASK = ((1 << RULES.length) - 1) & ~(1 << CAN_USE_FOOT_PAWS.ordinal());
    private static final Map<Level, Integer> CLIENT_MASKS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<ServerPlayer, Integer> SENT_MASKS = new WeakHashMap<>();
    private final BoolGameRule rule;

    FeatureRules(String path, String legacyName, boolean defaultValue) {
        rule = BoolGameRule.register(path, legacyName, defaultValue);
    }

    public boolean enabled(Level level) {
        if (level instanceof ServerLevel serverLevel) return rule.get(serverLevel);
        return (CLIENT_MASKS.getOrDefault(level, DEFAULT_MASK) & (1 << ordinal())) != 0;
    }

    public static void receive(Level level, int mask) {
        if (level != null) CLIENT_MASKS.put(level, mask & DEFAULT_MASK);
    }

    public static void initialize() {
        Net.registerClientbound(PacketGameRules.ID, PacketGameRules.CODEC);
        Events.onServerTickEnd(FeatureRules::sync);
    }

    private static void sync(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            int mask = 0;
            for (FeatureRules feature : RULES) {
                if (feature.enabled(Compat.level(player))) mask |= 1 << feature.ordinal();
            }
            Integer previous = SENT_MASKS.put(player, mask);
            if (previous == null || previous != mask || server.getTickCount() % 20 == 0) {
                Net.sendToClient(player, new PacketGameRules(mask));
            }
        }
    }
}

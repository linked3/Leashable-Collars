package com.dipilodopilasaurus.leashablecollars;

import java.util.Optional;
import java.util.UUID;

public record OwnerComponent(UUID uuid, String name, Optional<UUID> owned, Optional<String> ownedName,
                             boolean canLeashForcibly) {
    public OwnerComponent(UUID uuid, String name) {
        this(uuid, name, Optional.empty(), Optional.empty(), false);
    }

    public OwnerComponent(UUID uuid, String name, Optional<UUID> owned, Optional<String> ownedName) {
        this(uuid, name, owned, ownedName, false);
    }
}

package com.dipilodopilasaurus.leashablecollars.network;

//? if >=1.20.5 {
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
//?} else {
/*import com.dipilodopilasaurus.leashablecollars.network.compat.ByteBufCodecs;
import com.dipilodopilasaurus.leashablecollars.network.compat.RegistryFriendlyByteBuf;
import com.dipilodopilasaurus.leashablecollars.network.compat.StreamCodec;
*///?}

import java.util.List;

/** Vanilla grew collection stream codecs in 1.20.5; the shim below it has no {@code apply}. */
public final class NetCodecs {
    /** A paws allow-list can hold one id per registry entry, so the cap is generous rather than tight. */
    private static final int MAX_IDS = 65536;

    private NetCodecs() {
    }

    // Wildcarded because the eras hand back different buffer types and composite() wants a supertype.
    public static final StreamCodec<? super RegistryFriendlyByteBuf, List<String>> STRING_LIST =
            //? if >=1.20.5 {
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list(MAX_IDS));
            //?} else {
            /*ByteBufCodecs.list(ByteBufCodecs.STRING_UTF8, MAX_IDS);
            *///?}
}

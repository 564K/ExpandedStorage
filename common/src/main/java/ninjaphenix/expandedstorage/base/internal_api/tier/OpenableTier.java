package ninjaphenix.expandedstorage.base.internal_api.tier;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
@Internal
public class OpenableTier extends TargetedTier {
    private final int SLOTS;

    public OpenableTier(final Tier PARENT, final ResourceLocation BLOCK_TYPE, final int SLOTS) {
        super(PARENT.key(), PARENT.blockProperties(), PARENT.itemProperties(), BLOCK_TYPE, PARENT.miningLevel());
        this.SLOTS = SLOTS;
    }

    public final int slots() {
        return SLOTS;
    }
}

package ninjaphenix.expandedstorage.base.internal_api.tier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractStorageBlock;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.function.UnaryOperator;

@Experimental
@Internal
public class TargetedTier extends Tier {
    private final ResourceLocation BLOCK_TYPE;

    protected TargetedTier(final ResourceLocation KEY,
                           final UnaryOperator<BlockBehaviour.Properties> BLOCK_PROPERTIES,
                           final UnaryOperator<Item.Properties> ITEM_PROPERTIES,
                           final ResourceLocation BLOCK_TYPE,
                           final int MINING_LEVEL) {
        super(KEY, MINING_LEVEL, BLOCK_PROPERTIES, ITEM_PROPERTIES);
        this.BLOCK_TYPE = BLOCK_TYPE;
    }

    public final boolean appliesTo(final Block BLOCK) {
        return BLOCK instanceof AbstractStorageBlock && ((AbstractStorageBlock) BLOCK).blockType() == BLOCK_TYPE;
    }

    public final ResourceLocation blockType() {
        return BLOCK_TYPE;
    }
}

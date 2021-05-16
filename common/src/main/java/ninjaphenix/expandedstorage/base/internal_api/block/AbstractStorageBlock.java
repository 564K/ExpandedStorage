package ninjaphenix.expandedstorage.base.internal_api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractStorageBlockEntity;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Experimental
@Internal
public abstract class AbstractStorageBlock extends Block {
    private final ResourceLocation BLOCK_ID;
    private final ResourceLocation BLOCK_TIER;

    public AbstractStorageBlock(final Properties properties, final ResourceLocation BLOCK_ID, final ResourceLocation BLOCK_TIER) {
        super(properties);
        this.BLOCK_ID = BLOCK_ID;
        this.BLOCK_TIER = BLOCK_TIER;
    }

    public abstract ResourceLocation blockType();

    public final ResourceLocation blockId() {
        return BLOCK_ID;
    }

    public final ResourceLocation blockTier() {
        return BLOCK_TIER;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        final BlockEntity TEMP = level.getBlockEntity(pos);
        if (TEMP instanceof AbstractStorageBlockEntity && stack.hasCustomHoverName()) {
            ((AbstractOpenableStorageBlockEntity) TEMP).setCustomName(stack.getHoverName());
        }
    }
}

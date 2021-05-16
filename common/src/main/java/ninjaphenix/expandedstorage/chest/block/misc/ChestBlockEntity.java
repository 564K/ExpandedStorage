package ninjaphenix.expandedstorage.chest.block.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.internal_api.inventory.CompoundWorldlyContainer;
import ninjaphenix.expandedstorage.chest.block.ChestBlock;

public final class ChestBlockEntity extends AbstractOpenableStorageBlockEntity implements TickableBlockEntity {
    private int viewerCount;
    private float lastAnimationAngle;
    private float animationAngle;
    private int ticksOpen;

    public ChestBlockEntity(final BlockEntityType<ChestBlockEntity> BLOCK_ENTITY_TYPE, final ResourceLocation BLOCK_ID) {
        super(BLOCK_ENTITY_TYPE, BLOCK_ID);
    }

    public static int countViewers(final Level world, final WorldlyContainer instance, final int x, final int y, final int z) {
        return world.getEntitiesOfClass(Player.class, new AABB(x - 5, y - 5, z - 5, x + 6, y + 6, z + 6)).stream()
                    .filter(player -> player.containerMenu instanceof AbstractContainerMenu_)
                    .map(player -> ((AbstractContainerMenu_<?>) player.containerMenu).getContainer())
                    .filter(inventory -> inventory == instance ||
                            inventory instanceof CompoundWorldlyContainer && ((CompoundWorldlyContainer) inventory).consistsPartlyOf(instance))
                    .mapToInt(inv -> 1).sum();
    }

    private static int tickViewerCount(Level level, ChestBlockEntity entity, int ticksOpen, int x, int y, int z, int viewCount) {
        if (!level.isClientSide() && viewCount != 0 && (ticksOpen + x + y + z) % 200 == 0) {
            return countViewers(level, entity, x, y, z);
        }
        return viewCount;
    }

    @Override
    public boolean triggerEvent(final int ACTION_ID, final int VALUE) {
        if (ACTION_ID == 1) {
            viewerCount = VALUE;
            return true;
        }
        return super.triggerEvent(ACTION_ID, VALUE);
    }

    // Client only
    public float getLidOpenness(final float f) {
        return Mth.lerp(f, lastAnimationAngle, animationAngle);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void tick() {
        viewerCount = tickViewerCount(level, this, ++ticksOpen, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), viewerCount);
        lastAnimationAngle = animationAngle;
        if (viewerCount > 0 && animationAngle == 0.0F) {
            playSound(SoundEvents.CHEST_OPEN);
        }
        if (viewerCount == 0 && animationAngle > 0.0F || viewerCount > 0 && animationAngle < 1.0F) {
            animationAngle = Mth.clamp(animationAngle + (viewerCount > 0 ? 0.1F : -0.1F), 0, 1);
            if (animationAngle < 0.5F && lastAnimationAngle >= 0.5F) {
                playSound(SoundEvents.CHEST_CLOSE);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void playSound(final SoundEvent soundEvent) {
        final BlockState state = getBlockState();
        final DoubleBlockCombiner.BlockType mergeType = ChestBlock.getBlockType(state);
        final Vec3 soundPos;
        if (mergeType == DoubleBlockCombiner.BlockType.SINGLE) {
            soundPos = Vec3.atCenterOf(worldPosition);
        } else if (mergeType == DoubleBlockCombiner.BlockType.FIRST) {
            soundPos = Vec3.atCenterOf(worldPosition).add(Vec3.atLowerCornerOf(ChestBlock.getDirectionToAttached(state).getNormal()).scale(0.5D));
        } else {
            return;
        }
        level.playSound(null, soundPos.x(), soundPos.y(), soundPos.z(), soundEvent, SoundSource.BLOCKS, 0.5F,
                        level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void startOpen(final Player player) {
        if (player.isSpectator()) {
            return;
        }
        if (viewerCount < 0) {
            viewerCount = 0;
        }
        viewerCount++;
        onInvOpenOrClose();
    }

    @Override
    public void stopOpen(final Player player) {
        if (player.isSpectator()) {
            return;
        }
        viewerCount--;
        onInvOpenOrClose();
    }

    @SuppressWarnings("ConstantConditions")
    private void onInvOpenOrClose() {
        final Block TEMP = getBlockState().getBlock();
        if (TEMP instanceof ChestBlock) {
            ChestBlock BLOCK = (ChestBlock) TEMP;
            level.blockEvent(worldPosition, BLOCK, 1, viewerCount);
            level.updateNeighborsAt(worldPosition, BLOCK);
        }
    }
}

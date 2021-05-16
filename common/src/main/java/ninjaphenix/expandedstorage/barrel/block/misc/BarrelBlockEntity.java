package ninjaphenix.expandedstorage.barrel.block.misc;

import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.barrel.block.BarrelBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;

public class BarrelBlockEntity extends AbstractOpenableStorageBlockEntity {
    private int viewerCount;

    public BarrelBlockEntity(final BlockEntityType<BarrelBlockEntity> BLOCK_ENTITY_TYPE, final ResourceLocation BLOCK_ID) {
        super(BLOCK_ENTITY_TYPE, BLOCK_ID);
    }

    public void checkViewerCount() {
        //noinspection ConstantConditions
        viewerCount = AbstractOpenableStorageBlockEntity.countViewers(level, this, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        if (viewerCount > 0) {
            this.scheduleViewCountCheck();
        } else {
            final BlockState STATE = getBlockState();
            if (!(STATE.getBlock() instanceof BarrelBlock)) {
                this.setRemoved();
                return;
            }
            if (STATE.getValue(BlockStateProperties.OPEN)) {
                this.playSound(STATE, SoundEvents.BARREL_CLOSE);
                this.setOpen(STATE, false);
            }
        }
    }

    @Override
    public void startOpen(final Player PLAYER) {
        if (!PLAYER.isSpectator()) {
            if (viewerCount < 0) {
                viewerCount = 0;
            }
            ++viewerCount;
            final BlockState STATE = getBlockState();
            if (!STATE.getValue(BlockStateProperties.OPEN)) {
                this.playSound(STATE, SoundEvents.BARREL_OPEN);
                this.setOpen(STATE, true);
            }
            this.scheduleViewCountCheck();
        }
    }

    @Override
    public void stopOpen(final Player PLAYER) {
        if (!PLAYER.isSpectator()) {
            --viewerCount;
        }
    }

    private void setOpen(final BlockState STATE, final boolean OPEN) {
        //noinspection ConstantConditions
        level.setBlock(getBlockPos(), STATE.setValue(BlockStateProperties.OPEN, OPEN), 3);
    }

    private void playSound(final BlockState STATE, final SoundEvent SOUND) {
        final Vec3i FACING_VECTOR = STATE.getValue(BlockStateProperties.FACING).getNormal();
        final double X = worldPosition.getX() + 0.5D + FACING_VECTOR.getX() / 2.0D;
        final double Y = worldPosition.getY() + 0.5D + FACING_VECTOR.getY() / 2.0D;
        final double Z = worldPosition.getZ() + 0.5D + FACING_VECTOR.getZ() / 2.0D;
        //noinspection ConstantConditions
        level.playSound(null, X, Y, Z, SOUND, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    private void scheduleViewCountCheck() {
        //noinspection ConstantConditions
        level.getBlockTicks().scheduleTick(getBlockPos(), getBlockState().getBlock(), 5);
    }
}

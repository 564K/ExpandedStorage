package ninjaphenix.expandedstorage.base.internal_api.block.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Experimental
@Internal
public abstract class AbstractStorageBlockEntity extends BlockEntity implements Nameable {
    private LockCode lockKey;
    private Component customName;

    public AbstractStorageBlockEntity(final BlockEntityType<?> BLOCK_ENTITY_TYPE) {
        super(BLOCK_ENTITY_TYPE);
        lockKey = LockCode.NO_LOCK;
    }

    public static void alertBlockLocked(final Player PLAYER, final Component DISPLAY_NAME) {
        PLAYER.displayClientMessage(new TranslatableComponent("container.isLocked", DISPLAY_NAME), true);
        PLAYER.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void load(final BlockState STATE, final CompoundTag TAG) {
        super.load(STATE, TAG);
        lockKey = LockCode.fromTag(TAG);
        if (TAG.contains("CustomName", Utils.NBT_STRING_TYPE)) {
            customName = Component.Serializer.fromJson(TAG.getString("CustomName"));
        }
    }

    @Override
    public CompoundTag save(final CompoundTag TAG) {
        super.save(TAG);
        this.lockKey.addToTag(TAG);
        if (customName != null) {
            TAG.putString("CustomName", Component.Serializer.toJson(customName));
        }
        return TAG;
    }

    public boolean canPlayerInteractWith(final Player PLAYER) {
        return !PLAYER.isSpectator() && lockKey.unlocksWith(PLAYER.getMainHandItem());
    }

    // <editor-fold desc="// Display names">
    @Override
    public final Component getName() {
        return this.hasCustomName() ? customName : this.getDefaultName();
    }

    public abstract Component getDefaultName();

    @Override
    public final boolean hasCustomName() {
        return customName != null;
    }

    @Nullable
    @Override
    public final Component getCustomName() {
        return customName;
    }

    public final void setCustomName(final Component NAME) {
        customName = NAME;
    }
    // </editor-fold>
}

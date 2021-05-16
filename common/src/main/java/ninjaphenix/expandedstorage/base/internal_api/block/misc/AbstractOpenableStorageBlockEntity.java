package ninjaphenix.expandedstorage.base.internal_api.block.misc;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractOpenableStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.internal_api.inventory.CompoundWorldlyContainer;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

@Experimental
@Internal
public abstract class AbstractOpenableStorageBlockEntity extends AbstractStorageBlockEntity implements WorldlyContainer {
    private final ResourceLocation BLOCK_ID;
    protected Component containerName;
    private int slots;
    private NonNullList<ItemStack> inventory;
    private int[] slotsForFace;

    public AbstractOpenableStorageBlockEntity(final BlockEntityType<?> BLOCK_ENTITY_TYPE, final ResourceLocation BLOCK_ID) {
        super(BLOCK_ENTITY_TYPE);
        this.BLOCK_ID = BLOCK_ID;
        if (BLOCK_ID != null) {
            this.initialise(BLOCK_ID);
        }
    }

    protected static int countViewers(final Level LEVEL, final WorldlyContainer CONTAINER, final int X, final int Y, final int Z) {
        return LEVEL.getEntitiesOfClass(Player.class, new AABB(X - 5, Y - 5, Z - 5, X + 6, Y + 6, Z + 6)).stream()
                .filter(PLAYER -> PLAYER.containerMenu instanceof AbstractContainerMenu_<?>)
                .map(PLAYER -> ((AbstractContainerMenu_<?>) PLAYER.containerMenu).getContainer())
                .filter(OPEN_CONTAINER -> OPEN_CONTAINER == CONTAINER || OPEN_CONTAINER instanceof CompoundWorldlyContainer && ((CompoundWorldlyContainer) OPEN_CONTAINER).consistsPartlyOf(CONTAINER))
                .mapToInt(inv -> 1).sum();
    }

    private void initialise(final ResourceLocation BLOCK_ID) {
        Block BLOCK = Registry.BLOCK.get(BLOCK_ID);
        if (BLOCK instanceof AbstractOpenableStorageBlock) {
            AbstractOpenableStorageBlock STORAGE_BLOCK = (AbstractOpenableStorageBlock) BLOCK;
            slots = STORAGE_BLOCK.getSlotCount();
            slotsForFace = new int[slots];
            Arrays.setAll(slotsForFace, IntUnaryOperator.identity());
            inventory = NonNullList.withSize(slots, ItemStack.EMPTY);
            containerName = STORAGE_BLOCK.getContainerName();
        }
    }

    @Override
    public Component getDefaultName() {
        return containerName;
    }

    public final ResourceLocation getBlockId() {
        return BLOCK_ID;
    }

    @Override
    public void load(final BlockState STATE, final CompoundTag TAG) {
        super.load(STATE, TAG);
        final Block TEMP = STATE.getBlock();
        if (TEMP instanceof AbstractOpenableStorageBlock) {
            AbstractOpenableStorageBlock BLOCK = (AbstractOpenableStorageBlock) TEMP;
            this.initialise(BLOCK.blockId());
            ContainerHelper.loadAllItems(TAG, inventory);
        } else {
            throw new IllegalStateException("Block Entity attached to wrong block.");
        }
    }

    @Override
    public CompoundTag save(final CompoundTag TAG) {
        super.save(TAG);
        ContainerHelper.saveAllItems(TAG, inventory);
        return TAG;
    }

    // <editor-fold desc="// Wordly Container Impl">
    @Override
    public int[] getSlotsForFace(final Direction DIRECTION) {
        return slotsForFace;
    }

    public boolean canPlaceItemThroughFace(final int SLOT, final ItemStack STACK, @Nullable final Direction FACE) {
        return this.canPlaceItem(SLOT, STACK);
    }

    public boolean canTakeItemThroughFace(final int SLOT, final ItemStack STACK, final Direction FACE) {
        return true;
    }

    // <editor-fold desc="// Container Impl">
    @Override
    public int getContainerSize() {
        return slots;
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(final int SLOT) {
        return inventory.get(SLOT);
    }

    @Override
    public ItemStack removeItem(final int SLOT, final int AMOUNT) {
        final ItemStack STACK = ContainerHelper.removeItem(inventory, SLOT, AMOUNT);
        if (!STACK.isEmpty()) {
            this.setChanged();
        }
        return STACK;
    }

    @Override
    public ItemStack removeItemNoUpdate(final int SLOT) {
        return ContainerHelper.takeItem(inventory, SLOT);
    }

    @Override
    public void setItem(final int SLOT, final ItemStack STACK) {
        inventory.set(SLOT, STACK);
        if (STACK.getCount() > this.getMaxStackSize()) {
            STACK.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public boolean stillValid(final Player PLAYER) {
        if (level.getBlockEntity(worldPosition) != this) {
            return false;
        } else {
            return PLAYER.distanceToSqr(Vec3.atCenterOf(worldPosition)) <= 64;
        }
    }

    // <editor-fold desc="// Clearable Impl">
    @Override
    public void clearContent() {
        inventory.clear();
    }
    // </editor-fold>
    // </editor-fold>
    // </editor-fold>
}

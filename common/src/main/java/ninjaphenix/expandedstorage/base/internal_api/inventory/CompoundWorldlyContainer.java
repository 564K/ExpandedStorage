package ninjaphenix.expandedstorage.base.internal_api.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
@Internal
public final class CompoundWorldlyContainer implements WorldlyContainer {
    private final WorldlyContainer FIRST;
    private final WorldlyContainer SECOND;

    public CompoundWorldlyContainer(final WorldlyContainer FIRST, final WorldlyContainer SECOND) {
        this.FIRST = FIRST;
        this.SECOND = SECOND;
    }

    @Override
    public int[] getSlotsForFace(final Direction DIRECTION) {
        final int NUM_FIRST_SLOTS = FIRST.getContainerSize();
        final int[] FIRST_SLOTS = FIRST.getSlotsForFace(DIRECTION);
        final int[] SECOND_SLOTS = SECOND.getSlotsForFace(DIRECTION);
        final int[] COMBINED = new int[FIRST_SLOTS.length + SECOND_SLOTS.length];
        int index = 0;
        for (final int slot : FIRST_SLOTS) {
            COMBINED[index++] = slot;
        }
        for (final int slot : SECOND_SLOTS) {
            COMBINED[index++] = slot + NUM_FIRST_SLOTS;
        }
        return COMBINED;
    }

    @Override
    public boolean canPlaceItemThroughFace(final int SLOT, final ItemStack STACK, final Direction DIRECTION) {
        if (SLOT >= FIRST.getContainerSize()) {
            return SECOND.canPlaceItemThroughFace(SLOT - FIRST.getContainerSize(), STACK, DIRECTION);
        }
        return FIRST.canPlaceItemThroughFace(SLOT, STACK, DIRECTION);
    }

    @Override
    public boolean canTakeItemThroughFace(final int SLOT, final ItemStack STACK, final Direction DIRECTION) {
        if (SLOT >= FIRST.getContainerSize()) {
            return SECOND.canTakeItemThroughFace(SLOT - FIRST.getContainerSize(), STACK, DIRECTION);
        }
        return FIRST.canTakeItemThroughFace(SLOT, STACK, DIRECTION);
    }

    @Override
    public int getContainerSize() {
        return FIRST.getContainerSize() + SECOND.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return FIRST.isEmpty() && SECOND.isEmpty();
    }

    @Override
    public boolean stillValid(final Player PLAYER) {
        return FIRST.stillValid(PLAYER) && SECOND.stillValid(PLAYER);
    }

    @Override
    public void clearContent() {
        FIRST.clearContent();
        SECOND.clearContent();
    }

    @Override
    public void setChanged() {
        FIRST.setChanged();
        SECOND.setChanged();
    }

    @Override
    public void startOpen(final Player PLAYER) {
        FIRST.startOpen(PLAYER);
        SECOND.startOpen(PLAYER);
    }

    @Override
    public void stopOpen(final Player PLAYER) {
        FIRST.stopOpen(PLAYER);
        SECOND.stopOpen(PLAYER);
    }

    public boolean consistsPartlyOf(final WorldlyContainer CONTAINER) {
        return FIRST == CONTAINER || SECOND == CONTAINER;
    }

    public int getMaxStackSize() {
        return FIRST.getMaxStackSize();
    }

    @Override
    public ItemStack getItem(final int SLOT) {
        if (SLOT >= FIRST.getContainerSize()) {
            return SECOND.getItem(SLOT - FIRST.getContainerSize());
        }
        return FIRST.getItem(SLOT);
    }

    @Override
    public ItemStack removeItem(final int SLOT, final int AMOUNT) {
        if (SLOT >= FIRST.getContainerSize()) {
            return SECOND.removeItem(SLOT - FIRST.getContainerSize(), AMOUNT);
        }
        return FIRST.removeItem(SLOT, AMOUNT);
    }

    @Override
    public ItemStack removeItemNoUpdate(final int SLOT) {
        if (SLOT >= FIRST.getContainerSize()) {
            return SECOND.removeItemNoUpdate(SLOT - FIRST.getContainerSize());
        }
        return FIRST.removeItemNoUpdate(SLOT);
    }

    @Override
    public void setItem(final int SLOT, final ItemStack STACK) {
        if (SLOT >= FIRST.getContainerSize()) {
            SECOND.setItem(SLOT - FIRST.getContainerSize(), STACK);
        } else {
            FIRST.setItem(SLOT, STACK);
        }
    }

    @Override
    public boolean canPlaceItem(final int SLOT, final ItemStack STACK) {
        if (SLOT >= FIRST.getContainerSize()) {
            return SECOND.canPlaceItem(SLOT - FIRST.getContainerSize(), STACK);
        }
        return FIRST.canPlaceItem(SLOT, STACK);
    }
}

package ninjaphenix.expandedstorage.base.internal_api.inventory;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.inventory.screen.ScreenMeta;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.Collections;
import java.util.List;

@Experimental
@Internal
public abstract class AbstractContainerMenu_<T extends ScreenMeta> extends AbstractContainerMenu {
    public final BlockPos POS;
    public final T SCREEN_META;
    protected final Container CONTAINER;
    private final Component DISPLAY_NAME;

    public AbstractContainerMenu_(final MenuType<?> MENU_TYPE, final int WINDOW_ID, final BlockPos POS, final Container CONTAINER,
                                  final Inventory PLAYER_INVENTORY, final Component DISPLAY_NAME, final T SCREEN_META) {
        super(MENU_TYPE, WINDOW_ID);
        this.POS = POS;
        this.CONTAINER = CONTAINER;
        this.DISPLAY_NAME = DISPLAY_NAME;
        this.SCREEN_META = SCREEN_META;
        CONTAINER.startOpen(PLAYER_INVENTORY.player);
    }

    public static ResourceLocation getTexture(final String TYPE, final int SLOT_X_COUNT, final int SLOT_Y_COUNT) {
        return Utils.resloc(String.format("textures/gui/container/%s_%d_%d.png", TYPE, SLOT_X_COUNT, SLOT_Y_COUNT));
    }

    protected static <T extends ScreenMeta> T getNearestScreenMeta(final int INVENTORY_SIZE, final ImmutableMap<Integer, T> KNOWN_SIZES) {
        final T EXACT_SCREEN_META = KNOWN_SIZES.get(INVENTORY_SIZE);
        if (EXACT_SCREEN_META != null) {
            return EXACT_SCREEN_META;
        }
        final List<Integer> KEYS = KNOWN_SIZES.keySet().asList();
        final int index = Collections.binarySearch(KEYS, INVENTORY_SIZE);
        final int largestKey = KEYS.get(Math.abs(index) - 1);
        final T nearestMeta = KNOWN_SIZES.get(largestKey);
        if (nearestMeta != null && largestKey > INVENTORY_SIZE && largestKey - INVENTORY_SIZE <= nearestMeta.WIDTH) {
            return nearestMeta;
        }
        throw new RuntimeException("No screen can show an inventory of size " + INVENTORY_SIZE + ".");
    }

    @Override
    public boolean stillValid(final Player PLAYER) {
        return CONTAINER.stillValid(PLAYER);
    }

    public Component getDisplayName() {
        return DISPLAY_NAME.plainCopy();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        CONTAINER.stopOpen(player);
    }

    public final Container getContainer() {
        return CONTAINER;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack newStack = slot.getItem();
            originalStack = newStack.copy();
            if (index < SCREEN_META.TOTAL_SLOTS) {
                if (!this.moveItemStackTo(newStack, SCREEN_META.TOTAL_SLOTS, SCREEN_META.TOTAL_SLOTS + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(newStack, 0, SCREEN_META.TOTAL_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
            if (newStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return originalStack;
    }
}

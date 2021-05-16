package ninjaphenix.expandedstorage.base.inventory;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;
import ninjaphenix.expandedstorage.base.inventory.screen.ScrollableScreenMeta;

import java.util.function.IntUnaryOperator;

public final class ScrollableContainerMenu extends AbstractContainerMenu_<ScrollableScreenMeta> {
    // @formatter:off
    private static final ImmutableMap<Integer, ScrollableScreenMeta> SIZES = ImmutableMap.<Integer, ScrollableScreenMeta>builder()
            .put(27, new ScrollableScreenMeta(9, 3, 27, getTexture("shared", 9, 3), 208, 192)) // Wood
            .put(54, new ScrollableScreenMeta(9, 6, 54, getTexture("shared", 9, 6), 208, 240)) // Iron / Large Wood
            .put(81, new ScrollableScreenMeta(9, 6, 81, getTexture("shared", 9, 6), 208, 240)) // Gold
            .put(108, new ScrollableScreenMeta(9, 6, 108, getTexture("shared", 9, 6), 208, 240)) // Diamond / Large Iron
            .put(135, new ScrollableScreenMeta(9, 6, 135, getTexture("shared", 9, 6), 208, 240)) // Netherite
            .put(162, new ScrollableScreenMeta(9, 6, 162, getTexture("shared", 9, 6), 208, 240)) // Large Gold
            .put(216, new ScrollableScreenMeta(9, 6, 216, getTexture("shared", 9, 6), 208, 240)) // Large Diamond
            .put(270, new ScrollableScreenMeta(9, 6, 270, getTexture("shared", 9, 6), 208, 240)) // Large Netherite
            .build();
    // @formatter:on

    public ScrollableContainerMenu(final int WINDOW_ID, final BlockPos POS, final Container CONTAINER, final Inventory INVENTORY, final Component DISPLAY_NAME) {
        super(BaseCommon.SCROLL_MENU_TYPE, WINDOW_ID, POS, CONTAINER, INVENTORY, DISPLAY_NAME,
                AbstractContainerMenu_.getNearestScreenMeta(CONTAINER.getContainerSize(), SIZES));
        for (int i = 0; i < CONTAINER.getContainerSize(); i++) {
            final int SLOT_X_POS = i % SCREEN_META.WIDTH;
            final int SLOT_Y_POS = Mth.ceil((((double) (i - SLOT_X_POS)) / SCREEN_META.WIDTH));
            final int REAL_Y_POS = SLOT_Y_POS >= SCREEN_META.HEIGHT ? -2000 : SLOT_Y_POS * 18 + 18;
            this.addSlot(new Slot(CONTAINER, i, SLOT_X_POS * 18 + 8, REAL_Y_POS));
        }
        final int left = (SCREEN_META.WIDTH * 18 + 14) / 2 - 80;
        final int top = 18 + 14 + (SCREEN_META.HEIGHT * 18);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlot(new Slot(INVENTORY, y * 9 + x + 9, left + 18 * x, top + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(INVENTORY, x, left + 18 * x, top + 58));
        }
    }

    public void moveSlotRange(final int MIN_SLOT_INDEX, final int MAX_SLOT_INDEX, final int Y_DIFFERENCE) {
        for (int i = MIN_SLOT_INDEX; i < MAX_SLOT_INDEX; i++) {
            slots.get(i).y += Y_DIFFERENCE;
        }
    }

    public void setSlotRange(final int MIN_SLOT_INDEX, final int MAX_SLOT_INDEX, final IntUnaryOperator Y_MUTATOR) {
        for (int i = MIN_SLOT_INDEX; i < MAX_SLOT_INDEX; i++) {
            slots.get(i).y = Y_MUTATOR.applyAsInt(i);
        }
    }

    public static final class Factory implements ClientContainerMenuFactory<ScrollableContainerMenu> {
        @Override
        public ScrollableContainerMenu create(final int WINDOW_ID, final Inventory INVENTORY, final FriendlyByteBuf BUFFER) {
            if (BUFFER == null) {
                return null;
            }
            return new ScrollableContainerMenu(WINDOW_ID, BUFFER.readBlockPos(), new SimpleContainer(BUFFER.readInt()), INVENTORY, null);
        }
    }
}

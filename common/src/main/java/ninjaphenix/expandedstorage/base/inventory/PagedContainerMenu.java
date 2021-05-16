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
import ninjaphenix.expandedstorage.base.inventory.screen.PagedScreenMeta;

public final class PagedContainerMenu extends AbstractContainerMenu_<PagedScreenMeta> {
    // @formatter:off
    private static final ImmutableMap<Integer, PagedScreenMeta> SIZES = ImmutableMap.<Integer, PagedScreenMeta>builder()
            .put(27, new PagedScreenMeta(9, 3, 1, 27, getTexture("shared", 9, 3), 208, 192)) // Wood
            .put(54, new PagedScreenMeta(9, 6, 1, 54, getTexture("shared", 9, 6), 208, 240)) // Iron / Large Wood
            .put(81, new PagedScreenMeta(9, 6, 2, 81, getTexture("shared", 9, 6), 208, 240)) // Gold
            .put(108, new PagedScreenMeta(9, 6, 2, 108, getTexture("shared", 9, 6), 208, 240)) // Diamond / Large Iron
            .put(135, new PagedScreenMeta(9, 6, 3, 135, getTexture("shared", 9, 6), 208, 240)) // Netherite
            .put(162, new PagedScreenMeta(9, 6, 3, 162, getTexture("shared", 9, 6), 208, 240)) // Large Gold
            .put(216, new PagedScreenMeta(9, 6, 4, 216, getTexture("shared", 9, 6), 208, 240)) // Large Diamond
            .put(270, new PagedScreenMeta(9, 6, 5, 270, getTexture("shared", 9, 6), 208, 240)) // Large Netherite
            .build();
    // @formatter:on

    public PagedContainerMenu(final int WINDOW_ID, final BlockPos POS, final Container CONTAINER, final Inventory PLAYER_INVENTORY, final Component DISPLAY_NAME) {
        super(BaseCommon.PAGE_MENU_TYPE, WINDOW_ID, POS, CONTAINER, PLAYER_INVENTORY, DISPLAY_NAME,
                AbstractContainerMenu_.getNearestScreenMeta(CONTAINER.getContainerSize(), SIZES));
        resetSlotPositions(true);
        final int left = (SCREEN_META.WIDTH * 18 + 14) / 2 - 80;
        final int top = 18 + 14 + (SCREEN_META.HEIGHT * 18);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlot(new Slot(PLAYER_INVENTORY, y * 9 + x + 9, left + 18 * x, top + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(PLAYER_INVENTORY, x, left + 18 * x, top + 58));
        }
    }

    public void resetSlotPositions(final boolean CREATE_SLOTS) {
        for (int i = 0; i < CONTAINER.getContainerSize(); i++) {
            final int SLOT_X_POS = i % SCREEN_META.WIDTH;
            final int SLOT_Y_POS = Mth.ceil((((double) (i - SLOT_X_POS)) / SCREEN_META.WIDTH));
            final int REAL_Y_POS = SLOT_Y_POS >= SCREEN_META.HEIGHT ? (18 * (SLOT_Y_POS % SCREEN_META.HEIGHT)) - 2000 : SLOT_Y_POS * 18;
            if (CREATE_SLOTS) {
                this.addSlot(new Slot(CONTAINER, i, SLOT_X_POS * 18 + 8, REAL_Y_POS + 18));
            } else {
                slots.get(i).y = REAL_Y_POS + 18;
            }
        }
    }

    public void moveSlotRange(final int MIN_SLOT_INDEX, final int MAX_SLOT_INDEX, final int Y_DIFFERENCE) {
        for (int i = MIN_SLOT_INDEX; i < MAX_SLOT_INDEX; i++) {
            slots.get(i).y += Y_DIFFERENCE;
        }
    }

    public static final class Factory implements ClientContainerMenuFactory<PagedContainerMenu> {
        @Override
        public PagedContainerMenu create(final int WINDOW_ID, final Inventory INVENTORY, final FriendlyByteBuf BUFFER) {
            if (BUFFER == null) {
                return null;
            }
            return new PagedContainerMenu(WINDOW_ID, BUFFER.readBlockPos(), new SimpleContainer(BUFFER.readInt()), INVENTORY, null);
        }
    }
}

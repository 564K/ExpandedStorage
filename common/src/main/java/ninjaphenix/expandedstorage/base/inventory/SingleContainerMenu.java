package ninjaphenix.expandedstorage.base.inventory;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import ninjaphenix.expandedstorage.base.BaseCommon;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;
import ninjaphenix.expandedstorage.base.inventory.screen.SingleScreenMeta;

public final class SingleContainerMenu extends AbstractContainerMenu_<SingleScreenMeta> {
    // @formatter:off
    private static final ImmutableMap<Integer, SingleScreenMeta> SIZES = ImmutableMap.<Integer, SingleScreenMeta>builder()
            .put(27, new SingleScreenMeta(9, 3, 27, getTexture("shared", 9, 3), 208, 192)) // Wood
            .put(54, new SingleScreenMeta(9, 6, 54, getTexture("shared", 9, 6), 208, 240)) // Iron / Large Wood
            .put(81, new SingleScreenMeta(9, 9, 81, getTexture("shared", 9, 9), 208, 304)) // Gold
            .put(108, new SingleScreenMeta(12, 9, 108, getTexture("shared", 12, 9), 256, 304)) // Diamond / Large Iron
            .put(135, new SingleScreenMeta(15, 9, 135, getTexture("shared", 15, 9), 320, 304)) // Netherite
            .put(162, new SingleScreenMeta(18, 9, 162, getTexture("shared", 18, 9), 368, 304)) // Large Gold
            .put(216, new SingleScreenMeta(18, 12, 216, getTexture("shared", 18, 12), 368, 352)) // Large Diamond
            .put(270, new SingleScreenMeta(18, 15, 270, getTexture("shared", 18, 15), 368, 416)) // Large Netherite
            .build();
    // @formatter:on

    public SingleContainerMenu(final int WINDOW_ID, final BlockPos POS, final Container CONTAINER, final Inventory PLAYER_INVENTORY, final Component DISPLAY_NAME) {
        super(BaseCommon.SINGLE_MENU_TYPE, WINDOW_ID, POS, CONTAINER, PLAYER_INVENTORY, DISPLAY_NAME,
                AbstractContainerMenu_.getNearestScreenMeta(CONTAINER.getContainerSize(), SIZES));
        for (int i = 0; i < CONTAINER.getContainerSize(); i++) {
            final int x = i % SCREEN_META.WIDTH;
            final int y = (i - x) / SCREEN_META.WIDTH;
            this.addSlot(new Slot(CONTAINER, i, x * 18 + 8, y * 18 + 18));
        }
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

    public static final class Factory implements ClientContainerMenuFactory<SingleContainerMenu> {
        @Override
        public SingleContainerMenu create(final int WINDOW_ID, final Inventory INVENTORY, final FriendlyByteBuf BUFFER) {
            if (BUFFER == null) {
                return null;
            }
            return new SingleContainerMenu(WINDOW_ID, BUFFER.readBlockPos(), new SimpleContainer(BUFFER.readInt()), INVENTORY, null);
        }
    }
}

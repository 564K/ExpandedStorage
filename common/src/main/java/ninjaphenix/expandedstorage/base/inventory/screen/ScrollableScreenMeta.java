package ninjaphenix.expandedstorage.base.inventory.screen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class ScrollableScreenMeta extends ScreenMeta {
    public final int BLANK_SLOTS, TOTAL_ROWS;

    public ScrollableScreenMeta(int width, int height, int totalSlots, ResourceLocation texture, int textureWidth, int textureHeight) {
        super(width, height, totalSlots, texture, textureWidth, textureHeight);
        TOTAL_ROWS = Mth.ceil((double) totalSlots / width);
        BLANK_SLOTS = TOTAL_ROWS * width - totalSlots;
    }
}

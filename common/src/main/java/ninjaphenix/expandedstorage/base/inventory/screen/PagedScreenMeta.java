package ninjaphenix.expandedstorage.base.inventory.screen;

import net.minecraft.resources.ResourceLocation;

public final class PagedScreenMeta extends ScreenMeta {
    public final int BLANK_SLOTS, PAGES;

    public PagedScreenMeta(int width, int height, int pages, int totalSlots, ResourceLocation texture, int textureWidth, int textureHeight) {
        super(width, height, totalSlots, texture, textureWidth, textureHeight);
        PAGES = pages;
        BLANK_SLOTS = pages * width * height - totalSlots;
    }
}

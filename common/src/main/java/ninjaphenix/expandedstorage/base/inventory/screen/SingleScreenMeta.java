package ninjaphenix.expandedstorage.base.inventory.screen;

import net.minecraft.resources.ResourceLocation;

public final class SingleScreenMeta extends ScreenMeta {
    public final int BLANK_SLOTS;

    public SingleScreenMeta(int width, int height, int totalSlots, ResourceLocation texture, int textureWidth, int textureHeight) {
        super(width, height, totalSlots, texture, textureWidth, textureHeight);
        BLANK_SLOTS = width * height - totalSlots;
    }
}

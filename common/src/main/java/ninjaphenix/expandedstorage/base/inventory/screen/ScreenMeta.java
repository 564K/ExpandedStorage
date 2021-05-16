package ninjaphenix.expandedstorage.base.inventory.screen;

import net.minecraft.resources.ResourceLocation;

public abstract class ScreenMeta {
    public final int WIDTH, HEIGHT, TOTAL_SLOTS, TEXTURE_WIDTH, TEXTURE_HEIGHT;
    public final ResourceLocation TEXTURE;

    protected ScreenMeta(int width, int height, int totalSlots, ResourceLocation texture, int textureWidth, int textureHeight) {
        WIDTH = width;
        HEIGHT = height;
        TOTAL_SLOTS = totalSlots;
        TEXTURE = texture;
        TEXTURE_WIDTH = textureWidth;
        TEXTURE_HEIGHT = textureHeight;
    }
}

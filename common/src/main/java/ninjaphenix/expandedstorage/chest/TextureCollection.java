package ninjaphenix.expandedstorage.chest;

import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType;

public final class TextureCollection {
    private final ResourceLocation single;
    private final ResourceLocation left;
    private final ResourceLocation right;
    private final ResourceLocation top;
    private final ResourceLocation bottom;
    private final ResourceLocation front;
    private final ResourceLocation back;

    public TextureCollection(ResourceLocation single, ResourceLocation left, ResourceLocation right,
                             ResourceLocation top, ResourceLocation bottom, ResourceLocation front, ResourceLocation back) {
        this.single = single;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.front = front;
        this.back = back;
    }

    ResourceLocation getTexture(CursedChestType type) {
        switch (type) {
            case TOP:
                return this.top;
            case BOTTOM:
                return this.bottom;
            case FRONT:
                return this.front;
            case BACK:
                return this.back;
            case LEFT:
                return this.left;
            case RIGHT:
                return this.right;
            case SINGLE:
                return this.single;
            default:
                throw new IllegalArgumentException("TextureCollection#getTexture received an unknown CursedChestType.");
        }
    }
}

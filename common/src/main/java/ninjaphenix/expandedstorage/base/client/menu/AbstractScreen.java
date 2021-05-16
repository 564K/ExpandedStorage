package ninjaphenix.expandedstorage.base.client.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.inventory.screen.ScreenMeta;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractScreen<T extends AbstractContainerMenu_<R>, R extends ScreenMeta> extends AbstractContainerScreen<T> {
    protected final R SCREEN_META;
    private final Integer INVENTORY_LABEL_LEFT;

    protected AbstractScreen(T container, Inventory playerInventory, Component title, Function<R, Integer> inventoryLabelLeftFunction) {
        super(container, playerInventory, title);
        SCREEN_META = container.SCREEN_META;
        INVENTORY_LABEL_LEFT = inventoryLabelLeftFunction.apply(SCREEN_META);
    }

    @Override
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(SCREEN_META.TEXTURE);
        GuiComponent.blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        if (stack == null) { // Not sure why this can be null, but don't render in case it is.
            return;
        }
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        font.draw(stack, title, 8, 6, 4210752);
        font.draw(stack, inventory.getDisplayName(), INVENTORY_LABEL_LEFT, imageHeight - 96 + 2, 4210752);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected final void renderButtonTooltip(AbstractButton button, PoseStack stack, int x, int y) {
        this.renderTooltip(stack, button.getMessage(), x, y);
    }

    public abstract List<Rect2i> getExclusionZones();

    protected static class Image {
        public final int X, Y, WIDTH, HEIGHT, TEXTURE_X, TEXTURE_Y, TEXTURE_WIDTH, TEXTURE_HEIGHT;

        public Image(int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
            X = x;
            Y = y;
            WIDTH = width;
            HEIGHT = height;
            TEXTURE_X = textureX;
            TEXTURE_Y = textureY;
            TEXTURE_WIDTH = textureWidth;
            TEXTURE_HEIGHT = textureHeight;
        }

        public void render(PoseStack stack) {
            GuiComponent.blit(stack, X, Y, TEXTURE_X, TEXTURE_Y, WIDTH, HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }
}

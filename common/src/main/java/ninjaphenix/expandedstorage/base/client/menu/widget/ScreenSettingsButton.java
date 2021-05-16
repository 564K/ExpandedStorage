package ninjaphenix.expandedstorage.base.client.menu.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.platform.NetworkWrapper;

public final class ScreenSettingsButton extends Button {
    private final ResourceLocation TEXTURE;
    private boolean wasHovered;
    private boolean focused;

    public ScreenSettingsButton(int x, int y, OnTooltip onTooltip) {
        super(x, y, 18, 22, new TranslatableComponent("screen.expandedstorage.change_screen_button"), button -> NetworkWrapper.getInstance().c2s_openTypeSelectScreen(), onTooltip);
        TEXTURE = Utils.resloc("textures/gui/select_screen_button.png");
    }

    @Override
    public void render(PoseStack stack, int x, int y, float delta) {
        isHovered = this.isMouseOver(x, y);
        final boolean HOVERED_OR_FOCUSED = this.isHovered();
        if (wasHovered != HOVERED_OR_FOCUSED) {
            if (HOVERED_OR_FOCUSED) {
                if (focused) {
                    this.queueNarration(200);
                } else {
                    this.queueNarration(750);
                }
            } else {
                nextNarration = Long.MAX_VALUE;
            }
        }
        this.renderButton(stack, x, y, delta);
        this.narrate();
        wasHovered = HOVERED_OR_FOCUSED;
    }

    @Override
    public boolean changeFocus(boolean bl) {
        if (visible) {
            focused = !focused;
            this.onFocusedChanged(focused);
            return focused;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    protected void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
        Minecraft.getInstance().getTextureManager().bind(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GuiComponent.blit(stack, x, y, 0, this.isHovered() ? height : 0, width, height, 32, 48);
        if (isHovered) {
            renderToolTip(stack, mouseX, mouseY);
        } else if (this.isFocused()) {
            renderToolTip(stack, x, y);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX > this.x + 4 && mouseX < this.x + 17 && mouseY > this.y + 4 && mouseY < this.y + 17;
    }
}

package ninjaphenix.expandedstorage.base.client.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import ninjaphenix.expandedstorage.base.client.menu.widget.ScreenPickButton;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.platform.NetworkWrapper;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PickScreen extends Screen {
    private static final Map<ResourceLocation, Tuple<ResourceLocation, Component>> buttonSettings = new HashMap<>();
    private final Set<ResourceLocation> OPTIONS;
    private final Screen parent;
    private int TOP_PADDING;

    public PickScreen(Set<ResourceLocation> options, Screen parent) {
        super(new TranslatableComponent("screen.expandedstorage.screen_picker_title"));
        OPTIONS = options;
        this.parent = parent;
    }

    public static void declareButtonSettings(ResourceLocation containerType, ResourceLocation texture, Component text) {
        buttonSettings.putIfAbsent(containerType, new Tuple<>(texture, text));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    protected void init() {
        super.init();
        final boolean IGNORE_SINGLE = width < 370 || height < 386; // Smallest possible resolution a double netherite chest fits on.
        final int CHOICES = OPTIONS.size() - (IGNORE_SINGLE ? 1 : 0);
        final int COLUMNS = Math.min(Mth.intFloorDiv(width, 96), CHOICES);
        final int INNER_PADDING = Math.min((width - COLUMNS * 96) / (COLUMNS + 1), 20); // 20 is smallest gap for any screen.
        final int OUTER_PADDING = (width - (((COLUMNS - 1) * INNER_PADDING) + (COLUMNS * 96))) / 2;
        int x = 0;
        final int TOP_PADDING = (height - 96) / 2;
        this.TOP_PADDING = TOP_PADDING;
        for (ResourceLocation option : OPTIONS) {
            if (!(IGNORE_SINGLE && Utils.SINGLE_CONTAINER_TYPE.equals(option))) {
                Tuple<ResourceLocation, Component> settings = buttonSettings.get(option);
                this.addButton(new ScreenPickButton(OUTER_PADDING + (INNER_PADDING + 96) * x, TOP_PADDING, 96, 96,
                        settings.getA(), settings.getB(), button -> this.updatePlayerPreference(option),
                        (button, matrices, tX, tY) -> this.renderTooltip(matrices, button.getMessage(), tX, tY)));
                x++;
            }
        }
    }

    private void updatePlayerPreference(ResourceLocation selection) {
        NetworkWrapper.getInstance().c2s_setSendTypePreference(selection);
        this.onClose();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE && this.shouldCloseOnEsc()) {
            NetworkWrapper.getInstance().c2s_removeTypeSelectCallback();
            this.onClose();
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        if (stack == null) {
            return;
        } // Not sure why this can be null, but don't render in case it is.
        this.setBlitOffset(0);
        this.renderBackground(stack);
        final int NUM_BUTTONS = buttons.size();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < NUM_BUTTONS; i++) {
            buttons.get(i).render(stack, mouseX, mouseY, delta);
        }
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < NUM_BUTTONS; i++) {
            final AbstractWidget TEMP = buttons.get(i);
            if (buttons.get(i) instanceof ScreenPickButton) {
                ScreenPickButton button = (ScreenPickButton) TEMP;
                button.renderTooltip(stack, mouseX, mouseY);
            }
        }
        GuiComponent.drawCenteredString(stack, font, title, width / 2, Math.max(TOP_PADDING / 2, 0), 0xFFFFFFFF);
    }
}

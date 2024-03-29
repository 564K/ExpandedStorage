package ninjaphenix.expandedstorage.base.client.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import ninjaphenix.expandedstorage.base.client.menu.widget.ScreenPickButton;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.wrappers.NetworkWrapper;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PickScreen extends Screen {
    private static final Map<ResourceLocation, Tuple<ResourceLocation, Component>> BUTTON_SETTINGS = new HashMap<>();
    private final Set<ResourceLocation> options;
    private final Screen parent;
    private int topPadding;

    public PickScreen(Set<ResourceLocation> options, Screen parent) {
        super(new TranslatableComponent("screen.expandedstorage.screen_picker_title"));
        this.options = options;
        this.parent = parent;
    }

    public static void declareButtonSettings(ResourceLocation containerType, ResourceLocation texture, Component text) {
        PickScreen.BUTTON_SETTINGS.putIfAbsent(containerType, new Tuple<>(texture, text));
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return minecraft.level == null;
    }

    @Override
    protected void init() {
        super.init();
        boolean ignoreSingle = width < 370 || height < 386; // Smallest possible resolution a double netherite chest fits on.
        int choices = options.size() - (ignoreSingle ? 1 : 0);
        int columns = Math.min(Mth.intFloorDiv(width, 96), choices);
        int innerPadding = Math.min((width - columns * 96) / (columns + 1), 20); // 20 is smallest gap for any screen.
        int outerPadding = (width - (((columns - 1) * innerPadding) + (columns * 96))) / 2;
        int x = 0;
        int topPadding = (height - 96) / 2;
        this.topPadding = topPadding;
        for (ResourceLocation option : options) {
            if (!(ignoreSingle && Utils.SINGLE_CONTAINER_TYPE.equals(option))) {
                Tuple<ResourceLocation, Component> settings = PickScreen.BUTTON_SETTINGS.get(option);
                this.addButton(new ScreenPickButton(outerPadding + (innerPadding + 96) * x, topPadding, 96, 96,
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
        this.setBlitOffset(0);
        this.renderBackground(stack);
        int numButtons = buttons.size();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < numButtons; i++) {
            buttons.get(i).render(stack, mouseX, mouseY, delta);
        }
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < numButtons; i++) {
            if (buttons.get(i) instanceof ScreenPickButton button) {
                button.renderTooltip(stack, mouseX, mouseY);
            }
        }
        GuiComponent.drawCenteredString(stack, font, title, width / 2, Math.max(topPadding / 2, 0), 0xFFFFFFFF);
    }
}

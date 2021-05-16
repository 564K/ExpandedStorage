package ninjaphenix.expandedstorage.base.client.menu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.expandedstorage.base.client.menu.widget.ScreenSettingsButton;
import ninjaphenix.expandedstorage.base.inventory.ScrollableContainerMenu;
import ninjaphenix.expandedstorage.base.inventory.screen.ScrollableScreenMeta;
import ninjaphenix.expandedstorage.base.platform.ConfigWrapper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class ScrollableScreen extends AbstractScreen<ScrollableContainerMenu, ScrollableScreenMeta> {
    protected final boolean HAS_SCROLLBAR;
    private final boolean SCROLLING_UNRESTRICTED;
    private boolean isDragging;
    private int topRow;
    private ScreenSettingsButton screenSettingsButton;

    public ScrollableScreen(ScrollableContainerMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, (screenMeta) -> (screenMeta.WIDTH * 18 + 14) / 2 - 80);
        HAS_SCROLLBAR = SCREEN_META.TOTAL_ROWS != SCREEN_META.HEIGHT;
        imageWidth = 14 + 18 * SCREEN_META.WIDTH;
        imageHeight = 17 + 97 + 18 * SCREEN_META.HEIGHT;
        SCROLLING_UNRESTRICTED = ConfigWrapper.getInstance().isScrollingUnrestricted();
    }

    @Override
    protected void init() {
        super.init();
        if (HAS_SCROLLBAR) {
            isDragging = false;
            topRow = 0;
        }
        if (SCREEN_META.WIDTH == 9) {
            this.screenSettingsButton = addButton(new ScreenSettingsButton(leftPos - 15, topPos + imageHeight - 22, this::renderButtonTooltip));
        } else {
            int y = topPos + (SCREEN_META.HEIGHT * 18) + 12;
            this.screenSettingsButton = addButton(new ScreenSettingsButton(leftPos - 15, y, this::renderButtonTooltip));
        }
    }

    @Override
    protected void renderBg(PoseStack stack, float delta, int mouseX, int mouseY) {
        super.renderBg(stack, delta, mouseX, mouseY);
        if (HAS_SCROLLBAR) {
            final int CONTAINER_SLOTS_HEIGHT = SCREEN_META.HEIGHT * 18;
            final int SCROLLBAR_HEIGHT = CONTAINER_SLOTS_HEIGHT + (SCREEN_META.WIDTH > 9 ? 34 : 24);
            GuiComponent.blit(stack, leftPos + imageWidth - 4, topPos, imageWidth, 0, 22, SCROLLBAR_HEIGHT, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
            final int Y_OFFSET = Mth.floor((CONTAINER_SLOTS_HEIGHT - 17) * (((double) topRow) / (SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT)));
            GuiComponent.blit(stack, leftPos + imageWidth - 2, topPos + Y_OFFSET + 18, imageWidth, SCROLLBAR_HEIGHT, 12, 15, SCREEN_META.TEXTURE_WIDTH, SCREEN_META.TEXTURE_HEIGHT);
        }
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        final int SCROLLBAR_TOP_POS = topPos + 18;
        final int SCROLLBAR_LEFT_POS = leftPos + imageWidth - 2;
        return mouseX >= SCROLLBAR_LEFT_POS && mouseY >= SCROLLBAR_TOP_POS && mouseX < SCROLLBAR_LEFT_POS + 12 && mouseY < SCROLLBAR_TOP_POS + SCREEN_META.HEIGHT * 18;
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return super.hasClickedOutside(mouseX, mouseY, left, top, button) && !isMouseOverScrollbar(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (HAS_SCROLLBAR) {
            if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
                if (topRow != SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT) {
                    if (Screen.hasShiftDown()) {
                        this.setTopRow(topRow, Math.min(topRow + SCREEN_META.HEIGHT, SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT));
                    } else {
                        this.setTopRow(topRow, topRow + 1);
                    }
                }
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_PAGE_UP) {
                if (topRow != 0) {
                    if (Screen.hasShiftDown()) {
                        this.setTopRow(topRow, Math.max(topRow - SCREEN_META.HEIGHT, 0));
                    } else {
                        this.setTopRow(topRow, topRow - 1);
                    }
                }
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (HAS_SCROLLBAR && this.isMouseOverScrollbar(mouseX, mouseY) && button == 0) {
            isDragging = true;
            this.updateTopRow(mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (HAS_SCROLLBAR && isDragging) {
            this.updateTopRow(mouseY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void updateTopRow(double mouseY) {
        this.setTopRow(topRow, Mth.floor(Mth.clampedLerp(0, SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT, (mouseY - (topPos + 18)) / (SCREEN_META.HEIGHT * 18))));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (HAS_SCROLLBAR && (SCROLLING_UNRESTRICTED || this.isMouseOverScrollbar(mouseX, mouseY))) {
            final int newTop;
            if (delta < 0) {
                newTop = Math.min(topRow + (hasShiftDown() ? SCREEN_META.HEIGHT : 1), SCREEN_META.TOTAL_ROWS - SCREEN_META.HEIGHT);
            } else {
                newTop = Math.max(topRow - (hasShiftDown() ? SCREEN_META.HEIGHT : 1), 0);
            }
            this.setTopRow(topRow, newTop);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void setTopRow(final int OLD_TOP_ROW, final int NEW_TOP_ROW) {
        if (OLD_TOP_ROW == NEW_TOP_ROW) {
            return;
        }
        topRow = NEW_TOP_ROW;
        final int DELTA = NEW_TOP_ROW - OLD_TOP_ROW;
        final int ROWS = Math.abs(DELTA);
        if (ROWS < SCREEN_META.HEIGHT) {
            final int SET_AMOUNT = ROWS * SCREEN_META.WIDTH;
            final int MOVABLE_AMOUNT = (SCREEN_META.HEIGHT - ROWS) * SCREEN_META.WIDTH;
            if (DELTA > 0) {
                final int setOutBegin = OLD_TOP_ROW * SCREEN_META.WIDTH;
                final int movableBegin = NEW_TOP_ROW * SCREEN_META.WIDTH;
                final int setInBegin = movableBegin + MOVABLE_AMOUNT;
                menu.setSlotRange(setOutBegin, setOutBegin + SET_AMOUNT, index -> -2000);
                menu.moveSlotRange(movableBegin, setInBegin, -18 * ROWS);
                menu.setSlotRange(setInBegin, Math.min(setInBegin + SET_AMOUNT, SCREEN_META.TOTAL_SLOTS),
                        index -> 18 * Mth.intFloorDiv(index - movableBegin + SCREEN_META.WIDTH, SCREEN_META.WIDTH));
            } else {
                final int setInBegin = NEW_TOP_ROW * SCREEN_META.WIDTH;
                final int movableBegin = OLD_TOP_ROW * SCREEN_META.WIDTH;
                final int setOutBegin = movableBegin + MOVABLE_AMOUNT;
                menu.setSlotRange(setInBegin, setInBegin + SET_AMOUNT,
                        index -> 18 * Mth.intFloorDiv(index - setInBegin + SCREEN_META.WIDTH, SCREEN_META.WIDTH));
                menu.moveSlotRange(movableBegin, setOutBegin, 18 * ROWS);
                menu.setSlotRange(setOutBegin, Math.min(setOutBegin + SET_AMOUNT, SCREEN_META.TOTAL_SLOTS), index -> -2000);
            }
        } else {
            final int oldMin = OLD_TOP_ROW * SCREEN_META.WIDTH;
            menu.setSlotRange(oldMin, Math.min(oldMin + SCREEN_META.WIDTH * SCREEN_META.HEIGHT, SCREEN_META.TOTAL_SLOTS), index -> -2000);
            final int newMin = NEW_TOP_ROW * SCREEN_META.WIDTH;
            menu.setSlotRange(newMin, newMin + SCREEN_META.WIDTH * SCREEN_META.HEIGHT,
                    index -> 18 + 18 * Mth.intFloorDiv(index - newMin, SCREEN_META.WIDTH));
        }
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        if (HAS_SCROLLBAR) {
            final int row = topRow;
            super.resize(client, width, height);
            setTopRow(topRow, row);
        } else {
            super.resize(client, width, height);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (HAS_SCROLLBAR && isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public List<Rect2i> getExclusionZones() {
        final List<Rect2i> excludedAreas = new ArrayList<>();
        if (HAS_SCROLLBAR) {
            final int height = SCREEN_META.HEIGHT * 18 + (SCREEN_META.WIDTH > 9 ? 34 : 24);
            excludedAreas.add(new Rect2i(leftPos + imageWidth - 4, topPos, 22, height));
        }
        excludedAreas.add(new Rect2i(screenSettingsButton.x, screenSettingsButton.y, screenSettingsButton.getWidth(), screenSettingsButton.getHeight()));
        return excludedAreas;
    }
}

package ninjaphenix.expandedstorage.base.client.menu;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ninjaphenix.expandedstorage.base.client.menu.widget.ScreenSettingsButton;
import ninjaphenix.expandedstorage.base.inventory.SingleContainerMenu;
import ninjaphenix.expandedstorage.base.inventory.screen.SingleScreenMeta;

import java.util.Collections;
import java.util.List;

public final class SingleScreen extends AbstractScreen<SingleContainerMenu, SingleScreenMeta> {
    private ScreenSettingsButton screenSettingsButton;

    public SingleScreen(SingleContainerMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, (screenMeta) -> (screenMeta.WIDTH * 18 + 14) / 2 - 80);
        imageWidth = 14 + 18 * SCREEN_META.WIDTH;
        imageHeight = 17 + 97 + 18 * SCREEN_META.HEIGHT;
    }

    @Override
    protected void init() {
        super.init();
        final int Y = topPos + (SCREEN_META.WIDTH == 9 ? imageHeight - 22 : SCREEN_META.HEIGHT * 18 + 12);
        screenSettingsButton = this.addButton(new ScreenSettingsButton(leftPos - 15, Y, this::renderButtonTooltip));
    }

    public List<Rect2i> getExclusionZones() {
        return Collections.singletonList(new Rect2i(screenSettingsButton.x, screenSettingsButton.y, screenSettingsButton.getWidth(), screenSettingsButton.getHeight()));
    }
}

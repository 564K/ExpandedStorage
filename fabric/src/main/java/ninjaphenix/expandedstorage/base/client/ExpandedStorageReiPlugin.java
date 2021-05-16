package ninjaphenix.expandedstorage.base.client;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.client.menu.AbstractScreen;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

import java.util.Collections;
import java.util.stream.Collectors;

public class ExpandedStorageReiPlugin implements REIPluginV0 {
    @Override
    public ResourceLocation getPluginIdentifier() {
        return Utils.resloc("rei_plugin");
    }

    @Override
    public void registerBounds(DisplayHelper displayHelper) {
        BaseBoundsHandler.getInstance().registerExclusionZones(AbstractScreen.class, () -> {
            final Screen SCREEN = Minecraft.getInstance().screen;
            if (SCREEN instanceof AbstractScreen<?, ?>) {
                return ((AbstractScreen<?, ?>) SCREEN)
                        .getExclusionZones()
                        .stream()
                        .map(rect -> new Rectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        });
    }
}

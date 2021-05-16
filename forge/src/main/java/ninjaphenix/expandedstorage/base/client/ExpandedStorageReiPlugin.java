package ninjaphenix.expandedstorage.base.client;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.client.menu.AbstractScreen;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class ExpandedStorageReiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return Utils.resloc("jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AbstractScreen.class, new IGuiContainerHandler<AbstractScreen<?, ?>>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(AbstractScreen<?, ?> screen) {
                return screen.getExclusionZones().stream().map(rect -> new Rect2i(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight())).collect(Collectors.toList());
            }
        });
    }
}

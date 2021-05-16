package ninjaphenix.expandedstorage.base;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import ninjaphenix.expandedstorage.base.client.menu.PagedScreen;
import ninjaphenix.expandedstorage.base.client.menu.PickScreen;
import ninjaphenix.expandedstorage.base.client.menu.ScrollableScreen;
import ninjaphenix.expandedstorage.base.client.menu.SingleScreen;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

import java.util.HashSet;
import java.util.Set;

@Mod("expandedstorage")
public final class Main {
    public Main() {
        BaseCommon.initialize();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addGenericListener(Item.class, (RegistryEvent.Register<Item> event) -> {
            IForgeRegistry<Item> registry = event.getRegistry();
            BaseApi.getInstance().getAndClearItems().forEach((key, value) -> registry.register(value.setRegistryName(key)));
        });
        modEventBus.addGenericListener(MenuType.class, (RegistryEvent.Register<MenuType<?>> event) -> {
            IForgeRegistry<MenuType<?>> registry = event.getRegistry();
            registry.registerAll(BaseCommon.SINGLE_MENU_TYPE, BaseCommon.PAGE_MENU_TYPE, BaseCommon.SCROLL_MENU_TYPE);
        });
        modEventBus.addListener((FMLClientSetupEvent event) -> {
            MenuScreens.register(BaseCommon.SINGLE_MENU_TYPE, SingleScreen::new);
            MenuScreens.register(BaseCommon.PAGE_MENU_TYPE, PagedScreen::new);
            MenuScreens.register(BaseCommon.SCROLL_MENU_TYPE, ScrollableScreen::new);
        });
        ModLoadingContext.get().getActiveContainer().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (minecraft, screen) -> {
            Set<ResourceLocation> values = new HashSet<>();
            values.add(Utils.SINGLE_CONTAINER_TYPE);
            values.add(Utils.PAGE_CONTAINER_TYPE);
            values.add(Utils.SCROLL_CONTAINER_TYPE);
            return new PickScreen(values, screen);
        });

        new ninjaphenix.expandedstorage.barrel.Main();
        new ninjaphenix.expandedstorage.chest.Main();
        new ninjaphenix.expandedstorage.old_chest.Main();
    }
}

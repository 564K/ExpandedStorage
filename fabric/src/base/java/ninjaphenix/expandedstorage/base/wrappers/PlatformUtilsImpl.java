package ninjaphenix.expandedstorage.base.wrappers;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;

import java.util.function.Supplier;

final class PlatformUtilsImpl implements PlatformUtils {
    private static PlatformUtilsImpl INSTANCE;
    private Boolean isClient;

    public static PlatformUtilsImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlatformUtilsImpl();
        }
        return INSTANCE;
    }

    @Override
    public CreativeModeTab createTab(Supplier<ItemStack> icon) {
        FabricItemGroupBuilder.build(new ResourceLocation("dummy"), null); // Fabric API is dumb.
        return new CreativeModeTab(CreativeModeTab.TABS.length - 1, Utils.MOD_ID) {
            @Override
            public ItemStack makeIcon() {
                return icon.get();
            }
        };
    }

    @Override
    public boolean isClient() {
        if (isClient == null) {
            isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
        }
        return isClient;
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(ResourceLocation menuType, ClientContainerMenuFactory<T> factory) {
        return ScreenHandlerRegistry.registerExtended(menuType, factory::create);
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}

package ninjaphenix.expandedstorage.base.platform.fabric;

import com.mojang.datafixers.types.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.expandedstorage.base.config.button.ButtonOffset;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ClientContainerMenuFactory;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class PlatformUtilsImpl implements PlatformUtils {
    private Boolean isClient;

    private PlatformUtilsImpl() {

    }

    @SuppressWarnings("unused")
    public static PlatformUtilsImpl getInstance() {
        return new PlatformUtilsImpl();
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
    public ButtonOffset[] getButtonOffsetConfig() {
        return new ButtonOffset[] {
                new ButtonOffset(Collections.singleton("inventoryprofiles"), -12),
                new ButtonOffset(Collections.singleton("inventorysorter"), -18)
        };
    }

    @Override
    public Set<String> getLoadedModIds() {
        var modIds = new HashSet<String>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            var metadata = mod.getMetadata();
            modIds.add(metadata.getId());
            modIds.addAll(metadata.getProvides());
        }
        return modIds;
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> blockEntitySupplier, Set<Block> blocks, Type<?> type) {
        return new BlockEntityType<>(blockEntitySupplier::apply, blocks, type);
    }
}

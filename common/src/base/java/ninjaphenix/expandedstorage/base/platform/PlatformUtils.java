package ninjaphenix.expandedstorage.base.platform;

import com.mojang.datafixers.types.Type;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
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

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface PlatformUtils {
    /**
     * Should be private, do not use.
     */
    @Deprecated
    LazyLoadedValue<PlatformUtils> instance = new LazyLoadedValue<>(() -> Utils.getClassInstance(PlatformUtils.class, "ninjaphenix.expandedstorage.base.platform", "PlatformUtilsImpl"));

    static PlatformUtils getInstance() {
        return instance.get();
    }

    CreativeModeTab createTab(Supplier<ItemStack> icon);

    boolean isClient();

    <T extends AbstractContainerMenu> MenuType<T> createMenuType(ResourceLocation menuType, ClientContainerMenuFactory<T> factory);

    ButtonOffset[] getButtonOffsetConfig();

    Set<String> getLoadedModIds();

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> blockEntitySupplier, Set<Block> blocks, Type<?> type);
}
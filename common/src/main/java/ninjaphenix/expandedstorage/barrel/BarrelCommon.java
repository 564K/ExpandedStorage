package ninjaphenix.expandedstorage.barrel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninjaphenix.expandedstorage.barrel.block.BarrelBlock;
import ninjaphenix.expandedstorage.barrel.block.misc.BarrelBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;

import java.util.function.Predicate;

public final class BarrelCommon {
    public static final ResourceLocation BLOCK_TYPE = Utils.resloc("barrel");
    private static final int ICON_SUITABILITY = 998;
    private static BlockEntityType<BarrelBlockEntity> blockEntityType;

    private BarrelCommon() {

    }

    public static BlockEntityType<BarrelBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    static void setBlockEntityType(BlockEntityType<BarrelBlockEntity> blockEntityType) {
        if (BarrelCommon.blockEntityType == null) {
            BarrelCommon.blockEntityType = blockEntityType;
        }
    }

    public static boolean tryUpgradeBlock(UseOnContext context, ResourceLocation from, ResourceLocation to) {
        return false;
    }

    public static void registerTabIcon(BlockItem item) {
        BaseApi.getInstance().offerTabIcon(item, BarrelCommon.ICON_SUITABILITY);
    }

    public static void registerUpgradeBehaviours(Tag<Block> tag) {
        Predicate<Block> isUpgradableChestBlock = (block) -> block instanceof BarrelBlock || tag.contains(block);
        BaseApi.getInstance().defineBlockUpgradeBehaviour(isUpgradableChestBlock, BarrelCommon::tryUpgradeBlock);
    }
}

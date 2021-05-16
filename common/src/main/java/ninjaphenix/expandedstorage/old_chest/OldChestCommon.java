package ninjaphenix.expandedstorage.old_chest;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.old_chest.block.OldChestBlock;
import ninjaphenix.expandedstorage.old_chest.block.misc.OldChestBlockEntity;

import java.util.function.Predicate;

public final class OldChestCommon {
    public static final ResourceLocation BLOCK_TYPE = Utils.resloc("old_cursed_chest");
    private static final int ICON_SUITABILITY = 999;
    private static BlockEntityType<OldChestBlockEntity> blockEntityType;

    private OldChestCommon() {

    }

    public static BlockEntityType<OldChestBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    static void setBlockEntityType(BlockEntityType<OldChestBlockEntity> type) {
        if (OldChestCommon.blockEntityType == null) {
            OldChestCommon.blockEntityType = type;
        }
    }

    private static boolean tryUpgradeBlock(UseOnContext context, ResourceLocation from, ResourceLocation to) {
        return false;
    }

    public static void registerUpgradeBehaviours() {
        Predicate<Block> isUpgradableChestBlock = (block) -> block instanceof OldChestBlock;
        BaseApi.getInstance().defineBlockUpgradeBehaviour(isUpgradableChestBlock, OldChestCommon::tryUpgradeBlock);
    }

    static void registerTabIcon(Item item) {
        BaseApi.getInstance().offerTabIcon(item, OldChestCommon.ICON_SUITABILITY);
    }
}

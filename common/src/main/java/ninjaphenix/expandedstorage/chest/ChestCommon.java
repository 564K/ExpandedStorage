package ninjaphenix.expandedstorage.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType;
import ninjaphenix.expandedstorage.chest.block.ChestBlock;
import ninjaphenix.expandedstorage.chest.block.misc.ChestBlockEntity;
import ninjaphenix.expandedstorage.chest.internal_api.ChestApi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public final class ChestCommon {
    public static final ResourceLocation BLOCK_TYPE = Utils.resloc("cursed_chest");
    private static final int ICON_SUITABILITY = 1000;
    private static BlockEntityType<ChestBlockEntity> blockEntityType;

    private ChestCommon() {

    }

    static void registerTabIcon(Item item) {
        BaseApi.getInstance().offerTabIcon(item, ChestCommon.ICON_SUITABILITY);
    }

    public static BlockEntityType<ChestBlockEntity> getBlockEntityType() {
        return blockEntityType;
    }

    static void setBlockEntityType(BlockEntityType<ChestBlockEntity> type) {
        if (ChestCommon.blockEntityType == null) {
            ChestCommon.blockEntityType = type;
        }
    }

    static Set<ResourceLocation> registerChestTextures(Set<ChestBlock> blocks) {
        Set<ResourceLocation> textures = new HashSet<>();
        blocks.forEach(block -> {
            ResourceLocation id = block.blockId();
            ChestApi.INSTANCE.declareChestTextures(
                    id, Utils.resloc("entity/" + id.getPath() + "/single"),
                    Utils.resloc("entity/" + id.getPath() + "/left"),
                    Utils.resloc("entity/" + id.getPath() + "/right"),
                    Utils.resloc("entity/" + id.getPath() + "/top"),
                    Utils.resloc("entity/" + id.getPath() + "/bottom"),
                    Utils.resloc("entity/" + id.getPath() + "/front"),
                    Utils.resloc("entity/" + id.getPath() + "/back"));
            Arrays.stream(CursedChestType.values())
                    .map(type -> ChestApi.INSTANCE.getChestTexture(id, type))
                    .forEach(textures::add);
        });
        return textures;
    }

    private static boolean tryUpgradeBlock(UseOnContext context, ResourceLocation from, ResourceLocation to) {
        if (true) { // Disable block upgrade for now.
            return false;
        }
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof ChestBlock) { // Expanded Storage chest block.
            ChestBlock chestBlock = (ChestBlock) block;
            if (from == chestBlock.blockTier()) {
                System.out.println("Trying to upgrade ES Chest.");
                // calculate one or 2 chests
                // check if correct amount of upgrades
                // check if there is a block to upgrade to
                // upgrade
            }
        } else { // In wooden chest tag.
            if (from == Utils.WOOD_TIER.key()) {
                System.out.println("Trying to upgrade Chest.");
                // calculate one or 2 chests
                // check if correct amount of upgrades
                // check if there is a block to upgrade to
                // upgrade
            }
        }
        return false;
    }

    public static void registerUpgradeBehaviours(Tag<Block> tag) {
        Predicate<Block> isUpgradableChestBlock = (block) -> block instanceof ChestBlock || tag.contains(block);
        BaseApi.getInstance().defineBlockUpgradeBehaviour(isUpgradableChestBlock, ChestCommon::tryUpgradeBlock);
    }
}

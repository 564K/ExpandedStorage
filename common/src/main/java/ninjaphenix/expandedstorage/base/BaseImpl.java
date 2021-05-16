package ninjaphenix.expandedstorage.base;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import ninjaphenix.expandedstorage.base.client.menu.PickScreen;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.item.BlockUpgradeBehaviour;
import ninjaphenix.expandedstorage.base.internal_api.item.MutationBehaviour;
import ninjaphenix.expandedstorage.base.internal_api.tier.Tier;
import ninjaphenix.expandedstorage.base.item.StorageConversionKit;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public final class BaseImpl implements BaseApi {
    private static BaseImpl instance;
    private final Map<String, MutationBehaviour> MUTATION_BEHAVIOURS = new HashMap<>();
    private final Map<Predicate<Block>, BlockUpgradeBehaviour> BLOCK_UPGRADE_BEHAVIOURS = new HashMap<>();
    private final Map<Pair<ResourceLocation, ResourceLocation>, AbstractStorageBlock> BLOCKS = new HashMap<>();
    private Map<ResourceLocation, Item> items = new LinkedHashMap<>();
    private Item tabIcon = Items.ENDER_CHEST;
    private int suitability = -1;

    private BaseImpl() {

    }

    public static BaseImpl getInstance() {
        if (BaseImpl.instance == null) {
            BaseImpl.instance = new BaseImpl();
        }
        return BaseImpl.instance;
    }

    @Override
    public void defineMutationBehaviour(ResourceLocation blockType, MutationBehaviour behaviour) {
        if (MUTATION_BEHAVIOURS.containsKey(blockType.toString())) {
            throw new IllegalStateException("Tried to register duplicate mutation behaviour for " + blockType);
        }
        MUTATION_BEHAVIOURS.put(blockType.toString(), behaviour);
    }

    @Override
    public Optional<BlockUpgradeBehaviour> getBlockUpgradeBehaviour(final Block BLOCK) {
        for (final Map.Entry<Predicate<Block>, BlockUpgradeBehaviour> ENTRY : BLOCK_UPGRADE_BEHAVIOURS.entrySet()) {
            if (ENTRY.getKey().test(BLOCK)) {
                return Optional.of(ENTRY.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public void defineBlockUpgradeBehaviour(Predicate<Block> target, BlockUpgradeBehaviour behaviour) {
        BLOCK_UPGRADE_BEHAVIOURS.put(target, behaviour);
    }

    @Override
    @ApiStatus.Internal
    public MutationBehaviour getMutationBehaviour(String id) {
        return MUTATION_BEHAVIOURS.get(id);
    }

    @Override
    public void defineTierUpgradePath(Component addingMod, Tier... tiers) {
        int NUM_TIERS = tiers.length;
        for (int fromIndex = 0; fromIndex < NUM_TIERS - 1; fromIndex++) {
            final Tier FROM_TIER = tiers[fromIndex];
            for (int toIndex = fromIndex + 1; toIndex < NUM_TIERS; toIndex++) {
                final Tier TO_TIER = tiers[toIndex];
                ResourceLocation itemId = Utils.resloc(FROM_TIER.key().getPath() + "_to_" + TO_TIER.key().getPath() + "_conversion_kit");
                if (!items.containsKey(itemId)) {
                    Item.Properties properties = FROM_TIER.itemProperties()
                            .andThen(TO_TIER.itemProperties())
                            .apply(new Item.Properties().tab(Utils.TAB).stacksTo(16));
                    Item kit = new StorageConversionKit(properties, FROM_TIER.key(), TO_TIER.key(), addingMod);
                    this.register(itemId, kit);
                }
            }
        }
    }

    @Override
    @ApiStatus.Internal
    public void register(ResourceLocation itemId, Item item) {
        items.put(itemId, item);
    }

    @Override
    public void registerContainerButtonSettings(ResourceLocation containerType, ResourceLocation texture, Component text) {
        if (PlatformUtils.getInstance().isClient()) {
            PickScreen.declareButtonSettings(containerType, texture, text);
        } else {
            throw new IllegalStateException("registerContainerButtonSettings is client only");
        }
    }

    @Override
    public void registerTieredBlock(AbstractStorageBlock block) {
        BLOCKS.put(new Pair<>(block.blockType(), block.blockTier()), block);
    }

    @Override
    public AbstractStorageBlock getTieredBlock(ResourceLocation blockType, ResourceLocation tier) {
        return BLOCKS.get(new Pair<>(blockType, tier));
    }

    @Override
    @ApiStatus.Internal
    public Map<ResourceLocation, Item> getAndClearItems() {
        final Map<ResourceLocation, Item> ITEMS = items;
        items = null;
        return ITEMS;
    }

    @Override
    public void offerTabIcon(Item tabIcon, int suitability) {
        if (this.suitability < suitability) {
            this.suitability = suitability;
            this.tabIcon = tabIcon;
        }
    }

    @Override
    @ApiStatus.Internal
    public ItemStack tabIcon() {
        return new ItemStack(tabIcon);
    }
}

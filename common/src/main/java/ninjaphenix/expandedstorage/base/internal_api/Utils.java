package ninjaphenix.expandedstorage.base.internal_api;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockBehaviour;
import ninjaphenix.expandedstorage.base.internal_api.tier.Tier;
import ninjaphenix.expandedstorage.base.platform.PlatformUtils;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Experimental
@Internal
public final class Utils {
    @Internal
    public static final String MOD_ID = "expandedstorage";
    public static final CreativeModeTab TAB = PlatformUtils.getInstance().createTab(ninjaphenix.expandedstorage.base.internal_api.BaseApi.getInstance()::tabIcon);
    @Internal
    public static final Component ALT_USE = new TranslatableComponent("tooltip.expandedstorage.alt_use",
            new KeybindComponent("key.sneak").withStyle(ChatFormatting.GOLD),
            new KeybindComponent("key.use").withStyle(ChatFormatting.GOLD));

    // Default tiers which all modules can, but don't need to, specify blocks for.
    // Note: each module should register their own implementation of these tiers specific to their blocks.
    public static final Tier WOOD_TIER = Tier.of(resloc("wood"), Tiers.WOOD.getLevel());
    public static final Tier IRON_TIER = Tier.of(resloc("iron"), Tiers.STONE.getLevel(), BlockBehaviour.Properties::requiresCorrectToolForDrops);
    public static final Tier GOLD_TIER = Tier.of(resloc("gold"), Tiers.IRON.getLevel(), BlockBehaviour.Properties::requiresCorrectToolForDrops);
    public static final Tier DIAMOND_TIER = Tier.of(resloc("diamond"), Tiers.IRON.getLevel(), BlockBehaviour.Properties::requiresCorrectToolForDrops);
    public static final Tier OBSIDIAN_TIER = Tier.of(resloc("obsidian"), Tiers.DIAMOND.getLevel(), BlockBehaviour.Properties::requiresCorrectToolForDrops);
    public static final Tier NETHERITE_TIER = Tier.of(resloc("netherite"), Tiers.NETHERITE.getLevel(), BlockBehaviour.Properties::requiresCorrectToolForDrops, Item.Properties::fireResistant);

    // Slots for Storage Tiers
    public static final int WOOD_STACK_COUNT = 27;
    public static final int IRON_STACK_COUNT = 54;
    public static final int GOLD_STACK_COUNT = 81;
    public static final int DIAMOND_STACK_COUNT = 108;
    public static final int OBSIDIAN_STACK_COUNT = 108;
    public static final int NETHERITE_STACK_COUNT = 135;

    // NBT Tag Types
    /**
     * @deprecated Removing in 1.17, in 1.17 use {@link net.minecraft.nbt.Tag.TAG_STRING} instead.
     */
    @Deprecated
    @ScheduledForRemoval(inVersion = "8 (MC=1.17)")
    public static final int NBT_STRING_TYPE = 8;

    /**
     * @deprecated Removing in 1.17, in 1.17 use {@link net.minecraft.nbt.Tag.TAG_COMPOUND} instead.
     */
    @Deprecated
    @ScheduledForRemoval(inVersion = "8 (MC=1.17)")
    public static final int NBT_COMPOUND_TYPE = 10;

    /**
     * @deprecated Removing in 1.17, in 1.17 use {@link net.minecraft.nbt.Tag.TAG_BYTE} instead.
     */
    @Deprecated
    @ScheduledForRemoval(inVersion = "8 (MC=1.17)")
    public static final int NBT_BYTE_TYPE = 1;

    /**
     * @deprecated Removing in 1.17, in 1.17 use {@link net.minecraft.nbt.Tag.TAG_LIST} instead.
     */
    @Deprecated
    @ScheduledForRemoval(inVersion = "8 (MC=1.17)")
    public static final int NBT_LIST_TYPE = 9;

    // Item Cooldown
    public static final int QUARTER_SECOND = 5;

    // Container Types
    @Internal
    public static final ResourceLocation UNSET_CONTAINER_TYPE = Utils.resloc("auto");

    @Internal
    public static final ResourceLocation SINGLE_CONTAINER_TYPE = Utils.resloc("single");

    @Internal
    public static final ResourceLocation PAGE_CONTAINER_TYPE = Utils.resloc("page");

    @Internal
    public static final ResourceLocation SCROLL_CONTAINER_TYPE = Utils.resloc("scroll");

    // Config paths
    @Internal
    public static final String FABRIC_LEGACY_CONFIG_PATH = "ninjaphenix-container-library.json";

    @Internal
    public static final String CONFIG_PATH = "expandedstorage.json";

    @Internal
    public static final int CONTAINER_HEADER_HEIGHT = 17;

    @Internal
    public static final int INVENTORY_HEADER_HEIGHT = 14;

    @Internal
    public static final int SLOT_SIZE = 18;

    @Internal
    public static final int CONTAINER_PADDING_WIDTH = 7;

    private Utils() {

    }

    @Internal
    public static ResourceLocation resloc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    @Internal
    public static MutableComponent translation(String key, Object... params) {
        return new TranslatableComponent(key, params);
    }

    @Internal
    public static <K, V> Map<K, V> unmodifiableMap(Consumer<Map<K, V>> initialiser) {
        Map<K, V> map = new HashMap<>();
        initialiser.accept(map);
        return Collections.unmodifiableMap(map);
    }

    public static <T> T getClassInstance(final Class<T> INTERFACE_CLASS, final String COMMON_PACKAGE_PATH, final String CLASS_NAME) {
        final String CLASS_LOADER = Utils.class.getClassLoader().getClass().getName();
        final String PLATFORM;
        if ("net.fabricmc.loader.launch.knot.KnotClassLoader".equals(CLASS_LOADER)) {
            PLATFORM = "fabric";
        } else if ("cpw.mods.modlauncher.TransformingClassLoader".equals(CLASS_LOADER)) {
            PLATFORM = "forge";
        } else {
            throw new IllegalStateException("Unable to find mod-loader.");
        }
        final String FULL_CLASS_PATH = COMMON_PACKAGE_PATH + "." + PLATFORM + "." + CLASS_NAME;
        try {
            final Class<?> CLASS = Class.forName(FULL_CLASS_PATH, false, Utils.class.getClassLoader());
            if (INTERFACE_CLASS.isAssignableFrom(CLASS)) {
                try {
                    //noinspection unchecked
                    return (T) CLASS.getMethod("getInstance").invoke(null);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new IllegalStateException("Cannot find, access or call " + FULL_CLASS_PATH + "#getInstance.");
                }
            } else {
                throw new IllegalStateException(FULL_CLASS_PATH + " should be an instance of " + INTERFACE_CLASS.getName());
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No class found " + FULL_CLASS_PATH + ".");
        }
    }
}

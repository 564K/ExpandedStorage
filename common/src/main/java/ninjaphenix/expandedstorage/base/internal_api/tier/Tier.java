package ninjaphenix.expandedstorage.base.internal_api.tier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.function.UnaryOperator;

@Experimental
@Internal
public class Tier {
    private final ResourceLocation KEY;
    private final UnaryOperator<Item.Properties> ITEM_PROPERTIES;
    private final UnaryOperator<BlockBehaviour.Properties> BLOCK_PROPERTIES;
    private final int MINING_LEVEL;

    protected Tier(final ResourceLocation KEY,
                   final int MINING_LEVEL,
                   final UnaryOperator<BlockBehaviour.Properties> BLOCK_PROPERTIES,
                   final UnaryOperator<Item.Properties> ITEM_PROPERTIES) {
        this.KEY = KEY;
        this.ITEM_PROPERTIES = ITEM_PROPERTIES;
        this.BLOCK_PROPERTIES = BLOCK_PROPERTIES;
        this.MINING_LEVEL = MINING_LEVEL;
    }

    public static Tier of(final ResourceLocation KEY,
                          final int MINING_LEVEL) {
        return new Tier(KEY, MINING_LEVEL, UnaryOperator.identity(), UnaryOperator.identity());
    }

    public static Tier of(final ResourceLocation KEY,
                          final int MINING_LEVEL,
                          final UnaryOperator<BlockBehaviour.Properties> BLOCK_PROPERTIES) {
        return new Tier(KEY, MINING_LEVEL, BLOCK_PROPERTIES, UnaryOperator.identity());
    }

    public static Tier of(final ResourceLocation KEY,
                          final int MINING_LEVEL,
                          final UnaryOperator<BlockBehaviour.Properties> BLOCK_PROPERTIES,
                          final UnaryOperator<Item.Properties> ITEM_PROPERTIES) {
        return new Tier(KEY, MINING_LEVEL, BLOCK_PROPERTIES, ITEM_PROPERTIES);
    }

    public final ResourceLocation key() {
        return KEY;
    }

    public final UnaryOperator<Item.Properties> itemProperties() {
        return ITEM_PROPERTIES;
    }

    public UnaryOperator<BlockBehaviour.Properties> blockProperties() {
        return BLOCK_PROPERTIES;
    }

    public final int miningLevel() {
        return MINING_LEVEL;
    }

    public final boolean requiresTool() {
        return MINING_LEVEL > 0;
    }
}

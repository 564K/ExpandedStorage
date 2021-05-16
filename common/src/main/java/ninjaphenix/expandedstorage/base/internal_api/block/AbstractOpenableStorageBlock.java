package ninjaphenix.expandedstorage.base.internal_api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ContainerMenuFactory;
import ninjaphenix.expandedstorage.base.platform.NetworkWrapper;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
@Internal
public abstract class AbstractOpenableStorageBlock extends AbstractStorageBlock implements EntityBlock, WorldlyContainerHolder {
    private final ResourceLocation OPEN_STAT;
    private final int SLOTS;

    public AbstractOpenableStorageBlock(final Properties PROPERTIES,
                                        final ResourceLocation BLOCK_ID,
                                        final ResourceLocation BLOCK_TIER,
                                        final ResourceLocation OPEN_STAT,
                                        final int SLOTS) {
        super(PROPERTIES, BLOCK_ID, BLOCK_TIER);
        this.OPEN_STAT = OPEN_STAT;
        this.SLOTS = SLOTS;
    }

    public final int getSlotCount() {
        return SLOTS;
    }

    public final Component getContainerName() {
        return new TranslatableComponent(this.getDescriptionId());
    }

    @Override
    @SuppressWarnings("deprecation")
    public final InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player instanceof ServerPlayer) {
            final ServerPlayer PLAYER = (ServerPlayer) player;
            final ContainerMenuFactory MENU_FACTORY = this.createContainerFactory(state, level, pos);
            if (MENU_FACTORY != null) {
                if (MENU_FACTORY.canPlayerOpen(PLAYER)) {
                    NetworkWrapper.getInstance().s2c_openMenu(PLAYER, MENU_FACTORY);
                    PLAYER.awardStat(OPEN_STAT);
                    PiglinAi.angerNearbyPiglins(PLAYER, true);
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean bl) {
        if (!state.is(newState.getBlock())) {
            final BlockEntity TEMP = level.getBlockEntity(pos);
            if (TEMP instanceof AbstractOpenableStorageBlockEntity) {
                Containers.dropContents(level, pos, ((AbstractOpenableStorageBlockEntity) TEMP));
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, bl);
        }
    }

    protected ContainerMenuFactory createContainerFactory(final BlockState STATE, final LevelAccessor LEVEL, final BlockPos POS) {
        final BlockEntity ENTITY = LEVEL.getBlockEntity(POS);
        if (!(ENTITY instanceof AbstractOpenableStorageBlockEntity)) {
            return null;
        }
        final AbstractOpenableStorageBlockEntity CONTAINER = (AbstractOpenableStorageBlockEntity) ENTITY;
        return new ContainerMenuFactory() {
            @Override
            public void writeClientData(final ServerPlayer PLAYER, final FriendlyByteBuf BUFFER) {
                BUFFER.writeBlockPos(POS).writeInt(CONTAINER.getContainerSize());
            }

            @Override
            public Component displayName() {
                return CONTAINER.getDisplayName();
            }

            @Override
            public boolean canPlayerOpen(final ServerPlayer PLAYER) {
                if (CONTAINER.canPlayerInteractWith(PLAYER)) {
                    return true;
                }
                AbstractStorageBlockEntity.alertBlockLocked(PLAYER, this.displayName());
                return false;
            }

            @Override
            public AbstractContainerMenu createMenu(final int WINDOW_ID, final Inventory PLAYER_INVENTORY, final Player PLAYER) {
                if (CONTAINER.canPlayerInteractWith(PLAYER) && CONTAINER.stillValid(PLAYER)) {
                    return NetworkWrapper.getInstance().createMenu(WINDOW_ID, CONTAINER.getBlockPos(), CONTAINER, PLAYER_INVENTORY, displayName());
                }
                return null;
            }
        };
    }

    @Override // Keep for hoppers.
    public WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        final BlockEntity TEMP = level.getBlockEntity(pos);
        if (TEMP instanceof AbstractOpenableStorageBlockEntity) {
            return (AbstractOpenableStorageBlockEntity) TEMP;
        }
        return null;
    }
}

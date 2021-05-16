package ninjaphenix.expandedstorage.base.internal_api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.DoubleBlockCombiner.NeighborCombineResult;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractStorageBlockEntity;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType;
import ninjaphenix.expandedstorage.base.internal_api.inventory.CompoundWorldlyContainer;
import ninjaphenix.expandedstorage.base.internal_api.inventory.ContainerMenuFactory;
import ninjaphenix.expandedstorage.base.platform.NetworkWrapper;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiPredicate;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType.*;

@Experimental
@Internal
public abstract class AbstractChestBlock<T extends AbstractOpenableStorageBlockEntity> extends AbstractOpenableStorageBlock {
    public static final EnumProperty<CursedChestType> CURSED_CHEST_TYPE = EnumProperty.create("type", CursedChestType.class);
    private final DoubleBlockCombiner.Combiner<T, Optional<WorldlyContainer>> CONTAINER_GETTER = new DoubleBlockCombiner.Combiner<T, Optional<WorldlyContainer>>() {
        @Override
        public Optional<WorldlyContainer> acceptDouble(final T FIRST, final T SECOND) {
            return Optional.of(new CompoundWorldlyContainer(FIRST, SECOND));
        }

        @Override
        public Optional<WorldlyContainer> acceptSingle(final T SINGLE) {
            return Optional.of(SINGLE);
        }

        @Override
        public Optional<WorldlyContainer> acceptNone() {
            return Optional.empty();
        }
    };

    private final DoubleBlockCombiner.Combiner<T, Optional<ContainerMenuFactory>> MENU_GETTER = new DoubleBlockCombiner.Combiner<T, Optional<ContainerMenuFactory>>() {
        @Override
        public Optional<ContainerMenuFactory> acceptDouble(final T FIRST, final T SECOND) {
            Component containerName = FIRST.hasCustomName() ? FIRST.getName() : SECOND.hasCustomName() ? SECOND.getName() : Utils.translation("container.expandedstorage.generic_double", FIRST.getName());
            CompoundWorldlyContainer container = new CompoundWorldlyContainer(FIRST, SECOND);
            return Optional.of(new ContainerMenuFactory() {
                @Override
                public void writeClientData(final ServerPlayer PLAYER, final FriendlyByteBuf BUFFER) {
                    BUFFER.writeBlockPos(FIRST.getBlockPos()).writeInt(container.getContainerSize());
                }

                @Override
                public Component displayName() {
                    return containerName;
                }

                @Override
                public boolean canPlayerOpen(final ServerPlayer PLAYER) {
                    if (FIRST.canPlayerInteractWith(PLAYER) && SECOND.canPlayerInteractWith(PLAYER)) {
                        return true;
                    }
                    AbstractStorageBlockEntity.alertBlockLocked(PLAYER, this.displayName());
                    return false;
                }

                @Override
                public AbstractContainerMenu createMenu(final int WINDOW_ID, final Inventory PLAYER_INVENTORY, final Player PLAYER) {
                    if (FIRST.stillValid(PLAYER) && SECOND.stillValid(PLAYER)) {
                        return NetworkWrapper.getInstance().createMenu(WINDOW_ID, FIRST.getBlockPos(), container, PLAYER_INVENTORY, containerName);
                    }
                    return null;
                }
            });
        }

        @Override
        public Optional<ContainerMenuFactory> acceptSingle(final T SINGLE) {
            Component containerName = SINGLE.getName();
            return Optional.of(new ContainerMenuFactory() {
                @Override
                public void writeClientData(final ServerPlayer PLAYER, final FriendlyByteBuf BUFFER) {
                    BUFFER.writeBlockPos(SINGLE.getBlockPos()).writeInt(SINGLE.getContainerSize());
                }

                @Override
                public Component displayName() {
                    return containerName;
                }

                @Override
                public boolean canPlayerOpen(final ServerPlayer PLAYER) {
                    if (SINGLE.canPlayerInteractWith(PLAYER)) {
                        return true;
                    }
                    AbstractStorageBlockEntity.alertBlockLocked(PLAYER, this.displayName());
                    return false;
                }

                @Override
                public AbstractContainerMenu createMenu(final int WINDOW_ID, final Inventory PLAYER_INVENTORY, final Player PLAYER) {
                    if (SINGLE.stillValid(PLAYER)) {
                        return NetworkWrapper.getInstance().createMenu(WINDOW_ID, SINGLE.getBlockPos(), SINGLE, PLAYER_INVENTORY, containerName);
                    }
                    return null;
                }
            });
        }

        @Override
        public Optional<ContainerMenuFactory> acceptNone() {
            return Optional.empty();
        }
    };

    public AbstractChestBlock(Properties properties, ResourceLocation blockId, ResourceLocation blockTier,
                              ResourceLocation openStat, int slots) {
        super(properties, blockId, blockTier, openStat, slots);
        this.registerDefaultState(this.getStateDefinition().any().setValue(AbstractChestBlock.CURSED_CHEST_TYPE, SINGLE)
                .setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    public static Direction getDirectionToAttached(BlockState state) {
        CursedChestType value = state.getValue(CURSED_CHEST_TYPE);
        if (value == TOP) {
            return Direction.DOWN;
        } else if (value == BACK) {
            return state.getValue(HORIZONTAL_FACING);
        } else if (value == RIGHT) {
            return state.getValue(HORIZONTAL_FACING).getClockWise();
        } else if (value == BOTTOM) {
            return Direction.UP;
        } else if (value == FRONT) {
            return state.getValue(HORIZONTAL_FACING).getOpposite();
        } else if (value == LEFT) {
            return state.getValue(HORIZONTAL_FACING).getCounterClockWise();
        }
        throw new IllegalArgumentException("BaseChestBlock#getDirectionToAttached received an unexpected state.");
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState state) {
        switch (state.getValue(CURSED_CHEST_TYPE)) {
            case TOP:
            case LEFT:
            case FRONT:
                return DoubleBlockCombiner.BlockType.FIRST;
            case BACK:
            case RIGHT:
            case BOTTOM:
                return DoubleBlockCombiner.BlockType.SECOND;
            default:
                return DoubleBlockCombiner.BlockType.SINGLE;
        }
    }

    public static CursedChestType getChestType(Direction facing, Direction offset) {
        if (facing.getClockWise() == offset) {
            return RIGHT;
        } else if (facing.getCounterClockWise() == offset) {
            return LEFT;
        } else if (facing == offset) {
            return BACK;
        } else if (facing == offset.getOpposite()) {
            return FRONT;
        } else if (offset == Direction.DOWN) {
            return TOP;
        } else if (offset == Direction.UP) {
            return CursedChestType.BOTTOM;
        }
        return SINGLE;
    }

    @Override
    protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AbstractChestBlock.CURSED_CHEST_TYPE);
        builder.add(HORIZONTAL_FACING);
        appendAdditionalStateDefinitions(builder);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        CursedChestType chestType = SINGLE;
        final Direction direction_1 = context.getHorizontalDirection().getOpposite();
        final Direction direction_2 = context.getClickedFace();
        if (context.isSecondaryUseActive()) {
            final BlockState state;
            if (direction_2.getAxis().isVertical()) {
                state = level.getBlockState(pos.relative(direction_2.getOpposite()));
                if (state.getBlock() == this && state.getValue(CURSED_CHEST_TYPE) == SINGLE) {
                    Direction direction_3 = state.getValue(HORIZONTAL_FACING);
                    if (direction_3.getAxis() != direction_2.getAxis() && direction_3 == direction_1) {
                        chestType = direction_2 == Direction.UP ? TOP : CursedChestType.BOTTOM;
                    }
                }
            } else {
                Direction offsetDir = direction_2.getOpposite();
                final BlockState clickedBlock = level.getBlockState(pos.relative(offsetDir));
                if (clickedBlock.getBlock() == this && clickedBlock.getValue(CURSED_CHEST_TYPE) == SINGLE) {
                    if (clickedBlock.getValue(HORIZONTAL_FACING) == direction_2 && clickedBlock.getValue(HORIZONTAL_FACING) == direction_1) {
                        chestType = FRONT;
                    } else {
                        state = level.getBlockState(pos.relative(direction_2.getOpposite()));
                        if (state.getValue(HORIZONTAL_FACING).get2DDataValue() < 2) {
                            offsetDir = offsetDir.getOpposite();
                        }
                        if (direction_1 == state.getValue(HORIZONTAL_FACING)) {
                            chestType = (offsetDir == Direction.WEST || offsetDir == Direction.NORTH) ? LEFT : RIGHT;
                        }
                    }
                }
            }
        } else {
            for (final Direction dir : Direction.values()) {
                final BlockState state = level.getBlockState(pos.relative(dir));
                if (state.getBlock() != this || state.getValue(CURSED_CHEST_TYPE) != SINGLE || state.getValue(HORIZONTAL_FACING) != direction_1) {
                    continue;
                }
                final CursedChestType type = getChestType(direction_1, dir);
                if (type != SINGLE) {
                    chestType = type;
                    break;
                }
            }
        }
        return defaultBlockState().setValue(HORIZONTAL_FACING, direction_1).setValue(CURSED_CHEST_TYPE, chestType);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction offset, BlockState offsetState, LevelAccessor level,
                                  BlockPos pos, BlockPos offsetPos) {
        final DoubleBlockCombiner.BlockType mergeType = getBlockType(state);
        if (mergeType == DoubleBlockCombiner.BlockType.SINGLE) {
            final Direction facing = state.getValue(HORIZONTAL_FACING);
            if (!offsetState.hasProperty(CURSED_CHEST_TYPE)) {
                return state.setValue(CURSED_CHEST_TYPE, SINGLE);
            }
            final CursedChestType newType = getChestType(facing, offset);
            if (offsetState.getValue(CURSED_CHEST_TYPE) == newType.getOpposite() && facing == offsetState.getValue(HORIZONTAL_FACING)) {
                return state.setValue(CURSED_CHEST_TYPE, newType);
            }
        } else if (level.getBlockState(pos.relative(getDirectionToAttached(state))).getBlock() != this) {
            return state.setValue(CURSED_CHEST_TYPE, SINGLE);
        }
        return super.updateShape(state, offset, offsetState, level, pos, offsetPos);
    }

    protected void appendAdditionalStateDefinitions(StateDefinition.Builder<Block, BlockState> builder) {

    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    public final NeighborCombineResult<? extends T> combine(BlockState state, LevelAccessor level, BlockPos pos, boolean alwaysOpen) {
        BiPredicate<LevelAccessor, BlockPos> isChestBlocked = alwaysOpen ? (_level, _pos) -> false : this::isBlocked;
        return DoubleBlockCombiner.combineWithNeigbour(blockEntityType(), AbstractChestBlock::getBlockType,
                AbstractChestBlock::getDirectionToAttached, HORIZONTAL_FACING, state, level, pos,
                isChestBlocked);
    }

    protected abstract BlockEntityType<T> blockEntityType();

    protected boolean isBlocked(LevelAccessor level, BlockPos pos) {
        return false;
    }

    @Override
    protected ContainerMenuFactory createContainerFactory(final BlockState STATE, final LevelAccessor LEVEL, final BlockPos POS) {
        return combine(STATE, LEVEL, POS, false).apply(MENU_GETTER).orElse(null);
    }

    @Override // Keep for hoppers.
    public WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        return combine(state, level, pos, true).apply(CONTAINER_GETTER).orElse(null);
    }
}

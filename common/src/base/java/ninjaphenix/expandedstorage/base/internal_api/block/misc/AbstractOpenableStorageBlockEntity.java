package ninjaphenix.expandedstorage.base.internal_api.block.misc;

import com.google.common.base.Suppliers;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractOpenableStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.internal_api.inventory.CompoundWorldlyContainer;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

@Internal
@Experimental
public abstract class AbstractOpenableStorageBlockEntity extends AbstractStorageBlockEntity implements ICapabilityProvider {
    private final ResourceLocation blockId;
    protected Component containerName;
    private int slots;
    private NonNullList<ItemStack> inventory;
    private int[] slotsForFace;
    private LazyOptional<IItemHandler> itemHandler;
    private final Supplier<Container> container = Suppliers.memoize(() -> new Container() {
        @Override
        public int getContainerSize() {
            return slots;
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack stack : inventory) {
                if (!stack.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return inventory.get(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack stack = ContainerHelper.removeItem(inventory, slot, amount);
            if (!stack.isEmpty()) {
                this.setChanged();
            }
            return stack;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ContainerHelper.takeItem(inventory, slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            inventory.set(slot, stack);
            if (stack.getCount() > this.getMaxStackSize()) {
                stack.setCount(this.getMaxStackSize());
            }
            this.setChanged();
        }

        @Override
        public void setChanged() {
            AbstractOpenableStorageBlockEntity.this.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            return AbstractOpenableStorageBlockEntity.this.canContinueUse(player);
        }

        @Override
        public void clearContent() {
            inventory.clear();
        }
    });

    public AbstractOpenableStorageBlockEntity(BlockEntityType<?> blockEntityType, ResourceLocation blockId) {
        super(blockEntityType);
        this.blockId = blockId;
        if (blockId != null) {
            this.initialise(blockId);
        }
    }

    protected static int countViewers(Level level, Container container, int x, int y, int z) {
        return level.getEntitiesOfClass(Player.class, new AABB(x - 5, y - 5, z - 5, x + 6, y + 6, z + 6)).stream()
                    .filter(player -> player.containerMenu instanceof AbstractContainerMenu_<?>)
                    .map(player -> ((AbstractContainerMenu_<?>) player.containerMenu).getContainer())
                    .filter(openContainer -> openContainer == container ||
                            openContainer instanceof CompoundContainer compoundContainer && compoundContainer.contains(container))
                    .mapToInt(inv -> 1).sum();
    }

    public Container getContainerWrapper() {
        return container.get();
    }


    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (itemHandler == null) {
                itemHandler = LazyOptional.of(this::createItemHandler);
                return itemHandler.cast();
            }
        }
        return super.getCapability(capability, side);
    }

    @NotNull
    private IItemHandler createItemHandler() {
        return new IItemHandler() {
            @Override
            public int getSlots() {
                return slots;
            }

            @NotNull
            @Override
            public ItemStack getStackInSlot(int slot) {
                return inventory.get(slot);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack item, boolean simulate) {
                return ItemStack.EMPTY; // todo: implement
            }

            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY; // todo: implement
            }

            @Override
            public int getSlotLimit(int i) {
                return 64;
            }

            @Override
            public boolean isItemValid(int i, @NotNull ItemStack arg) {
                return true;
            }
        };
    }

    private void initialise(ResourceLocation blockId) {
        if (Registry.BLOCK.get(blockId) instanceof AbstractOpenableStorageBlock block) {
            slots = block.getSlotCount();
            slotsForFace = new int[slots];
            Arrays.setAll(slotsForFace, IntUnaryOperator.identity());
            inventory = NonNullList.withSize(slots, ItemStack.EMPTY);
            containerName = block.getContainerName();
        }
    }

    @Override
    public Component getDefaultName() {
        return containerName;
    }

    public final ResourceLocation getBlockId() {
        return blockId;
    }

    @Override
    public void load(BlockState state, CompoundTag tag) {
        super.load(state, tag);
        if (state.getBlock() instanceof AbstractOpenableStorageBlock block) {
            this.initialise(block.blockId());
            ContainerHelper.loadAllItems(tag, inventory);
        } else {
            throw new IllegalStateException("Block Entity attached to wrong block.");
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        ContainerHelper.saveAllItems(tag, inventory);
        return tag;
    }

    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    public int getItemCount() {
        return slots;
    }

    public boolean canContinueUse(Player player) {
        return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(Vec3.atCenterOf(worldPosition)) <= 64;
    }
}

package ninjaphenix.expandedstorage.base.internal_api.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
@Internal
public interface ClientContainerMenuFactory<T extends AbstractContainerMenu> {
    T create(final int WINDOW_ID, final Inventory INVENTORY, final FriendlyByteBuf BUFFER);
}

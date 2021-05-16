package ninjaphenix.expandedstorage.base.internal_api.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
@Internal
public interface ContainerMenuFactory {
    void writeClientData(final ServerPlayer PLAYER, final FriendlyByteBuf BUFFER);

    Component displayName();

    boolean canPlayerOpen(final ServerPlayer PLAYER);

    AbstractContainerMenu createMenu(final int WINDOW_ID, final Inventory PLAYER_INVENTORY, final Player PLAYER);
}

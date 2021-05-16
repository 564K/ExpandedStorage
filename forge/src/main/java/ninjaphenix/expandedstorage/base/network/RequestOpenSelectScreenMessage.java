package ninjaphenix.expandedstorage.base.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import ninjaphenix.expandedstorage.base.internal_api.inventory.AbstractContainerMenu_;
import ninjaphenix.expandedstorage.base.platform.NetworkWrapper;

import java.util.function.Supplier;

public final class RequestOpenSelectScreenMessage {
    public static void encode(RequestOpenSelectScreenMessage message, FriendlyByteBuf buffer) {

    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static RequestOpenSelectScreenMessage decode(FriendlyByteBuf buffer) {
        return new RequestOpenSelectScreenMessage();
    }

    public static void handle(RequestOpenSelectScreenMessage message, Supplier<NetworkEvent.Context> wrappedContext) {
        final NetworkEvent.Context context = wrappedContext.get();
        final ServerPlayer player = context.getSender();
        if (player != null) {
            final AbstractContainerMenu CONTAINER_MENU = player.containerMenu;
            if (CONTAINER_MENU instanceof AbstractContainerMenu_<?>) {
                final AbstractContainerMenu_<?> MENU = (AbstractContainerMenu_<?>) CONTAINER_MENU;
                context.enqueueWork(() -> NetworkWrapper.getInstance().s2c_openSelectScreen(player, (type) -> NetworkHooks.openGui(player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return MENU.getDisplayName();
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player1) {
                        return NetworkWrapper.getInstance().createMenu(windowId, MENU.POS, MENU.getContainer(), inventory, MENU.getDisplayName());
                    }
                }, buffer -> buffer.writeBlockPos(MENU.POS).writeInt(MENU.getContainer().getContainerSize()))));
            } else {
                context.enqueueWork(() -> NetworkWrapper.getInstance().s2c_openSelectScreen(player, null));
            }
            context.setPacketHandled(true);
        }
    }
}
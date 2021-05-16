package ninjaphenix.expandedstorage.base.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import ninjaphenix.expandedstorage.base.internal_api.BaseApi;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractStorageBlock;
import ninjaphenix.expandedstorage.base.internal_api.item.MutationBehaviour;
import ninjaphenix.expandedstorage.base.internal_api.item.MutationMode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class StorageMutator extends Item {
    public static final String BEHAVIOUR_DATA_KEY = "behaviour_data";
    private static final MutationMode DEFAULT_MODE = MutationMode.MERGE;
    private static final String BEHAVIOUR_KEY = "behaviour";
    private static final String MODE_KEY = "mode";

    public StorageMutator(Properties properties) {
        super(properties);
    }

    private static void setMode(final CompoundTag TAG, @Nullable MutationMode MODE) {
        if (MODE == null) {
            MODE = StorageMutator.DEFAULT_MODE;
        }
        TAG.putString(StorageMutator.MODE_KEY, MODE.toString());
        TAG.remove(BEHAVIOUR_DATA_KEY);
    }

    private static MutationMode getMode(final CompoundTag TAG) {
        if (TAG.contains(StorageMutator.MODE_KEY, Utils.NBT_BYTE_TYPE)) {
            StorageMutator.setMode(TAG, MutationMode.from(TAG.getByte(StorageMutator.MODE_KEY)));
        }
        if (TAG.contains(StorageMutator.MODE_KEY, Utils.NBT_STRING_TYPE)) {
            return MutationMode.from(TAG.getString(StorageMutator.MODE_KEY));
        }
        return StorageMutator.DEFAULT_MODE;
    }

    @Override
    public InteractionResult useOn(final UseOnContext CONTEXT) {
        CompoundTag tag = CONTEXT.getItemInHand().getOrCreateTag();
        Level level = CONTEXT.getLevel();
        Player player = CONTEXT.getPlayer();
        if (true) {
            player.displayClientMessage(new TextComponent("Storage Mutator is not yet implemented"), true);
            return InteractionResult.FAIL;
        }
        if (player != null) {
            player.getCooldowns().addCooldown(CONTEXT.getItemInHand().getItem(), Utils.QUARTER_SECOND);
        }
        Block block = level.getBlockState(CONTEXT.getClickedPos()).getBlock();
        if (block instanceof AbstractStorageBlock) {
            String behaviourKey = ((AbstractStorageBlock) block).blockType().toString();
            if (tag.contains(StorageMutator.BEHAVIOUR_KEY, Utils.NBT_STRING_TYPE)) { // Try continue behaviour usage
                String readBehaviour = tag.getString(StorageMutator.BEHAVIOUR_KEY);
                if (readBehaviour.equals(behaviourKey)) { // Continue behaviour usage
                    MutationBehaviour behaviour = BaseApi.getInstance().getMutationBehaviour(readBehaviour);
                    if (behaviour == null) { // Cannot find behaviour, module un-installed
                        clearBehaviour(tag);
                        return InteractionResult.FAIL;
                    } else { // Continue using behaviour
                        InteractionResult result = behaviour.continueUseOn(CONTEXT, StorageMutator.getMode(tag));
                        if (result == InteractionResult.PASS) {
                            return InteractionResult.sidedSuccess(level.isClientSide());
                        }
                        clearBehaviour(tag);
                        return result;
                    }
                } else { // User clicked on different block than current behaviour
                    clearBehaviour(tag);
                    return startNewMutation(CONTEXT, tag, level.isClientSide(), behaviourKey);
                }
            } else { // Start behaviour
                return startNewMutation(CONTEXT, tag, level.isClientSide(), behaviourKey);
            }
        } else { // Storage mutator clicked on non-compatible block
            return InteractionResult.FAIL;
        }
    }

    private InteractionResult startNewMutation(final UseOnContext CONTEXT, final CompoundTag TAG, final boolean IS_CLIENT, final String BEHAVIOUR_NAME) {
        MutationBehaviour behaviour = BaseApi.getInstance().getMutationBehaviour(BEHAVIOUR_NAME);
        if (behaviour != null) {
            InteractionResult result = behaviour.startUseOn(CONTEXT, StorageMutator.getMode(TAG));
            if (!IS_CLIENT && result == InteractionResult.SUCCESS) {
                TAG.putString(StorageMutator.BEHAVIOUR_KEY, BEHAVIOUR_NAME);
            }
            return InteractionResult.sidedSuccess(IS_CLIENT);
        } else {
            // no swing, cannot use on block
            return InteractionResult.FAIL;
        }
    }

    private void clearBehaviour(CompoundTag tag) {
        tag.remove(StorageMutator.BEHAVIOUR_KEY);
        tag.remove(StorageMutator.BEHAVIOUR_DATA_KEY);
    }

    @Override
    protected String getOrCreateDescriptionId() {
        return "item.expandedstorage.storage_mutator";
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            CompoundTag tag = stack.getOrCreateTag();
            MutationMode mode = StorageMutator.getMode(tag).next();
            setMode(tag, mode);
            player.displayClientMessage(getToolModeComponent(mode), true);
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    private MutableComponent getToolModeComponent(MutationMode mode) {
        return new TranslatableComponent("tooltip.expandedstorage.storage_mutator.tool_mode",
                new TranslatableComponent("tooltip.expandedstorage.storage_mutator." + mode));
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        StorageMutator.setMode(stack.getOrCreateTag(), null);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        MutationMode mode = StorageMutator.getMode(stack.getOrCreateTag());
        list.add(getToolModeComponent(mode).withStyle(ChatFormatting.GRAY));
        list.add(Utils.translation("tooltip.expandedstorage.storage_mutator.description_" + mode, Utils.ALT_USE).withStyle(ChatFormatting.GRAY));
    }
}

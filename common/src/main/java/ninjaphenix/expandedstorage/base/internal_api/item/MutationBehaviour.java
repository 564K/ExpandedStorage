package ninjaphenix.expandedstorage.base.internal_api.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.item.StorageMutator;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
@Internal
public interface MutationBehaviour {
    InteractionResult startUseOn(UseOnContext context, MutationMode mode);

    InteractionResult continueUseOn(UseOnContext context, MutationMode mode);

    default CompoundTag getOrCreateData(CompoundTag tag) {
        if (tag.contains(StorageMutator.BEHAVIOUR_DATA_KEY, Utils.NBT_COMPOUND_TYPE)) {
            return tag.getCompound(StorageMutator.BEHAVIOUR_DATA_KEY);
        }
        CompoundTag data = new CompoundTag();
        tag.put(StorageMutator.BEHAVIOUR_DATA_KEY, data);
        return data;
    }
}

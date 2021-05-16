package ninjaphenix.expandedstorage.old_chest.block.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.AbstractOpenableStorageBlockEntity;

public final class OldChestBlockEntity extends AbstractOpenableStorageBlockEntity {
    public OldChestBlockEntity(final BlockEntityType<OldChestBlockEntity> BLOCK_ENTITY_TYPE, final ResourceLocation BLOCK_ID) {
        super(BLOCK_ENTITY_TYPE, BLOCK_ID);
    }
}

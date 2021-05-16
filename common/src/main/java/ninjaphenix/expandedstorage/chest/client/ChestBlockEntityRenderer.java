package ninjaphenix.expandedstorage.chest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType;
import ninjaphenix.expandedstorage.chest.block.ChestBlock;
import ninjaphenix.expandedstorage.chest.block.misc.ChestBlockEntity;
import ninjaphenix.expandedstorage.chest.internal_api.ChestApi;

import java.util.Map;

public final class ChestBlockEntityRenderer extends BlockEntityRenderer<ChestBlockEntity> {
    private static final BlockState DEFAULT_STATE = Registry.BLOCK.get(Utils.resloc("wood_chest")).defaultBlockState();

    private static final Map<CursedChestType, SingleChestModel> MODELS = Utils.unmodifiableMap(map -> {
        map.put(CursedChestType.SINGLE, new SingleChestModel());
        map.put(CursedChestType.FRONT, new FrontChestModel());
        map.put(CursedChestType.BACK, new BackChestModel());
        map.put(CursedChestType.TOP, new TopChestModel());
        map.put(CursedChestType.BOTTOM, new BottomChestModel());
        map.put(CursedChestType.LEFT, new LeftChestModel());
        map.put(CursedChestType.RIGHT, new RightChestModel());
    });
    private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction> LID_OPENNESS_FUNCTION_GETTER = new DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction>() {
        @Override
        public Float2FloatFunction acceptDouble(final ChestBlockEntity FIRST, final ChestBlockEntity SECOND) {
            return (DELTA) -> Math.max(FIRST.getLidOpenness(DELTA), SECOND.getLidOpenness(DELTA));
        }

        @Override
        public Float2FloatFunction acceptSingle(final ChestBlockEntity SINGLE) {
            return SINGLE::getLidOpenness;
        }

        @Override
        public Float2FloatFunction acceptNone() { // Should be an illegal case, but we'll provide a fallback just in case.
            return (DELTA) -> DELTA;
        }
    };

    public ChestBlockEntityRenderer(final BlockEntityRenderDispatcher DISPATCHER) {
        super(DISPATCHER);
    }

    @Override
    public void render(final ChestBlockEntity ENTITY, final float DELTA, final PoseStack STACK, final MultiBufferSource SOURCE, final int LIGHT, final int OVERLAY) {
        final ResourceLocation BLOCK_ID = ENTITY.getBlockId();
        final BlockState STATE = ENTITY.hasLevel() ? ENTITY.getBlockState() :
                ChestBlockEntityRenderer.DEFAULT_STATE.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        final Block TEMP = STATE.getBlock();
        if (BLOCK_ID == null || !(TEMP instanceof ChestBlock)) {
            return;
        }
        final ChestBlock BLOCK = (ChestBlock) TEMP;
        final CursedChestType CHEST_TYPE = STATE.getValue(AbstractChestBlock.CURSED_CHEST_TYPE);
        final SingleChestModel MODEL = ChestBlockEntityRenderer.MODELS.get(CHEST_TYPE);
        STACK.pushPose();
        STACK.translate(0.5D, 0.5D, 0.5D);
        STACK.mulPose(Vector3f.YP.rotationDegrees(-STATE.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
        STACK.translate(-0.5D, -0.5D, -0.5D);
        final DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> COMPOUND_PROPERTY_ACCESSOR = ENTITY.hasLevel() ?
                BLOCK.combine(STATE, ENTITY.getLevel(), ENTITY.getBlockPos(), true) :
                DoubleBlockCombiner.Combiner::acceptNone;
        final VertexConsumer CONSUMER = new Material(Sheets.CHEST_SHEET, ChestApi.INSTANCE.getChestTexture(BLOCK_ID, CHEST_TYPE)).buffer(SOURCE, RenderType::entityCutout);
        final float LID_OPENNESS = COMPOUND_PROPERTY_ACCESSOR.apply(ChestBlockEntityRenderer.LID_OPENNESS_FUNCTION_GETTER).get(DELTA);
        final int BRIGHTNESS = COMPOUND_PROPERTY_ACCESSOR.apply(new BrightnessCombiner<>()).applyAsInt(LIGHT);
        MODEL.setLidPitch(LID_OPENNESS);
        MODEL.render(STACK, CONSUMER, BRIGHTNESS, OVERLAY);
        STACK.popPose();
    }
}

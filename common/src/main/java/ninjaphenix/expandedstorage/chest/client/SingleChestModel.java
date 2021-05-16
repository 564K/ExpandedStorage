package ninjaphenix.expandedstorage.chest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public class SingleChestModel extends Model {
    protected final ModelPart LID;
    protected final ModelPart BASE;

    public SingleChestModel(final int textureWidth, final int textureHeight) {
        super(RenderType::entityCutout);
        texWidth = textureWidth;
        texHeight = textureHeight;
        LID = new ModelPart(this, 0, 0);
        BASE = new ModelPart(this, 0, 19);
    }

    public SingleChestModel() {
        this(64, 48);
        LID.addBox(0, 0, 0, 14, 5, 14, 0);
        LID.addBox(6, -2, 14, 2, 4, 1, 0);
        LID.setPos(1, 9, 1);
        BASE.addBox(0, 0, 0, 14, 10, 14, 0);
        BASE.setPos(1, 0, 1);
    }

    public final void setLidPitch(float pitch) {
        float p = 1.0f - pitch;
        LID.xRot = -((1.0F - p * p * p) * 1.5707964F);
    }

    public final void render(final PoseStack stack, final VertexConsumer consumer, final int i, final int j) {
        this.renderToBuffer(stack, consumer, i, j, 1, 1, 1, 1);
    }

    @Override
    public final void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, float r, float g, float b, float f) {
        BASE.render(stack, consumer, light, overlay);
        LID.render(stack, consumer, light, overlay);
    }
}

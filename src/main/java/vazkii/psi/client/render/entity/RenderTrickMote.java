package vazkii.psi.client.render.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import vazkii.psi.common.entity.EntityTrickMote;

public class RenderTrickMote extends EntityRenderer<EntityTrickMote> {

    public RenderTrickMote(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(EntityTrickMote livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(EntityTrickMote entity) {
        return null;
    }
}

package vazkii.psi.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.ICAD;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.ISpellImmune;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.Psi;
import vazkii.psi.common.lib.LibEntityNames;
import vazkii.psi.common.lib.LibResources;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class EntityTrickMote extends Entity implements ISpellImmune {
    @ObjectHolder(registryName = "minecraft:entity_type", value = LibResources.PREFIX_MOD + LibEntityNames.TRICK_MOTE)
    public static EntityType<EntityTrickMote> TYPE;

    private static final int LIVE_TIME = 200;
    private static final int PARTICLE_COUNT = 10;

    private static final String TAG_COLORIZER = "colorizer";
    private static final String TAG_TIME_ALIVE = "timeAlive";
    private static final String TAG_DST_X = "dstX";
    private static final String TAG_DST_Y = "dstY";
    private static final String TAG_DST_Z = "dstZ";
    private static final String TAG_TRACK_SPEED = "trackSpeed";

    public static final EntityDataAccessor<ItemStack> COLORIZER_DATA = SynchedEntityData.defineId(EntityTrickMote.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> TIME_ALIVE = SynchedEntityData.defineId(EntityTrickMote.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DST_X = SynchedEntityData.defineId(EntityTrickMote.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DST_Y = SynchedEntityData.defineId(EntityTrickMote.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DST_Z = SynchedEntityData.defineId(EntityTrickMote.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRACK_SPEED = SynchedEntityData.defineId(EntityTrickMote.class, EntityDataSerializers.FLOAT);

    private Runnable effect;
    private Supplier<Vec3> tracker;

    public EntityTrickMote(EntityType<?> type, Level worldIn) {
        super(type, worldIn);
    }

    public static EntityTrickMote create(SpellContext context, BlockPos target, Runnable effect) {
        Vec3 targetPos = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        return create(context, effect, () -> targetPos);
    }

    public static EntityTrickMote create(SpellContext context, Vector3 target, Runnable effect) {
        Vec3 targetPos = new Vec3(target.x, target.y, target.z);
        return create(context, effect, () -> targetPos);
    }

    public static EntityTrickMote create(SpellContext context, Entity target, Runnable effect) {
        return create(context, effect, target::position);
    }

    public static EntityTrickMote create(SpellContext context, Runnable effect, Supplier<Vec3> tracker) {
        ItemStack cad = PsiAPI.getPlayerCAD(context.caster);
        ItemStack colorizer = cad.isEmpty() ? ItemStack.EMPTY : ((ICAD) cad.getItem()).getComponentInSlot(cad, EnumCADComponent.DYE);
        Vec3 pos = context.focalPoint.position().add(0, context.focalPoint instanceof Player ? context.focalPoint.getEyeHeight() : 0, 0);
        Vec3 targetPos = tracker.get();
        Vec3 delta = targetPos.subtract(pos);
        float speed = 1;
        if (delta.lengthSqr() <= 0.1 * 0.1) {
            vfx(pos, delta, colorizer);
            effect.run();
            return null;
        }
        EntityTrickMote mote = new EntityTrickMote(TYPE, context.focalPoint.level);
        mote.entityData.set(COLORIZER_DATA, colorizer);
        mote.setDst(targetPos);
        mote.entityData.set(TRACK_SPEED, speed);
        mote.effect = effect;
        mote.tracker = tracker;
        mote.setPos(pos);
        context.focalPoint.level.addFreshEntity(mote);
        return mote;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(COLORIZER_DATA, ItemStack.EMPTY);
        entityData.define(TIME_ALIVE, 0);
        entityData.define(DST_X, 0F);
        entityData.define(DST_Y, 0F);
        entityData.define(DST_Z, 0F);
        entityData.define(TRACK_SPEED, 1F);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag tagCompound) {
        CompoundTag colorizerCmp = new CompoundTag();
        ItemStack colorizer = entityData.get(COLORIZER_DATA);
        if(!colorizer.isEmpty()) colorizerCmp = colorizer.save(colorizerCmp);
        tagCompound.put(TAG_COLORIZER, colorizerCmp);

        tagCompound.putInt(TAG_TIME_ALIVE, getTimeAlive());

        tagCompound.putFloat(TAG_DST_X, entityData.get(DST_X));
        tagCompound.putFloat(TAG_DST_Y, entityData.get(DST_Y));
        tagCompound.putFloat(TAG_DST_Z, entityData.get(DST_Z));
        tagCompound.putFloat(TAG_TRACK_SPEED, entityData.get(TRACK_SPEED));
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag tagCompound) {
        CompoundTag colorizerCmp = tagCompound.getCompound(TAG_COLORIZER);
        ItemStack colorizer = ItemStack.of(colorizerCmp);
        entityData.set(COLORIZER_DATA, colorizer);

        setTimeAlive(tagCompound.getInt(TAG_TIME_ALIVE));

        entityData.set(DST_X, tagCompound.getFloat(TAG_DST_X));
        entityData.set(DST_Y, tagCompound.getFloat(TAG_DST_Y));
        entityData.set(DST_Z, tagCompound.getFloat(TAG_DST_Z));
        entityData.set(TRACK_SPEED, tagCompound.getFloat(TAG_TRACK_SPEED));
    }

    @Override
    public void tick() {
        super.tick();

        int timeAlive = getTimeAlive();
        if(timeAlive > LIVE_TIME) discard();
        setTimeAlive(timeAlive + 1);

        if(level.isClientSide) {
            ItemStack colorizer = entityData.get(COLORIZER_DATA);
            vfx(position(), getDeltaMovement(), colorizer);
        } else {
            if (tracker == null || effect == null) {
                discard();
                return;
            }
            Vec3 dst = updateDst();
            if(dst.distanceToSqr(getX(), getY(), getZ()) < 0.1 * 0.1) {
                effect.run();
                discard();
            }
        }

        homeTowardsDst();
    }
    
    private static void vfx(Vec3 pos, Vec3 motion, ItemStack colorizer) {
        int colorVal = Psi.proxy.getColorForColorizer(colorizer);
        float r = PsiRenderHelper.r(colorVal) / 255F;
        float g = PsiRenderHelper.g(colorVal) / 255F;
        float b = PsiRenderHelper.b(colorVal) / 255F;
        
        for(int i = 0; i < PARTICLE_COUNT; i++) {
            double t = Math.random();
            Vec3 p = pos.add(motion.scale(t));
            float dx = (float) (Math.random() - 0.5f) * 0.025f;
            float dy = (float) (Math.random() - 0.5f) * 0.025f;
            float dz = (float) (Math.random() - 0.5f) * 0.025f;
            Psi.proxy.sparkleFX(p.x, p.y, p.z, r, g, b, dx, dy, dz, 1.2f, 15);
        }
    }

    private Vec3 updateDst() {
        Vec3 dst = tracker.get();
        setDst(dst);
        return dst;
    }
    
    private void setDst(Vec3 dst) {
        entityData.set(DST_X, (float) dst.x);
        entityData.set(DST_Y, (float) dst.y);
        entityData.set(DST_Z, (float) dst.z);
    }

    private void homeTowardsDst() {
        Vec3 dst = new Vec3(entityData.get(DST_X), entityData.get(DST_Y), entityData.get(DST_Z));
        Vec3 delta = dst.subtract(getX(), getY(), getZ());
        float speed = entityData.get(TRACK_SPEED);
        Vec3 motion = delta.lengthSqr() > speed * speed ? delta.normalize().scale(speed) : delta;
        setDeltaMovement(motion);
        setPos(position().add(motion));
    }

    public int getTimeAlive() {
        return entityData.get(TIME_ALIVE);
    }

    public void setTimeAlive(int i) {
        entityData.set(TIME_ALIVE, i);
    }

    @Override
    public boolean isImmune() {
        return true;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

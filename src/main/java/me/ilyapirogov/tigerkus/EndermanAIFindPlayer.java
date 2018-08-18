package me.ilyapirogov.tigerkus;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Random;

public class EndermanAIFindPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
    private final EntityEnderman enderman;
    private Predicate<Entity> isTiger;
    /**
     * The player
     */
    private EntityPlayer player;
    private int aggroTime;
    private int teleportTime;
    protected Random rand;

    public EndermanAIFindPlayer(EntityEnderman enderman, Predicate<Entity> isTiger) {
        super(enderman, EntityPlayer.class, false);
        this.enderman = enderman;
        this.isTiger = isTiger;
        this.rand = new Random();
    }

    /**
     * Checks to see if this enderman should be attacking this player
     */
    private boolean shouldAttackPlayer(EntityPlayer player) {
        ItemStack itemstack = player.inventory.armorInventory.get(3);

        if (itemstack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN)) {
            return false;
        } else {
            Vec3d vec3d = player.getLook(1.0F).normalize();
            Vec3d vec3d1 = new Vec3d(this.enderman.posX - player.posX, this.enderman.getEntityBoundingBox().minY + (double) this.enderman.getEyeHeight() - (player.posY + (double) player.getEyeHeight()), this.enderman.posZ - player.posZ);
            double d0 = vec3d1.lengthVector();
            vec3d1 = vec3d1.normalize();
            double d1 = vec3d.dotProduct(vec3d1);
            return d1 > 1.0D - 0.025D / d0 && player.canEntityBeSeen(this.enderman);
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        double d0 = this.getTargetDistance();
        this.player = this.enderman.world.getNearestAttackablePlayer(this.enderman.posX, this.enderman.posY, this.enderman.posZ, d0, d0, (Function) null, new Predicate<EntityPlayer>() {
            public boolean apply(@Nullable EntityPlayer player) {
                return player != null && !isTiger.apply(player) && EndermanAIFindPlayer.this.shouldAttackPlayer(player);
            }
        });
        return this.player != null;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.aggroTime = 5;
        this.teleportTime = 0;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.player = null;
        super.resetTask();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        if (this.player != null) {
            if (!this.shouldAttackPlayer(this.player)) {
                return false;
            } else {
                this.enderman.faceEntity(this.player, 10.0F, 10.0F);
                return true;
            }
        } else {
            return this.targetEntity != null
                    && ((EntityPlayer) this.targetEntity).isEntityAlive()
                    || super.shouldContinueExecuting();
        }
    }


    /**
     * Teleport the enderman to another entity
     */
    protected boolean teleportToEntity(Entity p_70816_1_)
    {
        Vec3d vec3d = new Vec3d(this.enderman.posX - p_70816_1_.posX, this.enderman.getEntityBoundingBox().minY + (double)(this.enderman.height / 2.0F) - p_70816_1_.posY + (double)p_70816_1_.getEyeHeight(), this.enderman.posZ - p_70816_1_.posZ);
        vec3d = vec3d.normalize();
        double d0 = 16.0D;
        double d1 = this.enderman.posX + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
        double d2 = this.enderman.posY + (double)(this.rand.nextInt(16) - 8) - vec3d.y * 16.0D;
        double d3 = this.enderman.posZ + (this.rand.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;
        return this.teleportTo(d1, d2, d3);
    }


    /**
     * Teleport the enderman
     */
    private boolean teleportTo(double x, double y, double z) {
        net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(this.enderman, x, y, z, 0);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return false;
        boolean flag = this.enderman.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());

        if (flag) {
            this.enderman.world.playSound((EntityPlayer) null, this.enderman.prevPosX, this.enderman.prevPosY, this.enderman.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, this.enderman.getSoundCategory(), 1.0F, 1.0F);
            this.enderman.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        }

        return flag;
    }

    /**
     * Teleport the enderman to a random nearby position
     */
    protected boolean teleportRandomly() {
        double d0 = this.enderman.posX + (this.rand.nextDouble() - 0.5D) * 64.0D;
        double d1 = this.enderman.posY + (double) (this.rand.nextInt(64) - 32);
        double d2 = this.enderman.posZ + (this.rand.nextDouble() - 0.5D) * 64.0D;
        return this.teleportTo(d0, d1, d2);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask() {
        if (this.player != null) {
            if (--this.aggroTime <= 0) {
                this.targetEntity = this.player;
                this.player = null;
                super.startExecuting();
            }
        } else {
            if (this.targetEntity != null) {
                if (this.shouldAttackPlayer(this.targetEntity)) {
                    if (this.targetEntity.getDistanceSq(this.enderman) < 16.0D) {
                        this.teleportRandomly();
                    }

                    this.teleportTime = 0;
                } else if (this.targetEntity.getDistanceSq(this.enderman) > 256.0D && this.teleportTime++ >= 30 && this.teleportToEntity(this.targetEntity)) {
                    this.teleportTime = 0;
                }
            }

            super.updateTask();
        }
    }
}

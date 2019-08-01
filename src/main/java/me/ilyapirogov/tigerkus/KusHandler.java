package me.ilyapirogov.tigerkus;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class KusHandler {
    private static Predicate<Entity> isTiger = player -> {
        return player instanceof EntityPlayer && TigerKus.tigerUuids.contains(player.getPersistentID());
    };

    @SubscribeEvent
    public void onBecomeTarget(LivingSetAttackTargetEvent ev) {
        EntityLivingBase entity = ev.getEntityLiving();
        EntityLivingBase target = ev.getTarget();

        if (target != null && entity instanceof EntityLiving && isTiger.apply(target)) {
            ((EntityLiving) entity).setAttackTarget(null);
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        if (TigerKus.fearEveryone && entity instanceof IMob && entity instanceof EntityCreature) {
            EntityCreature creature = (EntityCreature) entity;
            creature.tasks.addTask(0,
                    new EntityAIAvoidEntity<>(
                            creature,
                            EntityPlayer.class,
                            isTiger,
                            TigerKus.avoidDistance,
                            TigerKus.farSpeedIn,
                            TigerKus.nearSpeedIn
                    ));
        }
    }
}

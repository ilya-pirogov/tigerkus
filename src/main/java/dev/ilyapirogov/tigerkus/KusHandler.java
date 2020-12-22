package dev.ilyapirogov.tigerkus;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AmbientEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import java.util.function.Predicate;


public class KusHandler {
    private static final Predicate<LivingEntity> isTiger = player -> {
        return player instanceof PlayerEntity && TigerKus.tigerUuids.contains(player.getUniqueID());
    };

    @SubscribeEvent
    public void onBecomeTarget(LivingSetAttackTargetEvent ev) {
        LivingEntity entity = ev.getEntityLiving();
        LivingEntity target = ev.getTarget();

        if (target != null && isTiger.test(target) && entity instanceof MobEntity) {
            ((MobEntity) entity).setAttackTarget(null);
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent ev) {
        LivingEntity entity = ev.getEntityLiving();
        DamageSource damage = ev.getSource();
        Entity source = damage.getImmediateSource();

        if (isTiger.test(entity) && source instanceof MobEntity) {
            ev.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        if (CommonConfig.GENERAL.fearEveryone.get() && entity instanceof CreatureEntity) {
            CreatureEntity creature = (CreatureEntity) entity;
            if (!(creature instanceof IMob)) {
                return;
            }

            creature.goalSelector.addGoal(0, new AvoidEntityGoal<>(creature, PlayerEntity.class,
                    (float) (double)CommonConfig.GENERAL.avoidDistance.get(),
                    CommonConfig.GENERAL.farSpeedIn.get(),
                    CommonConfig.GENERAL.nearSpeedIn.get(),
                    isTiger)
            );
        }
    }
}

package me.ilyapirogov.tigerkus;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class KusHandler {
    private static Predicate<Entity> isTiger = new Predicate<Entity>() {
        public boolean apply(@Nullable Entity player) {
            return player instanceof EntityPlayer && TigerKus.tigerUuids.contains(player.getPersistentID());
        }
    };

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityMob) {
            EntityMob mob = (EntityMob) entity;

            for (EntityAITasks.EntityAITaskEntry taskEntry : mob.targetTasks.taskEntries) {
                if (taskEntry.action instanceof EntityAINearestAttackableTarget<?>) {
                    EntityAINearestAttackableTarget ai = (EntityAINearestAttackableTarget) taskEntry.action;

                    // black magic starts here
                    try {
                        Class klass = ai.getClass();
                        Field[] fields = klass.getDeclaredFields();
                        if (fields.length == 0) {
                            klass = klass.getSuperclass();
                            fields = klass.getDeclaredFields();
                        }

                        if (fields.length < 4) {
                            continue;
                        }

                        Field targetEntitySelector = fields[3];
                        targetEntitySelector.setAccessible(true);

                        Field modifiersField = Field.class.getDeclaredField("modifiers");
                        modifiersField.setAccessible(true);
                        modifiersField.setInt(targetEntitySelector, targetEntitySelector.getModifiers() & ~Modifier.FINAL);

                        Predicate<Entity> predicate = (Predicate<Entity>) targetEntitySelector.get(ai);
                        targetEntitySelector.set(ai, new Predicate<Entity>() {
                            public boolean apply(@Nullable Entity entity) {
                                if (isTiger.apply(entity)) {
                                    return false;
                                }
                                return predicate.apply(entity);
                            }
                        });

                    } catch (Exception ex) {
                        TigerKus.logger.error(ex);
                        TigerKus.logger.warn("Tiger can't do kus' for {} :(", mob.getDisplayName());
                    }
                }
            }

            if (TigerKus.fearEveryone) {
                mob.tasks.addTask(0,
                        new EntityAIAvoidEntity<>(
                                mob,
                                EntityPlayer.class,
                                isTiger,
                                TigerKus.avoidDistance,
                                TigerKus.farSpeedIn,
                                TigerKus.nearSpeedIn
                        ));
            }
        }
    }
}

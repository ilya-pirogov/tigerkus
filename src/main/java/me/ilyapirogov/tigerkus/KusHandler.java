package me.ilyapirogov.tigerkus;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
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

    private static void TryToPatchPredicate(Object obj) throws NoSuchFieldException, IllegalAccessException {
        // this is a black magic! \O/ ==---* vzhuh
        Field targetEntitySelector = null;
        Class klass = obj.getClass();

        for (Field declaredField : klass.getDeclaredFields()) {
            if (declaredField.getType().getSimpleName().equals("Predicate")) {
                targetEntitySelector = declaredField;
                break;
            }
        }

        if (targetEntitySelector == null) {
            return;
        }

        targetEntitySelector.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(targetEntitySelector, targetEntitySelector.getModifiers() & ~Modifier.FINAL);

        Predicate<Entity> predicate = (Predicate<Entity>) targetEntitySelector.get(obj);
        targetEntitySelector.set(obj, new Predicate<Entity>() {
            public boolean apply(@Nullable Entity entity) {
                if (isTiger.apply(entity)) {
                    return false;
                }
                return predicate.apply(entity);
            }
        });
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityMob) {
            EntityMob mob = (EntityMob) entity;

            for (EntityAITasks.EntityAITaskEntry taskEntry : mob.targetTasks.taskEntries) {
                try {

                    if (taskEntry.action instanceof EntityAINearestAttackableTarget<?>
                            || taskEntry.action instanceof EntityAIFindEntityNearestPlayer) {
                        TryToPatchPredicate(taskEntry);
                    }

                } catch (Exception ex) {
                    TigerKus.logger.error(ex);
                    TigerKus.logger.warn("Tiger can't do kus' for {} :(", mob.getDisplayName());
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

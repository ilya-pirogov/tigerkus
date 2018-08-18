package me.ilyapirogov.tigerkus;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class KusHandler {
    private static Predicate<Entity> isTiger = new Predicate<Entity>() {
        public boolean apply(@Nullable Entity player) {
            return player instanceof EntityPlayer && TigerKus.tigerUuids.contains(player.getPersistentID());
        }
    };

    private static void TryToPatchPredicate(Object obj, java.lang.Class<?> klass) throws NoSuchFieldException, IllegalAccessException {
        // this is a black magic! \O/ ==---* vzhuh
        Field targetEntitySelector = null;

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

    private static void TryToReplaceKusAI(EntityAITasks.EntityAITaskEntry taskEntry, EntityLiving mob) throws IllegalAccessException {
        Class<?> klass = taskEntry.action.getClass();
        Field[] fields = klass.getDeclaredFields();
        boolean entityCallsForHelp = false;
        Class<?>[] excludedReinforcementTypes = new Class[]{};
        int priority = taskEntry.priority;

        if (fields.length < 3) {
            return;
        }

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType().getSimpleName().equals("boolean")) {
                entityCallsForHelp = (boolean) field.get(taskEntry.action);
            }

            if (field.getType().getSimpleName().equals("Class[]")) {
                excludedReinforcementTypes = (Class<?>[]) field.get(taskEntry.action);
            }
        }

        EntityAIDoesntKusTarget newAi = new EntityAIDoesntKusTarget((EntityCreature) mob, isTiger, entityCallsForHelp, excludedReinforcementTypes);

        mob.targetTasks.removeTask(taskEntry.action);
        mob.targetTasks.addTask(priority, newAi);
    }

    private static void TryToReplaceEndermanAI(EntityAITasks.EntityAITaskEntry taskEntry, EntityEnderman mob) {
        int priority = taskEntry.priority;

        EndermanAIFindPlayer newAi = new EndermanAIFindPlayer(mob, isTiger);

        mob.targetTasks.removeTask(taskEntry.action);
        mob.targetTasks.addTask(priority, newAi);
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof IMob && entity instanceof EntityLiving) {
            EntityLiving mob = (EntityLiving) entity;

            List<EntityAITasks.EntityAITaskEntry> entries = new ArrayList<>(mob.targetTasks.taskEntries);

            for (EntityAITasks.EntityAITaskEntry taskEntry : entries) {
                try {
                    String name = taskEntry.action.getClass().getName();

                    if (taskEntry.action instanceof EntityAINearestAttackableTarget<?>
                            || taskEntry.action instanceof EntityAIFindEntityNearestPlayer) {
                        TryToPatchPredicate(taskEntry.action, taskEntry.action.getClass());
                    }

                    if (taskEntry.action instanceof EntityAIHurtByTarget) {
                        TryToReplaceKusAI(taskEntry, mob);
                        continue;
                    }

                    if (mob instanceof EntityEnderman && name.endsWith("AIFindPlayer")) {
                        TryToReplaceEndermanAI(taskEntry, (EntityEnderman)mob);
                        continue;
                    }

                    if (name.endsWith("AISpiderTarget") || name.endsWith("AIFindPlayer") || name.endsWith("AITargetAggressor")) {
                        TryToPatchPredicate(taskEntry.action, taskEntry.action.getClass().getSuperclass());
                    }
                } catch (Exception ex) {
                    TigerKus.logger.error(ex);
                    TigerKus.logger.warn("Tiger can't do kus' for {} :(", mob.getDisplayName());
                }
            }

            if (TigerKus.fearEveryone && mob instanceof EntityMob) {
                mob.tasks.addTask(0,
                        new EntityAIAvoidEntity<>(
                                (EntityMob) mob,
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

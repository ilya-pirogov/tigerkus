package dev.pirogov.tigerkus.mixin;

import dev.pirogov.tigerkus.TigerKus;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.pirogov.tigerkus.TigerKus.CONFIG;

@SuppressWarnings({"ConstantValue", "DataFlowIssue"})
@Mixin(Mob.class)
public class MobTargetsMixin
{
    @Final
    @Shadow
    protected GoalSelector goalSelector;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(EntityType<?> entityType, Level level, CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (CONFIG.current.avoiding.enabled && level != null && !level.isClientSide
                && self instanceof PathfinderMob mob && self instanceof Enemy) {

            this.goalSelector.addGoal(3, new AvoidEntityGoal<>(mob, Player.class,
                    livingEntity -> livingEntity instanceof Player && CONFIG.isTiger(livingEntity.getUUID()),
                    CONFIG.current.avoiding.maxDist,
                    CONFIG.current.avoiding.walkSpeedModifier,
                    CONFIG.current.avoiding.sprintSpeedModifier,
                    EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
        }
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void setTarget(LivingEntity livingEntity, CallbackInfo ci) {
        if (livingEntity instanceof Player && CONFIG.isTiger(livingEntity.getUUID())) {
            ci.cancel();
        }
    }
}

package dev.ilyapirogov.tigerkus;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.monster.CreeperEntity;

import java.util.function.Predicate;

public class SmartCreeperSwellGoal extends CreeperSwellGoal {
    public SmartCreeperSwellGoal(CreeperEntity entitycreeperIn, Predicate<LivingEntity> targetPredicate) {
        super(entitycreeperIn);
    }
}

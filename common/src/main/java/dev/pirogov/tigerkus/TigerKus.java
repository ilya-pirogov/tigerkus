package dev.pirogov.tigerkus;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TigerKus
{

	public static final String MOD_ID = "tigerkus";

	public static final Logger LOGGER = LogManager.getFormatterLogger(MOD_ID);

	private static final Set<UUID> TIGERS = new HashSet<>();

	public static void init() {
		TIGERS.add(UUID.fromString("424303b4-09a6-4297-a45e-88d1c3903a60"));

		EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
			if (entity instanceof Player && TIGERS.contains(entity.getUUID())) {
				Entity badGuy = source.getEntity();
				if (badGuy instanceof Mob) {
					return EventResult.interruptFalse();
				}
			}
			return EventResult.pass();
		});

		TickEvent.PLAYER_POST.register(player -> {
			if (!TIGERS.contains(player.getUUID())) {
				return;
			}

			List<Monster> entities = player.level().getNearbyEntities(
					Monster.class,
					TargetingConditions
							.forCombat()
							.ignoreLineOfSight()
							.selector(l -> ((Monster) l).getTarget() == player),
					player,
					AABB.ofSize(player.position(), 32, 32, 32)
			);

			entities.forEach(monster -> {
				monster.setLastHurtByPlayer(null);
				monster.setTarget(null);
			});
		});
	}
}

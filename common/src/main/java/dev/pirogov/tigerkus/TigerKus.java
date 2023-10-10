package dev.pirogov.tigerkus;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.pirogov.tigerkus.config.ClothConfig;
import dev.pirogov.tigerkus.config.Manager;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TigerKus
{
	public static final String MOD_ID = "tigerkus";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static Manager CONFIG;


	public static void init() {
		AutoConfig.register(ClothConfig.class, JanksonConfigSerializer::new);
		CONFIG = new Manager(AutoConfig.getConfigHolder(ClothConfig.class));

		LifecycleEvent.SERVER_STARTING.register(TigerKusCommands::new);

		EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
			if (CONFIG.current.repulsion.enabled && entity instanceof Player player
					&& source.getEntity() instanceof LivingEntity badGuy && CONFIG.isTiger(entity.getUUID())) {

				double dx = badGuy.position().x - player.position().x;
				double dz = badGuy.position().z - player.position().z;
				Vec3 d = new Vec3(dx, 0, dz).normalize().add(0, 0.5, 0).scale(CONFIG.current.repulsion.strength);
				badGuy.setDeltaMovement(badGuy.getDeltaMovement().add(d));
				return EventResult.interruptFalse();
			}
			return EventResult.pass();
		});

		LOGGER.info("Initialized");
	}
}

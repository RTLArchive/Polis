package io.github.hsyyid.polis.listeners;

import io.github.hsyyid.polis.Polis;
import io.github.hsyyid.polis.utils.ConfigManager;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class EntityDamageListener
{
	@Listener
	public void onEntityDamaged(DamageEntityEvent event)
	{
		String isClaimed = ConfigManager.isClaimed(event.getTargetEntity().getLocation());

		if (!isClaimed.equals("false"))
		{
			// If a player attacked this entity
			if (event.getCause().root() instanceof Player)
			{
				Player player = (Player) event.getCause().root();

				// And this area is a SafeZone
				if (isClaimed.equals("SafeZone"))
				{
					// but this player has toggled admin-bypass or the entity is usable.
					if (Polis.adminBypassMode.contains(player.getUniqueId()) || ConfigManager.canUseInSafeZone(event.getTargetEntity().getType().getId()))
					{
						// Disable entity protection
						return;
					}
					// if it's a player
					else if (event.getTargetEntity().getType() == EntityTypes.PLAYER)
					{
						event.setBaseDamage(0);
						event.setCancelled(true);
					}
				}
				else
				{
					// Get their team name
					String playerTeamName = ConfigManager.getTeam(player.getUniqueId());

					// If they are not attacking a player, but it is an entity claimed by another Polis
					if (event.getTargetEntity().getType() != EntityTypes.PLAYER && !playerTeamName.equals(isClaimed))
					{
						event.setBaseDamage(0);
						event.setCancelled(true);
					}
					// If they are attacking a player
					else if (event.getTargetEntity().getType() == EntityTypes.PLAYER)
					{
						Player target = (Player) event.getTargetEntity();
						String targetPlayerTeamName = ConfigManager.getTeam(target.getUniqueId());

						// If they are both in the same team... (and different people)
						if (targetPlayerTeamName != null && playerTeamName != null && !target.getUniqueId().equals(player.getUniqueId()) && targetPlayerTeamName.equals(playerTeamName))
						{
							player.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "You cannot hurt people in your Polis."));
							target.sendMessage(Text.of(TextColors.DARK_RED, player.getName(), TextColors.RED, " tried to hurt you."));
							event.setBaseDamage(0);
							event.setCancelled(true);
						}
					}
				}
			}
			// Otherwise if it's just in a SafeZone
			else if (isClaimed.equals("SafeZone"))
			{
				// And this area is a SafeZone, but this entity is interactable
				if (ConfigManager.canUseInSafeZone(event.getTargetEntity().getType().getId()))
				{
					// Disable entity protection
					return;
				}
				else
				{
					// Otherwise, protect all entities.
					event.setBaseDamage(0);
					event.setCancelled(true);
				}
			}
		}
	}
}

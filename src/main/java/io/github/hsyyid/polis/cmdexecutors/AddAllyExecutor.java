package io.github.hsyyid.polis.cmdexecutors;

import io.github.hsyyid.polis.utils.ConfigManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class AddAllyExecutor implements CommandExecutor
{
	public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException
	{
		String townName = ctx.<String> getOne("town name").get();

		if (src instanceof Player)
		{
			Player player = (Player) src;

			if (ConfigManager.getTeams().contains(townName))
			{
				String playerTeamName = ConfigManager.getTeam(player.getUniqueId());
				boolean playerIsAMember = false;

				if (playerTeamName != null)
				{
					try
					{
						if (ConfigManager.getAllies(playerTeamName) != null && ConfigManager.getAllies(playerTeamName).contains(townName))
						{
							player.sendMessage(Text.of(TextColors.GREEN, "[Polis]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You are already allies!"));
							return CommandResult.success();
						}
					}
					catch (NullPointerException e)
					{

					}
					try
					{
						if (ConfigManager.getEnemies(playerTeamName) != null && ConfigManager.getEnemies(playerTeamName).contains(townName))
						{
							ConfigManager.removeEnemy(playerTeamName, townName);
							ConfigManager.removeEnemy(townName, playerTeamName);
							ConfigManager.addAlly(playerTeamName, townName, true);
							player.sendMessage(Text.of(TextColors.GREEN, "[Polis]: ", TextColors.GOLD, "Successfully set town ", TextColors.GREEN, townName, TextColors.GOLD, " as an ally!"));
							return CommandResult.success();
						}
					}
					catch (NullPointerException e)
					{
						;
					}

					ConfigManager.addAlly(playerTeamName, townName, true);
					player.sendMessage(Text.of(TextColors.GREEN, "[Polis]: ", TextColors.GOLD, "Successfully set town ", TextColors.GREEN, townName, TextColors.GOLD, " as an ally!"));
				}
				else if (playerIsAMember)
				{
					player.sendMessage(Text.of(TextColors.GREEN, "[Polis]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Ask your leader to set allies!"));
				}
				else
				{
					player.sendMessage(Text.of(TextColors.GREEN, "[Polis]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "You're not part of a town!"));
				}
			}
			else
			{
				player.sendMessage(Text.of(TextColors.GREEN, "[Polis]: ", TextColors.DARK_RED, "Error! ", TextColors.RED, "Town does not exist!"));
			}
		}
		else if (src instanceof ConsoleSource)
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /addally!"));
		}
		else if (src instanceof CommandBlockSource)
		{
			src.sendMessage(Text.of(TextColors.DARK_RED, "Error! ", TextColors.RED, "Must be an in-game player to use /addally!"));
		}

		return CommandResult.success();
	}
}

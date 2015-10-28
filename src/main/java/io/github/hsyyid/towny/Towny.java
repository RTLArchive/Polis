package io.github.hsyyid.towny;

import com.google.inject.Inject;
import io.github.hsyyid.towny.cmdexecutors.AddAllyExecutor;
import io.github.hsyyid.towny.cmdexecutors.AddEnemyExecutor;
import io.github.hsyyid.towny.cmdexecutors.AddExecutiveExecutor;
import io.github.hsyyid.towny.cmdexecutors.CreateTownExecutor;
import io.github.hsyyid.towny.cmdexecutors.DeleteTownExecutor;
import io.github.hsyyid.towny.cmdexecutors.HQExecutor;
import io.github.hsyyid.towny.cmdexecutors.InviteExecutor;
import io.github.hsyyid.towny.cmdexecutors.JoinTownExecutor;
import io.github.hsyyid.towny.cmdexecutors.KickMemberExecutor;
import io.github.hsyyid.towny.cmdexecutors.LeaveTownExecutor;
import io.github.hsyyid.towny.cmdexecutors.RemoveExecutiveExecutor;
import io.github.hsyyid.towny.cmdexecutors.SetHQExecutor;
import io.github.hsyyid.towny.cmdexecutors.SetLeaderExecutor;
import io.github.hsyyid.towny.cmdexecutors.TownClaimExecutor;
import io.github.hsyyid.towny.cmdexecutors.TownInfoExecutor;
import io.github.hsyyid.towny.cmdexecutors.TownListExecutor;
import io.github.hsyyid.towny.listeners.PlayerBreakBlockListener;
import io.github.hsyyid.towny.listeners.PlayerInteractListener;
import io.github.hsyyid.towny.listeners.PlayerPlaceBlockListener;
import io.github.hsyyid.towny.utils.Invite;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Plugin(id = "Towny", name = "Towny", version = "0.1", dependencies = "required-after:TotalEconomy;")
public class Towny
{
	public static Game game;
	public static ConfigurationNode config;
	public static ConfigurationLoader<CommentedConfigurationNode> configurationManager;
	public static ArrayList<Invite> invites = new ArrayList<>();

	@Inject
	private Logger logger;

	public Logger getLogger()
	{
		return logger;
	}

	@Inject
	@DefaultConfig(sharedRoot = true)
	private File dConfig;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> confManager;

	@Listener
	public void onServerInit(GameInitializationEvent event)
	{
		getLogger().info("Towny loading...");
		game = event.getGame();

		// Config File
		try
		{
			if (!dConfig.exists())
			{
				dConfig.createNewFile();
				config = confManager.load();
				confManager.save(config);
			}
			configurationManager = confManager;
			config = confManager.load();

		}
		catch (IOException exception)
		{
			getLogger().error("The default configuration could not be loaded or created!");
		}

		CommandSpec joinTownCommandSpec = CommandSpec.builder()
			.description(Texts.of("Join Town Command"))
			.permission("towny.join")
			.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("town name"))))
			.executor(new JoinTownExecutor())
			.build();

		game.getCommandDispatcher().register(this, joinTownCommandSpec, "jointown");

		CommandSpec setHQCommandSpec = CommandSpec.builder()
			.description(Texts.of("Set Town HQ Command"))
			.permission("towny.hq.set")
			.executor(new SetHQExecutor())
			.build();

		game.getCommandDispatcher().register(this, setHQCommandSpec, "sethq");

		CommandSpec HQCommandSpec = CommandSpec.builder()
			.description(Texts.of("Teleport to Town HQ Command"))
			.permission("towny.hq.use")
			.executor(new HQExecutor())
			.build();

		game.getCommandDispatcher().register(this, HQCommandSpec, "hq");

		CommandSpec inviteTownCommandSpec = CommandSpec.builder()
			.description(Texts.of("Towny Invite Command"))
			.permission("towny.invite")
			.arguments(GenericArguments.onlyOne(GenericArguments.player(Texts.of("player"), game)))
			.executor(new InviteExecutor())
			.build();

		game.getCommandDispatcher().register(this, inviteTownCommandSpec, "invite");

		CommandSpec addEnemyCommandSpec = CommandSpec.builder()
			.description(Texts.of("Add Enemy Command"))
			.permission("towny.enemy.add")
			.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("town name"))))
			.executor(new AddEnemyExecutor())
			.build();

		game.getCommandDispatcher().register(this, addEnemyCommandSpec, "addenemy");

		CommandSpec kickMemberCommandSpec = CommandSpec.builder()
			.description(Texts.of("Kick Member Command"))
			.permission("towny.kick.use")
			.arguments(GenericArguments.onlyOne(GenericArguments.player(Texts.of("player"), game)))
			.executor(new KickMemberExecutor())
			.build();

		game.getCommandDispatcher().register(this, kickMemberCommandSpec, "kickmember");

		CommandSpec addAllyCommandSpec = CommandSpec.builder()
			.description(Texts.of("Add Ally Command"))
			.permission("towny.ally.add")
			.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("town name"))))
			.executor(new AddAllyExecutor())
			.build();

		game.getCommandDispatcher().register(this, addAllyCommandSpec, "addally");

		CommandSpec leaveTownCommandSpec = CommandSpec.builder()
			.description(Texts.of("Leave Town Command"))
			.permission("towny.leave")
			.executor(new LeaveTownExecutor())
			.build();

		game.getCommandDispatcher().register(this, leaveTownCommandSpec, "leavetown");
		
		CommandSpec claimCommandSpec = CommandSpec.builder()
			.description(Texts.of("Claim Command"))
			.permission("towny.claim")
			.executor(new TownClaimExecutor())
			.build();

		game.getCommandDispatcher().register(this, claimCommandSpec, "townclaim");

		CommandSpec deleteTownCommandSpec = CommandSpec.builder()
			.description(Texts.of("Delete Town Command"))
			.permission("towny.delete")
			.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("town name"))))
			.executor(new DeleteTownExecutor())
			.build();

		game.getCommandDispatcher().register(this, deleteTownCommandSpec, "deletetown", "disbandtown");

		CommandSpec townCommandSpec = CommandSpec.builder()
			.description(Texts.of("Town Info Command"))
			.permission("towny.info")
			.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("town name"))))
			.executor(new TownInfoExecutor())
			.build();

		game.getCommandDispatcher().register(this, townCommandSpec, "town");

		CommandSpec townListCommandSpec = CommandSpec.builder()
			.description(Texts.of("Town List Command"))
			.permission("towny.list")
			.executor(new TownListExecutor())
			.build();

		game.getCommandDispatcher().register(this, townListCommandSpec, "towns");

		CommandSpec addTownCommandSpec = CommandSpec.builder()
			.description(Texts.of("Create Town Command"))
			.permission("towny.add")
			.arguments(GenericArguments.onlyOne(GenericArguments.string(Texts.of("town name"))))
			.executor(new CreateTownExecutor())
			.build();

		game.getCommandDispatcher().register(this, addTownCommandSpec, "createtown");

		CommandSpec setLeaderCommandSpec = CommandSpec.builder()
			.description(Texts.of("Set Leader of Town Command"))
			.permission("towny.leader.set")
			.arguments(GenericArguments.seq(
				GenericArguments.onlyOne(GenericArguments.player(Texts.of("player"), game)),
				GenericArguments.onlyOne(GenericArguments.string(Texts.of("town name")))))
			.executor(new SetLeaderExecutor())
			.build();

		game.getCommandDispatcher().register(this, setLeaderCommandSpec, "setleader");

		CommandSpec addExecutiveCommandSpec = CommandSpec.builder()
			.description(Texts.of("Adds Executive of Town Command"))
			.permission("towny.executive.add")
			.arguments(GenericArguments.seq(
				GenericArguments.onlyOne(GenericArguments.player(Texts.of("player"), game)),
				GenericArguments.onlyOne(GenericArguments.string(Texts.of("town name")))))
			.executor(new AddExecutiveExecutor())
			.build();

		game.getCommandDispatcher().register(this, addExecutiveCommandSpec, "addexecutive");

		CommandSpec removeExecutiveCommandSpec = CommandSpec.builder()
			.description(Texts.of("Remove Executive of Town Command"))
			.permission("towny.executive.remove")
			.arguments(GenericArguments.onlyOne(GenericArguments.player(Texts.of("player"), game)))
			.executor(new RemoveExecutiveExecutor())
			.build();

		game.getCommandDispatcher().register(this, removeExecutiveCommandSpec, "removeexec", "removeexecutive");

		game.getEventManager().registerListeners(this, new PlayerInteractListener());
		game.getEventManager().registerListeners(this, new PlayerBreakBlockListener());
		game.getEventManager().registerListeners(this, new PlayerPlaceBlockListener());

		getLogger().info("-----------------------------");
		getLogger().info("Towny was made by HassanS6000!");
		getLogger().info("Please post all errors on the Sponge Thread or on GitHub!");
		getLogger().info("Have fun, and enjoy! :D");
		getLogger().info("-----------------------------");
		getLogger().info("Towny loaded!");
	}

	public static ConfigurationLoader<CommentedConfigurationNode> getConfigManager()
	{
		return configurationManager;
	}
}

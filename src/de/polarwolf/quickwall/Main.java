package de.polarwolf.quickwall;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import de.polarwolf.heliumballoon.api.HeliumBalloonAPI;
import de.polarwolf.heliumballoon.api.HeliumBalloonProvider;
import de.polarwolf.heliumballoon.balloons.BalloonDefinition;
import de.polarwolf.heliumballoon.balloons.walls.ConfigWall;
import de.polarwolf.heliumballoon.balloons.walls.Wall;
import de.polarwolf.heliumballoon.behavior.BehaviorDefinition;
import de.polarwolf.heliumballoon.config.ConfigHelper;
import de.polarwolf.heliumballoon.config.ConfigSection;
import de.polarwolf.heliumballoon.config.templates.ConfigTemplate;
import de.polarwolf.heliumballoon.exception.BalloonException;

public final class Main extends JavaPlugin {

	protected String sectionName;
	protected String templateName;
	protected String behaviorName;
	protected String balloonName;

	protected HeliumBalloonAPI api = null;
	protected Location wallLocation = null;
	protected Wall activeWall = null;

	// Get the location of the wall as defined in the createQuickWall statement.
	public Location getWallLocation() {
		return wallLocation;
	}

	// Get the current active Wall.
	public Wall getActiveWall() {
		return activeWall;
	}

	// Place a new wall in the world.
	public boolean createQuickWall(CommandSender sender, Location location) {
		// This plugin is limited to one wall at the same time.
		// You can enhance this by implementing a list of active walls.
		if (activeWall != null) {
			sender.sendMessage("You have already placed a wall. Remove it first with /quickwallremove");
			return false;
		}

		// Get the HeliumAPI.
		api = HeliumBalloonProvider.getAPI();
		if (api == null) {
			sender.sendMessage("The HeliumBalloon API is not avail");
			return false;
		}

		try {
			// Keep things simple.
			// Take an existing template.
			// Here we use "Demo6" from the HeliumPluing's demo config.yml
			ConfigSection section = api.findSection(sectionName);
			if (section == null) {
				sender.sendMessage(String.format("Section %s not found.", sectionName));
				sender.sendMessage("Please set loadLocalConfig to true in HeliumBalloon Plugin config");
				return false;
			}
			ConfigTemplate template = section.findTemplate(templateName);
			if (template == null) {
				sender.sendMessage(String.format("Template %s not found.", templateName));
				sender.sendMessage("Please restore the demos in HeliumBalloon Plugin config");
				return false;
			}

			// Save the location for the wall,
			// so we can rebuild it in the BalloonRefreshEvent.
			this.wallLocation = location;

			// Collect needed information
			ConfigHelper configHelper = api.getConfigHelper();
			BehaviorDefinition behaviordefinition = api.findBehaviorDefinition(behaviorName);
			BalloonDefinition balloonDefinition = api.findBalloonDefinition(balloonName);

			// Create the Wall configuration.
			// Because the location is immutable, we need to create the config on the fly.
			ConfigWall configWall = new ConfigWall("QuickWall", "QuickWall", balloonDefinition, configHelper, template,
					behaviordefinition, location);
			// Create the wall and place it in the world.
			// Store the object, so we can remove it later.
			activeWall = api.createWall(configWall, location.getWorld());

		} catch (BalloonException e) {
			sender.sendMessage(e.getMessage());
			return false;
		}

		sender.sendMessage("Place OK");
		return true;
	}

	// Remove the Wall from the world.
	public boolean removeQuickWall(CommandSender sender) {
		if (activeWall == null) {
			sender.sendMessage("You don't have a wall. Create it first with /quickwallplace");
			return false;
		}

		// Don't simply call activeCall.cancel(),
		// because this does not remove the Wall from the manager.
		api.destroyWall(activeWall);
		activeWall = null;

		sender.sendMessage("Remove OK");
		return true;
	}

	// If we receive a BalloonRefreshEvent, all Balloons are already removed.
	// We need to clear our own data, so the EventHandler can create a new Wall.
	public void clearQuickWall() {
		activeWall = null;
	}

	// Plugin initialization
	@Override
	public void onEnable() {
		saveDefaultConfig();
		sectionName = getConfig().getString("targetSection", "HeliumBalloon");
		templateName = getConfig().getString("targetTemplate", "demo6");
		behaviorName = getConfig().getString("targetBehavior", "fixed");
		balloonName = getConfig().getString("targetBalloon", "walls");

		new RefreshListener(this);
		new PlaceCommand(this);
		new RemoveCommand(this);
		this.getLogger().info("You can now place and remove Walls.");
		this.getLogger().info("Use /quickwallplace and /quickwallremove");
	}

}

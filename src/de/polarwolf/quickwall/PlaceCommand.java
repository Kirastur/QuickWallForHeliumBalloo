package de.polarwolf.quickwall;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PlaceCommand implements CommandExecutor {

	public static final String COMMAND_PLACE = "quickwallplace";
	protected final Main main;

	public PlaceCommand(Main main) {
		this.main = main;
		main.getCommand(COMMAND_PLACE).setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 3) {
			sender.sendMessage("Usage: /quickwallplace X Y Z");
			return false;
		}

		double corX;
		double corY;
		double corZ;
		try {
			corX = Double.parseDouble(args[0]);
			corY = Double.parseDouble(args[1]);
			corZ = Double.parseDouble(args[2]);
		} catch (Exception e) {
			sender.sendMessage("Cannot parse coordinates");
			return true;
		}
		Vector position = new Vector(corX, corY, corZ);

		World world;
		if (sender instanceof Player player) {
			world = player.getWorld();
		} else {
			world = main.getServer().getWorld("world");
		}

		if (!main.createQuickWall(sender, position.toLocation(world))) {
			sender.sendMessage("Have you deleted the demos in HeliumBalloon's config.yml?");
		}
		return true;
	}

}

package de.polarwolf.quickwall;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RemoveCommand implements CommandExecutor {

	public static final String COMMAND_REMOVE = "quickwallremove";
	protected final Main main;

	public RemoveCommand(Main main) {
		this.main = main;
		main.getCommand(COMMAND_REMOVE).setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 0) {
			sender.sendMessage("Usage: /quickwallremove");
			return false;
		}
		
		main.removeQuickWall(sender);
		return true;
	}

}
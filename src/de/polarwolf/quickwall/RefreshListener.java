package de.polarwolf.quickwall;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import de.polarwolf.heliumballoon.events.BalloonRefreshAllEvent;

public class RefreshListener implements Listener {

	protected final Main main;

	public RefreshListener(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBalloonRefreshAllEvent(BalloonRefreshAllEvent event) {
		if (main.getActiveWall() != null) {
			CommandSender sender = main.getServer().getConsoleSender();
			Location wallLocation = main.getWallLocation();
			main.clearQuickWall();
			main.createQuickWall(sender, wallLocation);
		}
	}

}

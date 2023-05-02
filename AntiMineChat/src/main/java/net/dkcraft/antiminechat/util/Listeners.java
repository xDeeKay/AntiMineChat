package net.dkcraft.antiminechat.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dkcraft.antiminechat.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Listeners implements Listener {

	public Main plugin;

	private Map<Player, Location> locations;

	public Listeners(Main instance) {
		this.plugin = instance;
		locations = new HashMap<Player, Location>();
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		List<String> kickPhrases = plugin.getConfig().getStringList("KickPhrases");
		if (plugin.getConfig().getBoolean("PhraseKick", true)) {
			if (!player.hasPermission("antiminechat.bypass")) {
				for (String phrases : kickPhrases) {
					if (event.getMessage().equals(phrases)) {
						player.kickPlayer(ChatColor.translateAlternateColorCodes ('&', (plugin.getConfig().getString("KickMessage"))));
						event.setCancelled(true);
						return;
					}
				}
			}
		}
		if (plugin.getConfig().getBoolean("MoveToSpeak", true)) {
			if (!player.hasPermission("antiminechat.bypass")) {
				try {
					if (player.getLocation().distanceSquared(locations.get(player)) < 1.0D) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.translateAlternateColorCodes ('&', (plugin.getConfig().getString("SpeakError"))));
					}
				}
				catch (IllegalArgumentException e) {
				}
			}
		}
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (plugin.getConfig().getBoolean("MoveToCommand", true)) {
			if (!player.hasPermission("antiminechat.bypass")) {
				try {
					if (event.getPlayer().getLocation().distanceSquared(locations.get(event.getPlayer())) < 1.0D) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes ('&', (plugin.getConfig().getString("CommandError"))));
					}
				}
				catch (IllegalArgumentException e) {
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (!locations.containsKey(player)) {
			locations.put(event.getPlayer(), player.getLocation());
			if (plugin.getConfig().getBoolean("MoveKick", true)) {
				if (!player.hasPermission("antiminechat.bypass")) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Bukkit.getServer().getPluginManager().getPlugin("AntiMineChat")), new Runnable() {
						public void run() {
							if (event.getPlayer().getLocation().distanceSquared(locations.get(player)) < 1.0D) {
								player.kickPlayer(ChatColor.translateAlternateColorCodes ('&', (plugin.getConfig().getString("KickMessage"))));
							} else {
								locations.remove(player);
							}
						}
					}, plugin.getConfig().getInt("MoveKickTime"));// 60 L == 3 sec, 20 ticks == 1 sec
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (locations.containsKey(player)) {
			locations.remove(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		locations.put(player, event.getTo());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		if (locations.containsKey(player)) {
			locations.remove(player);
		}
	}
}

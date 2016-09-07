package de.redstoneworld.redinteract;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    private final RedInteract plugin;

    public InteractListener(RedInteract plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBlockInteract(PlayerInteractEvent event) {
        if (plugin.hasPendingRequest(event.getPlayer())) {
            event.setCancelled(true);
            handleRequest(event.getPlayer(), event.getClickedBlock().getLocation());
        } else if (event.isCancelled()){
            if (plugin.isRegistered(event.getClickedBlock().getLocation())) {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEntityInteract(PlayerInteractEntityEvent event) {
        if (plugin.hasPendingRequest(event.getPlayer())) {
            event.setCancelled(true);
            handleRequest(event.getPlayer(), event.getRightClicked().getLocation());
        } else if (event.isCancelled()){
            if (plugin.isRegistered(event.getRightClicked().getLocation())) {
                event.setCancelled(false);
            }
        }
    }

    private void handleRequest(Player player, Location location) {
        InteractRequest request = plugin.getPendingRequest(player);
        LocationInfo loc = new LocationInfo(location);
        if (plugin.execute(request.getType(), loc)) {
            player.sendMessage(ChatColor.GREEN + request.getType().toString() + "ED " + loc.toString());
        } else {
            player.sendMessage(ChatColor.RED + "Failed to " + request.getType() + toString() + " " + loc.toString() + "! It was " + (request.getType() == InteractRequest.Type.ADD ? "already" : "not") + " registered!");
        }
        plugin.removePendingRequest(player);
    }
}

package de.redstoneworld.redinteract;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    private final RedInteract plugin;

    public InteractListener(RedInteract plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBlockInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

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
        if (event.getRightClicked() == null) {
            return;
        }

        if (plugin.hasPendingRequest(event.getPlayer())) {
            event.setCancelled(true);
            handleRequest(event.getPlayer(), event.getRightClicked().getLocation());
        } else if (event.isCancelled()){
            if (plugin.isRegistered(event.getRightClicked().getLocation())) {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            if (event.getBlockPlaced().getType() == event.getBlockReplacedState().getType() && plugin.isRegistered(event.getBlock().getLocation())) {
                event.setCancelled(false);
            }
        }
    }

    private void handleRequest(Player player, Location location) {
        InteractRequest request = plugin.getPendingRequest(player);
        LocationInfo loc = new LocationInfo(location);
        if (plugin.execute(request.getType(), loc)) {
            player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + " " + request.getType() + "ED " + loc);
        } else {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + " Failed to " + request.getType() + " " + loc + "! It was " + (request.getType() == InteractRequest.Type.ADD ? "already" : "not") + " registered!");
        }
        plugin.removePendingRequest(player);
    }
}

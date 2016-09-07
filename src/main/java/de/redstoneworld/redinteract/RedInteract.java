package de.redstoneworld.redinteract;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class RedInteract extends JavaPlugin {

    private ConfigAccessor locationsConfig;

    private Set<LocationInfo> locations;

    private Map<UUID, InteractRequest> pendingRequests;

    private int requestTimeout;
    private String prefix;
    private int nearbyDistance;

    public void onEnable() {
        locationsConfig = new ConfigAccessor(this, "locations.yml");
        loadConfig();
        getCommand("redinteract").setExecutor(new RedInteractCommand(this));
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
    }

    public boolean loadConfig() {
        pendingRequests = new HashMap<UUID, InteractRequest>();
        locations = new HashSet<LocationInfo>();
        saveDefaultConfig();
        reloadConfig();
        requestTimeout = getConfig().getInt("requestTimeout");
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
        nearbyDistance = getConfig().getInt("nearbyDistance");
        locationsConfig.reloadConfig();
        for (String locStr : locationsConfig.getConfig().getStringList("locations")) {
            try {
                LocationInfo loc = LocationInfo.fromString(locStr);
                locations.add(loc);
            } catch (IllegalArgumentException e) {
                getLogger().log(Level.SEVERE, e.getMessage());
            }
        }
        return true;
    }

    public boolean hasPendingRequest(Player player) {
        return getPendingRequest(player) != null && pendingRequests.get(player.getUniqueId()).getTime() + requestTimeout * 1000 > System.currentTimeMillis();
    }

    public InteractRequest getPendingRequest(Player player) {
        return pendingRequests.get(player.getUniqueId());
    }

    public void addPendingRequest(Player player, InteractRequest.Type type) {
        pendingRequests.put(player.getUniqueId(), new InteractRequest(type));
        player.sendMessage(ChatColor.YELLOW + "Click the block/entity you want to " + type + " the location of in the next " + requestTimeout + " seconds!");
    }

    public InteractRequest removePendingRequest(Player player) {
        return pendingRequests.remove(player.getUniqueId());
    }

    public boolean execute(InteractRequest.Type type, LocationInfo loc) {
        if (type == InteractRequest.Type.ADD) {
            if (locations.add(loc)) {
                List<String> locations = locationsConfig.getConfig().getStringList("locations");
                locations.add(loc.toString());
                locationsConfig.getConfig().set("locations", locations);
                locationsConfig.saveConfig();
                return true;
            }
        } else if (type == InteractRequest.Type.REMOVE) {
            if (locations.remove(loc)) {
                List<String> locations = locationsConfig.getConfig().getStringList("locations");
                locations.remove(loc.toString());
                locationsConfig.getConfig().set("locations", locations);
                locationsConfig.saveConfig();
                return true;
            }
        }
        return false;
    }

    public boolean isRegistered(Location loc) {
        return isRegistered(new LocationInfo(loc));
    }

    public boolean isRegistered(LocationInfo loc) {
        return locations.contains(loc);
    }

    public String getPrefix() {
        return prefix;
    }

    public List<LocationInfo> getNearbyLocations(Location source) {
        return getNearbyLocations(new LocationInfo(source));
    }

    private List<LocationInfo> getNearbyLocations(LocationInfo source) {
        Map<Integer, List<LocationInfo>> distances = new HashMap<Integer, List<LocationInfo>>();

        for (LocationInfo loc : locations) {
            try {
                int distanceSquared = loc.distanceSquared(source);
                if (distanceSquared < nearbyDistance * nearbyDistance) {
                    if (!distances.containsKey(distanceSquared)) {
                        distances.put(distanceSquared, new ArrayList<LocationInfo>());
                    }
                    distances.get(distanceSquared).add(loc);
                }
            } catch (IllegalArgumentException e) {
                // Do nothing
            }
        }

        List<LocationInfo> locations = new ArrayList<LocationInfo>();
        for (int i = 0; i < nearbyDistance * nearbyDistance; i++) {
            if (distances.containsKey(i)) {
                locations.addAll(distances.get(i));
            }
        }
        return locations;
    }

    public int getNearbyDistance() {
        return nearbyDistance;
    }
}

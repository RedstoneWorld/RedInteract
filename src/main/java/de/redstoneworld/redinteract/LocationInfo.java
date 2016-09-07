package de.redstoneworld.redinteract;

import org.bukkit.Location;

class LocationInfo {
    private String worldName;
    private int x;
    private int y;
    private int z;

    public LocationInfo(String worldName, int x, int y, int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LocationInfo(Location loc) {
        this(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return worldName + "/" + x + "/" + y + "/" + z;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public static LocationInfo fromString(String locStr) {
        String[] parts = locStr.split("/");
        if (parts.length == 4) {
            int[] coords = new int[3];
            for (int i = 0; i < 3; i++) {
                try {
                    coords[i] = Integer.parseInt(parts[i + 1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(parts[i + 1] + " is not a valid integer in the string '" + locStr + "'!");
                }
            }
            return new LocationInfo(parts[0], coords[0], coords[1], coords[2]);
        }
        throw new IllegalArgumentException(locStr + " is not a valid location string with the format 'worldname/x/y/z'!");
    }
}

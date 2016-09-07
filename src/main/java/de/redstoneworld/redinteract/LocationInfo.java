package de.redstoneworld.redinteract;

import org.bukkit.Location;

class LocationInfo {
    private String worldName;
    private int x;
    private int y;
    private int z;

    public final static char STRING_SEPARATOR = ' ';

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return worldName + STRING_SEPARATOR + x + STRING_SEPARATOR + y + STRING_SEPARATOR + z;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o instanceof LocationInfo) {
            LocationInfo loc = (LocationInfo) o;
            return loc.getWorldName().equals(this.getWorldName())
                    && loc.getX() == this.getX()
                    && loc.getY() == this.getY()
                    && loc.getZ() == this.getZ();
        }
        return false;
    }

    public static LocationInfo fromString(String locStr) {
        String[] parts = locStr.split(String.valueOf(STRING_SEPARATOR));
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
        throw new IllegalArgumentException(locStr + " is not a valid location string with the format 'worldname x y z'!");
    }

    public int distanceSquared(LocationInfo loc) {
        if(loc == null) {
            throw new IllegalArgumentException("Cannot measure distance to a null location");
        } else if(loc.getWorldName().equalsIgnoreCase(this.getWorldName())) {
            return square(this.getX() - loc.getX()) + square(this.getY() - loc.getY()) + square(this.getZ() - loc.getZ());
        } else {
            throw new IllegalArgumentException("Cannot measure distance between two worlds (" + loc.getWorldName() + " - " + this.getWorldName() + ")");
        }
    }

    private int square(int i) {
        return i * i;
    }
}

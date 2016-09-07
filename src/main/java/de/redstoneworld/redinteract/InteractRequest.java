package de.redstoneworld.redinteract;

public class InteractRequest {
    private final Type type;
    private final long time = System.currentTimeMillis();

    public InteractRequest(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public enum Type {
        ADD,
        REMOVE
    }
}

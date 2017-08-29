package org.cat.eye.engine.container.discovery.gossip;

/**
 * Created by Kotov on 25.08.2017.
 */
public class Heartbeat {

    private final long version;

    private final long generation;

    public Heartbeat(long version, long generation) {
        this.version = version;
        this.generation = generation;
    }

    public Heartbeat(Heartbeat oldHeartbeat) {
        this.generation = oldHeartbeat.getGeneration();
        this.version = oldHeartbeat.getVersion() + 1;
    }

    public long getVersion() {
        return version;
    }

    public long getGeneration() {
        return generation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Heartbeat)) return false;

        Heartbeat heartbeat = (Heartbeat) o;

        if (version != heartbeat.version) return false;
        return generation == heartbeat.generation;
    }

    @Override
    public int hashCode() {
        int result = (int) (version ^ (version >>> 32));
        result = 31 * result + (int) (generation ^ (generation >>> 32));
        return result;
    }
}

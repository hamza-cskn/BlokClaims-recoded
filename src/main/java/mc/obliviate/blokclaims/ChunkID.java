package mc.obliviate.blokclaims;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Objects;

public class ChunkID {

    private final int x;
    private final int z;
    private final String world;


    public ChunkID(int x, int z, String world) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public ChunkID(String claimID) {
        String[] data = claimID.split(",");
        this.world = data[0];
        this.x = Integer.parseInt(data[1]);
        this.z = Integer.parseInt(data[2]);
    }
    public ChunkID(Chunk chunk) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.world = chunk.getWorld().getName();
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return world + "," + x + "," + z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkID)) return false;
        if (toString().equalsIgnoreCase(o.toString())) return true;
        return Objects.equals(toString(), o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }
}

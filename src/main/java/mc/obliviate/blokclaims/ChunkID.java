package mc.obliviate.blokclaims;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class ChunkID {

    private final int x;
    private final int z;
    private final World world;


    public ChunkID(int x, int z, World world) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public ChunkID(String claimID) {
        String[] data = claimID.split(",");
        this.world = Bukkit.getWorld(data[0]);
        this.x = Integer.parseInt(data[1]);
        this.z = Integer.parseInt(data[2]);
    }

    public World getWorld() {
        return world;
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return world.getName() + "," + x + "," + z;
    }
}

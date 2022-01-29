package mc.obliviate.blokclaims.chunkid;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Objects;

public class ChunkID implements ChunkIdentifier{

	private final int x;
	private final int z;
	private final String world;


	public ChunkID(final int x, final int z, final String world) {
		this.x = x;
		this.z = z;
		this.world = world;
	}

	public ChunkID(final String claimID) {
		final String[] data = claimID.split(",");
		this.world = data[0];
		this.x = Integer.parseInt(data[1]);
		this.z = Integer.parseInt(data[2]);
	}

	public ChunkID(final Chunk chunk) {
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
	public boolean equals(final Object o) {
		if (!(o instanceof ChunkIdentifier)) return false;
		final ChunkIdentifier ob = (ChunkIdentifier) o;
		if (ob.getX() == getX() && ob.getZ() == getZ() && ob.getWorld().getName().equalsIgnoreCase(getWorld().getName()))
			return true;
		return Objects.equals(toString(), o);
	}

	@Override
	public int hashCode() {
		return Objects.hash(toString());
	}
}

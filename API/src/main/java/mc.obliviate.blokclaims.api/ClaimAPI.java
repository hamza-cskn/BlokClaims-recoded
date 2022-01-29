package mc.obliviate.blokclaims.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public interface ClaimAPI {

	IClaim getClaimAt(final Chunk chunk);

	default IClaim getClaimAt(final Location location) {
		return getClaimAt(location.getChunk());
	}

	boolean isClaimWorld(final UUID worldUniqueId);

	default boolean isClaimWorld(final World world) {
		return isClaimWorld(world.getUID());
	}

	ChunkIdentifier getChunkID(final String world, final int x, final int z);

	default ChunkIdentifier getChunkID(Chunk chunk) {
		return getChunkID(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
	}

}

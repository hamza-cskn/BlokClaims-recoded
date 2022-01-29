package mc.obliviate.blokclaims.chunkid;

import org.bukkit.World;

public interface ChunkIdentifier {

	World getWorld();

	int getZ();

	int getX();

}

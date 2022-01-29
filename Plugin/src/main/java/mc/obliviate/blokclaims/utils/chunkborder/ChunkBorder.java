package mc.obliviate.blokclaims.utils.chunkborder;

import com.hakan.borderapi.api.HBorderColor;
import com.hakan.borderapi.api.HWorldBorder;
import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.chunkid.ChunkID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChunkBorder {

	private final BlokClaims plugin;

	public ChunkBorder(BlokClaims plugin) {
		this.plugin = plugin;
	}

	public void sendChunkPacket(Player player, ChunkID chunkID) {
		final HWorldBorder hWorldBorder = BlokClaims.getBorderAPI().getBorderCreator().setCenter(getChunkCenter(chunkID)).setSize(16).setColor(HBorderColor.BLUE).setDamageAmount(0).setWarningDistance(0).setWarningTime(1).create();

		hWorldBorder.send(player);
		new BukkitRunnable() {
			@Override
			public void run() {
				hWorldBorder.remove(player);
			}
		}.runTaskLater(plugin, 20 * 3);


	}

	private Location getChunkCenter(ChunkID chunkID) {
		World world = chunkID.getWorld();
		int x = chunkID.getX();
		int z = chunkID.getZ();
		return new Location(world, x * 16 + 8, 0, z * 16 + 8);
	}
}

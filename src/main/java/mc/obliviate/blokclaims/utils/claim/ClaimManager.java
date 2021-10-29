package mc.obliviate.blokclaims.utils.claim;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.DataHandler;
import mc.obliviate.blokclaims.utils.debug.Debug;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static mc.obliviate.blokclaims.BlokClaims.useHolographicDisplay;
import static mc.obliviate.blokclaims.utils.claim.ClaimUtils.isClaimWorld;

public class ClaimManager {

	private final BlokClaims plugin;
	private final DataHandler dataHandler;

	public ClaimManager(BlokClaims plugin) {
		this.plugin = plugin;
		this.dataHandler = plugin.getDataHandler();
	}


	// GET CLAIM_DATA
	public ClaimData getClaimData(Location location) {
		return getClaimData(ClaimUtils.getChunkID(location.getChunk()));
	}

	public ClaimData getClaimData(String chunkStringID) {
		return getClaimData(new ChunkID(chunkStringID));
	}

	public ClaimData getClaimData(ChunkID chunk) {
		ChunkID chunkID = dataHandler.getAllChunkList().get(chunk);
		if (chunkID == null) return null;
		Debug.log(chunkID + "-- is main chunk id", true);
		return dataHandler.getAllClaimDataList().get(chunkID);
	}

	public boolean isInOwnClaim(Player p) {
		return p != null && getClaimData(p.getLocation()).getOwner().equals(p.getUniqueId());
	}


	public void createClaim(Player owner, Location mainBlock) {

		mainBlock = mainBlock.getBlock().getLocation();
		ClaimData data = getClaimData(mainBlock);

		if (data == null) {
			if (isClaimWorld(mainBlock.getWorld())) {
				ChunkID mainChunk = ClaimUtils.getChunkID(mainBlock.getChunk());

				List<UUID> memberList = new ArrayList<>();
				List<ChunkID> chunkList = new ArrayList<>();
				memberList.add(owner.getUniqueId());
				ChunkID chunkID = ClaimUtils.getChunkID(mainBlock.getChunk());
				Bukkit.broadcastMessage(chunkID + "");
				dataHandler.getAllChunkList().put(chunkID, chunkID);
				Bukkit.broadcastMessage(dataHandler.getAllChunkList() + "");
				chunkList.add(chunkID);


				int energy = plugin.getConfigHandler().getConfig().getInt("first-claim-time", 43200);
				data = new ClaimData(plugin, owner.getUniqueId(), memberList, chunkList, mainBlock, chunkID, energy, null, null);
				dataHandler.getAllClaimDataList().put(mainChunk, data);


				mainBlock.getBlock().setType(Material.BEDROCK);

				if (useHolographicDisplay) {
					data.updateHologram();
				}
			} else {
				owner.sendMessage(Message.getConfigMessage("you-can-it-in-this-world"));

			}
		} else {
			owner.sendMessage(Message.getConfigMessage("this-chunk-already-claimed"));

		}
	}

	public ChunkID getCurrentChunkID(Player p) {
		//TODO replace null as playerCurrentClaimData.get(p.getUniqueId());
		ChunkID chunkID = null;
		if (chunkID != null) return chunkID;
		return ClaimUtils.getChunkID(p.getLocation());
	}


	public void deleteClaim(ClaimData cd) {

		if (cd == null) {
			Bukkit.getLogger().severe("BlokClaims tried to delete a null claim data.");
			return;
		}
		if (dataHandler.getAllClaimDataList().containsKey(cd.getClaimID())) {
			//ASYNC koktu buralar
			BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				plugin.getSqlManager().deleteClaimData(cd.getClaimID());
				//Burası da SYNC kokuyo
				Bukkit.getScheduler().runTask(plugin, () -> {
					Bukkit.broadcastMessage("Bir claim'in süresi doldu! ");
					cd.getMainBlock().getBlock().setType(Material.AIR);
					if (cd.getHologram() != null) cd.getHologram().delete();
					for (ChunkID chunkID : cd.getChunkList()) {
						dataHandler.getAllChunkList().remove(chunkID);
					}
					dataHandler.getAllClaimDataList().remove(cd.getClaimID());
				});


			});
			Bukkit.broadcastMessage("deleted a claim data. sync: " + task.isSync() + ". " + task.getTaskId());
		} else {
			Bukkit.getLogger().severe("BlokClaims tried a claim data but claimDataList does not contains it.");
		}

	}


}

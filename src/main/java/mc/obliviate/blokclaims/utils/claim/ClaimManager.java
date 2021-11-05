package mc.obliviate.blokclaims.utils.claim;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.handlers.DataHandler;
import mc.obliviate.blokclaims.member.ClaimMember;
import mc.obliviate.blokclaims.utils.debug.Debug;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
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
	public Claim getClaimData(Location location) {
		return getClaimData(ClaimUtils.getChunkID(location.getChunk()));
	}

	public Claim getClaimData(String chunkStringID) {
		return getClaimData(new ChunkID(chunkStringID));
	}

	public Claim getClaimData(ChunkID chunk) {
		final ChunkID chunkID = dataHandler.getAllChunkList().get(chunk);
		if (chunkID == null) return null;
		Debug.log(chunkID + "-- is main chunk id", true);
		return dataHandler.getAllClaimDataList().get(chunkID);
	}

	public boolean isInOwnClaim(Player p) {
		return p != null && getClaimData(p.getLocation()).getOwner().equals(p.getUniqueId());
	}


	public void createClaim(Player owner, Location mainBlock) {

		mainBlock = mainBlock.getBlock().getLocation();
		Claim data = getClaimData(mainBlock);

		if (data == null) {
			if (isClaimWorld(mainBlock.getWorld())) {
				ChunkID mainChunk = ClaimUtils.getChunkID(mainBlock.getChunk());

				final HashMap<UUID, ClaimMember> memberList = new HashMap<>();
				final List<ChunkID> chunkList = new ArrayList<>();
				memberList.put(owner.getUniqueId(), new ClaimMember(owner.getUniqueId()));
				ChunkID chunkID = ClaimUtils.getChunkID(mainBlock.getChunk());
				dataHandler.getAllChunkList().put(chunkID, chunkID);
				chunkList.add(chunkID);


				int energy = plugin.getConfigHandler().getConfig().getInt("first-claim-time", 43200);
				data = new Claim(plugin, owner.getUniqueId(), memberList, chunkList, mainBlock, chunkID, energy, null);
				dataHandler.getAllClaimDataList().put(mainChunk, data);


				mainBlock.getBlock().setType(Material.BEDROCK);

				if (useHolographicDisplay) {
					data.updateHologram(plugin);
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
		return ClaimUtils.getChunkID(p.getLocation());
	}


	public void deleteClaim(Claim cd) {
		if (cd == null) {
			Bukkit.getLogger().severe("BlokClaims tried to delete a null claim data.");
			return;
		}
		if (dataHandler.getAllClaimDataList().containsKey(cd.getClaimID())) {
			//ASYNC koktu buralar
			final BukkitTask task = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				plugin.getSqlManager().deleteClaimData(cd.getClaimID());
				//Burası da SYNC kokuyo
				Bukkit.getScheduler().runTask(plugin, () -> {
					Bukkit.broadcastMessage("Bir claim'in süresi doldu! ");
					cd.getMainBlock().getBlock().setType(Material.AIR);
					if (cd.getHologram(plugin) != null) cd.getHologram(plugin).delete();
					for (ChunkID chunkID : cd.getChunkList()) {
						dataHandler.getAllChunkList().remove(chunkID);
					}
					dataHandler.getAllClaimDataList().remove(cd.getClaimID());
				});
			});
			Bukkit.broadcastMessage("Deleted a claim data. sync: " + task.isSync() + ". " + task.getTaskId());
		} else {
			Bukkit.getLogger().severe("BlokClaims tried a claim data but claimDataList does not contains it.");
		}

	}


}

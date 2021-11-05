package mc.obliviate.blokclaims.claim;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.energy.Energy;
import mc.obliviate.blokclaims.homes.ClaimHome;
import mc.obliviate.blokclaims.invite.Invite;
import mc.obliviate.blokclaims.member.ClaimMember;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.debug.Debug;
import mc.obliviate.blokclaims.utils.energy.EnergyUtils;
import mc.obliviate.blokclaims.utils.timer.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class Claim {

	private final ChunkID claimID;
	private final HashMap<UUID, ClaimMember> members;
	private final List<ChunkID> chunkList;
	private final Energy energy;
	private final HashMap<String, Invite> invites = new HashMap<>();
	private UUID owner;
	private List<ClaimHome> homeList;
	private Location mainBlock;
	private long time;


	public Claim(BlokClaims plugin, UUID owner,
	             HashMap<UUID, ClaimMember> members,
	             List<ChunkID> chunkList,
	             Location mainBlock,
	             ChunkID claimID,
	             long energy,
	             List<ClaimHome> homeList) {
		this.homeList = homeList;
		this.owner = owner;
		this.members = members;
		this.chunkList = chunkList;
		this.claimID = claimID;
		this.mainBlock = mainBlock;
		this.energy = new Energy(getClaimID(), energy);
		setTime(plugin, EnergyUtils.convertEnergyToTime(chunkList.size(), this.energy));
	}


	public Energy getEnergy() {
		return energy;
	}

	public void setEnergy(BlokClaims plugin, long energy) {
		this.energy.setEnergy(energy);
		setTime(plugin, EnergyUtils.convertEnergyToTime(chunkList.size(), this.energy));
		if (this.energy.getAmount() <= 0) plugin.getClaimManager().deleteClaim(this);
	}

	public void updateTimeConversion(BlokClaims plugin) {
		setTime(plugin, EnergyUtils.convertEnergyToTime(chunkList.size(), this.energy));
	}

	public void updateEnergyDecrement(BlokClaims plugin) {
		getEnergy().setChunks(chunkList.size());
		updateTimeConversion(plugin);
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public HashMap<UUID, ClaimMember> getMembers() {
		return members;
	}

	public List<ChunkID> getChunkList() {
		return chunkList;
	}

	public ChunkID getClaimID() {
		return claimID;
	}

	public Location getMainBlock() {
		return mainBlock;
	}

	public void setMainBlock(BlokClaims plugin, Location mainBlock) {
		this.mainBlock.getBlock().setType(Material.AIR);
		this.mainBlock = mainBlock.getBlock().getLocation();
		mainBlock.getBlock().setType(Material.BEDROCK);
		updateHologram(plugin,true);
	}

	public List<ClaimHome> getHomeList() {
		return homeList;
	}

	public boolean addHome(ClaimHome home) {
		if (homeList == null) {
			homeList = new ArrayList<>();
		} else {
			for (ClaimHome homes : homeList) {
				if (homes == null) Bukkit.getLogger().warning("[BlokClaims] A home is null - claimID: " + getClaimID());
				else if (home == null)
					Bukkit.getLogger().warning("[BlokClaims] Req home is null - claimID: " + getClaimID());
				else if (homes.getName().equalsIgnoreCase(home.getName())) return false;

			}
		}
		homeList.add(home);
		return true;
	}

	public void removeHome(ClaimHome home) {
		homeList.remove(home);
	}

	public long getTime() {
		return time;
	}

	private void setTime(BlokClaims plugin, long time) {
		this.time = time;
		updateHologram(plugin);
	}

	public void addMember(UUID newMember) {
		if (members.containsKey(newMember)) return;
		this.members.put(newMember, new ClaimMember(newMember));
	}

	public void removeMember(UUID member) {
		if (member.equals(owner)) return;
		members.remove(member);
	}

	public void addChunk(BlokClaims plugin, ChunkID newChunk) {
		if (!this.chunkList.contains(newChunk)) {
			this.chunkList.add(newChunk);
			plugin.getDataHandler().getAllChunkList().put(newChunk, getClaimID());
			updateEnergyDecrement(plugin);
		}
	}

	public boolean removeChunk(BlokClaims plugin, ChunkID chunk) {
		if (this.chunkList.contains(chunk)) {
			this.chunkList.remove(chunk);
			plugin.getDataHandler().getAllChunkList().remove(chunk);
			updateEnergyDecrement(plugin);
			return true;
		}
		return false;
	}

	public Hologram getHologram(BlokClaims plugin) {
		plugin.getHologramHandler().getHologramList().remove(null);
		for (Hologram holo : HologramsAPI.getHolograms(plugin)) {
			if (!holo.getLocation().getWorld().isChunkLoaded(holo.getLocation().getBlockX() / 16, holo.getLocation().getBlockZ() / 16))
				return null;
			if (getClaimID().equals(ClaimUtils.getChunkID(holo.getLocation().getChunk()))) {
				return holo;

			}

		}
		return null;

	}

	public void removeHolograms(BlokClaims plugin) {
		for (final Hologram holo : HologramsAPI.getHolograms(plugin)) {
			if (holo.getLocation().getWorld().isChunkLoaded(holo.getLocation().getBlockX() / 16, holo.getLocation().getBlockZ() / 16)) {
				if (getClaimID().equals(ClaimUtils.getChunkID(holo.getLocation().getChunk()))) {
					holo.delete();
				}
			} else {
				Debug.log("chunk not loaded: " + holo.getLocation());
			}
		}
	}

	public void updateHologram(BlokClaims plugin) {
		updateHologram(plugin, false);
	}

	public void updateHologram(final BlokClaims plugin, final boolean delete) {
		Hologram holo = getHologram(plugin);
		final Location mainBlock = getMainBlock();
		if (delete && holo != null) {
			plugin.getHologramHandler().getHologramList().remove(holo);
			removeHolograms(plugin);
			//holo.delete();
		}

		if (holo == null || holo.isDeleted()) {
			if (mainBlock != null) {
				if (!mainBlock.getWorld().isChunkLoaded(mainBlock.getBlockX() / 16, mainBlock.getBlockZ() / 16))
					return;
				holo = HologramsAPI.createHologram(plugin, mainBlock.clone().add(0.5, 2, 0.5));
				plugin.getHologramHandler().getHologramList().add(holo);
			} else {
				Debug.log("Main block is null..", true);
				return;
			}
		} else {
			holo.clearLines();
		}

		final String owner = Bukkit.getOfflinePlayer(getOwner()).getName();
		holo.appendTextLine("Sahip: " + ChatColor.COLOR_CHAR + "b" + owner);
		holo.appendTextLine("Süre: " + ChatColor.COLOR_CHAR + "3" + TimerUtils.getFormattedTime(getTime()));
		holo.appendTextLine("Enerji: " + ChatColor.COLOR_CHAR + "e" + getEnergy().getAmount() + " Enerji");
	}

	public HashMap<String, Invite> getInvites() {
		return invites;
	}


	public void addInvite(Invite invite) {
		if (invite == null || invite.isExpired()) return;
		String uuid = invite.getTarget().getUniqueId().toString();
		invites.put(uuid, invite);

	}

	public void removeInvite(String uuid) {
		invites.remove(uuid);
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Claim)) return false;
		Claim claim = (Claim) o;
		return getClaimID().equals(claim.getClaimID());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClaimID());
	}

}

package mc.obliviate.blokclaims.claim;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.energy.Energy;
import mc.obliviate.blokclaims.homes.ClaimHome;
import mc.obliviate.blokclaims.invite.Invite;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.debug.Debug;
import mc.obliviate.blokclaims.utils.energy.EnergyUtils;
import mc.obliviate.blokclaims.utils.timer.TimerUtils;
import org.bukkit.*;

import java.util.*;

public class ClaimData {

    private final BlokClaims plugin;
    private final ChunkID claimID;
    private UUID owner;
    private final List<UUID> memberList;
    private final List<ChunkID> chunkList;
    private List<ClaimHome> homeList;
    private Location mainBlock;
    private final Energy energy;
    private long time;
    private HashMap<UUID, ClaimPermission> permissionStates = new HashMap<>();
    private final HashMap<String, Invite> invites = new HashMap<>();


    //TODO Plugin instance!
    public ClaimData(BlokClaims plugin,
                     UUID owner,
                     List<UUID> memberList,
                     List<ChunkID> chunkList,
                     Location mainBlock,
                     ChunkID claimID,
                     long energy,
                     HashMap<UUID, ClaimPermission> permissionState,
                     List<ClaimHome> homeList) {
        this.plugin = plugin;
        this.homeList = homeList;
        this.owner = owner;
        this.memberList = memberList;
        this.chunkList = chunkList;
        this.claimID = claimID;
        this.mainBlock = mainBlock;
        this.energy = new Energy(getClaimID(), energy);
        setTime(EnergyUtils.convertEnergyToTime(chunkList.size(), this.energy));
        if (permissionState != null) this.permissionStates = permissionState;
    }


    public Energy getEnergy() {
        return energy;
    }

    public void updateTimeConvertion() {
        setTime(EnergyUtils.convertEnergyToTime(chunkList.size(), this.energy));
    }


    public void updateEnergyDecreament() {
        getEnergy().setChunks(chunkList.size());
        updateTimeConvertion();
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }


    public List<UUID> getMemberList() {
        return memberList;
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


    public void setMainBlock(Location mainBlock) {
        this.mainBlock.getBlock().setType(Material.AIR);
        this.mainBlock = mainBlock.getBlock().getLocation();
        mainBlock.getBlock().setType(Material.BEDROCK);
        updateHologram(true);
    }

    public void setEnergy(long energy) {
        this.energy.setEnergy(energy);
        setTime(EnergyUtils.convertEnergyToTime(chunkList.size(), this.energy));
        if (this.energy.getAmount() <= 0) plugin.getClaimCore().deleteClaim(this);
    }

    private void setTime(long time) {
        this.time = time;
        updateHologram();
    }

    public void addMember(UUID newMember) {
        if (!this.memberList.contains(newMember)) {
            this.memberList.add(newMember);
        }
    }

    public void removeMember(UUID member) {
        if (member.equals(owner)) return;
        if (permissionStates != null) permissionStates.remove(member.toString());
        this.memberList.remove(member);
    }

    public void addChunk(ChunkID newChunk) {
        if (!this.chunkList.contains(newChunk)) {
            this.chunkList.add(newChunk);
            plugin.getDataHandler().getAllChunkList().put(newChunk, getClaimID());
            updateEnergyDecreament();
        }
    }

    public boolean removeChunk(ChunkID chunk) {
        if (this.chunkList.contains(chunk)) {
            this.chunkList.remove(chunk);
            plugin.getDataHandler().getAllChunkList().remove(chunk);
            updateEnergyDecreament();
            return true;
        }
        return false;
    }

    public Hologram getHologram() {
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

    public void removeHolograms() {
        for (Hologram holo : HologramsAPI.getHolograms(plugin)) {
            if (holo.getLocation().getWorld().isChunkLoaded(holo.getLocation().getBlockX() / 16, holo.getLocation().getBlockZ() / 16)) {
                if (getClaimID().equals(ClaimUtils.getChunkID(holo.getLocation().getChunk()))) {
                    holo.delete();
                }
            } else {
                Debug.log("chunk not loaded: " + holo.getLocation());
            }
        }
    }

    public void updateHologram() {
        updateHologram(false);
    }

    public void updateHologram(boolean delete) {


        Hologram holo = getHologram();
        Location mainBlock = getMainBlock();
        if (delete && holo != null) {
            plugin.getHologramHandler().getHologramList().remove(holo);
            removeHolograms();
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


        String owner = Bukkit.getOfflinePlayer(getOwner()).getName();
        holo.appendTextLine("Sahip: " + ChatColor.COLOR_CHAR + "b" + owner);
        holo.appendTextLine("Süre: " + ChatColor.COLOR_CHAR + "3" + TimerUtils.getFormattedTime(getTime()));
        holo.appendTextLine("Enerji: " + ChatColor.COLOR_CHAR + "e" + getEnergy().getAmount() + " Enerji");


    }


    public ClaimPermission getPermissionState(OfflinePlayer member) {
        return getPermissionState(member.getUniqueId());
    }

    public ClaimPermission getPermissionState(UUID member) {
        if (permissionStates == null) return new ClaimPermission(plugin, member, claimID, null);
        return permissionStates.get(member);
    }

    public void addPermission(UUID uuid, ClaimPermissionType permission) {
        ClaimPermission cps = permissionStates.get(uuid);
        if (cps == null) {
            List<ClaimPermissionType> permissions = new ArrayList<>();
            permissions.add(permission);
            cps = new ClaimPermission(plugin, uuid, claimID, permissions);
            permissionStates.put(uuid, cps);
        } else {
            cps.addPermission(permission);
        }
    }

    public void removePermission(UUID uuid, ClaimPermissionType permission) {
        ClaimPermission cps = permissionStates.get(uuid);
        if (cps == null) return;
        cps.removePermission(permission);
        if (cps.getPermissions().size() == 0) permissionStates.remove(uuid);
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
        if (!(o instanceof ClaimData)) return false;
        ClaimData claimData = (ClaimData) o;
        return getClaimID().equals(claimData.getClaimID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClaimID());
    }

}

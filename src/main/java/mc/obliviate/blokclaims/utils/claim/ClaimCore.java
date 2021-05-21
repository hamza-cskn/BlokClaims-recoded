package mc.obliviate.blokclaims.utils.claim;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.DataHandler;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

import static mc.obliviate.blokclaims.BlokClaims.useHolographicDisplay;
import static mc.obliviate.blokclaims.utils.claim.ClaimUtils.isClaimWorld;

public class ClaimCore {

    private final BlokClaims plugin;
    private final DataHandler dataHandler;

    public ClaimCore(BlokClaims plugin) {
        this.plugin = plugin;
        this.dataHandler = plugin.getDataHandler();
    }


    // GET CLAIM_DATA
    public ClaimData getClaimData(Location location) {
        return getClaimData(ClaimUtils.getChunkID(location.getChunk()).toString());
    }

    public ClaimData getClaimData(String chunkStringID) {
        return dataHandler.getAllClaimDataList().get(dataHandler.getAllChunkList().get(new ChunkID(chunkStringID)));
    }

    public ClaimData getClaimData(ChunkID chunk) {
        return dataHandler.getAllClaimDataList().get(
                dataHandler.getAllChunkList().get(chunk)
        );
    }
    public boolean isInOwnClaim(Player p) {
        return p != null && getClaimData(p.getLocation()).getOwner().equals(p);
    }



    public void createClaim(Player owner, Location mainBlock) {

        mainBlock = mainBlock.getBlock().getLocation();
        ClaimData data = getClaimData(mainBlock);

        if (data == null) {
            if (isClaimWorld(mainBlock.getWorld())) {
                ChunkID mainChunk = ClaimUtils.getChunkID(mainBlock.getChunk());

                List<OfflinePlayer> memberList = new ArrayList<>();
                List<ChunkID> chunkList = new ArrayList<>();
                memberList.add(owner);
                ChunkID chunkID = ClaimUtils.getChunkID(mainBlock.getChunk());
                dataHandler.getAllChunkList().put(chunkID, chunkID);
                chunkList.add(chunkID);


                int energy = plugin.getConfigHandler().getConfig().getInt("first-claim-time", 43200);
                data = new ClaimData(plugin, owner, memberList, chunkList, mainBlock, chunkID, energy, null, null);
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
                if (plugin.getSqlHandler().deleteClaimData(cd)) {
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
                }

            });
            Bukkit.broadcastMessage("deleted a claim data. sync: " + task.isSync() + ". " + task.getTaskId());
        } else {
            Bukkit.getLogger().severe("BlokClaims tried a claim data but claimDataList does not contains it.");
        }

    }




}

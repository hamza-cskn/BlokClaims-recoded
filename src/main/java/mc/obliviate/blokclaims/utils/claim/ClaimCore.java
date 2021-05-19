package mc.obliviate.blokclaims.utils.claim;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.DataHandler;
import mc.obliviate.blokclaims.utils.debug.Debug;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static mc.obliviate.blokclaims.BlokClaims.useHolographicDisplay;

public class ClaimCore {

    private final BlokClaims plugin;
    private final DataHandler dataHandler;

    public ClaimCore(BlokClaims plugin) {
        this.plugin = plugin;
        this.dataHandler = plugin.getDataHandler();
    }

    // GET CHUNK_ID
    public static ChunkID getChunkID(Chunk chunk) {
        if (chunk != null) {
            return new ChunkID(chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ());
        }
        Debug.log("ChunkID(chunk) is null");
        return null;
    }

    public static ChunkID getChunkID(CompletableFuture<Chunk> chunk) {
        if (chunk != null) {
            try {
                return new ChunkID(chunk.get().getWorld().getName() + "," + chunk.get().getX() + "," + chunk.get().getZ());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        Debug.log("ChunkID(async chunk) is null");
        return null;
    }

    public static ChunkID getChunkID(Location loc) {
        if (loc != null) {
            return new ChunkID(loc.getWorld().getName() + "," + (int) loc.getX() / 16 + "," + (int) loc.getZ() / 16);
        }
        Debug.log("ChunkID(loc) is null");
        return null;
    }

    public static ChunkID getChunkID(World world, int x, int z, boolean isLocation) {
        int n = 1;
        if (isLocation) {
            n = 16;
        }
        if (world != null) {
            return new ChunkID(world.getName() + "," + x / n + "," + z / n);
        }
        Debug.log("ChunkID(wxz) is null");
        return null;
    }

    // GET CLAIM_DATA
    public ClaimData getClaimData(Location location) {
        return getClaimData(ClaimCore.getChunkID(location.getChunk()).toString());
    }

    public ClaimData getClaimData(String chunkStringID) {
        return dataHandler.getAllClaimDataList().get(dataHandler.getAllChunkList().get(new ChunkID(chunkStringID)));
    }

    public ClaimData getClaimData(ChunkID chunk) {
        return getClaimData(chunk.toString());
    }


    //
    public void broadcastClaimMembers(ClaimData cd, String str) {
        for (UUID uuid : cd.getMemberList()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;
            p.sendMessage(str);
        }
    }

    public static boolean isClaimWorld(World world) {
        return BlokClaims.getWorldList().contains(world.getName());
    }

    public boolean isInOwnClaim(Player p) {
        return p != null && getClaimData(p.getLocation()).getOwner().equals(p);
    }

    public ChunkID getCurrentChunkID(Player p) {
        ChunkID chunkID = playerCurrentClaimData.get(p.getUniqueId());
        if (chunkID != null) return chunkID;
        return getChunkID(p.getLocation());
    }

    public void createClaim(Player owner, Location mainBlock) {

        mainBlock = mainBlock.getBlock().getLocation();
        ClaimData data = getClaimData(mainBlock);

        if (data == null) {
            if (isClaimWorld(mainBlock.getWorld())) {
                ChunkID mainChunk = ClaimCore.getChunkID(mainBlock.getChunk());

                List<OfflinePlayer> memberList = new ArrayList<>();
                List<ChunkID> chunkList = new ArrayList<>();
                memberList.add(owner);
                ChunkID chunkID = ClaimCore.getChunkID(mainBlock.getChunk());
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

package mc.obliviate.blokclaims.handlers;

import mc.obliviate.blokclaims.chunkid.ChunkID;
import mc.obliviate.blokclaims.claim.Claim;

import java.util.HashMap;

public class DataHandler {


    /**
     * Main Chunk, ClaimData
     */
    private final HashMap<ChunkID, Claim> allClaimDataList = new HashMap<>();

    /**
     * Any Chunk, Main Chunk
     */
    private final HashMap<ChunkID, ChunkID> allChunkList = new HashMap<>();

    public HashMap<ChunkID, ChunkID> getAllChunkList() {
        return allChunkList;
    }

    public HashMap<ChunkID, Claim> getAllClaimDataList() {
        return allClaimDataList;
    }

    public void setAllClaimDataList(final HashMap<ChunkID, Claim> newState) {
        if (newState != null) {
            allClaimDataList.clear();
            allClaimDataList.putAll(newState);
        }
    }

    public void setAllChunkList(final HashMap<ChunkID, ChunkID> newState) {
        if (newState != null) {
            allChunkList.clear();
            allChunkList.putAll(newState);
        }
    }
}

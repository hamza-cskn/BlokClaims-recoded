package mc.obliviate.blokclaims.handlers;

import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.ClaimData;

import java.util.HashMap;

public class DataHandler {


    /**
     * Main Chunk, ClaimData
     */
    private final HashMap<ChunkID, ClaimData> allClaimDataList = new HashMap<>();

    /**
     * Any Chunk, Main Chunk
     */
    private final HashMap<ChunkID, ChunkID> allChunkList = new HashMap<>();

    public HashMap<ChunkID, ChunkID> getAllChunkList() {
        return allChunkList;
    }

    public HashMap<ChunkID, ClaimData> getAllClaimDataList() {
        return allClaimDataList;
    }

    public void setAllClaimDataList(HashMap<ChunkID, ClaimData> newState) {
        if (newState != null) {
            allClaimDataList.clear();
            allClaimDataList.putAll(newState);
        }
    }

    public void setAllChunkList(HashMap<ChunkID, ChunkID> newState) {
        if (newState != null) {
            allChunkList.clear();
            allChunkList.putAll(newState);
        }
    }
}

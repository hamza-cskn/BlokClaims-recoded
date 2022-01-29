package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
public class ExplosionListener extends ListenerHandler implements Listener {

    //TODO THIS LISTENER ARE NOT NEED LISTENER HANDLER
    public ExplosionListener(BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockExplosion(BlockExplodeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent e) {
        e.setCancelled(true);
        /*
        Debug.log("Entity Patlaması gerçekleşti!");
        List<Chunk> chunkList = new ArrayList<>();
        HashMap<Chunk, Boolean> checkedChunks = new HashMap<>();
        for (Block block : e.blockList().toArray(new Block[e.blockList().size()])) {
            if (!chunkList.contains(block.getChunk())) {
                chunkList.add(block.getChunk());
                checkedChunks.put(block.getChunk(), Main.allChunkList.containsKey(ClaimManager.getChunkID(block.getChunk()).toString()));
            }

            if (checkedChunks.get(block.getChunk())) {
                e.blockList().remove(block);
            }
        }
        */
    }

}

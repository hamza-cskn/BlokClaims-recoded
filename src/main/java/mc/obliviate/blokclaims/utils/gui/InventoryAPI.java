package mc.obliviate.blokclaims.utils.gui;

import mc.obliviate.blokclaims.BlokClaims;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class InventoryAPI {

    private final BlokClaims plugin;
    private final InventoryAPI instance;
    private final HashMap<UUID, GUI> players = new HashMap<>();

    public InventoryAPI(BlokClaims plugin){
        this.plugin = plugin;
        instance = this;

        plugin.getServer().getPluginManager().registerEvents(new InvListeners(this), plugin);
    }

    public InventoryAPI getInstance(){
        return instance;
    }

    public BlokClaims getPlugin() {
        return plugin;
    }

    public HashMap<UUID, GUI> getPlayers() {
        return players;
    }

    public GUI getPlayersCurrentGui(Player player) {
        return players.get(player.getUniqueId());
    }

}

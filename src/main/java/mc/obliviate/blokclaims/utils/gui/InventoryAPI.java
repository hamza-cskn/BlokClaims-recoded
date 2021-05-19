package mc.obliviate.blokclaims.utils.gui;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class InventoryAPI {

    private final Plugin plugin;
    private final InventoryAPI instance;
    private final HashMap<UUID, GUI> players = new HashMap<>();

    public InventoryAPI(Plugin plugin){
        this.plugin = plugin;
        instance = this;

        plugin.getServer().getPluginManager().registerEvents(new InvListeners(this), plugin);
    }

    public InventoryAPI getInstance(){
        return instance;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public HashMap<UUID, GUI> getPlayers() {
        return players;
    }

    public GUI getPlayersCurrentGui(Player player) {
        return players.get(player.getUniqueId());
    }

}

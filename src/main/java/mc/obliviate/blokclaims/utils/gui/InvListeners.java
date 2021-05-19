package mc.obliviate.blokclaims.utils.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InvListeners implements Listener {

    private final InventoryAPI inventoryAPI;

    public InvListeners(InventoryAPI inventoryAPI) {
        this.inventoryAPI = inventoryAPI;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        GUI openGui = inventoryAPI.getPlayersCurrentGui((Player) event.getWhoClicked());
        if (openGui == null) return;
        openGui.onClick(event);
        if (event.getClickedInventory() == null) return;
        int index = event.getRawSlot();
        if (event.getSlot() == index) {
            event.setCancelled(true);
        } else {
            switch (event.getAction()) {
                    //SHIFT CLICK etc.
                case MOVE_TO_OTHER_INVENTORY:
                    //DOUBLE CLICK WITH CURSOR
                case COLLECT_TO_CURSOR:
                    //SOMETIMES HACKED CLIENT CLICK etc.
                case UNKNOWN:
                    event.setCancelled(true);
            }
        }
        Hytem item = openGui.getItems().get(index);

        if (item == null) return;
        if (item.getAction() == null) {
            return;
        }

        item.getAction().onClick(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        GUI openGui = inventoryAPI.getPlayersCurrentGui(player);
        if (openGui == null) return;
        if (!event.getInventory().equals(openGui.getInventory())) return;
        openGui.setClosed(true);
        openGui.onClose(event);
        inventoryAPI.getPlayers().remove(player.getUniqueId());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        GUI openGui = inventoryAPI.getPlayersCurrentGui(player);
        if (openGui == null) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        GUI openGui = inventoryAPI.getPlayersCurrentGui(player);
        if (openGui == null) return;

        openGui.onOpen(event);
    }

}

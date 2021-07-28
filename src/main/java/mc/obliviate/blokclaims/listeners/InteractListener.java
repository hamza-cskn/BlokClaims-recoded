package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.gui.ClaimStoneGUI;
import mc.obliviate.blokclaims.utils.debug.Debug;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.Powerable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;

import java.util.Objects;

public class InteractListener extends ListenerHandler implements Listener {

    public InteractListener(BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteractEvent(PlayerInteractEvent e) {
        Debug.log("PlayerInteractEvent");
        if (!ClaimUtils.isClaimWorld(e.getPlayer().getWorld())) return;
        Debug.log("-3", true, Debug.DebugType.LIME);
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Debug.log("-2", true, Debug.DebugType.LIME);
            if (!Objects.equals(e.getHand(), EquipmentSlot.HAND)) return;
            Debug.log("-1", true, Debug.DebugType.LIME);
            if (e.getClickedBlock() == null) return;
            Debug.log("0: " + ClaimUtils.getChunkID(e.getClickedBlock().getLocation()), true, Debug.DebugType.LIME);
            ClaimData cd = cm.getClaimData(e.getClickedBlock().getLocation());
            if (cd == null) return;
            Material type = e.getClickedBlock().getType();
            Debug.log("1", true, Debug.DebugType.LIME);
            if (type.equals(Material.BEDROCK)) {
                Debug.log("2", true, Debug.DebugType.LIME);
                if (cd.getMemberList().contains(e.getPlayer().getUniqueId())) {
                    Debug.log("3", true, Debug.DebugType.LIME);
                    if (cd.getMainBlock().equals(e.getClickedBlock().getLocation())) {
                        Debug.log("4", true, Debug.DebugType.LIME);
                        //TODO Player current claim data
                        //BlokClaims.playerCurrentClaimData.put(e.getPlayer().getUniqueId(), cd.getClaimID());
                        //TODO Open main claim gui
                        new ClaimStoneGUI(plugin).open(e.getPlayer());
                        e.setCancelled(true);
                    }
                } else {
                    e.setCancelled(true);
                    e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.interact-cancel"));
                }

                //is it container?
            } else if (e.getClickedBlock().getState() instanceof InventoryHolder) {
                ClaimPermission cps = cd.getPermissionState(e.getPlayer());
                boolean permState = cps != null && cps.hasPermission("USE_CONTAINERS");

                if (!permState) {
                    e.setCancelled(true);
                    e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.use-containers-cancel"));

                }

            } else if (e.getClickedBlock().getBlockData() instanceof Powerable) {
                ClaimPermission cps = cd.getPermissionState(e.getPlayer());
                boolean permState = cps != null && cps.hasPermission("USE_POWERABLES");
                //Bukkit.broadcastMessage("Powerable: " + e.getClickedBlock().getType().toString());
                if (!permState) {
                    e.setCancelled(true);
                    e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.powerable-cancel"));
                    //e.getPlayer().sendMessage(Message.sendConfigMessage("claim-guard.powerable-cancel"));


                }
            }

        } else if (e.getAction().equals(Action.PHYSICAL)) {
            if (e.getClickedBlock() == null) return;
            ClaimData cd = cm.getClaimData(e.getClickedBlock().getLocation());
            if (cd == null) return;

            ClaimPermission cps = cd.getPermissionState(e.getPlayer());
            boolean permState;
            switch (e.getClickedBlock().getType()) {

                case FARMLAND:
                    permState = cps != null && cps.hasPermission("PLACE_BREAK_BLOCK");

                    if (!permState) {
                        e.setCancelled(true);
                        e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.farmland-destroy-cancel"));
                    }
                    break;
                case TRIPWIRE:
                case ACACIA_PRESSURE_PLATE:
                case BIRCH_PRESSURE_PLATE:
                case CRIMSON_PRESSURE_PLATE:
                case DARK_OAK_PRESSURE_PLATE:
                case HEAVY_WEIGHTED_PRESSURE_PLATE:
                case JUNGLE_PRESSURE_PLATE:
                case LIGHT_WEIGHTED_PRESSURE_PLATE:
                case OAK_PRESSURE_PLATE:
                case POLISHED_BLACKSTONE_PRESSURE_PLATE:
                case SPRUCE_PRESSURE_PLATE:
                case STONE_PRESSURE_PLATE:
                case WARPED_PRESSURE_PLATE:
                    permState = cps != null && cps.hasPermission("USE_POWERABLES");

                    if (!permState) {
                        e.setCancelled(true);
                        e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.powerable-cancel"));
                    }
                    break;
            }
        }
    }


}



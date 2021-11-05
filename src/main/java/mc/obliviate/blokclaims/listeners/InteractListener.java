package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.gui.ClaimStoneGUI;
import mc.obliviate.blokclaims.utils.debug.Debug;
import mc.obliviate.blokclaims.utils.message.Message;
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
        if (!ClaimUtils.isClaimWorld(e.getPlayer().getWorld())) return;

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {

            if (!Objects.equals(e.getHand(), EquipmentSlot.HAND)) return;

            if (e.getClickedBlock() == null) return;

            final Claim cd = getClaimUtils().getClaimManager().getClaimData(e.getClickedBlock().getLocation());
            if (cd == null) return;

            Material type = e.getClickedBlock().getType();

            if (type.equals(Material.BEDROCK)) {

                if (cd.getMembers().containsKey(e.getPlayer().getUniqueId())) {

                    if (cd.getMainBlock().equals(e.getClickedBlock().getLocation())) {

                        new ClaimStoneGUI(e.getPlayer()).open();
                        e.setCancelled(true);
                    }
                } else {
                    e.setCancelled(true);
                    e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.interact-cancel"));
                }

                //is it container?
            } else if (e.getClickedBlock().getState() instanceof InventoryHolder) {
                final boolean permState = checkPermission(e.getPlayer(), ClaimPermissionType.USE_CONTAINERS, e.getClickedBlock().getLocation());

                if (!permState) {
                    e.setCancelled(true);
                    e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.use-containers-cancel"));

                }

            } else if (e.getClickedBlock().getBlockData() instanceof Powerable) {
                final boolean permState = checkPermission(e.getPlayer(), ClaimPermissionType.USE_POWERABLES, e.getClickedBlock().getLocation());
                if (!permState) {
                    e.setCancelled(true);
                    e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.powerable-cancel"));
                    //e.getPlayer().sendMessage(Message.sendConfigMessage("claim-guard.powerable-cancel"));


                }
            }

        } else if (e.getAction().equals(Action.PHYSICAL)) {
            onPhysicalInteract(e);
        }
    }

    public void onPhysicalInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        switch (e.getClickedBlock().getType()) {

            case FARMLAND:
                final boolean farmPerm = checkPermission(e.getPlayer(), ClaimPermissionType.PLACE_BREAK_BLOCK, e.getClickedBlock().getLocation());

                if (!farmPerm) {
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
                final boolean platePerm = checkPermission(e.getPlayer(), ClaimPermissionType.USE_POWERABLES, e.getClickedBlock().getLocation());

                if (!platePerm) {
                    e.setCancelled(true);
                    e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.powerable-cancel"));
                }
                break;
        }
    }


}



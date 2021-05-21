package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.utils.claim.ClaimCore;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class EntityInteractListener extends ListenerHandler implements Listener {

    public EntityInteractListener(BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler
    public void onArmorStandManipule(PlayerArmorStandManipulateEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getRightClicked().getWorld())) return;
        ClaimData cd = cm.getClaimData(e.getRightClicked().getLocation());
        if (cd == null) return;
        ClaimPermission cps = cd.getPermissionState(e.getPlayer());
        boolean permState = cps != null && cps.hasPermission("ARMOR_STAND_INTERACT");

        if (permState) return;
        e.setCancelled(true);
        e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.armor-stand-interact-cancel"));
    }

    @EventHandler
    public void onItemFrameInteract(PlayerInteractEntityEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getRightClicked().getWorld())) return;
        if (e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
            ClaimData cd = cm.getClaimData(e.getRightClicked().getLocation());
            if (cd == null) return;
            ClaimPermission cps = cd.getPermissionState(e.getPlayer());
            boolean permState = cps != null && cps.hasPermission("ITEM_FRAME_INTERACT");
            if (permState) return;
            e.setCancelled(true);
            e.getPlayer().sendActionBar(Message.getConfigMessage("claim-guard.item-frame-interact-cancel"));
        }
    }

    //DESTROY EVENTS
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getEntity().getWorld())) return;
        if (!e.getDamager().getType().equals(EntityType.PLAYER)) return;

        ClaimData cd = cm.getClaimData(e.getEntity().getLocation());
        if (cd == null) return;
        Player p = (Player) e.getDamager();
        ClaimPermission cps = cd.getPermissionState(p);
        boolean permState;
        switch (e.getEntityType()) {
            case PAINTING:
            case ITEM_FRAME:
            case ARMOR_STAND:

                permState = cps != null && cps.hasPermission(e.getEntityType() + "_INTERACT");
                if (permState) return;
                e.setCancelled(true);
                String type = "";
                switch (e.getEntityType()) {
                    case ITEM_FRAME:
                        type = "item-frame";
                        break;
                    case ARMOR_STAND:
                        type = "armor-stand";
                        break;
                }
                p.sendActionBar(Message.getConfigMessage("claim-guard." + type + "-interact-cancel"));

                break;
            default:
                permState = cps != null && cps.hasPermission("INTERACT_MOBS");
                if (permState) return;
                e.setCancelled(true);
        }
    }

    //DESTROY EVENTS
    @EventHandler
    public void onHangingBreakEntity(HangingBreakByEntityEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getEntity().getWorld())) return;
        if (e.getRemover() != null && e.getRemover().getType().equals(EntityType.PLAYER)) {
            switch (e.getEntity().getType()) {
                case ITEM_FRAME:
                case ARMOR_STAND: //actually armor stands does not need it.
                case PAINTING:
                    ClaimData cd = cm.getClaimData(e.getEntity().getLocation());

                    if (cd == null) return;

                    Player p = (Player) e.getRemover();
                    ClaimPermission cps = cd.getPermissionState(p);
                    boolean permState = cps != null && cps.hasPermission("USE_BUCKET");

                    if (permState) return;
                    e.setCancelled(true);
                    p.sendActionBar(Message.getConfigMessage("claim-guard.item-frame-interact-cancel"));
                    break;
            }
        }
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getEntity().getWorld())) return;
        ClaimData cd = cm.getClaimData(e.getEntity().getLocation());
        if (cd == null) return;
        ClaimPermission cps = cd.getPermissionState(e.getPlayer());
        boolean permState = cps != null && cps.hasPermission("INTERACT_MOBS");
        if (permState) return;
        e.setCancelled(true);

    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getEntity().getWorld())) return;
        if (e.getTarget() != null && e.getTarget().getType().equals(EntityType.PLAYER)) {
            Player p = (Player) e.getTarget();
            ClaimData cd = cm.getClaimData(e.getEntity().getLocation());
            if (cd == null) return;
            ClaimPermission cps = cd.getPermissionState(p);
            boolean permState = cps != null && cps.hasPermission("INTERACT_MOBS");
            if (permState) return;
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMount(EntityMountEvent e) {
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            ClaimData cd = cm.getClaimData(e.getMount().getLocation());
            if (cd == null) return;
            ClaimPermission cps = cd.getPermissionState((Player) e.getEntity());
            boolean permState = cps != null && cps.hasPermission("INTERACT_MOBS");
            if (permState) return;
            e.setCancelled(true);
        }
    }

}

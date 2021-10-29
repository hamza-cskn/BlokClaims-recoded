package mc.obliviate.blokclaims.listeners;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.handlers.ListenerHandler;
import mc.obliviate.blokclaims.permission.ClaimPermission;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ProjectileListener extends ListenerHandler implements Listener {

    public ProjectileListener(BlokClaims plugin) {
        super(plugin);
    }

    @EventHandler
    public void onProjectileHitEntity(EntityDamageByEntityEvent e) {
        if (!ClaimUtils.isClaimWorld(e.getEntity().getWorld())) return;

        if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            Projectile projectile = (Projectile) e.getDamager();
            if (projectile.getShooter() instanceof Player) {
                ClaimData cd = cm.getClaimData(e.getEntity().getLocation());
                if (cd != null) {
                    ClaimPermission cps = cd.getPermissionState((Player) projectile.getShooter());
                    if (cps != null && cps.hasPermission(ClaimPermissionType.INTERACT_MOBS)) return;
                    e.setCancelled(true);
                }
            }
        }
    }

}

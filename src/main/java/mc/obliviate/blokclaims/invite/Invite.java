package mc.obliviate.blokclaims.invite;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.utils.debug.Debug;
import mc.obliviate.blokclaims.utils.message.Message;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class Invite {

    private boolean expired;
    private ClaimData cd;
    private OfflinePlayer target;
    private OfflinePlayer inviter;
    private Boolean answer = null;
    private int expireTime;
    private long invitedTime;
    private final BlokClaims plugin;

    public Invite(BlokClaims plugin, Player inviter, OfflinePlayer invited, ClaimData cd) {
        this(plugin, inviter, invited, cd, plugin.getConfigHandler().getConfig().getInt("invite-expire-time", 120));
    }

    public Invite(BlokClaims plugin, Player inviter, OfflinePlayer invited, ClaimData cd, int expireTime) {
        this.plugin = plugin;

        if (cd.getInvites().containsKey(invited.getUniqueId().toString())) {
            inviter.sendMessage(Message.getConfigMessage("invite.already-invited")
                    .replace("{inviter}", invited.getName()));
            expired = true;
            return;
        }

        if (!invited.isOnline()) {
            inviter.sendMessage(Message.getConfigMessage("target-is-not-online"));
            expired = true;
            onExpire();
            return;
        }
        Player target = (Player) invited;
        this.expireTime = expireTime;
        this.target = target;
        this.cd = cd;
        this.inviter = inviter;
        this.invitedTime = System.currentTimeMillis();
        Debug.log(invitedTime + "..");

        inviter.sendMessage(Message.getConfigMessage("invite.target-has-invited")
                .replace("{claimID}", cd.getClaimID().toString())
                .replace("{target}", target.getName())
                .replace("{expire_time}", expireTime + ""));
        target.sendMessage(Message.getConfigMessage("invite.you-invited")
                .replace("{inviter}", inviter.getName())
                .replace("{expire_time}", expireTime + ""));

        TextComponent button = new TextComponent(Message.color("&a&l[KABUL ET]"));
        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim katıl"));
        TextComponent button2 = new TextComponent(Message.color(" &c&l[REDDET]"));
        button2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim reddet"));

        target.spigot().sendMessage(button, button2);

        expired = false;
        new BukkitRunnable(){
            public void run() {
                if (!expired && answer == null) {
                    expired = true;
                    onExpire();
                    target.sendMessage(Message.getConfigMessage("invite.invite-expired")
                            .replace("{inviter}", inviter.getName()));
                }
            }}.runTaskLater(plugin, expireTime* 20L);


    }

    public int getExpireTime() {
        return this.expireTime;
    }

    private void onExpire() {
        cd.removeInvite(target.getUniqueId().toString());
    }

    public String getFormattedExpireTimeLeft() {
        long time = (invitedTime + (1000L * getExpireTime()) - System.currentTimeMillis())/1000;
        long minute;
        minute = TimeUnit.MINUTES.toHours(time);
        time -= (minute * 60);


        return minute + "dk " + time + "sn";
    }

    public boolean isExpired() {
        return expired;
    }

    public ClaimData getClaimData() {
        return cd;
    }

    public OfflinePlayer getInviter() {
        return inviter;
    }

    public OfflinePlayer getTarget() {
        return target;
    }

    public Player getOnlineInviter() {
        return (Player) inviter;
    }

    public Player getOnlineTarget() {
        return (Player) target;
    }

    public void setResult(boolean answer) {
        expired = true;
        onExpire();
        this.answer = answer;

        //ACCEPT
        if (answer) {
            getOnlineInviter().sendMessage(Message.getConfigMessage("invite.target-accepted-the-invite"));
            Player player = getOnlineTarget();
            player.sendMessage(Message.getConfigMessage("invite.successfully-accepted"));

            cd.addMember(getTarget());
        }
        //DECLINE
        else {
            getOnlineInviter().sendMessage(Message.getConfigMessage("invite.target-declined-the-invite"));
            getOnlineTarget().sendMessage(Message.getConfigMessage("invite.successfully-declined"));

        }

    }


}

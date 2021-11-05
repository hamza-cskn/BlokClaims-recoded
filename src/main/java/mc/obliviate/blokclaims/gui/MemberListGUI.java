package mc.obliviate.blokclaims.gui;

import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.member.ClaimMember;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import xyz.efekurbann.inventory.GUI;
import xyz.efekurbann.inventory.Hytem;

public class MemberListGUI extends GUI {

	private final Claim cd;

	public MemberListGUI(Player player, Claim cd) {
		super(player, "claim-stone-gui", "Claim Üyeleri", 27);
		this.cd = cd;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		super.onOpen(event);

		fillRow(new Hytem(Material.BLACK_STAINED_GLASS_PANE).setName(""), 0);

		addItem(0, new Hytem(Material.ARROW).onClick(e -> {
					new ClaimStoneGUI((Player) e.getWhoClicked());
				})
				.setName(Message.color("<#red>Geri Dön"))
				.setLore(Message.color("<#gray>Claim yönetim menüsüne dön.")));

		int i = 9;
		for (final ClaimMember member : cd.getMembers().values()) {
			addItem(i++, new Hytem(Material.SKULL_BANNER_PATTERN).setName(Message.color("<#yellow>" + member.getOfflinePlayer().getName())));
		}
	}
}

package mc.obliviate.blokclaims.gui;

import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.member.ClaimMember;
import mc.obliviate.blokclaims.utils.message.MessageUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MemberListGUI extends GUI {

	private final Claim claim;

	public MemberListGUI(Player player, Claim claim) {
		super(player, "claim-stone-gui", "Claim Üyeleri", 3);
		this.claim = claim;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		fillRow(new Icon(Material.BLACK_STAINED_GLASS_PANE).setName(""), 0);

		addItem(0, new Icon(Material.ARROW).onClick(e -> {
					new ClaimStoneGUI((Player) e.getWhoClicked(), claim).open();
				})
				.setName(MessageUtils.parseColor("<#red>Geri Dön"))
				.setLore(MessageUtils.parseColor("<#gray>Claim yönetim menüsüne dön.")));

		int i = 9;
		for (final ClaimMember member : claim.getMembers().values()) {
			addItem(i++, new Icon(Material.SKULL_BANNER_PATTERN).setName(MessageUtils.parseColor("<#yellow>" + member.getOfflinePlayer().getName()))
					.onClick(e -> {
						new MemberManagementGUI(player, member).open();
					}));
		}
	}
}

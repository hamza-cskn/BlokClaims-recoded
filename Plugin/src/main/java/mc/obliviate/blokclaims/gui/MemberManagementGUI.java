package mc.obliviate.blokclaims.gui;

import mc.obliviate.blokclaims.member.ClaimMember;
import mc.obliviate.blokclaims.utils.message.MessageUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MemberManagementGUI extends GUI {

	private final ClaimMember member;

	public MemberManagementGUI(Player player, ClaimMember member) {
		super(player, "member-management-gui", "Üye Yönetim: " + member.getOfflinePlayer().getName(), 3);
		this.member = member;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		addItem(13, new Icon(Material.LECTERN)
				.setName(MessageUtils.parseColor("<#aqua>Izinleri Düzenle"))
				.setLore(MessageUtils.parseColor("<#gray>Üyenin claim bölgenizde yapabileceği"),
						MessageUtils.parseColor("<#gray>aktiviteleri düzenlemenizi sağlar."), "", MessageUtils.parseColor("<#yellow>Tıkla ve izinleri düzenle!"))
				.onClick(e -> {
					new PermissionManagementGUI(player,member).open();
				}));

		addItem(15, new Icon(Material.BARRIER).setName(MessageUtils.parseColor("<#red>Üyeyi At"))
				.setLore(MessageUtils.parseColor("<#gray>Üyeyi claim bölgenizden atar."), "",
						MessageUtils.parseColor("<#red>Üyeyi atmak için &nçift tıklayın<#red>!")).onClick(e -> {
			if (e.getClick().equals(ClickType.DOUBLE_CLICK)) {
				player.sendMessage("double clicked");
			}
		}));

		addItem(11, new Icon(Material.PAPER).setName(MessageUtils.parseColor("<#aqua>Üye Kaydı"))
				.setLore(MessageUtils.parseColor("<#gray>Üyenin yaptığı bir takım"),
						MessageUtils.parseColor("<#gray>işlemleri listeler."), "",
						MessageUtils.parseColor("<#red>Çok yakında!")).onClick(e -> {

		}));

	}


}

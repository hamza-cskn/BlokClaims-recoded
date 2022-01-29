package mc.obliviate.blokclaims.gui;

import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.utils.message.MessageUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ClaimStoneGUI extends GUI {

	private final Claim claim;

	public ClaimStoneGUI(Player player, Claim claim) {
		super(player, "claim-stone-gui", MessageUtils.getMessage("gui-elements.claim-stone-gui.title"), 3);
		this.claim = claim;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		addItem(10, new Icon(Material.MAP).onClick(e -> {
			new MapGUI((Player) e.getWhoClicked()).open();
		}).setName(MessageUtils.parseColor("<#green>Harita")).setLore(
				"",
				MessageUtils.parseColor("<#gray>Etrafınızdaki claim bölgelerini"),
				MessageUtils.parseColor("<#gray>incelemenizi sağlar."),
				"",
				MessageUtils.parseColor("<#yellow>Tıkla ve haritayı aç!")
		));


		addItem(13, new Icon(Material.TOTEM_OF_UNDYING).onClick(e -> {
			new MemberListGUI((Player) e.getWhoClicked(), claim).open();
		}).setName(MessageUtils.parseColor("<#green>Claim Üyeleri")).setLore(
				"",
				MessageUtils.parseColor("<#gray>Claim bölgenizdeki ekip"),
				MessageUtils.parseColor("<#gray>üyelerini sıralar."),
				"",
				MessageUtils.parseColor("<#gray>Şu anda &f" + claim.getMembers().size()+" adet <#gray>üye bulunuyor."),
				"",
				MessageUtils.parseColor("<#yellow>Tıkla ve haritayı aç!")
		));

		addItem(15, new Icon(Material.DAYLIGHT_DETECTOR).onClick(e -> {
			new EnergyGUI((Player) e.getWhoClicked(), claim).open();
		}).setName(MessageUtils.parseColor("<#green>Enerji")).setLore(
				"",
				MessageUtils.parseColor("<#gray>Claim bölgeniz büyüdükçe"),
				MessageUtils.parseColor("<#gray>daha çok enerjiye ihtiyaç."),
				MessageUtils.parseColor("<#gray>duyar."),
				"",
				MessageUtils.parseColor("<#yellow>Tıkla ve enerji satın al!")
		));
	}
}

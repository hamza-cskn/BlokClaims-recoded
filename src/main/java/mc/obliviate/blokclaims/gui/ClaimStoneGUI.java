package mc.obliviate.blokclaims.gui;

import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import xyz.efekurbann.inventory.GUI;
import xyz.efekurbann.inventory.Hytem;

public class ClaimStoneGUI extends GUI {

	public ClaimStoneGUI(Player player) {
		super(player, "claim-stone-gui", Message.getConfigMessage("gui-elements.claim-stone-gui.title", true), 3);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		super.onOpen(event);
		addItem(10, new Hytem(Material.MAP).onClick(e -> {
			new MapGUI((Player) e.getWhoClicked());
		}).setName(Message.color("<#green>Harita")).setLore(
				"",
				Message.color("<#gray>Etrafınızdaki claim bölgelerini"),
				Message.color("<#gray>incelemenizi sağlar."),
				"",
				Message.color("<#yellow>Tıkla ve haritayı aç!")
		));
	}
}

package mc.obliviate.blokclaims.gui;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.utils.message.MessageUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class EnergyGUI extends GUI {

	private final Claim claim;

	public EnergyGUI(Player player, Claim claim) {
		super(player, "energy-gui", "Enerji Satın al", 4);
		this.claim = claim;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

		addItem(12, putPurchaseIcon(100, 1000));
		addItem(13, putPurchaseIcon(200, 2000));
		addItem(14, putPurchaseIcon(300, 3000));

	}

	public Icon putPurchaseIcon(int price, long energy) {
		return new Icon(Material.CHEST_MINECART).setName(MessageUtils.parseColor("<#aqua>" + energy + " Enerji"))
				.setLore(MessageUtils.parseColor("<#gray>Süre: <#orange>" + energy), MessageUtils.parseColor("<#gray>Bedel: <#yellow>" + price))
				.onClick(e -> {
					claim.setEnergy((BlokClaims) getPlugin(), claim.getEnergy().getAmount() + energy);
					player.closeInventory();
				});

	}


}

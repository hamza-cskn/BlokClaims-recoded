package mc.obliviate.blokclaims.gui;

import mc.obliviate.blokclaims.member.ClaimMember;
import mc.obliviate.blokclaims.permission.ClaimPermissionType;
import mc.obliviate.blokclaims.utils.message.MessageUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class PermissionManagementGUI extends GUI {

	private final ClaimMember member;

	public PermissionManagementGUI(Player player, ClaimMember member) {
		super(player, "permission-management-gui", "Claim İzinleri", 6);
		this.member = member;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		int slot = 10;
		for (ClaimPermissionType type : ClaimPermissionType.values()) {
			//slot += 9;
			//if ((int) Math.floor(slot / 9d) == inv.getSize() - 1) { slot = (slot - (int) Math.floor(slot / 9d) * 9) + 11; }

			slot++;
			int mod = (((int) Math.floor(slot / 9d) - 2) + slot);

			if (mod != 10 && mod % 5 == 0) {
				slot += 4;
			}

			final boolean hasPermission = member.getPermissions().hasPermission(type);

			final Material material = hasPermission ? Material.GREEN_DYE : Material.GRAY_DYE;
			final String color = hasPermission ? "<#green>" : "<#red>";
			final String access = hasPermission ? "<#green>izinli" : "<#red>izinsiz";

			addItem(slot, new Icon(material)
					.setName(MessageUtils.parseColor(color) + type)
					.setLore(MessageUtils.parseColor("<#gray>Şu anda: " + access), "", MessageUtils.parseColor("<#yellow>Tıklayın ve değiştirin."))
					.onClick(e -> {
						if (hasPermission) {
							member.getPermissions().removePermission(type);
						} else {
							member.getPermissions().addPermission(type);
						}

						open();
					}));

		}
	}
}

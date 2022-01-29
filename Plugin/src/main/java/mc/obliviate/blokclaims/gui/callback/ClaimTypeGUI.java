package mc.obliviate.blokclaims.gui.callback;

import mc.obliviate.blokclaims.chunkid.ChunkID;
import mc.obliviate.blokclaims.chunkid.ClaimChunkType;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.utils.message.MessageUtils;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ClaimTypeGUI extends GUI {

	private final Claim claim;
	private final ChunkID chunkID;
	private final Callback whenConfirm;

	public ClaimTypeGUI(Player player, Claim claim, ChunkID chunkID, Callback whenConfirm) {
		super(player, "claim-type-gui", "Claim Türü Seç", 3);
		this.claim = claim;
		this.chunkID = chunkID;
		this.whenConfirm = whenConfirm;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		addItem(11, new Icon(Material.GRASS_BLOCK).setName(MessageUtils.parseColor("<#green>Normal Claim"))
				.setLore(MessageUtils.parseColor("<#gray>Normal claim bölgesi"),MessageUtils.parseColor("<#gray>güvenli ve sana özel."),"",MessageUtils.parseColor("<#yellow>Tıkla ve seç!"))
				.onClick(e ->{
					whenConfirm.confirm(ClaimChunkType.NORMAL);
				}));
		addItem(12, new Icon(Material.GRASS_BLOCK).setName(MessageUtils.parseColor("<#green>Tarım Claim"))
				.setLore(MessageUtils.parseColor("<#gray>Kaktüsler yalnızca bu"),MessageUtils.parseColor("<#gray>bölgede büyürler."),"",MessageUtils.parseColor("<#yellow>Tıkla ve seç!"))
				.onClick(e ->{
					whenConfirm.confirm(ClaimChunkType.FARM);
				}));
		addItem(13, new Icon(Material.GRASS_BLOCK).setName(MessageUtils.parseColor("<#green>Korumalı Claim"))
				.setLore(MessageUtils.parseColor("<#gray>Korumalı bölgelere hiçkimse"),MessageUtils.parseColor("<#gray>zarar veremez ve daha"),MessageUtils.parseColor("<#gray>az enerji harcarlar."),"",MessageUtils.parseColor("<#yellow>Tıkla ve seç!"))
				.onClick(e ->{
					whenConfirm.confirm(ClaimChunkType.PROTECTED);
				}));
	}

	public interface Callback {
		void confirm(ClaimChunkType type);
	}
}

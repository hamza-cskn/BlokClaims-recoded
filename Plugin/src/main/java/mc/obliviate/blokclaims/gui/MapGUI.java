package mc.obliviate.blokclaims.gui;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.chunkid.ChunkID;
import mc.obliviate.blokclaims.claim.Claim;
import mc.obliviate.blokclaims.gui.callback.ConfirmationGUI;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.claim.chunkcheckers.ClaimableChunks;
import mc.obliviate.blokclaims.utils.claim.chunkcheckers.UnclaimableChunks;
import mc.obliviate.blokclaims.utils.debug.Logger;
import mc.obliviate.blokclaims.utils.message.MessageUtils;
import mc.obliviate.blokclaims.utils.message.placeholder.PlaceholderUtil;
import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapGUI extends GUI {

	private final BlokClaims plugin;
	private Player player;
	private ChunkID playerIsHere;
	private int centerX;
	private int centerZ;
	private Claim claim;

	public MapGUI(Player player) {
		super(player, "map-gui", MessageUtils.getMessage("gui-elements.map-gui.title"), 6);
		plugin = (BlokClaims) getPlugin();
	}


	@Override
	public void onOpen(InventoryOpenEvent event) {
		final long aaa = System.nanoTime();
		this.player = (Player) event.getPlayer();
		playerIsHere = ClaimUtils.getChunkID(player.getLocation());
		claim = plugin.getClaimManager().getClaimData(playerIsHere);
		putMap(playerIsHere.getX(), playerIsHere.getZ(), new ArrayList<>());
		final long bbb = System.nanoTime();
		Logger.debug("Menu Calculated and opened in " + (double) (bbb - aaa) / 1000000.0 + "ms", true);

	}

	private void putMap(int param_x, int param_z, List<ChunkID> markedChunks) {
		centerX = param_x;
		centerZ = param_z;
		String worldName = player.getWorld().getName();

		//Fill Chunk Icons
		for (int m = 0; m < 45; m++) {

			int z = (int) Math.floor(m / 9d);
			int x = (m - z * 9);

			ChunkID ChunkID_Icon = new ChunkID((x - 4 + centerX), (z - 2 + centerZ), worldName);

			if (markedChunks.contains(ChunkID_Icon)) {
				addItem(m, new Icon(new ItemStack(Material.RED_STAINED_GLASS_PANE)).setName("<#red>Seçilmiş"));
			} else {
				addItem(m, getClaimIcon(claim.getClaimID().toString(), ChunkID_Icon));
			}

		}


		//Navigation Buttons
		long cooldown;

		if (!player.isOp()) {
			cooldown = System.currentTimeMillis() + 1000L;
		} else {
			cooldown = 0L;
		}

		List<String> lore = Arrays.asList("§81sn bekleme süresi");
		addItem(45, new Icon(new ItemStack(Material.SPECTRAL_ARROW)).onClick(event -> {
			if (System.currentTimeMillis() > cooldown) {
				putMap(centerX - 1, centerZ, markedChunks);
			}
		}).setName("§eSol §6§l◀").setLore(lore));
		addItem(46, new Icon(new ItemStack(Material.SPECTRAL_ARROW)).onClick(event -> {
			if (System.currentTimeMillis() > cooldown) {
				putMap(centerX, centerZ - 1, markedChunks);
			}
		}).setName("§eYukarı §6§l▲").setLore(lore));
		addItem(47, new Icon(new ItemStack(Material.SPECTRAL_ARROW)).onClick(event -> {
			if (System.currentTimeMillis() > cooldown) {
				putMap(centerX, centerZ + 1, markedChunks);
			}
		}).setName("§eAşağı §6§l▼").setLore(lore));
		addItem(48, new Icon(new ItemStack(Material.SPECTRAL_ARROW)).onClick(event -> {
			if (System.currentTimeMillis() > cooldown) {
				putMap(centerX + 1, centerZ, markedChunks);
			}
		}).setName("§eSağ §6§l▶").setLore(lore));


	}


	private Icon getClaimIcon(String mainChunkID, ChunkID chunkID) {
		//cd = tıklanılan chunk'ın claim datası olacak.
		String chunkStringID = chunkID.toString();

		Claim cd = plugin.getClaimManager().getClaimData(chunkID);
		List<String> lore = new ArrayList<>();
		lore.add(chunkStringID);
		Material mat;
		if (playerIsHere.toString().equalsIgnoreCase(chunkStringID)) {
			mat = Material.BLACK_STAINED_GLASS_PANE;
			lore.add("Buradasınız");
		} else {
			mat = Material.GRAY_STAINED_GLASS_PANE;
		}
		Logger.debug(chunkStringID + " : " + (cd == null));
		if (cd != null) {
			if (cd.getMembers().containsKey(player.getUniqueId())) {
				if (cd.getClaimID().toString().equalsIgnoreCase(chunkStringID)) {
					return new Icon(new ItemStack(Material.BLUE_STAINED_GLASS_PANE)).setName(MessageUtils.parseColor("&aAna Claim")).setLore(lore);
				}


				return new Icon(new ItemStack(Material.LIME_STAINED_GLASS_PANE)).onClick(event -> {
					if (event.getClick().isRightClick()) {
						openRemoveChunkGUI(cd, chunkStringID);
					}
				}).setName(MessageUtils.parseColor("<#green>Alt Claim")).setLore(lore);


			} else {
				//TODO USE NICKNAME INSTEAD UUID
				lore.add("§7Sahip: §e" + cd.getOwner());
				lore.add("§7Üye sayısı: §e" + cd.getMembers().size());
				return new Icon(new ItemStack(Material.RED_STAINED_GLASS_PANE)).setName(MessageUtils.parseColor("<#red>Alınmış Claim")).setLore(lore);
			}
		}


		return new Icon(new ItemStack(mat)).onClick(event -> {
			if (event.getClick().isLeftClick()) claimChunk(mainChunkID, chunkID);
			else if (event.getClick().isRightClick()) plugin.getChunkBorder().sendChunkPacket(player, chunkID);
		}).setName(MessageUtils.parseColor("<#gray>Sahipsiz Claim")).setLore(lore);
	}

	private void claimChunk(String mainChunkID, ChunkID chunkID) {
		if (mainChunkID == null) {
			player.sendMessage(MessageUtils.parseColor("<#red>Bir hata oluştu!"));
			return;
		}
		Logger.debug(mainChunkID + " mainChunkID", false, Logger.DebugType.GREEN);
		Logger.debug(chunkID + " chunkStringID", false, Logger.DebugType.GREEN);


		final ClaimableChunks claimableChunks = new ClaimableChunks(plugin, mainChunkID, chunkID.toString());
		if (claimableChunks.isClaimable()) {
			final Claim cdata = plugin.getClaimManager().getClaimData(new ChunkID(mainChunkID));
			if (cdata != null) {
				cdata.addChunk(plugin, chunkID);

				putMap(centerX, centerZ, new ArrayList<>());
			} else {
				Logger.warn("Claim data could not found. (Debug details:" + mainChunkID + ", " + chunkID + ")");
			}
		}
	}

	private void openRemoveChunkGUI(final Claim cd, final String chunkStringID) {
		final ChunkID chunkID = new ChunkID(chunkStringID);
		final UnclaimableChunks unclaimableChunks = new UnclaimableChunks(plugin.getClaimManager(), cd, chunkID);
		if (unclaimableChunks.isConnected()) {
			final ConfirmationGUI conf = new ConfirmationGUI(player, () -> {
				if (cd.removeChunk(plugin, chunkID)) {
					MessageUtils.sendMessage(player, "chunk-has-been-deleted", new PlaceholderUtil().add("{chunk}", chunkStringID));
				} else {
					player.sendMessage("§cChunk kaldırılma esnasında bir sorun oluştu!");
				}
			});
			conf.open();
			//conf.getInventory().setItem(13, ClickableItem.empty(new GuiUtils().createCustomItem(Material.PAPER, "§d" + chunkStringID + " §ebölgesi siliniyor!", Arrays.asList("", "§7Bu bölgeyi kalıcı olarak", "§7silmek istediğinize emin", "§7misiniz?"), 1)));

		} else {
			player.sendMessage("§cBu chunk'ı claim bölgenizden kaldıramazsınız.");
		}
	}


}

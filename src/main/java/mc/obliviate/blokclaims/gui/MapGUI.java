package mc.obliviate.blokclaims.gui;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.ChunkID;
import mc.obliviate.blokclaims.claim.ClaimData;
import mc.obliviate.blokclaims.gui.confirmationgui.ConfirmationGUI;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.claim.chunkcheckers.ClaimableChunks;
import mc.obliviate.blokclaims.utils.claim.chunkcheckers.UnclaimableChunks;
import mc.obliviate.blokclaims.utils.debug.Debug;
import mc.obliviate.blokclaims.utils.gui.GUI;
import mc.obliviate.blokclaims.utils.gui.Hytem;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapGUI extends GUI {

    private Player player;
    private ChunkID playerIsHere;
    private int centerX;
    private int centerZ;
    private ClaimData claimData;

    public MapGUI(BlokClaims plugin) {
        super(plugin, "map-gui", Message.getConfigMessage("gui-elements.map-gui.title", true), 54);

    }


    @Override
    public void onOpen(InventoryOpenEvent event) {
        long aaa = System.nanoTime();
        this.player = (Player) event.getPlayer();
        playerIsHere = ClaimUtils.getChunkID(player.getLocation());
        claimData = getPlugin().getClaimCore().getClaimData(playerIsHere);
        putMap(playerIsHere.getX(), playerIsHere.getZ(), new ArrayList<>());
        long bbb = System.nanoTime();
        Debug.log("Menu Calculated and opened in " + (double) (bbb - aaa) / 1000000.0 + "ms", true);

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
                addItem(m, new Hytem(new ItemStack(Material.RED_STAINED_GLASS_PANE)).setName("<#red>Seçilmiş"));
            } else {
                addItem(m, getClaimIcon(claimData.getClaimID().toString(), ChunkID_Icon));
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
        addItem(45, new Hytem(new ItemStack(Material.SPECTRAL_ARROW), event -> {
            if (System.currentTimeMillis() > cooldown) {
                putMap(centerX - 1, centerZ, markedChunks);
            }
        }).setName("§eSol §6§l◀").setLore(lore));
        addItem(46, new Hytem(new ItemStack(Material.SPECTRAL_ARROW), event -> {
            if (System.currentTimeMillis() > cooldown) {
                putMap(centerX, centerZ-1, markedChunks);
            }
        }).setName("§eYukarı §6§l▲").setLore(lore));
        addItem(47, new Hytem(new ItemStack(Material.SPECTRAL_ARROW), event -> {
            if (System.currentTimeMillis() > cooldown) {
                putMap(centerX, centerZ+1, markedChunks);
            }
        }).setName("§eAşağı §6§l▼").setLore(lore));
        addItem(48, new Hytem(new ItemStack(Material.SPECTRAL_ARROW), event -> {
            if (System.currentTimeMillis() > cooldown) {
                putMap(centerX + 1, centerZ, markedChunks);
            }
        }).setName("§eSağ §6§l▶").setLore(lore));


    }


    private Hytem getClaimIcon(String mainChunkID, ChunkID chunkID) {
        //cd = tıklanılan chunk'ın claim datası olacak.
        String chunkStringID = chunkID.toString();

        ClaimData cd = getPlugin().getClaimCore().getClaimData(chunkID);
        List<String> lore = new ArrayList<>();
        lore.add(chunkStringID);
        Material mat;
        if (playerIsHere.toString().equalsIgnoreCase(chunkStringID)) {
            mat = Material.BLACK_STAINED_GLASS_PANE;
            lore.add("Buradasınız");
        } else {
            mat = Material.GRAY_STAINED_GLASS_PANE;
        }
        Debug.log(chunkStringID + " : " + (cd == null));
        if (cd != null) {
            if (cd.getMemberList().contains(player.getUniqueId())) {
                if (cd.getClaimID().toString().equalsIgnoreCase(chunkStringID)) {
                    return new Hytem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE)).setName(Message.color("&aAna Claim")).setLore(lore);
                }
                return new Hytem(new ItemStack(Material.LIME_STAINED_GLASS_PANE), event -> {
                    if (event.getClick().isRightClick()) {
                        openRemoveChunkGUI(cd, chunkStringID);
                    }
                }).setName(Message.color("<#green>Alt Claim")).setLore(lore);


            } else {
                //TODO USE NICKNAME INSTEAD UUID
                lore.add("§7Sahip: §e" + cd.getOwner());
                lore.add("§7Üye sayısı: §e" + cd.getMemberList().size());
                return new Hytem(new ItemStack(Material.RED_STAINED_GLASS_PANE)).setName(Message.color("<#red>Alınmış Claim")).setLore(lore);
            }
        }


        return new Hytem(new ItemStack(mat), event -> {
            if (event.getClick().isLeftClick()) claimChunk(mainChunkID, chunkID);
            else if (event.getClick().isRightClick()) getPlugin().getChunkBorder().sendChunkPacket(player, chunkID);
        }).setName(Message.color("<#gray>Sahipsiz Claim")).setLore(lore);


    }

    private void claimChunk(String mainChunkID, ChunkID chunkID) {
        if (mainChunkID == null) {
            player.sendMessage(Message.color("<#red>Bir hata oluştu!"));
            return;
        }
        Debug.log(mainChunkID + " mainChunkID", false, Debug.DebugType.GREEN);
        Debug.log(chunkID + " chunkStringID", false, Debug.DebugType.GREEN);


        ClaimableChunks claimableChunks = new ClaimableChunks(getPlugin(), mainChunkID, chunkID.toString());
        if (claimableChunks.isClaimable()) {
            ClaimData cdata = getPlugin().getClaimCore().getClaimData(new ChunkID(mainChunkID));
            if (cdata != null) {
                cdata.addChunk(chunkID);
                String[] split = cdata.getClaimID().toString().split(",");
                putMap(centerX, centerZ, new ArrayList<>());
            } else {
                Debug.warn("Claim data could not found. (Debug details:" + mainChunkID + ", " + chunkID + ")");
            }
        }

    }

    private void openRemoveChunkGUI(ClaimData cd, String chunkStringID) {
        ChunkID chunkID = new ChunkID(chunkStringID);
        UnclaimableChunks unclaimableChunks = new UnclaimableChunks(getPlugin().getClaimCore(), cd, chunkID);
        if (unclaimableChunks.isConnected()) {
            ConfirmationGUI conf = new ConfirmationGUI(getPlugin(), () -> {
                if (cd.removeChunk(chunkID)) {
                    player.sendMessage(Message.getConfigMessage("chunk-has-been-deleted").replace("{chunk}", chunkStringID));
                } else {
                    player.sendMessage("§cChunk kaldırılma esnasında bir sorun oluştu!");
                }
            });
            conf.open(player);
            //conf.getInventory().setItem(13, ClickableItem.empty(new GuiUtils().createCustomItem(Material.PAPER, "§d" + chunkStringID + " §ebölgesi siliniyor!", Arrays.asList("", "§7Bu bölgeyi kalıcı olarak", "§7silmek istediğinize emin", "§7misiniz?"), 1)));

        } else {
            player.sendMessage("§cBu chunk'ı claim bölgenizden kaldıramazsınız.");
        }
    }


}

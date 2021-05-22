package mc.obliviate.blokclaims.gui;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.utils.gui.GUI;
import mc.obliviate.blokclaims.utils.gui.Hytem;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ClaimStoneGUI extends GUI {

    public ClaimStoneGUI(BlokClaims plugin) {
        super(plugin, "claim-stone-gui", Message.getConfigMessage("gui-elements.claim-stone-gui.title", true), 27);


    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        super.onOpen(event);
        addItem(10, new Hytem(Material.MAP, e -> {
            new MapGUI(getPlugin()).open((Player) e.getWhoClicked());
        }).setName(Message.color("<#green>Harita")).setLore(
                "",
                Message.color("<#gray>Etrafınızdaki claim bölgelerini"),
                Message.color("<#gray>incelemenizi sağlar."),
                "",
                Message.color("<#yellow>Tıkla ve haritayı aç!")
        ));
    }
}

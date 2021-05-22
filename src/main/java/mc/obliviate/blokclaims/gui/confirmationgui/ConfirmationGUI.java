package mc.obliviate.blokclaims.gui.confirmationgui;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.utils.gui.GUI;
import mc.obliviate.blokclaims.utils.gui.Hytem;
import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ConfirmationGUI extends GUI {

    private final ConfirmCallback whenConfirm;

    public ConfirmationGUI(BlokClaims plugin, ConfirmCallback whenConfirm) {
        super(plugin, "confirmation-gui", "Emin misiniz?", 27);
        this.whenConfirm = whenConfirm;
    }
    public ConfirmationGUI(BlokClaims plugin, String title, int size, ConfirmCallback whenConfirm) {
        super(plugin, "confirmation-gui", title, size);
        this.whenConfirm = whenConfirm;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        super.onOpen(event);

        addItem(11, new Hytem(new ItemStack(Material.LIME_CONCRETE_POWDER), e -> {
            e.getWhoClicked().closeInventory();
            this.whenConfirm.confirm();

        }).setName(Message.color("<#green>&lEvet, onaylıyorum.")));


        addItem(15, new Hytem(new ItemStack(Material.RED_CONCRETE_POWDER), e -> {
            e.getWhoClicked().closeInventory();
        }).setName(Message.color("<#red>&lHayır, onaylamıyorum.")));

    }

    public interface ConfirmCallback {
        void confirm();
    }

}

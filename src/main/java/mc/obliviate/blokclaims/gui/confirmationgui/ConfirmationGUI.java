package mc.obliviate.blokclaims.gui.confirmationgui;

import mc.obliviate.blokclaims.utils.message.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import xyz.efekurbann.inventory.GUI;
import xyz.efekurbann.inventory.Hytem;

public class ConfirmationGUI extends GUI {

    private final ConfirmCallback whenConfirm;

    public ConfirmationGUI(Player player, ConfirmCallback whenConfirm) {
        this(player, "Emin misiniz?", 27, whenConfirm);
    }

    public ConfirmationGUI(Player player, String title, int size, ConfirmCallback whenConfirm) {
        super(player, "confirmation-gui", title, size);
        this.whenConfirm = whenConfirm;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        super.onOpen(event);

        addItem(11, new Hytem(new ItemStack(Material.LIME_CONCRETE_POWDER)).onClick( e -> {
            e.getWhoClicked().closeInventory();
            this.whenConfirm.confirm();

        }).setName(Message.color("<#green>&lEvet, onaylıyorum.")));


        addItem(15, new Hytem(new ItemStack(Material.RED_CONCRETE_POWDER)).onClick( e -> {
            e.getWhoClicked().closeInventory();
        }).setName(Message.color("<#red>&lHayır, onaylamıyorum.")));

    }

    public interface ConfirmCallback {
        void confirm();
    }

}

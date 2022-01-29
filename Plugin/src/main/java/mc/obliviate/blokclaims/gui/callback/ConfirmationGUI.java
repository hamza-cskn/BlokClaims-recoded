package mc.obliviate.blokclaims.gui.callback;

import mc.obliviate.inventory.GUI;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ConfirmationGUI extends GUI {

    private final Callback whenConfirm;

    public ConfirmationGUI(Player player, Callback whenConfirm) {
        this(player, "Emin misiniz?", 3, whenConfirm);
    }

    public ConfirmationGUI(Player player, String title, int size, Callback whenConfirm) {
        super(player, "confirmation-gui", title, size);
        this.whenConfirm = whenConfirm;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        super.onOpen(event);

        addItem(11, new Icon(new ItemStack(Material.LIME_CONCRETE_POWDER)).onClick(e -> {
            e.getWhoClicked().closeInventory();
            this.whenConfirm.confirm();

        }).setName(Message.color("<#green>&lEvet, onaylıyorum.")));


        addItem(15, new Icon(new ItemStack(Material.RED_CONCRETE_POWDER)).onClick( e -> {
            e.getWhoClicked().closeInventory();
        }).setName(Message.color("<#red>&lHayır, onaylamıyorum.")));

    }

    public interface Callback {
        void confirm();
    }

}

package mc.obliviate.blokclaims.utils.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hytem {

    private final Action action;
    private final ItemStack item;

    public Hytem(ItemStack item){
        this.item = item;
        this.action = event -> {};
    }
    public Hytem(ItemStack item, Action action){
        this.action = action;
        this.item = item;
    }
    public Hytem(Material mat, Action action){
        this.action = action;
        this.item = new ItemStack(mat);
    }

    public Hytem setName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return this;
    }
    public Hytem setLore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return this;
    }
    public Hytem setLore(String ...lore) {
        return setLore(new ArrayList<>(Arrays.asList(lore)));
    }
    public Hytem appendLore(List<String> appendLore) {
        List<String> lore = item.getItemMeta().getLore();
        if (lore != null) lore.addAll(appendLore);
        else lore = appendLore;
        return setLore(lore);
    }

    public Hytem appendLore(String... appendLore) {
        return appendLore(new ArrayList<>(Arrays.asList(appendLore)));
    }

    public Hytem setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public Action getAction() {
        return action;
    }

    public ItemStack getItem() {
        return item;
    }

}


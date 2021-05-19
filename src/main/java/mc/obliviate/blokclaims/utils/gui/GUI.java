package mc.obliviate.blokclaims.utils.gui;

import mc.obliviate.blokclaims.utils.gui.animation.TextAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GUI implements InventoryHolder {

    private final InventoryAPI api;
    private final Plugin plugin;
    private final Inventory inventory;
    private final TextAnimation textAnimation = new TextAnimation();
    private final Map<Integer, Hytem> items = new HashMap<>();
    private final String title;
    private final String id;
    private final Map<Integer, ItemStack> animations = new HashMap<>();
    private boolean closed = false;
    private final int size;

    public GUI(InventoryAPI api, String id, String title, int size) {
        this.plugin = api.getPlugin();
        this.api = api;
        this.size = size;
        this.title = title;
        this.id = id;


        this.inventory = Bukkit.createInventory(null, size, title);
    }

    /**
     * EVENTS
     */
    public void onClick(InventoryClickEvent e) {
    }

    public void onOpen(InventoryOpenEvent event) {
    }

    public void onClose(InventoryCloseEvent event) {
    }

    /**
     * METHODS
     */
    public void open(Player player) {
        api.getPlayers().put(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    public void fillGui(ItemStack item) {
        fillGui(item, new ArrayList<>());
    }
    public void fillGui(ItemStack item, List<Integer> blacklisted_slots) {
        for (int slot = 0; slot < size; slot++) {
            if (!blacklisted_slots.contains(slot)) {
                addItem(slot, item);
            }
        }
    }

    public void addItem(int slot, Hytem item) {
        if (inventory.getSize() < slot) {
            throw new IndexOutOfBoundsException("Slot cannot be bigger than inventory size! [ " + slot + " > " + inventory.getSize() + " ]");
        }
        if (item == null) {
            throw new NullPointerException("Item cannot be null!");
        }

        items.remove(slot);
        items.put(slot, new Hytem(item.getItem(), item.getAction()));
        inventory.setItem(slot, item.getItem());
    }

    public void addItem(int slot, ItemStack item) {
        addItem(slot, new Hytem(item, null));
    }

    public void addItem(ItemStack item) {
        addItem(inventory.firstEmpty(), new Hytem(item, null));
    }


    /**
     * Animated Items works with TextAnimation API
     * Also, if you want to use, you must it on gui open event.
     **/
    public void addAnimatedItem(int slot, Hytem item, String name, Color... colors) {
        List<String> frames = new TextAnimation().getFrames_gradientString(name, colors);
        addAnimatedItem(slot, item, 2, frames);
    }
    public void addAnimatedItem(int slot, Hytem item, String name, int delay, Color... colors) {
        List<String> frames = new TextAnimation().getFrames_gradientString(name, colors);
        addAnimatedItem(slot, item, delay, frames);
    }

    public void addAnimatedItem(int slot, Hytem item, List<String> animationFrames) {
        addAnimatedItem(slot, item, 2, animationFrames);
    }
    public void addAnimatedItem(int slot, Hytem item, int delay, List<String> animationFrames) {
        addItem(slot, item);


        final int[] cFrame = {0};
        animations.put(slot, item.getItem());
        Update updateTask = task -> {
            if (!animations.containsKey(slot)) task.cancel();
            ItemStack solidItem = animations.get(slot);
            ItemStack itemOnGui = inventory.getItem(slot);
            if (solidItem.getItemMeta() != null && itemOnGui != null && itemOnGui.getType().equals(solidItem.getType())) {
                ItemMeta meta = solidItem.getItemMeta();
                meta.setDisplayName(animationFrames.get(cFrame[0]));
                solidItem.setItemMeta(meta);
                inventory.setItem(slot, solidItem);
                cFrame[0]++;
                if (cFrame[0] == animationFrames.size()) cFrame[0] = 0;
            } else {
                task.cancel();
            }
        };

        update(0, delay, updateTask);
    }

    public Map<Integer, ItemStack> getAnimations() {
        return animations;
    }

    public void stopAnimation(int slot) {
        animations.remove(slot);
    }

    public void setAnimationItem(int slot, ItemStack item) {
        if (animations.containsKey(slot)) {
            animations.put(slot, item);
        }
    }

    public void update(int runLater, int period, final Update update) {
        final BukkitTask[] bukkitTask = new BukkitTask[]{null};

        if (api != null) {
            bukkitTask[0] = (new BukkitRunnable() {
                public void run() {
                    if (!isClosed())
                        update.update(bukkitTask[0]);
                    else this.cancel();

                }

                @Override
                public synchronized void cancel() throws IllegalStateException {
                    super.cancel();
                    Bukkit.broadcastMessage("animation cancelled");
                }
            }).runTaskTimer(plugin, (long) runLater, (long) period);
        }

    }


    /**
     * GETTERS
     */
    public Map<Integer, Hytem> getItems() {
        return items;
    }

    public String getId() {
        return id;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public TextAnimation getAnimationAPI() {
        return textAnimation;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public boolean isClosed() {
        return closed;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}

package mc.obliviate.blokclaims.handlers;

import mc.obliviate.blokclaims.BlokClaims;
import mc.obliviate.blokclaims.utils.debug.Debug;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class ConfigHandler {

    private final BlokClaims plugin;
    private YamlConfiguration config;
    private File file;

    public ConfigHandler(BlokClaims plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder().getPath() + File.separator + "config.yml");

        this.config = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            copyDefaults();
        }
    }

    public void copyDefaults() {
        HashMap<String, Object> defaultConfigNodes = new HashMap<>();

        defaultConfigNodes.put("first-claim-time", 43200);
        defaultConfigNodes.put("backup-interval", 300);
        defaultConfigNodes.put("", 3);
        defaultConfigNodes.put("default-home-teleport-delay", 3);
        defaultConfigNodes.put("claim-worlds", Arrays.asList("flat"));


        for (String key : defaultConfigNodes.keySet()) {
            if (!config.contains(key)) {
                config.set(key, defaultConfigNodes.get(key));
            }

        }
        for (String worldName : config.getStringList("claim-worlds")) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                Debug.warn("WORLD COULD NOT FOUND: " + worldName);
            }else {
                plugin.getWorldList().add(worldName);
            }
        }
        Bukkit.getLogger().info(plugin.getWorldList().size() + " adet dünya bulundu.");

    }


    public void save() {
        try {
            this.config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }
}

package mc.obliviate.blokclaims;

import mc.obliviate.blokclaims.commands.PlayerCommands;
import mc.obliviate.blokclaims.handlers.*;
import mc.obliviate.blokclaims.listeners.*;
import mc.obliviate.blokclaims.utils.SignUtils;
import mc.obliviate.blokclaims.utils.chunkborder.ChunkBorder;
import mc.obliviate.blokclaims.utils.claim.ClaimCore;
import mc.obliviate.blokclaims.utils.gui.InventoryAPI;
import mc.obliviate.blokclaims.utils.teleport.TeleportUtil;
import mc.obliviate.blokclaims.utils.timer.Timer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BlokClaims extends JavaPlugin {

    private InventoryAPI inventoryAPI;
    private SignUtils signUtils;
    private SQLHandler sqlHandler;
    private ConfigHandler configHandler;
    private HologramHandler hologramHandler;
    private DataHandler dataHandler;
    private ClaimCore claimCore;
    private ChunkBorder chunkBorder;
    private TeleportUtil teleportUtil;
    private Economy economy;
    private EconomyHandler economyHandler;


    private static final List<String> worldList = new ArrayList<>();

    public enum CLAIM_PERMISSIONS {
        PLACE_BREAK_SPAWNER,
        PLACE_BREAK_BLOCK,
        ARMOR_STAND_INTERACT,
        ITEM_FRAME_INTERACT,
        USE_BUCKET,
        USE_CONTAINERS,
        USE_POWERABLES,
        OPEN_LOGS,
        INTERACT_MOBS,
        MANAGE_MEMBERS,
        EDIT_HOMES
    }

    public static boolean useHolographicDisplay;


    @Override
    public void onEnable() {

        Bukkit.getLogger().info("BlokClaims-recoded v" + getDescription().getVersion() + " enabling...");
        final Listener canJoin = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                event.getPlayer().kickPlayer("§cLütfen bekleyin!\n\n§fBlokClaims§7 verileri şu anda yüklenme aşamasında.\n§7Biraz bekledikten sonra tekrar deneyin.");
            }
        };

        setupCommands();
        setupHandlers();
        sqlHandler.createTableIfNotExits();
        registerListeners();

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getLogger().warning("*** Vault bulunamadı! ***");
            Bukkit.getLogger().warning("*** BlokClaims ekonomiyi kullanamayacak. ***");
        } else {
            if (!setupEconomy()) {
                Bukkit.getLogger().severe("Vault kurulumu esnasında bir sorun oluştu!");
            }
        }


        new Timer(this);

        useHolographicDisplay = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        if (!useHolographicDisplay) {
            getLogger().severe("*** HolographicDisplays bulunamadı! ***");
            getLogger().severe("*** BlokClaims hologramları aktif edilemeyecek. ***");
        }

        Bukkit.getPluginManager().registerEvents(canJoin, this);

        PlayerJoinEvent.getHandlerList().unregister(canJoin);

    }


    //TODO RECHECK ALL LISTENERS
    //TODO USE ENUM LIST
    private void registerListeners() {

        PluginManager pm = Bukkit.getPluginManager();


        pm.registerEvents(new InteractListener(this), this);
        pm.registerEvents(new FlowListener(this), this);
        pm.registerEvents(new PistonListener(this), this);
        pm.registerEvents(new TeleportListener(this), this);
        pm.registerEvents(new BucketListener(this), this);
        pm.registerEvents(new GrowListener(this), this);
        pm.registerEvents(new BlockPlaceListener(this), this);
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new BurnListener(this), this);
        pm.registerEvents(new ExplosionListener(this), this);
        pm.registerEvents(new RedstoneListener(this), this);
        pm.registerEvents(new EntityInteractListener(this), this);
        pm.registerEvents(new DispenserListener(this), this);
        pm.registerEvents(new ProjectileListener(this), this);


    }

    private void setupCommands() {
        getCommand("claim").setExecutor(new PlayerCommands(this));
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }


    private void setupHandlers() {
        inventoryAPI = new InventoryAPI(this);
        signUtils = new SignUtils(this);
        configHandler = new ConfigHandler(this);
        hologramHandler = new HologramHandler();
        chunkBorder = new ChunkBorder(this);
        dataHandler = new DataHandler();
        claimCore = new ClaimCore(this);
        teleportUtil = new TeleportUtil(this);
        sqlHandler = new SQLHandler(this);
        economyHandler = new EconomyHandler(this);

    }

    public static List<String> getWorldList() {
        return worldList;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public InventoryAPI getInventoryAPI() {
        return inventoryAPI;
    }

    public SignUtils getSignUtils() {
        return signUtils;
    }

    public SQLHandler getSqlHandler() {
        return sqlHandler;
    }

    public ClaimCore getClaimCore() {
        return claimCore;
    }

    public HologramHandler getHologramHandler() {
        return hologramHandler;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public ChunkBorder getChunkBorder() {
        return chunkBorder;
    }

    public TeleportUtil getTeleportUtil() {
        return teleportUtil;
    }

    public Economy getEconomy() {
        return economy;
    }

    public EconomyHandler getEconomyHandler() {
        return economyHandler;
    }
}

package mc.obliviate.blokclaims;

import mc.obliviate.blokclaims.handlers.ConfigHandler;
import mc.obliviate.blokclaims.handlers.DataHandler;
import mc.obliviate.blokclaims.handlers.HologramHandler;
import mc.obliviate.blokclaims.handlers.SQLHandler;
import mc.obliviate.blokclaims.utils.SignUtils;
import mc.obliviate.blokclaims.utils.claim.ClaimCore;
import mc.obliviate.blokclaims.utils.gui.InventoryAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlokClaims extends JavaPlugin {

    private InventoryAPI inventoryAPI;
    private SignUtils signUtils;
    private SQLHandler sqlHandler;
    private ConfigHandler configHandler;
    private HologramHandler hologramHandler;
    private DataHandler dataHandler;
    private ClaimCore claimCore;


    private static final List<String> worldList = new ArrayList<>();
    public final List<String> allValidPermissions = Arrays.asList(
            //ALL PERMISSIONS
            "PLACE_BREAK_SPAWNER",
            "PLACE_BREAK_BLOCK",
            "ARMOR_STAND_INTERACT",
            "ITEM_FRAME_INTERACT",
            "USE_BUCKET",
            "USE_CONTAINERS",
            "USE_POWERABLES",
            "OPEN_LOGS",
            "INTERACT_MOBS",
            "MANAGE_MEMBERS",
            "EDIT_HOMES"
    );
    public static boolean useHolographicDisplay;


    @Override
    public void onEnable() {
        Bukkit.getLogger().info("BlokClaims v" + getDescription().getVersion() + " enabling...");
        setupHandlers();
        setupCommands();

        useHolographicDisplay = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        if (!useHolographicDisplay) {
            getLogger().severe("*** HolographicDisplays bulunamadı! ***");
            getLogger().severe("*** BlokClaims hologramları aktif edilemeyecek. ***");
        }

    }

    private void setupCommands() {
    }


    private void setupHandlers() {
        inventoryAPI = new InventoryAPI(this);
        signUtils = new SignUtils(this);
        sqlHandler = new SQLHandler(this);
        configHandler = new ConfigHandler(this);
        claimCore = new ClaimCore(this);
        hologramHandler = new HologramHandler();

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
}

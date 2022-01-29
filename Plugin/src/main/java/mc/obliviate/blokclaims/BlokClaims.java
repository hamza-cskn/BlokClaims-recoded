package mc.obliviate.blokclaims;

import com.hakan.borderapi.bukkit.BorderAPI;
import mc.obliviate.blokclaims.commands.claim.ClaimCMD;
import mc.obliviate.blokclaims.handlers.*;
import mc.obliviate.blokclaims.handlers.database.SQLManager;
import mc.obliviate.blokclaims.listeners.*;
import mc.obliviate.blokclaims.utils.chunkborder.ChunkBorder;
import mc.obliviate.blokclaims.utils.claim.ClaimManager;
import mc.obliviate.blokclaims.utils.claim.ClaimUtils;
import mc.obliviate.blokclaims.utils.sign.SignUtils;
import mc.obliviate.blokclaims.utils.teleport.TeleportUtil;
import mc.obliviate.blokclaims.utils.timer.Timer;
import mc.obliviate.inventory.InventoryAPI;
import me.despical.commandframework.CommandFramework;
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

	private static final List<String> worldList = new ArrayList<>();
	public static boolean useHolographicDisplay;
	private static BorderAPI borderAPI;
	private final InventoryAPI inventoryAPI = new InventoryAPI(this);
	private final HologramHandler hologramHandler = new HologramHandler();
	private final DataHandler dataHandler = new DataHandler();
	private final ClaimManager claimManager = new ClaimManager(this);
	private final ClaimUtils claimUtils = new ClaimUtils(claimManager);
	private final ChunkBorder chunkBorder = new ChunkBorder(this);
	private final TeleportUtil teleportUtil = new TeleportUtil(this);
	private final EconomyHandler economyHandler = new EconomyHandler(this);
	private CommandFramework commandFramework;
	private SignUtils signUtils;
	private SQLManager sqlManager;
	private ConfigHandler configHandler;
	private Economy economy;

	public static List<String> getWorldList() {
		return worldList;
	}

	public static BorderAPI getBorderAPI() {
		return borderAPI;
	}

	@Override
	public void onEnable() {

		Bukkit.getLogger().info("BlokClaims-recoded v" + getDescription().getVersion() + " enabling...");
		final Listener canJoin = new Listener() {
			@EventHandler
			public void onJoin(PlayerJoinEvent event) {
				event.getPlayer().kickPlayer("§cLütfen bekleyin!\n\n§fBlokClaims§7 verileri şu anda yüklenme aşamasında.\n§7Biraz bekledikten sonra tekrar deneyin.");
			}
		};

		setupHandlers();
		sqlManager.init();
		registerListeners();
		setupCommands();

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

	@Override
	public void onDisable() {
		Bukkit.getLogger().info("BlokClaims disabling");
		sqlManager.save(false);
		sqlManager.disconnect();
	}

	//TODO RECHECK ALL LISTENERS
	//TODO USE ENUM LIST
	private void registerListeners() {

		final PluginManager pm = Bukkit.getPluginManager();

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
		commandFramework.registerCommands(new ClaimCMD(this));
	}

	private boolean setupEconomy() {
		final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	private void setupHandlers() {
		commandFramework = new CommandFramework(this);

		sqlManager = new SQLManager(getDataFolder().getPath(), this);
		inventoryAPI.init();
		signUtils = new SignUtils(this);
		configHandler = new ConfigHandler(this);
		borderAPI = BorderAPI.getInstance(this);
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

	public SQLManager getSqlManager() {
		return sqlManager;
	}

	public ClaimManager getClaimManager() {
		return claimManager;
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

	public ClaimUtils getClaimUtils() {
		return claimUtils;
	}
}

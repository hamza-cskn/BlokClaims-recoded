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

    //private final BlokClaims plugin;
    private YamlConfiguration config;
    private final File configFile;

    private final File messageFile;
    public static YamlConfiguration messages;

    public ConfigHandler(BlokClaims plugin) {
        //this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder().getPath() + File.separator + "config.yml");
        this.messageFile = new File(plugin.getDataFolder().getPath() + File.separator + "messages.yml");
        loadConfig();
        loadMessage();

    }


    public void loadMessage() {
        messages = YamlConfiguration.loadConfiguration(messageFile);

        // dcn = default config nodes
        HashMap<String, String> dcn = new HashMap<>();

        dcn.put("prefix", "&8[&aBlok Claims&8] ");
        dcn.put("you-can-not-do-this", "&cBu işlemi gerçekleştiremezsiniz.");
        dcn.put("you-can-it-in-this-world", "&cBu dünyada bu işlemi gerçekleştiremezsiniz.");
        dcn.put("you-dont-have-enough-permission", "&cBu işlemi yalnızca yetkililer yapabilir.");
        dcn.put("target-is-not-online", "&cBu oyuncu çevrimiçi değil.");
        dcn.put("target-already-added-this-claim", "&cBu oyuncu zaten eklemeye çalıştığınız claim bölgesine eklenmiş.");
        dcn.put("your-home-successfully-added", "&eClaim bölgesine &a{home}&e adlı eviniz başarıyla eklendi!");
        dcn.put("you-are-not-on-your-claim", "&cŞu anda kendi bölgenizde bulunmuyorsunuz.");
        dcn.put("teleporting-to-claim-home", "&eClaim evinize ışınlanmanıza &f{time_left} saniye &ekaldı.");
        dcn.put("successfully-teleported-to-claim-home", "&eBaşarıyla &a{home}&e isimli claim evinize ışınlandırıldınız.");
        dcn.put("claim-stone-has-moved", "&7Claim taşını taşıma işlemi başarıyla gerçekleştirildi.");
        dcn.put("you-can-not-empty-lava-bucket-here", "&cSahipsiz claim bölgesine lav kovası boşaltamazsınız.");
        dcn.put("your-location-is-not-clear", "&cBulunduğunuz lokasyonda blok bulunuyor. Lütfen konumunuzu temizledikten sonra tekrar deneyin.");
        dcn.put("target-is-not-member-of-your-claim", "&cBu oyuncu claim bölgenizin bir üyesi değil.");
        dcn.put("target-successfully-kicked-from-your-claim", "&c{player} adlı oyuncu claim bölgenizden başarıyla atıldı.");
        dcn.put("config-reloaded", "&7Yapılandırma dosyaları başarıyla yeniden yüklendi. ({ms}ms)");
        dcn.put("claim-guard.teleport-cancel", "[NO_PREFIX]&cYetkisiz olduğunuz bir claim bölgesine olan ışınlanmanız iptal edildi.");
        dcn.put("claim-guard.interact-cancel", "[NO_PREFIX]&cBu bölgede etkileşimde bulunamazsınız.");
        dcn.put("claim-guard.use-bucket-cancel", "[NO_PREFIX]&cBu bölgede kova kullanamazsınız.");
        dcn.put("claim-guard.block-place-cancel", "[NO_PREFIX]&cBu bölgeye blok yerleştiremezsiniz.");
        dcn.put("claim-guard.block-break-cancel", "[NO_PREFIX]&cBu bölgedeki blokları kıramazsınız.");
        dcn.put("claim-guard.spawner-place-cancel", "[NO_PREFIX]&cBu bölgeye spawner yerleştiremezsiniz.");
        dcn.put("claim-guard.spawner-break-cancel", "[NO_PREFIX]&cBu bölgedeki spawnerları kıramazsınız.");
        dcn.put("claim-guard.item-frame-interact-cancel", "[NO_PREFIX]&cBu bölgedeki eşya çerçevelerine ve tablolara dokunamazsınız.");
        dcn.put("claim-guard.armor-stand-interact-cancel", "[NO_PREFIX]&cBu bölgedeki zırh askılıklarına dokunamazsınız.");
        dcn.put("claim-guard.use-containers-cancel", "[NO_PREFIX]&cBu bölgede sandık, varil ve benzeri blokları kullanamazsınız veya kıramazsınız.");
        dcn.put("claim-guard.farmland-destroy-cancel", "[NO_PREFIX]&cBu bölgedeki tarım bölgelerine zarar veremezsiniz.");
        dcn.put("claim-guard.powerable-cancel", "[NO_PREFIX]&cBu bölgede kızıltaş enerjisi ile çalışan blokları kullanamazsınız.");
        dcn.put("chunk-has-been-deleted", "&c{chunk} isimli chunk bölgesini claiminizden kalıcı olarak kaldırdınız.");
        dcn.put("home.teleporting.title", "&aIşınlandırılıyorsunuz");
        dcn.put("home.teleporting.subtitle", "&e{time} saniye");
        dcn.put("home.teleported.title", "&aIşınlandırıldınız");
        dcn.put("home.teleported.subtitle", "&e{home} adlı eve ışınlandırıldınız.");
        dcn.put("home.teleport-cancelled.title", "&cIptal Edildi");
        dcn.put("home.teleport-cancelled.subtitle", "&7Işınlanmanız iptal edildi.");
        dcn.put("home.list-name", "&d{home}");
        dcn.put("usage.addhome", "&aDoğru kullanım: &7/blokclaims addhome <ev adı>");
        dcn.put("usage.invite", "&aDoğru kullanım: &7/blokclaims invite <oyuncu adı>");
        dcn.put("permissions.delete-member.name", "&cÜyeyi bölgeden çıkar");
        dcn.put("invite.target-has-invited", "&d{target}&7 adlı oyuncuyu &a{claimID} &7Claim bölgenize davet ettiniz. Eğer davete {expire_time}sn içerisinde cevap verilmezse otomatik olarak reddedilecek.");
        dcn.put("invite.you-invited", "&d{inviter}&7 adlı oyuncu sizi Claim bölgesine davet etti. Eğer davete {expire_time}sn içerisinde cevap verilmezse otomatik olarak reddedilecek.\n&aKabul etmek&7 için &e/claim katıl&7 komutunu kullanın.");
        dcn.put("invite.invite-expired", "&c{inviter}&7 adlı oyuncunun Claim bölgesi daveti zaman aşımına uğradı.");
        dcn.put("invite.already-invited", "&c{inviter}&7 adlı oyuncu zaten davet edildi.");
        dcn.put("invite.list-title", "&a&lCLAIM BÖLGESİ&8- &7Davetler\n&7[Davet edilen] [Davet eden] [Kalan süre]");
        dcn.put("invite.list-format", "&8- &7{target} {inviter} &e{time_left}");
        dcn.put("invite.list-no-invite", "&cHiç davet bulunamadı.");
        dcn.put("invite.no-invite-found", "&cHiç davet bulunamadı.");




        for (String key : dcn.keySet()) {
            Debug.log("Configuring: " + key, true);
            if (!messages.contains(key)) {
                messages.set(key, dcn.get(key));
            }

        }

        if (!messages.contains("help-message")) {
            messages.set("help-message", Arrays.asList(
                    "<#orange>Blok Dünyası",
                    "",
                    "&6* &e/claim yardım &8- &7 Bu yazıları gösterir.",
                    "&6* &e/claim create &8- &7 Yeni bir Claim bölgesi oluşturur.",
                    "&6* &e/claim addhome &8- &7 Claim evi eklemenizi sağlar.",
                    ""
            ));

        }
        if (!messages.contains("add-new-member-sign-lines")) {
            messages.set("add-new-member-sign-lines", Arrays.asList(
                    "",
                    "^^^^^^^^",
                    "&cEklemek istediğiniz",
                    "&cüyenin adını yazın!"
            ));

        }
        if (!messages.contains("home.list-description")) {
            messages.set("home.list-description", Arrays.asList(
                    "",
                    "&7Bölge eviniz sizin ve ekibinizin",
                    "&7ışınlanma noktasıdır.",
                    "",
                    "&eSol tıklayın ve ışınlanın",
                    "&eSağ tıklayın ve düzenleyin"
            ));

        }
        if (!messages.contains("permissions.delete-member.description")) {
            messages.set("permissions.delete-member.description", Arrays.asList(
                    "",
                    "&c{player}&7 adlı oyuncunun izinlerini",
                    "&7claim bölgenizden siler."

            ));

        }

        for (BlokClaims.CLAIM_PERMISSIONS perm : BlokClaims.CLAIM_PERMISSIONS.values()) {
            if (!messages.contains("permissions." + perm + ".name")) {
                messages.set("permissions." + perm + ".name", perm);
            }
            if (!messages.contains("permissions." + perm + ".description")) {
                messages.set("permissions." + perm + ".description", Arrays.asList("&7Bu &e{player}&7 adlı oyuncu", "&7izni açmak ve kapatmak için", "&7bu ikonu kullanın."));
            }

        }

        //String name = Main.messages.getString("permissions." + permission + ".name", permission);
        //List<String> lore = Main.messages.getStringList("permissions." + permission + ".description");

        save(messages, messageFile);


    }

    public void loadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
        if (!configFile.exists()) {
            loadDefaultConfig();
        }
        loadWorlds();
    }

    public void loadDefaultConfig() {
        HashMap<String, Object> defaultConfigNodes = new HashMap<>();

        defaultConfigNodes.put("first-claim-time", 43200);
        defaultConfigNodes.put("backup-interval", 300);
        defaultConfigNodes.put("default-home-teleport-delay", 3);
        defaultConfigNodes.put("claim-worlds", Arrays.asList("world1", "world2"));


        for (String key : defaultConfigNodes.keySet()) {
            if (!config.contains(key)) {
                config.set(key, defaultConfigNodes.get(key));
            }
        }
        save(config, configFile);


    }

    private void loadWorlds() {
        for (String worldName : config.getStringList("claim-worlds")) {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                Debug.warn("[ERROR] World could not found: " + worldName);
            } else {
                BlokClaims.getWorldList().add(worldName);
            }
        }
        Bukkit.getLogger().info(BlokClaims.getWorldList().size() + " adet dünya bulundu.");
    }


    public void save(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }


}

package mc.obliviate.blokclaims.homes;

import org.bukkit.Location;
import org.bukkit.Material;

public class ClaimHome {


    private final Location loc;
    private final Material icon;
    private final String name;

    public ClaimHome(Location loc, Material icon, String name) {
        this.loc = loc;
        this.icon = icon;
        this.name = name;
    }

    public Location getLoc() {
        return loc;
    }

    public Material getIcon() {
        if (icon == null) {
            return Material.RED_BED;
        }
        return icon;
    }

    public String getName() {
        if (name == null) {
            return "Bilinmeyen Ev";
        }
        return name;
    }

}

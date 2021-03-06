package Color_yr.HeartAgeUtils.Hook;

import Color_yr.HeartAgeUtils.HeartAgeUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Hook {
    private static vault vault;
    private static blocklocker blocklocker;

    private static boolean vaultHook;
    private static boolean blockLockerHook;

    public static void init() {

        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            vault = new vault();
            if (vault.setupEconomy()) {
                vaultHook = true;
                HeartAgeUtils.log.info("[HeartAgeUtils]vault已挂钩");
            } else {
                HeartAgeUtils.log.info("[HeartAgeUtils]vault未挂钩");
            }
        }

        if (Bukkit.getPluginManager().getPlugin("BlockLocker") != null) {
            blocklocker = new blocklocker();
            blockLockerHook = true;
            HeartAgeUtils.log.info("[HeartAgeUtils]BlockLocker已挂钩");
        } else {
            HeartAgeUtils.log.info("[HeartAgeUtils]BlockLocker未挂钩");
        }
    }

    public static boolean vaultCheck(Player player, int cost) {
        if (vaultHook) {
            return vault.check(player, cost);
        }
        return true;
    }

    public static void vaultCost(Player player, int cost, String message) {
        if (vaultHook) {
            vault.cost(player, cost, message);
        }
    }

    public static boolean isAllowed(Player player, Block block) {
        if (!blockLockerHook)
            return true;
        return blocklocker.can(player, block, true);
    }
    public static boolean isOwner(Player player, Block block) {
        if (!blockLockerHook)
            return true;
        return blocklocker.owner(player, block, true);
    }
}

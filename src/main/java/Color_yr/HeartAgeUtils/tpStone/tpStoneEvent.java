package Color_yr.HeartAgeUtils.tpStone;

import Color_yr.HeartAgeUtils.Config.configMain;
import Color_yr.HeartAgeUtils.DeathChest.deathChestDo;
import Color_yr.HeartAgeUtils.HeartAgeUtils;
import Color_yr.HeartAgeUtils.NMS.ItemNBTSet;
import Color_yr.HeartAgeUtils.Obj.languageObj;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;

public class tpStoneEvent implements Listener {

    @EventHandler
    public void itemClick(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null)
            return;
        Material test = item.getType();
        if (test.equals(tpStoneDo.item)) {
            ItemNBTSet ItemNbt = new ItemNBTSet(item);
            e.setCancelled(true);
            Player player = e.getPlayer();
            if (!ItemNbt.hasKey("uuid")) {
                player.closeInventory();
                return;
            }
            String uuid = ItemNbt.getString("uuid");

            languageObj lan = HeartAgeUtils.configMain.lan;
            if (!tpStoneDo.toStoneSave.containsKey(uuid)) {
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1.0f, 1.0f);
                player.sendMessage(lan.getTitle() + lan.getTpStoneNoDate());
                return;
            }
            tpStoneSaveObj obj = tpStoneDo.toStoneSave.get(uuid);
            Inventory inv = Bukkit.createInventory(e.getPlayer(), InventoryType.DISPENSER,
                    lan.getTpStoneTitle() + obj.getName());
            ItemStack itemStack = new ItemStack(Material.COMPASS);
            ItemMeta temp1;
            for (Map.Entry<String, locationObj> temp : obj.getSel().entrySet()) {
                String slot = temp.getKey().replace("sel", "");
                locationObj locationObj = temp.getValue();
                ItemNbt = new ItemNBTSet(itemStack);
                ItemNbt.setBoolean("disable", false);
                ItemNbt.setInt("x", locationObj.getX());
                ItemNbt.setInt("y", locationObj.getY());
                ItemNbt.setInt("z", locationObj.getZ());
                String world = locationObj.getWorld();
                ItemNbt.setNbt("world", world != null ? world : "world");
                itemStack = ItemNbt.saveNBT();
                temp1 = itemStack.getItemMeta();
                temp1.setDisplayName(locationObj.getName());
                temp1.setLore(new ArrayList<String>() {{
                    this.add(lan.getTpStoneShow());
                    this.add("§aX：§b" + locationObj.getX() + " §aY：§b" + locationObj.getY() + " §aZ：§b" + locationObj.getZ());
                }});
                itemStack.setItemMeta(temp1);
                inv.setItem(Integer.decode(slot) - 1, itemStack);
            }
            itemStack = new ItemStack(Material.BARRIER);
            temp1 = itemStack.getItemMeta();
            temp1.setDisplayName(lan.getTpStoneUnlock());
            temp1.setLore(new ArrayList<String>() {
                {
                    this.add(lan.getTpStoneUnlockNeed());
                }
            });
            itemStack.setItemMeta(temp1);
            ItemNbt = new ItemNBTSet(itemStack);
            ItemNbt.setBoolean("disable", true);
            itemStack = ItemNbt.saveNBT();
            for (int i = 0; i < 9; i++) {
                ItemStack a = inv.getItem(i);
                if (a == null) {
                    inv.setItem(i, itemStack);
                }
            }
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
            deathChestDo.guiSave.put(player.getName(), player.openInventory(inv));
        } else if (e.getHand() == EquipmentSlot.OFF_HAND) {
            ItemStack offhand = e.getPlayer().getInventory().getItemInOffHand();
            ItemStack mainhand = e.getPlayer().getInventory().getItemInMainHand();
            if (mainhand.getType().equals(tpStoneDo.item) && offhand.getType().equals(tpStoneDo.updateItem)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void selGui(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand.getType().equals(tpStoneDo.item)) {
                e.setCancelled(true);
                languageObj lan = HeartAgeUtils.configMain.lan;
                if (deathChestDo.guiSave.containsKey(player.getName())) {
                    InventoryView inv = deathChestDo.guiSave.get(player.getName());
                    if (!inv.getTitle().contains(lan.getTpStoneTitle())) {
                        player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1.0f, 1.0f);
                        player.closeInventory();
                        deathChestDo.guiSave.remove(player.getName());
                        return;
                    }
                    if (inv.getPlayer().equals(player)) {
                        ItemStack item = inv.getItem(e.getSlot());
                        ItemNBTSet ItemNbt = new ItemNBTSet(item);
                        if (ItemNbt.getBoolean("disable")) {
                            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0f, 1.0f);
                            player.sendMessage(lan.getTitle() + lan.getTpStoneUnlockSlot());
                        } else if (!ItemNbt.hasKey("disable") || (!ItemNbt.hasKey("x") || !ItemNbt.hasKey("y") || !ItemNbt.hasKey("z"))) {
                            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1.0f, 1.0f);
                            player.closeInventory();
                            deathChestDo.guiSave.remove(player.getName());
                            return;
                        } else if (e.getClick() == ClickType.LEFT) {
                            int x = ItemNbt.getInt("x");
                            int y = ItemNbt.getInt("y") + 1;
                            int z = ItemNbt.getInt("z");
                            String worldName = ItemNbt.getString("world");
                            World world = Bukkit.getWorld(worldName);

                            if (x == 0 && y == 0 && z == 0) {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, 1.0f);
                                player.sendMessage(lan.getTitle() + lan.getTpStoneCantTp());
                            } else {
                                player.teleport(new org.bukkit.Location(world != null ? world : player.getWorld(), x, y, z));
                                player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1.0f, 1.0f);
                                player.sendMessage(lan.getTitle() + lan.getTpStoneTp());
                            }
                        } else if (e.getClick() == ClickType.RIGHT) {
                            org.bukkit.Location location = player.getLocation();
                            ItemNbt = new ItemNBTSet(hand);
                            String uuid = ItemNbt.getString("uuid");
                            if (!tpStoneDo.toStoneSave.containsKey(uuid)) {
                                player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1.0f, 1.0f);
                                player.sendMessage(lan.getTitle() + lan.getTpStoneError());
                            } else {
                                tpStoneSaveObj stone = tpStoneDo.toStoneSave.get(uuid);
                                tpStoneObjSet set = new tpStoneObjSet(stone);
                                locationObj location1 = set.getSel(e.getSlot());
                                location1.setX((int) location.getX());
                                location1.setY((int) location.getY());
                                location1.setZ((int) location.getZ());
                                location1.setWorld(location.getWorld().getName());
                                set.setSel(e.getSlot(), location1);
                                configMain.tpStone.save(stone, uuid);
                                tpStoneDo.toStoneSave.put(uuid, stone);
                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                                player.sendMessage(lan.getTitle() + lan.getTpStoneSave());
                            }
                        }
                    }
                    player.closeInventory();
                    deathChestDo.guiSave.remove(player.getName());
                }
            }
        }
    }
}

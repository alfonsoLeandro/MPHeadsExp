package com.github.alfonsoleandro.mpheadsexp.listeners;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.managers.HeadsManager;
import com.github.alfonsoleandro.mpheadsexp.managers.Settings;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class HeadBreakListener implements Listener {

    private final HeadsExp plugin;
    private final HeadsManager headsManager;
    private final Settings settings;

    public HeadBreakListener(HeadsExp plugin) {
        this.plugin = plugin;
        this.headsManager = plugin.getHeadsManager();
        this.settings = plugin.getSettings();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockDropItemEvent event){
        Block block = event.getBlock();
        if(!(block.getType().equals(Material.PLAYER_HEAD) || block.getType().equals(Material.PLAYER_WALL_HEAD))) return;
        Skull skull = (Skull) block.getState();
        String headData = skull.getPersistentDataContainer().get(new NamespacedKey(this.plugin, "MPHeads"),
                PersistentDataType.STRING);
        if(headData == null) return;

        if(headData.startsWith("PLAYER-HEAD")){
            ItemStack head = null;
            for(ItemStack drop : block.getDrops()){
                if(drop.getType().equals(Material.PLAYER_HEAD)){
                    head = drop;
                    break;
                }
            }
            if(head == null) return;
            List<Item> itemsToDrop = event.getItems();
            Collection<ItemStack> blockDrops = block.getDrops();

            Objects.requireNonNull(head.getItemMeta()).getPersistentDataContainer()
                    .set(new NamespacedKey(this.plugin, "MPHeads"),
                            PersistentDataType.STRING, headData);

            itemsToDrop.removeIf(item -> blockDrops.contains(item.getItemStack()));

            Item headItemEntity = block.getWorld().spawn(block.getLocation(), Item.class);
            headItemEntity.setItemStack(head);
            itemsToDrop.add(headItemEntity);


        }else if(headData.startsWith("HEAD")){
            String mobType = headData.split(":")[1];
            ItemStack head;
            if(this.headsManager.getMobHeadData(mobType) == null){
                head = this.settings.getUnknownHeadItem();
            }else{
                head = this.headsManager.getMobHeadData(mobType).getHeadItem();
            }
            List<Item> itemsToDrop = event.getItems();
            Collection<ItemStack> blockDrops = block.getDrops();

            Objects.requireNonNull(head.getItemMeta()).getPersistentDataContainer()
                    .set(new NamespacedKey(this.plugin, "MPHeads"),
                            PersistentDataType.STRING, headData);

            itemsToDrop.removeIf(item -> blockDrops.contains(item.getItemStack()));

            Item headItemEntity = block.getWorld().dropItem(block.getLocation(), head);
            headItemEntity.remove(); //prevent double drops
            itemsToDrop.add(headItemEntity);

        }
    }
}

package com.github.alfonsoleandro.mpheadsexp.listeners;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.managers.Settings;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class HeadPlaceListener implements Listener {

    private final HeadsExp plugin;
    private final Settings settings;

    public HeadPlaceListener(HeadsExp plugin){
        this.plugin = plugin;
        this.settings = plugin.getSettings();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event){
        ItemStack inHand = event.getItemInHand();

        if(!inHand.getType().equals(Material.PLAYER_HEAD) || !inHand.hasItemMeta()) return;

        PersistentDataContainer data = Objects.requireNonNull(inHand.getItemMeta()).getPersistentDataContainer();
        String headData = data.get(new NamespacedKey(this.plugin, "MPHeads"), PersistentDataType.STRING);
        if(headData != null){
            if(this.settings.isAllowPlacingHeads()){
                boolean isHead = headData.startsWith("HEAD") || headData.startsWith("PLAYER-HEAD");
                if(!isHead) return;
                Skull placed = (Skull) event.getBlockPlaced().getState();
                PersistentDataContainer blockDataContainer = placed.getPersistentDataContainer();
                blockDataContainer.set(new NamespacedKey(this.plugin, "MPHeads"),
                        PersistentDataType.STRING, headData);
                placed.update();
            }else {
                event.setCancelled(true);
                this.plugin.getMessageSender().send(event.getPlayer(), Message.CANNOT_PLACE_HEAD);
            }
        }



    }

}

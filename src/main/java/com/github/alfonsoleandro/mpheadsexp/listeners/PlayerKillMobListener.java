package com.github.alfonsoleandro.mpheadsexp.listeners;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.managers.HeadsManager;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import com.github.alfonsoleandro.mpheadsexp.managers.Settings;
import com.github.alfonsoleandro.mpheadsexp.managers.utils.MobHeadData;
import com.github.alfonsoleandro.mpheadsexp.managers.utils.PlayerHeadData;
import com.github.alfonsoleandro.mpheadsexp.utils.Message;
import com.github.alfonsoleandro.mputils.managers.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PlayerKillMobListener implements Listener {

    private final HeadsExp plugin;
    private final HeadsManager headsManager;
    private final LevelsManager levelsManager;
    private final Settings settings;
    private final MessageSender<Message> messageSender;
    private final Random r = new Random();

    public PlayerKillMobListener(HeadsExp plugin){
        this.plugin = plugin;
        this.headsManager = plugin.getHeadsManager();
        this.levelsManager = plugin.getLevelsManager();
        this.settings = plugin.getSettings();
        this.messageSender = plugin.getMessageSender();
    }


    @EventHandler
    public void onKill(EntityDeathEvent event){
        if(event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        ItemStack head;

        if(event.getEntityType().equals(EntityType.PLAYER)){
            Player killed = (Player) event.getEntity();
            PlayerHeadData playerHeadData = this.headsManager.getPlayerHeadData(killed.getName());
            head = this.headsManager.getPlayerHead(killed.getName());
            if(head == null) return;

            //Get % of killed player's balance.
            double percentage = playerHeadData == null ?
                    this.settings.getDefaultPlayerHeadBalance() :
                    playerHeadData.getPrice();
            double balance = this.plugin.getEconomy().getBalance(killed);
            double amount = balance * (Math.min(percentage, 100.0) / 100.0);

            this.plugin.getEconomy().withdrawPlayer(killed, amount);

            //Messages
            this.messageSender.send(killer, Message.PLAYER_HEAD_DROPPED,
                    "%player%", killed.getName());
            this.messageSender.send(killer, Message.YOUR_HEAD_DROPPED,
                    "%balance%", String.valueOf(percentage),
                    "%amount%", String.valueOf(amount));

        }else {
            MobHeadData mhd = this.headsManager.getMobHeadData(event.getEntityType().toString());
            head = this.headsManager.getMobHeadData(event.getEntityType().toString()).getHeadItem();
            if(head == null) return;

            int conProb = this.settings.getMobHeadDropProbability();
            int prob = this.r.nextInt(100/conProb)+1;
            Bukkit.broadcastMessage("probs: "+conProb+" result: "+prob);
            if(prob == 100/conProb) return;
//            if(3 > this.r.nextDouble(10)) return;

            int required = mhd.getRequiredLevel();
            int current = this.levelsManager.getLevel(killer.getUniqueId());

            //Messages
            if(required > current) {
                this.messageSender.send(killer, Message.NOT_ENOUGH_LEVEL,
                        "%level%", String.valueOf(required),
                        "%type%", event.getEntityType().toString(),
                        "%current_level%", String.valueOf(current)
                        );
                return;
            }
            this.messageSender.send(killer, Message.MOB_HEAD_DROPPED,
                    "%type%", event.getEntityType().toString());
        }

        event.getDrops().add(head);
    }


}

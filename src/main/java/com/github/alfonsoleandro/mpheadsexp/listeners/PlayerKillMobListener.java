package com.github.alfonsoleandro.mpheadsexp.listeners;

import com.github.alfonsoleandro.mpheadsexp.HeadsExp;
import com.github.alfonsoleandro.mpheadsexp.managers.LevelsManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PlayerKillMobListener implements Listener {

    private final HeadsExp plugin;
    private final Random r;

    public PlayerKillMobListener(HeadsExp plugin){
        this.plugin = plugin;
        r = new Random();
    }


    @EventHandler
    public void onKill(EntityDeathEvent event){
        if(event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        ItemStack head;

        if(event.getEntityType().equals(EntityType.PLAYER)){
            head = plugin.getHeadsManager().getPlayerHead(event.getEntity().getName());
            if(head == null) return;
            FileConfiguration config = plugin.getConfigYaml().getAccess();
            Player killed = (Player) event.getEntity();

            //Get % of killed player's balance.
            double percentage = config.contains("player heads."+killed.getName()) ?
                    config.getDouble("player heads."+killed.getName()+".balance") :
                    config.getDouble("player heads.default head.balance");
            double balance = plugin.getEconomy().getBalance(killed);
            double amount = balance * (Math.min(percentage, 100.0) / 100.0);
            plugin.getEconomy().withdrawPlayer(killed, amount);

            //Messages
            plugin.getMessageSender().send(killer,
                    plugin.getLanguageYaml().getAccess().getString("player head dropped", "&aYou just received %player%'s head!")
                            .replace("%player%", killed.getName()));

            plugin.getMessageSender().send(killed,
                    plugin.getLanguageYaml().getAccess().getString("your head dropped", "&cYou just dropped your head and lost %balance%% of your balance (%amount%)")
                            .replace("%balance%", String.valueOf(percentage))
                            .replace("%amount%", String.valueOf(amount)));

        }else {
            head = plugin.getHeadsManager().getMobHeadData(event.getEntityType().toString()).getHeadItem();
            if(head == null) return;
            //TODO leave hardcoded probability?
            if(3 > r.nextInt(10)) return;

            LevelsManager manager = plugin.getLevelsManager();
            int required = manager.getLevelRequiredForHead(event.getEntityType().toString());
            int current = manager.getLevel(killer.getUniqueId());

            //Messages
            if(required > current) {
                plugin.getMessageSender().send(killer,
                        plugin.getLanguageYaml().getAccess().getString("not enough level", "&cYou need to be at least level %level% to get a %type% head, you are currently level %current_level%")
                                .replace("%level%", String.valueOf(required))
                                .replace("%type%", event.getEntityType().toString())
                                .replace("%current_level%", String.valueOf(current)));
                return;
            }
            plugin.getMessageSender().send(killer,
                    plugin.getLanguageYaml().getAccess().getString("head dropped", "&aThe %type% you just killed dropped their head!")
                            .replace("%type%", event.getEntityType().toString()));
        }

        event.getDrops().add(head);
    }


}

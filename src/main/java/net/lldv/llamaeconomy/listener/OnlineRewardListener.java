package net.lldv.llamaeconomy.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.PluginTask;
import net.lldv.llamaeconomy.LlamaEconomy;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OnlineRewardListener implements Listener {

    private final PluginBase plugin;
    private final ConcurrentHashMap<String, Long> playerOnlineTime = new ConcurrentHashMap<>();

    public OnlineRewardListener(PluginBase plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().scheduleRepeatingTask(plugin, new PluginTask<PluginBase>(plugin) {
            @Override
            public void onRun(int currentTick) {
                rewardPlayers();
            }
        }, 36000);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerOnlineTime.put(event.getPlayer().getName(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerOnlineTime.remove(event.getPlayer().getName());
    }

    private void rewardPlayers() {
        long currentTime = System.currentTimeMillis();
        for (ConcurrentHashMap.Entry<String, Long> entry : playerOnlineTime.entrySet()) {
            long loginTime = entry.getValue();
            long onlineTime = currentTime - loginTime;
            long rewardIntervalMillis = 36000L;
            if (onlineTime >= rewardIntervalMillis) {
                Player player = plugin.getServer().getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    giveReward(player);
                    playerOnlineTime.put(entry.getKey(), currentTime);
                }
            }
        }
    }

    private void giveReward(Player player) {
        player.sendMessage("§lВы получили 100 монет за 30 минут игры!");
            UUID playerUUID = player.getUniqueId();
            LlamaEconomy.getAPI().addMoney(playerUUID, 100.0);
        }
}

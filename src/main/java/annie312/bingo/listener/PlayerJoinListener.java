package annie312.bingo.listener;

import annie312.bingo.manager.GameManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener implements Listener {

    private final GameManager gameManager;
    private final JavaPlugin plugin;

    public PlayerJoinListener(GameManager gameManager, JavaPlugin plugin) {
        this.gameManager = gameManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (gameManager.isPickMode()) {
            // Выдаем креатив на следующем тике, чтобы клиент гарантированно инициализировал Креатив-меню
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (player.isOnline()) {
                    player.setGameMode(GameMode.CREATIVE);
                    player.getInventory().clear();
                }
            });
        }
    }
}
package annie312.bingo;

import annie312.bingo.listener.BingoListener;
import annie312.bingo.listener.CreativeListener;
import annie312.bingo.listener.PlayerJoinListener;
import annie312.bingo.manager.*;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Bingo extends JavaPlugin {

    @Override
    public void onEnable() {

        ObjectiveManager objectiveManager = new ObjectiveManager();
        TimerManager timer = new TimerManager(this);

        GameManager gameManager = new GameManager(
                this,
                objectiveManager,
                null,
                timer
        );

        BingoScoreboard scoreboard = new BingoScoreboard(
                gameManager,
                objectiveManager
        );

        gameManager.setScoreboard(scoreboard);

        Objects.requireNonNull(getCommand("bingo"))
                .setExecutor(gameManager);

        getServer().getPluginManager().registerEvents(
                new BingoListener(
                        gameManager,
                        objectiveManager,
                        scoreboard
                ),
                this
        );

        getServer().getPluginManager().registerEvents(
                new CreativeListener(
                        gameManager,
                        objectiveManager,
                        scoreboard
                ),
                this
        );

        // Регистрация обработчика входа
        getServer().getPluginManager().registerEvents(
                new PlayerJoinListener(
                        gameManager,
                        this
                ),
                this
        );

        getLogger().info("Bingo 2.0 started!");
    }
}
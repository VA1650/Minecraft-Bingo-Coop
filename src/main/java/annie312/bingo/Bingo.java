package annie312.bingo;


import annie312.bingo.listener.BingoListener;
import annie312.bingo.listener.CreativeListener;

import annie312.bingo.manager.*;


import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


public final class Bingo extends JavaPlugin {


    @Override
    public void onEnable(){


        ObjectiveManager objectiveManager = new ObjectiveManager();


        BingoScoreboard scoreboard = new BingoScoreboard(
                objectiveManager
        );


        TimerManager timer = new TimerManager(
                this
        );


        GameManager gameManager = new GameManager(
                this,
                objectiveManager,
                scoreboard,
                timer
        );



        Objects.requireNonNull(getCommand("bingo"))
                .setExecutor(gameManager);



        getServer()
                .getPluginManager()
                .registerEvents(

                        new BingoListener(
                                gameManager,
                                objectiveManager,
                                scoreboard
                        ),

                        this
                );



        getServer()
                .getPluginManager()
                .registerEvents(

                        new CreativeListener(
                                objectiveManager,
                                scoreboard
                        ),

                        this
                );



        getLogger()
                .info("Bingo 2.0 started!");

    }


}
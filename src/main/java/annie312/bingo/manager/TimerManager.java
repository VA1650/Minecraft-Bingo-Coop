package annie312.bingo.manager;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


import org.bukkit.Bukkit;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;



public class TimerManager {


    private final JavaPlugin plugin;


    private BukkitTask task;


    private BossBar bossBar;



    private long startTime;

    private boolean running;




    public TimerManager(JavaPlugin plugin){

        this.plugin = plugin;

    }







    public void start(){


        stop();



        running = true;


        startTime =
                System.currentTimeMillis();




        bossBar =
                Bukkit.createBossBar(
                        "⏱ BINGO 00:00",
                        BarColor.GREEN,
                        BarStyle.SOLID
                );



        for(Player player :
                Bukkit.getOnlinePlayers()){


            bossBar.addPlayer(player);

        }







        task =
                Bukkit.getScheduler()
                        .runTaskTimer(
                                plugin,
                                () -> {


                                    if(!running)
                                        return;



                                    update();



                                },
                                0L,
                                20L
                        );


    }








    private void update(){


        long seconds =
                getSeconds();



        String time =
                formatTime(seconds);





        bossBar.setTitle(
                "⏱ BINGO " + time
        );



        /*
          Прогресс чисто визуальный.
          После часа не ломается.
        */

        bossBar.setProgress(
                Math.min(
                        1.0,
                        (seconds % 3600)
                                /
                                3600.0
                )
        );



        Component action =
                Component.text(
                                "⏱ Время: "
                        )
                        .color(
                                NamedTextColor.YELLOW
                        )
                        .append(
                                Component.text(
                                                time
                                        )
                                        .color(
                                                NamedTextColor.WHITE
                                        )
                        );



        for(Player player :
                Bukkit.getOnlinePlayers()){


            player.sendActionBar(
                    action
            );

        }

    }









    public void stop(){


        running = false;



        if(task != null){

            task.cancel();

            task = null;

        }




        if(bossBar != null){

            bossBar.removeAll();

            bossBar = null;

        }

    }









    public long getSeconds(){


        if(!running)
            return 0;



        return (
                System.currentTimeMillis()
                        -
                        startTime
        )
                /
                1000;

    }








    public String getFormattedTime(){


        return formatTime(
                getSeconds()
        );

    }








    private String formatTime(
            long seconds
    ){


        long min =
                seconds / 60;


        long sec =
                seconds % 60;



        return String.format(
                "%02d:%02d",
                min,
                sec
        );

    }



}
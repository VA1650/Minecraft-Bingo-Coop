package annie312.bingo.manager;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public class GameManager implements CommandExecutor {


    private final JavaPlugin plugin;

    private final ObjectiveManager objectives;
    private final BingoScoreboard scoreboard;
    private final TimerManager timer;


    private boolean active = false;



    public GameManager(
            JavaPlugin plugin,
            ObjectiveManager objectives,
            BingoScoreboard scoreboard,
            TimerManager timer
    ){

        this.plugin = plugin;
        this.objectives = objectives;
        this.scoreboard = scoreboard;
        this.timer = timer;

    }






    public boolean isNotActive(){

        return !active;

    }







    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ){


        if(!(sender instanceof Player player))
            return true;



        if(args.length == 0){

            help(player);
            return true;

        }



        switch(args[0].toLowerCase()){


            case "pick" ->
                    pick(player);



            case "random" ->
                    random(player,args);



            case "start" ->
                    start(player);



            case "stop" ->
                    stop();



            case "list" ->
                    list(player);


            default ->
                    help(player);

        }


        return true;

    }









    private void pick(Player player){


        if(active){

            msg(
                    player,
                    "Игра уже идёт!"
            );

            return;

        }



        objectives.clear();



        for(Player p :
                Bukkit.getOnlinePlayers()){


            p.setGameMode(
                    GameMode.CREATIVE
            );


            p.getInventory()
                    .clear();

        }



        scoreboard.update();



        Bukkit.broadcast(
                Component.text(
                                "★ Режим выбора Bingo включён ★"
                        )
                        .color(
                                NamedTextColor.AQUA
                        )
        );

    }









    private void random(
            Player player,
            String[] args
    ){


        if(active){

            msg(
                    player,
                    "Игра уже идёт!"
            );

            return;

        }



        int amount = 25;



        if(args.length > 1){

            try{

                amount =
                        Integer.parseInt(args[1]);

            }
            catch(NumberFormatException e){

                msg(
                        player,
                        "Нужно число."
                );

                return;

            }

        }



        objectives.generateRandom(
                amount
        );



        scoreboard.update();



        Bukkit.broadcast(
                Component.text(
                                "Создано целей: "
                        )
                        .color(
                                NamedTextColor.LIGHT_PURPLE
                        )
                        .append(
                                Component.text(amount)
                        )
        );

    }









    private void start(Player player){


        if(objectives.size() == 0){

            msg(
                    player,
                    "Сначала выбери цели!"
            );

            return;

        }



        if(active){

            msg(
                    player,
                    "Игра уже запущена!"
            );

            return;

        }




        active = true;



        for(Player p :
                Bukkit.getOnlinePlayers()){


            p.getInventory()
                    .clear();


            p.setGameMode(
                    GameMode.SURVIVAL
            );

        }



        timer.start();


        scoreboard.update();



        Bukkit.broadcast(
                Component.text(
                                "★ BINGO НАЧАЛОСЬ ★"
                        )
                        .color(
                                NamedTextColor.GOLD
                        )
        );

    }









    private void stop(){


        active = false;


        timer.stop();


        objectives.clear();


        scoreboard.update();



        Bukkit.broadcast(
                Component.text(
                                "Bingo остановлено."
                        )
                        .color(
                                NamedTextColor.RED
                        )
        );

    }









    public void finish(){


        if(!active)
            return;



        active = false;



        String time =
                timer.getFormattedTime();



        timer.stop();



        Bukkit.broadcast(
                Component.text(
                                "★ ★ BINGO ПОБЕДА ★ ★"
                        )
                        .color(
                                NamedTextColor.GREEN
                        )
        );



        Bukkit.broadcast(
                Component.text(
                                "Время: "
                        )
                        .append(
                                Component.text(time)
                        )
        );



        Bukkit.getScheduler()
                .runTaskLater(
                        plugin,
                        Bukkit::shutdown,
                        100L
                );

    }









    private void list(Player player){


        player.sendMessage(
                Component.text(
                                "=== ЦЕЛИ ==="
                        )
                        .color(
                                NamedTextColor.GOLD
                        )
        );



        objectives.getAll()
                .forEach(objective ->

                        player.sendMessage(
                                Component.translatable(
                                        objective.getMaterial()
                                                .translationKey()
                                )
                        )

                );

    }








    private void help(Player player){


        player.sendMessage(
                Component.text(
                        "/bingo pick"
                )
        );

        player.sendMessage(
                Component.text(
                        "/bingo random <число>"
                )
        );

        player.sendMessage(
                Component.text(
                        "/bingo start"
                )
        );

        player.sendMessage(
                Component.text(
                        "/bingo stop"
                )
        );

        player.sendMessage(
                Component.text(
                        "/bingo list"
                )
        );

    }









    private void msg(
            Player player,
            String text
    ){

        player.sendMessage(
                Component.text(text)
                        .color(NamedTextColor.RED)
        );

    }

}
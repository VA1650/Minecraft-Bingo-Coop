package annie312.bingo.manager;


import annie312.bingo.objective.BingoObjective;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;


import java.util.HashMap;
import java.util.Map;



public class BingoScoreboard {


    private final ObjectiveManager objectives;


    private final Scoreboard board;
    private final Objective objective;


    private final Map<Integer, Team> lines =
            new HashMap<>();



    public BingoScoreboard(
            ObjectiveManager objectives
    ){

        this.objectives = objectives;


        board =
                Bukkit.getScoreboardManager()
                        .getNewScoreboard();



        objective =
                board.registerNewObjective(
                        "bingo",
                        Criteria.DUMMY,
                        Component.text("★ BINGO ★")
                                .color(
                                        NamedTextColor.GOLD
                                )
                );



        objective.setDisplaySlot(
                DisplaySlot.SIDEBAR
        );


        createLines();

    }







    private void createLines(){


        for(int i = 0; i < 15; i++){


            String entry =
                    "§" + Integer.toHexString(i)
                            + "§r";



            Team team =
                    board.registerNewTeam(
                            "line_" + i
                    );



            team.addEntry(entry);



            objective
                    .getScore(entry)
                    .setScore(
                            15 - i
                    );



            lines.put(
                    i,
                    team
            );

        }

    }







    public void update(){


        clear();



        int index = 0;



        Team counter =
                lines.get(index++);



        counter.prefix(
                Component.text(
                                "Осталось: "
                        )
                        .color(
                                NamedTextColor.YELLOW
                        )
                        .append(
                                Component.text(
                                                objectives.remaining()
                                        )
                                        .color(
                                                NamedTextColor.WHITE
                                        )
                        )
        );





        for(BingoObjective objective :
                objectives.getAll()){


            if(index >= 15)
                break;



            Team team =
                    lines.get(index++);



            Component name =
                    Component.translatable(
                            objective.getMaterial()
                                    .translationKey()
                    );



            if(objective.isCompleted()){


                team.prefix(
                        Component.text("✔ ")
                                .color(
                                        NamedTextColor.GREEN
                                )
                                .append(name)
                );


            } else {


                team.prefix(
                        Component.text("◆ ")
                                .color(
                                        NamedTextColor.GRAY
                                )
                                .append(name)
                );

            }


        }



        send();

    }








    private void clear(){


        for(Team team :
                lines.values()){


            team.prefix(
                    Component.empty()
            );

        }

    }








    private void send(){


        for(Player player :
                Bukkit.getOnlinePlayers()){


            player.setScoreboard(
                    board
            );

        }

    }

}
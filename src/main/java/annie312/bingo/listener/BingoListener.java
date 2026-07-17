package annie312.bingo.listener;


import annie312.bingo.manager.BingoScoreboard;
import annie312.bingo.manager.GameManager;
import annie312.bingo.manager.ObjectiveManager;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;



public class BingoListener implements Listener {


    private final GameManager gameManager;
    private final ObjectiveManager objectives;
    private final BingoScoreboard scoreboard;



    public BingoListener(
            GameManager gameManager,
            ObjectiveManager objectives,
            BingoScoreboard scoreboard
    ){

        this.gameManager = gameManager;
        this.objectives = objectives;
        this.scoreboard = scoreboard;

    }







    @EventHandler
    public void onPickup(
            PlayerAttemptPickupItemEvent event
    ){


        if(gameManager.isNotActive())
            return;



        ItemStack item =
                event.getItem()
                        .getItemStack();



        check(
                event.getPlayer(),
                item.getType()
        );

    }









    @EventHandler
    public void onCraft(
            CraftItemEvent event
    ){


        if(!(event.getWhoClicked()
                instanceof Player player))
            return;



        if(gameManager.isNotActive())
            return;



        check(
                player,
                event.getRecipe()
                        .getResult()
                        .getType()
        );

    }









    private void check(
            Player player,
            Material material
    ){


        if(!objectives.contains(material))
            return;



        boolean completed =
                objectives.complete(
                        material,
                        player.getUniqueId()
                );



        if(!completed)
            return;





        Bukkit.broadcast(
                Component.text(
                                "✔ "
                        )
                        .color(
                                NamedTextColor.GREEN
                        )
                        .append(
                                Component.text(
                                                player.getName()
                                        )
                                        .color(
                                                NamedTextColor.WHITE
                                        )
                        )
                        .append(
                                Component.text(
                                                " нашёл "
                                        )
                                        .color(
                                                NamedTextColor.GRAY
                                        )
                        )
                        .append(
                                Component.translatable(
                                                material.translationKey()
                                        )
                                        .color(
                                                NamedTextColor.GOLD
                                        )
                        )
        );





        player.playSound(
                player.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                1f,
                1f
        );



        player.spawnParticle(
                Particle.HAPPY_VILLAGER,
                player.getLocation()
                        .add(0,1,0),
                15,
                0.4,
                0.5,
                0.4,
                0
        );



        scoreboard.update();





        if(objectives.finished()){


            gameManager.finish();

        }

    }

}
package annie312.bingo.listener;


import annie312.bingo.manager.BingoScoreboard;
import annie312.bingo.manager.GameManager;
import annie312.bingo.manager.ObjectiveManager;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;


public class CreativeListener implements Listener {


    private final GameManager gameManager;
    private final ObjectiveManager objectives;
    private final BingoScoreboard scoreboard;



    public CreativeListener(
            GameManager gameManager,
            ObjectiveManager objectives,
            BingoScoreboard scoreboard
    ){

        this.gameManager = gameManager;
        this.objectives = objectives;
        this.scoreboard = scoreboard;

    }


    @EventHandler
    public void onCreativeClick(
            InventoryCreativeEvent event
    ){

        Player player = (Player) event.getWhoClicked();

        if(!(event.getWhoClicked()
                instanceof Player) || !(player.getGameMode() == GameMode.CREATIVE))
            return;

        if(!gameManager.isNotActive())
            return;



        Material material =
                event.getCursor()
                        .getType();



        if(material == Material.AIR)
            return;



        if(objectives.contains(material))
            return;



        objectives.add(material);



        Bukkit.broadcast(
                Component.text(
                                "+ "
                        )
                        .color(
                                NamedTextColor.GREEN
                        )
                        .append(
                                Component.translatable(
                                        material.translationKey()
                                )
                        )
                        .append(
                                Component.text(
                                                " добавлен в Bingo"
                                        )
                                        .color(
                                                NamedTextColor.GRAY
                                        )
                        )
        );



        scoreboard.update();

    }








    @EventHandler
    public void onDrop(
            PlayerDropItemEvent event
    ){

        Player player = event.getPlayer();

        if(!(player.getGameMode() == GameMode.CREATIVE))
            return;


        if(!gameManager.isNotActive())
            return;


        Material material =
                event.getItemDrop()
                        .getItemStack()
                        .getType();



        if(!objectives.contains(material))
            return;



        objectives.remove(material);



        Bukkit.broadcast(
                Component.text(
                                "- "
                        )
                        .color(
                                NamedTextColor.RED
                        )
                        .append(
                                Component.translatable(
                                        material.translationKey()
                                )
                        )
                        .append(
                                Component.text(
                                                " удалён из Bingo"
                                        )
                                        .color(
                                                NamedTextColor.GRAY
                                        )
                        )
        );



        event.getItemDrop()
                .remove();



        scoreboard.update();

    }

}
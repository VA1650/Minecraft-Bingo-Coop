package annie312.bingo.listener;


import annie312.bingo.manager.BingoScoreboard;
import annie312.bingo.manager.ObjectiveManager;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;



public class CreativeListener implements Listener {


    private final ObjectiveManager objectives;
    private final BingoScoreboard scoreboard;



    public CreativeListener(
            ObjectiveManager objectives,
            BingoScoreboard scoreboard
    ){

        this.objectives = objectives;
        this.scoreboard = scoreboard;

    }







    @EventHandler
    public void onCreativeClick(
            InventoryCreativeEvent event
    ){


        if(!(event.getWhoClicked()
                instanceof Player))
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
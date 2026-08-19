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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameManager implements CommandExecutor {

    private final JavaPlugin plugin;
    private final ObjectiveManager objectives;
    private BingoScoreboard scoreboard;
    private final TimerManager timer;

    private boolean active = false;
    private boolean pickMode = false;
    private final Set<UUID> playersAtStart = new HashSet<>();

    public GameManager(
            JavaPlugin plugin,
            ObjectiveManager objectives,
            BingoScoreboard scoreboard,
            TimerManager timer
    ) {
        this.plugin = plugin;
        this.objectives = objectives;
        this.scoreboard = scoreboard;
        this.timer = timer;
    }

    public boolean isNotActive() {
        return !active;
    }

    public void setScoreboard(BingoScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public boolean isPlayerAtStart(UUID playerUuid) {
        return playersAtStart.contains(playerUuid);
    }

    public boolean isPickMode() {
        return pickMode;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String[] args
    ) {
        if (!(sender instanceof Player player))
            return true;

        if (args.length == 0) {
            help(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "pick" -> pick(player);
            case "random" -> random(player, args);
            case "clear" -> clear(player);
            case "start" -> start(player);
            case "stop" -> stop();
            case "list" -> list(player);
            default -> help(player);
        }

        return true;
    }

    private void pick(Player player) {
        if (active) {
            msg(player, "Игра уже идёт!");
            return;
        }

        if (pickMode) {
            msg(player, "Режим выбора уже включён!");
            return;
        }

        pickMode = true;
        objectives.clear();

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(GameMode.CREATIVE);
            p.getInventory().clear();
        }

        scoreboard.update();

        Bukkit.broadcast(
                Component.text("★ Режим выбора Bingo включён ★")
                        .color(NamedTextColor.AQUA)
        );
    }

    private void random(Player player, String[] args) {
        if (active) {
            msg(player, "Игра уже идёт!");
            return;
        }

        int amount = 25;

        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                msg(player, "Нужно число.");
                return;
            }
        }

        objectives.generateRandom(amount);
        pickMode = true;

        // Выдаем Всем Креатив и очищаем инвентарь для выбора/просмотра
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(GameMode.CREATIVE);
            p.getInventory().clear();
        }

        scoreboard.update();

        Bukkit.broadcast(
                Component.text("Создано случайных целей: ")
                        .color(NamedTextColor.LIGHT_PURPLE)
                        .append(Component.text(amount))
        );
    }

    private void clear(Player player) {
        if (active) {
            msg(player, "Нельзя очистить цели во время игры!");
            return;
        }

        objectives.clear();
        scoreboard.update();

        Bukkit.broadcast(
                Component.text("Список целей Bingo был очищен.")
                        .color(NamedTextColor.RED)
        );
    }

    private void start(Player player) {
        if (objectives.size() == 0) {
            msg(player, "Сначала выбери цели!");
            return;
        }

        if (active) {
            msg(player, "Игра уже запущена!");
            return;
        }

        active = true;
        pickMode = false;

        playersAtStart.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            playersAtStart.add(p.getUniqueId());
            p.getInventory().clear();
            p.setGameMode(GameMode.SURVIVAL);
        }

        timer.start();
        scoreboard.update();

        Bukkit.broadcast(
                Component.text("★ BINGO НАЧАЛОСЬ ★")
                        .color(NamedTextColor.GOLD)
        );
    }

    private void stop() {
        if (!active) {
            Bukkit.broadcast(
                    Component.text("Игра не идёт!")
                            .color(NamedTextColor.RED)
            );
            return;
        }

        active = false;
        pickMode = false;
        playersAtStart.clear();

        timer.stop();
        objectives.clear();
        scoreboard.update();

        Bukkit.broadcast(
                Component.text("Bingo остановлено.")
                        .color(NamedTextColor.RED)
        );
    }

    public void finish() {
        if (!active)
            return;

        active = false;
        String time = timer.getFormattedTime();
        timer.stop();

        Bukkit.broadcast(
                Component.text("★ ★ BINGO ПОБЕДА ★ ★")
                        .color(NamedTextColor.GREEN)
        );

        Bukkit.broadcast(
                Component.text("Время: ").append(Component.text(time))
        );

        Bukkit.getScheduler().runTaskLater(
                plugin,
                Bukkit::shutdown,
                100L
        );
    }

    private void list(Player player) {
        player.sendMessage(
                Component.text("=== ЦЕЛИ ===").color(NamedTextColor.GOLD)
        );

        objectives.getAll().forEach(objective ->
                player.sendMessage(
                        Component.translatable(objective.getMaterial().translationKey())
                )
        );
    }

    private void help(Player player) {
        player.sendMessage(Component.text("/bingo pick"));
        player.sendMessage(Component.text("/bingo random <число>"));
        player.sendMessage(Component.text("/bingo clear"));
        player.sendMessage(Component.text("/bingo start"));
        player.sendMessage(Component.text("/bingo stop"));
        player.sendMessage(Component.text("/bingo list"));
    }

    private void msg(Player player, String text) {
        player.sendMessage(
                Component.text(text).color(NamedTextColor.RED)
        );
    }
}
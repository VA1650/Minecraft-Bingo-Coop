package annie312.bingo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class Bingo extends JavaPlugin implements Listener {

    private final Set<Material> commonObjectives = new HashSet<>();
    private boolean isGameActive = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            player.sendMessage(Component.text("--- BINGO CO-OP ---", NamedTextColor.GOLD, TextDecoration.BOLD));
            player.sendMessage(Component.text("/bingo pick - Выбор целей (Креатив)", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("/bingo random [число] - Рандом блоков", NamedTextColor.LIGHT_PURPLE));
            player.sendMessage(Component.text("/bingo list - Список всех целей в чат", NamedTextColor.AQUA));
            player.sendMessage(Component.text("/bingo start - НАЧАТЬ ИГРУ", NamedTextColor.GREEN, TextDecoration.BOLD));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "pick" -> {
                isGameActive = false;
                commonObjectives.clear();
                Bukkit.broadcast(Component.text("Режим выбора включен! Все игроки в Креативе.", NamedTextColor.AQUA));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.setGameMode(GameMode.CREATIVE);
                    updateGlobalScoreboard();
                }
            }
            case "random" -> {
                isGameActive = false;
                int count = (args.length > 1) ? Integer.parseInt(args[1]) : 5;
                generateRandomObjectives(count);
                Bukkit.broadcast(Component.text("Сгенерировано " + count + " случайных целей!", NamedTextColor.LIGHT_PURPLE));
                updateGlobalScoreboard();
            }
            case "list" -> {
                if (commonObjectives.isEmpty()) {
                    player.sendMessage(Component.text("Целей пока нет.", NamedTextColor.RED));
                    return true;
                }
                player.sendMessage(Component.text("--- ТЕКУЩИЕ ЦЕЛИ ---", NamedTextColor.GOLD));
                for (Material m : commonObjectives) {
                    player.sendMessage(Component.text("- ", NamedTextColor.GRAY).append(Component.translatable(m.translationKey(), NamedTextColor.WHITE)));
                }
            }
            case "start" -> {
                if (commonObjectives.isEmpty()) {
                    player.sendMessage(Component.text("Сначала выберите предметы!", NamedTextColor.RED));
                    return true;
                }
                isGameActive = true;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.getInventory().clear();
                    p.setGameMode(GameMode.SURVIVAL);
                }
                Bukkit.broadcast(Component.text("ИГРА НАЧАЛАСЬ! Удачи команде!", NamedTextColor.GOLD, TextDecoration.BOLD));
                updateGlobalScoreboard();
            }
        }
        return true;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!isGameActive && commonObjectives.contains(e.getItemDrop().getItemStack().getType())) {
            Material dropped = e.getItemDrop().getItemStack().getType();
            commonObjectives.remove(dropped);

            Bukkit.broadcast(Component.text("Цель удалена: ", NamedTextColor.RED)
                    .append(Component.translatable(dropped.translationKey()))
                    .append(Component.text(" (убрал " + e.getPlayer().getName() + ")", NamedTextColor.GRAY)));

            updateGlobalScoreboard();
            e.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onCreativeClick(InventoryCreativeEvent event) {
        if (!isGameActive) {
            Material type = event.getCursor().getType();
            if (type != Material.AIR && !commonObjectives.contains(type)) {
                commonObjectives.add(type);

                Bukkit.broadcast(Component.text("Добавлена цель: ", NamedTextColor.AQUA)
                        .append(Component.translatable(type.translationKey(), NamedTextColor.WHITE))
                        .append(Component.text(" (выбрал " + event.getWhoClicked().getName() + ")", NamedTextColor.GRAY)));

                updateGlobalScoreboard();
            }
        }
    }

    @EventHandler
    public void onJoin() {
        updateGlobalScoreboard();
    }

    private void checkInventory(Player player) {
        if (!isGameActive || commonObjectives.isEmpty()) return;
        boolean changed = false;
        Iterator<Material> it = commonObjectives.iterator();
        while (it.hasNext()) {
            Material m = it.next();
            if (player.getInventory().contains(m)) {
                it.remove();
                Bukkit.broadcast(Component.text("НАЙДЕНО: ", NamedTextColor.GOLD)
                        .append(Component.translatable(m.translationKey(), NamedTextColor.GREEN, TextDecoration.BOLD))
                        .append(Component.text(" (добыл " + player.getName() + ")", NamedTextColor.WHITE)));
                changed = true;
            }
        }
        if (changed) {
            if (commonObjectives.isEmpty()) finishGame();
            else updateGlobalScoreboard();
        }
    }

    @EventHandler public void onPickup(PlayerAttemptPickupItemEvent e) { Bukkit.getScheduler().runTaskLater(this, () -> checkInventory(e.getPlayer()), 1L); }
    @EventHandler public void onInvClick(InventoryClickEvent e) { if (e.getWhoClicked() instanceof Player p) Bukkit.getScheduler().runTaskLater(this, () -> checkInventory(p), 1L); }
    @EventHandler public void onCraft(CraftItemEvent e) { Bukkit.getScheduler().runTaskLater(this, () -> checkInventory((Player) e.getWhoClicked()), 1L); }

    private void generateRandomObjectives(int count) {
        List<Material> allBlocks = Arrays.stream(Material.values())
                .filter(m -> m.isBlock() && m.isItem() && !m.isAir() && !m.name().contains("LEGACY"))
                .filter(m -> !m.name().contains("COMMAND") && !m.name().contains("STRUCTURE") && !m.name().contains("BARRIER"))
                .toList();
        commonObjectives.clear();
        while (commonObjectives.size() < Math.min(count, allBlocks.size())) {
            commonObjectives.add(allBlocks.get(ThreadLocalRandom.current().nextInt(allBlocks.size())));
        }
    }

    private void updateGlobalScoreboard() {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective obj = board.getObjective("bingo");
        if (obj != null) obj.unregister();
        obj = board.registerNewObjective("bingo", Criteria.DUMMY, Component.text("= BINGO CO-OP =", NamedTextColor.GOLD, TextDecoration.BOLD));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        int i = 0;
        for (Material m : commonObjectives) {
            if (i++ > 14) break;
            obj.getScore("§f" + m.name().toLowerCase().replace("_", " ")).setScore(commonObjectives.size() - i);
        }
        for (Player p : Bukkit.getOnlinePlayers()) p.setScoreboard(board);
    }

    private void finishGame() {
        isGameActive = false;
        Bukkit.broadcast(Component.text("ПОБЕДА! ВЕСЬ СПИСОК СОБРАН!", NamedTextColor.GREEN, TextDecoration.BOLD));
        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) p.kick(Component.text("Мир пересоздается!"));
            Bukkit.shutdown();
        }, 200L);
    }
}
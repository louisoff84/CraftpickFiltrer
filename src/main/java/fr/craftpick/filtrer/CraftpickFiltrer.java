package fr.craftpick.filtrer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftpickFiltrer extends JavaPlugin {
    private ChatFilter filter;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        filter = new ChatFilter(this);
        getServer().getPluginManager().registerEvents(filter, this);
        getLogger().info("CraftpickFiltrer est actif.");
    }

    public String color(String value) {
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("craftpickfiltrer.admin")) {
            sender.sendMessage(color(getConfig().getString("messages.no-permission", "&cPermission refusée.")));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            filter.reload();
            sender.sendMessage(color(getConfig().getString("messages.reloaded", "&aConfiguration rechargée.")));
            return true;
        }
        sender.sendMessage(color("&e/" + label + " reload"));
        return true;
    }
}

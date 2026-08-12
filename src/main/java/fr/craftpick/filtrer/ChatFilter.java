package fr.craftpick.filtrer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

final class ChatFilter implements Listener {
    private final CraftpickFiltrer plugin;
    private final Map<UUID, Long> lastMessage = new HashMap<UUID, Long>();
    private List<Pattern> blocked = new ArrayList<Pattern>();
    private List<Pattern> regex = new ArrayList<Pattern>();
    private Pattern linkPattern;

    ChatFilter(CraftpickFiltrer plugin) {
        this.plugin = plugin;
        reload();
    }

    void reload() {
        blocked = new ArrayList<Pattern>();
        regex = new ArrayList<Pattern>();
        for (String word : plugin.getConfig().getStringList("blocked-words")) {
            if (!word.trim().isEmpty()) blocked.add(Pattern.compile("(?iu)(?<![\\p{L}\\p{N}])" + Pattern.quote(word.trim()) + "(?![\\p{L}\\p{N}])"));
        }
        for (String expression : plugin.getConfig().getStringList("blocked-regex")) {
            try { regex.add(Pattern.compile(expression, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)); }
            catch (RuntimeException ex) { plugin.getLogger().warning("Regex ignorée: " + expression); }
        }
        linkPattern = Pattern.compile("(?i)(?:https?://|www\\.|(?:[a-z0-9-]+\\.)+(?:com|net|org|fr|gg|io|xyz|me))(?:\\S*)");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("craftpickfiltrer.bypass")) return;
        String message = event.getMessage();
        String reason = null;

        if (plugin.getConfig().getBoolean("anti-spam.enabled", true)) {
            long now = System.currentTimeMillis();
            Long previous = lastMessage.put(player.getUniqueId(), now);
            long delay = plugin.getConfig().getLong("anti-spam.delay-ms", 1200L);
            if (previous != null && now - previous < delay) reason = "spam";
        }
        if (reason == null && plugin.getConfig().getBoolean("block-links", true) && linkPattern.matcher(message).find()) reason = "lien";
        if (reason == null && plugin.getConfig().getBoolean("block-caps", true) && isCaps(message)) reason = "majuscules";
        if (reason == null && matches(message)) reason = "mot interdit";

        if (reason != null) {
            event.setCancelled(true);
            player.sendMessage(plugin.color(plugin.getConfig().getString("messages.blocked", "&cMessage bloqué: &f%reason%").replace("%reason%", reason)));
            notifyStaff(player, message, reason);
        }
    }

    private boolean matches(String message) {
        String normalized = message.toLowerCase(Locale.ROOT).replaceAll("[._*\\- ]+", " ");
        for (Pattern pattern : blocked) if (pattern.matcher(normalized).find()) return true;
        for (Pattern pattern : regex) if (pattern.matcher(message).find()) return true;
        return false;
    }

    private boolean isCaps(String message) {
        int letters = 0, upper = 0;
        for (char c : message.toCharArray()) if (Character.isLetter(c)) { letters++; if (Character.isUpperCase(c)) upper++; }
        int minimum = plugin.getConfig().getInt("caps.minimum-letters", 8);
        double ratio = plugin.getConfig().getDouble("caps.maximum-ratio", 0.75D);
        return letters >= minimum && ((double) upper / letters) > ratio;
    }

    private void notifyStaff(Player author, String message, String reason) {
        if (!plugin.getConfig().getBoolean("staff-alerts", true)) return;
        String alert = plugin.color(plugin.getConfig().getString("messages.staff-alert", "&8[&cFiltre&8] &e%player%&7: %message% &8(&c%reason%&8)")
                .replace("%player%", author.getName()).replace("%message%", message).replace("%reason%", reason));
        for (Player online : plugin.getServer().getOnlinePlayers()) if (online.hasPermission("craftpickfiltrer.alerts")) online.sendMessage(alert);
    }
}

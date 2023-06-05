//   #============================================#
//        MODERATION/ AUTO BROADCAST PLUGIN
//      GitHub: comingsoon. . .
//      Spigot: comingsoon..:
//                  by fatleooooo0
//   #============================================#



package me.claysmc.clayschatmod2;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatModerationPlugin extends Plugin implements Listener {
    private List<String> blockedWords;
    private String moderationMessage;
    private String notificationMessage;
    private String prefix;
    private Set<ProxiedPlayer> alertedPlayers;

    @Override
    public void onEnable() {
        loadConfiguration();
        getProxy().getPluginManager().registerListener(this, this);
        alertedPlayers = new HashSet<>();
    }

    private void loadConfiguration() {
        blockedWords = getConfig().getStringList("blocked_words");
        moderationMessage = getConfig().getString("moderation_message");
        notificationMessage = getConfig().getString("notification_message");
        prefix = getConfig().getString("prefix");
    }

    private Configuration getConfig() {
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {
        if (event.isCancelled() || !(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();

        // Transform uppercase words to lowercase
        String lowercaseMessage = message.toLowerCase();

        // Block command spam and identical consecutive messages
        if (message.startsWith("/") || message.equals(player.getPendingConnection().getListener().getHost())) {
            event.setCancelled(true);
            return;
        }

        // Block forbidden words
        for (String blockedWord : blockedWords) {
            if (lowercaseMessage.contains(blockedWord)) {
                event.setCancelled(true);
                String finalMessage = moderationMessage.replace("%player%", player.getName())
                        .replace("%word%", blockedWord)
                        .replace("%prefix%", prefix);

                // Censor the blocked word with asterisks
                String censoredMessage = message.replaceAll("(?i)\\b" + blockedWord + "\\b", "*".repeat(blockedWord.length()));

                player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', finalMessage)));

                if (alertedPlayers.contains(player)) {
                    String notification = notificationMessage.replace("{player}", player.getName())
                            .replace("{word}", blockedWord)
                            .replace("%prefix%", prefix);
                    player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', notification)));
                    player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', censoredMessage)));
                }
                return;
            }
        }
    }

    public void addAlertedPlayer(ProxiedPlayer player) {
        alertedPlayers.add(player);
    }

    public void removeAlertedPlayer(ProxiedPlayer player) {
        alertedPlayers.remove(player);
    }
}

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AutoBroadcastManager {
    private final Plugin plugin;
    private final List<String> messages;
    private final String prefix;
    private ScheduledTask autoBroadcastTask;

    public AutoBroadcastManager(Plugin plugin, List<String> messages, String prefix) {
        this.plugin = plugin;
        this.messages = messages;
        this.prefix = prefix;
    }

    public void startAutoBroadcast(int interval) {
        if (autoBroadcastTask != null) {
            autoBroadcastTask.cancel();
        }

        autoBroadcastTask = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            String message = getRandomMessage();
            broadcastMessage(message);
        }, interval, interval, TimeUnit.MINUTES);
    }

    public void stopAutoBroadcast() {
        if (autoBroadcastTask != null) {
            autoBroadcastTask.cancel();
            autoBroadcastTask = null;
        }
    }

    private String getRandomMessage() {
        Random random = new Random();
        int index = random.nextInt(messages.size());
        return prefix + messages.get(index);
    }

    private void broadcastMessage(String message) {
        TextComponent component = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        plugin.getProxy().broadcast(component);
    }
}

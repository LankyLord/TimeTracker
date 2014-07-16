/*
 * Copyright (c) 2013 - 2014 cedeel.
 * All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The name of the author may not be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package be.darnell.timetracker;

import be.darnell.timetracker.commands.PlaytimeCommand;
import be.darnell.timetracker.commands.SeenCommand;
import be.darnell.timetracker.listeners.TimeTrackerPlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Plugin extends JavaPlugin {
    private TimeTracker tracker;

    // Join message
    private String joinMsg;

    @Override
    public void onEnable() {
        tracker = new TimeTracker(this);
        saveDefaultConfig();
        registerEvents();
        registerCommands();

        for (Player p : getServer().getOnlinePlayers())
            addPlayerAsync(p.getUniqueId());

        joinMsg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("MessageColour", "&e")) +
                getConfig().getString("JoinMessage", "Welcome %p to the server!");

        this.getLogger().info("All good. Loaded successfully");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        tracker.shutDown();
        this.getLogger().info("Disabled successfully");
    }

    /**
     * Register commands with the server
     */
    private void registerCommands() {
        this.getCommand("seen").setExecutor(new SeenCommand(tracker));
        this.getCommand("playtime").setExecutor(new PlaytimeCommand(tracker));
    }

    /**
     * Register events with the server
     */
    private void registerEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new TimeTrackerPlayerListener(this), this);
    }

    /**
     * Add a player to the list in memory
     *
     * @param id The UUID of the player to add
     */
    public void addPlayerAsync(final UUID id) {
        new BukkitRunnable() {
            @Override
            public void run() {
                tracker.addPlayer(id);
                if (tracker.isFirstSession(id)) {
                    Bukkit.getServer().broadcastMessage(joinMsg.replace("%p", getServer().getPlayer(id).getDisplayName()));
                }
            }
        }.runTaskAsynchronously(this);
    }

    /**
     * Remove a player from the list in memory
     *
     * @param id The UUID of  the player to remove
     */
    public void removePlayerAsync(final UUID id) {
        new BukkitRunnable() {
            @Override
            public void run() {
                tracker.removePlayer(id);
            }
        }.runTaskAsynchronously(this);
    }
}

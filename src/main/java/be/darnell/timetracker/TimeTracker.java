/*
 * Copyright (c) 2013 cedeel.
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

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TimeTracker extends JavaPlugin {

    protected Map<String, Long> players;
    private YamlConfiguration Data = null;
    private File DataFile = null;
    private static final String DATAFILENAME = "Data.yml";
    private String joinMsg;

    protected static String humanTime(long start, long end) {
        if (start != -1L) {
            long finaltime = (end - start) / 1000L;
            if (finaltime >= 86400L) {
                String s = (finaltime >= 172800L) ? "days" : "day";
                return (finaltime / 86400L + " " + s);
            } else if (finaltime >= 3600L) {
                String s = (finaltime >= 7200L) ? "hours" : "hour";
                return (finaltime / 3600L + " " + s);
            } else if (finaltime >= 60L) {
                String s = (finaltime >= 120L) ? "minutes" : "minute";
                return (finaltime / 60L + " " + s);
            } else {
                String s = (finaltime >= 2) ? "seconds" : "second";
                return (finaltime + " " + s);
            }
        }
        return null;
    }

    @Override
    public void onDisable() {
        for(String p : players.keySet()) {
            removePlayer(p);
        }
        saveData();
        System.out.println(this + " is now disabled!");
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new TimeTrackerPlayerListener(this), this);
        saveDefaultConfig();
        saveData();
        players = new HashMap<String, Long>();

        this.getCommand("seen").setExecutor(new SeenCommand(this));
        this.getCommand("playtime").setExecutor(new PlaytimeCommand(this));

        joinMsg = getConfig().getString("JoinMessage");
        for(Player p : getServer().getOnlinePlayers()) {
            addPlayer(p.getName());
        }

        System.out.println(this + " is now enabled.");
    }

    public long getFirstSeen(String name) {
        return getData().getLong(name.toLowerCase() + ".first", -1L);
    }

    public long getLastSeen(String name) {
        return getData().getLong(name.toLowerCase() + ".last", -1L);
    }

    public long getPlayTime(String name) {
        return getData().getLong(name.toLowerCase() + ".playtime", -1L);
    }

    protected void addPlayTime(String name, long value) {
        getData().set(name.toLowerCase() + ".playtime", Long.valueOf(getPlayTime(name) + value));
        saveData();
    }

    protected void setFirstSeen(String name, long value) {
        getData().set(name.toLowerCase() + ".first", Long.valueOf(value));
        saveData();
    }

    protected void setLastSeen(String name, long value) {
        getData().set(name.toLowerCase() + ".last", Long.valueOf(value));
        saveData();
    }

    private void reloadData() {
        if (DataFile == null)
            DataFile = new File(getDataFolder(), DATAFILENAME);
        Data = YamlConfiguration.loadConfiguration(DataFile);

        InputStream defData = this.getResource(DATAFILENAME);
        if (defData != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defData);
            Data.setDefaults(defConfig);
        }
    }

    protected void removePlayer(String name) {
        long time = (new Date()).getTime();
        setLastSeen(name, time);
        addPlayTime(name, time - players.get(name));
        players.remove(name);
    }

    protected void addPlayer(String name) {
        long last = getLastSeen(name);
        long first = getFirstSeen(name);
        long ex = (new Date()).getTime();
        players.put(name, ex);
        if (last == -1L || first == -1L) {
            setFirstSeen(name, ex);
            getServer().broadcastMessage(ChatColor.YELLOW + joinMsg.replace("%p", name));
        }
    }

    private YamlConfiguration getData() {
        if (Data == null)
            this.reloadData();
        return Data;
    }

    private void saveData() {
        if (Data == null || DataFile == null)
            return;
        try {
            getData().save(DataFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + DataFile, ex);
        }
    }
}

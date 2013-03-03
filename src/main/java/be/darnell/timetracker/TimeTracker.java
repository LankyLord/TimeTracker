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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TimeTracker extends JavaPlugin {

  Map<String, Long> players;
  private FileConfiguration Data = null;
  private File DataFile = null;

  @Override
  public void onDisable() {
    System.out.println(this + " is now disabled!");
    saveData();
  }

  @Override
  public void onEnable() {
    PluginManager pluginManager = this.getServer().getPluginManager();
    pluginManager.registerEvents(new TimeTrackerPlayerListener(this), this);
    saveData();
    players = new HashMap<String, Long>();

    this.getCommand("seen").setExecutor(new SeenCommand(this));
    this.getCommand("playtime").setExecutor(new PlaytimeCommand(this));
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

  public void reloadData() {
    if (DataFile == null)
      DataFile = new File(getDataFolder(), "Data.yml");
    Data = YamlConfiguration.loadConfiguration(DataFile);

    InputStream defData = this.getResource("Data.yml");
    if (defData != null) {
      YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defData);
      Data.setDefaults(defConfig);
    }
  }

  public FileConfiguration getData() {
    if (Data == null)
      this.reloadData();
    return Data;
  }

  public void saveData() {
    if (Data == null || DataFile == null)
      return;
    try {
      getData().save(DataFile);
    } catch (IOException ex) {
      this.getLogger().log(Level.SEVERE, "Could not save config to " + DataFile, ex);
    }
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

  protected static String humanTime(long start, long end) {
    if (start != -1L) {
      long difference = end - start;
      long finaltime = difference / 1000L;
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
}

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
package be.darnell.simpleseen;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleSeen extends JavaPlugin {
  Map<String, Long> players;

  @Override
  public void onDisable() {
    System.out.println(this + " is now disabled!");
  }

  @Override
  public void onEnable() {
    PluginManager pluginManager = this.getServer().getPluginManager();
    pluginManager.registerEvents(new SimpleSeenPlayerListener(this), this);
    this.saveDefaultConfig();
    players = new HashMap<String, Long>();

    this.getCommand("seen").setExecutor(new SeenCommand(this));
    this.getCommand("playtime").setExecutor(new PlaytimeCommand(this));
    System.out.println(this + " is now enabled.");
  }
  
  public long getFirstSeen(String name) {
    return getConfig().getLong(name.toLowerCase() + ".first", -1L);
  }
  
  public long getLastSeen(String name) {
    return getConfig().getLong(name.toLowerCase() + ".last", -1L);
  }
  
  public long getPlayTime(String name) {
    return getConfig().getLong(name.toLowerCase() + ".playtime", -1L);
  }
  
  protected void addPlayTime(String name, long value) {
    getConfig().set(name.toLowerCase() + ".playtime", Long.valueOf(getPlayTime(name) + value));
    saveConfig();
  }
  
  protected void setFirstSeen(String name, long value) {
    getConfig().set(name.toLowerCase() + ".first", Long.valueOf(value));
    saveConfig();
  }
  
  protected void setLastSeen(String name, long value) {
    getConfig().set(name.toLowerCase() + ".last", Long.valueOf(value));
    saveConfig();
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

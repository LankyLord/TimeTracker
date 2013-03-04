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

import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class TimeTrackerPlayerListener implements Listener {

  private final TimeTracker plugin;
  private final String joinMsg;

  public TimeTrackerPlayerListener(TimeTracker plugin) {
    this.plugin = plugin;
    joinMsg = plugin.getConfig().getString("JoinMessage");
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    long time = (new Date()).getTime();
    String name = event.getPlayer().getName();
    plugin.setLastSeen(name, time);
    plugin.addPlayTime(name, time - plugin.players.get(name));
    plugin.players.remove(name);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    String name = event.getPlayer().getName();
    long last = plugin.getLastSeen(name);
    long first = plugin.getFirstSeen(name);
    long ex = (new Date()).getTime();
    plugin.players.put(name, ex);
    if (last == -1L || first == -1L) {
      plugin.setFirstSeen(name, ex);
      plugin.getServer().broadcastMessage(ChatColor.YELLOW + joinMsg.replace("%p", name));
    }
  }
}

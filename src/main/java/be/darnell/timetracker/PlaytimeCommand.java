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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class PlaytimeCommand implements CommandExecutor {

  final TimeTracker plugin;

  PlaytimeCommand(TimeTracker plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    if (cs instanceof Player) {
      Player player = (Player) cs;
      if (args.length < 1) {
        long first = plugin.getFirstSeen(player.getName());
        if (first != -1L)
          player.sendMessage(ChatColor.YELLOW + "Your first login was "
                  + ChatColor.GREEN + TimeTracker.humanTime(first, (new Date()).getTime())
                  + ChatColor.YELLOW + " ago.");
        long now = (new Date()).getTime();
        player.sendMessage(ChatColor.YELLOW + "Current session has lasted "
                + ChatColor.GREEN
                + TimeTracker.humanTime(plugin.players.get(player.getName()), now));
        player.sendMessage(ChatColor.YELLOW + "You have spent a total of "
                + ChatColor.GREEN + TimeTracker.humanTime(0L,
                (now - plugin.players.get(player.getName()))
                + plugin.getPlayTime(player.getName()))
                + ChatColor.YELLOW + " on this server.");
      } else
        player.sendMessage(ChatColor.RED + "Usage: /playtime");

      return true;
    } else
      return false;
  }
}

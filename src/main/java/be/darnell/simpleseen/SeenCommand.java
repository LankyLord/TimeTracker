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

import java.util.Date;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class SeenCommand implements CommandExecutor {

  final SimpleSeen plugin;

  SeenCommand(SimpleSeen plugin) {
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
    Player player = (Player) cs;
    String safenick = player.getName().toLowerCase().replaceAll("\'", "\"");
    String safenick1 = safenick.replaceAll("Â§f", "");
    if (args.length > 0) {
      String cel = args[0].toLowerCase();
      String seen = plugin.getConfig().getString(cel);
      String first = plugin.getConfig().getString(cel + "+");
      if (seen != null) {
        long lStartTime = Long.parseLong(seen);
        long lEndTime = (new Date()).getTime();
        long difference = lEndTime - lStartTime;
        long finaltime = difference / 60000L;
        String operation = "minute(s)";
        if (finaltime > 60L) {
          finaltime /= 60L;
          operation = "hour(s)";
          if (finaltime > 24L) {
            finaltime /= 24L;
            operation = "day(s)";
          }
        }

        if (first != null) {
          long lStartTime1 = Long.parseLong(first);
          long lEndTime1 = (new Date()).getTime();
          long difference1 = lEndTime1 - lStartTime1;
          long finaltime1 = difference1 / 60000L;
          String operation2 = "minute(s)";
          if (finaltime1 > 60L) {
            finaltime1 /= 60L;
            operation2 = "hour(s)";
            if (finaltime1 > 24L) {
              finaltime1 /= 24L;
              operation2 = "day(s)";
            }
          }

          if (cel.equalsIgnoreCase(safenick1))
            player.sendMessage(ChatColor.RED + "Still trying to find yourself, bud?");
          else {
            List list = this.plugin.getServer().matchPlayer(cel);
            if (list.size() == 1)
              player.sendMessage(ChatColor.GREEN + cel + " is online right now! Say hey!");
            else if (seen == null)
              player.sendMessage(ChatColor.RED + "That player has never been here before.");
            else {
              player.sendMessage(ChatColor.RED + cel + " was last seen on this server " + finaltime + " " + operation + " ago.");
              player.sendMessage(ChatColor.RED + cel + "\'s first logon was " + finaltime1 + " " + operation2 + " ago.");
            }
          }
        } else
          player.sendMessage(ChatColor.RED + "That player has never been here before.");
      } else
        player.sendMessage(ChatColor.RED + "That player has never been here before.");
    } else
      player.sendMessage(ChatColor.RED + "Usage: /seen <username>");

    return true;
  }
}

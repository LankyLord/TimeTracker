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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Date;
import java.util.List;

public final class SeenCommand implements CommandExecutor {

    private final TimeTracker plugin;

    SeenCommand(TimeTracker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String alias, String[] args) {
        if (args.length > 0) {
            String playerName = args[0].toLowerCase();
            long first = plugin.getFirstSeen(playerName);
            long seen = plugin.getLastSeen(playerName);
            if (first != -1L)
                if (playerName.equalsIgnoreCase(sender.getName().toLowerCase()))
                    sender.sendMessage(ChatColor.YELLOW + "Still trying to find yourself, bud?");
                else {
                    List list = plugin.getServer().matchPlayer(playerName);
                    sender.sendMessage(ChatColor.AQUA + "===== " + ChatColor.GREEN + "Player times for " + playerName + ChatColor.AQUA + " =====");
                    if (seen != -1L)
                        sender.sendMessage(ChatColor.YELLOW + "Last seen " + ChatColor.GREEN + plugin.sinceString(plugin.getLastSeen(playerName), (new Date()).getTime()));
                    sender.sendMessage(ChatColor.YELLOW + "First logon was " + ChatColor.GREEN + plugin.sinceString(plugin.getFirstSeen(playerName), (new Date()).getTime()));
                    sender.sendMessage(ChatColor.YELLOW + "Has spent " + ChatColor.GREEN + TimeTracker.humanTime(0L, plugin.getPlayTime(playerName)) + ChatColor.YELLOW + " on the server.");
                    if (list.size() == 1)
                        sender.sendMessage(ChatColor.GREEN + playerName + " is online right now! Say hey!");
                }
            else
                sender.sendMessage(ChatColor.YELLOW + "That player has never been here before.");
        } else
            sender.sendMessage(ChatColor.RED + "Usage: /seen <username>");

        return true;
    }
}

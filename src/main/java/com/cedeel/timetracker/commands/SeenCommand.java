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
package com.cedeel.timetracker.commands;

import com.cedeel.timetracker.TimeTracker;
import com.cedeel.timetracker.TrackedPlayer;
import com.cedeel.timetracker.Util.UUIDFetcher;
import com.cedeel.timetracker.Util.Util;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public final class SeenCommand implements CommandExecutor {

    private final TimeTracker tracker;

    public SeenCommand(TimeTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String alias, String[] args) {
        if (args.length > 0) {
            String playerName = args[0];
            UUID playerId;
            try {
                playerId = UUIDFetcher.getUUIDOf(playerName);
                playerName = UUIDFetcher.getNameOf(playerName);
            } catch (Exception e) {
                return false;
            }
            TrackedPlayer tracked = tracker.getPlayer(playerId);
            if (tracked != null) {
                if (tracked.getFirstJoined() != Util.UNINITIALISED_TIME)
                    if (playerName.equalsIgnoreCase(sender.getName().toLowerCase()))
                        sender.sendMessage(YELLOW + "Still trying to find yourself, bud?");
                    else {
                        sender.sendMessage(AQUA + "===== " + GREEN + "Player times for " + playerName + AQUA + " =====");
                        if (tracked.getLastSeen() != Util.UNINITIALISED_TIME)
                            sender.sendMessage(YELLOW + "Last seen " + GREEN + tracker.sinceString(tracked.getLastSeen(), (new Date()).getTime()));
                        sender.sendMessage(YELLOW + "First logon was " + GREEN + tracker.sinceString(tracked.getFirstJoined(), (new Date()).getTime()));
                        sender.sendMessage(YELLOW + "Has spent " + GREEN + Util.humanTime(0L, tracked.getPlaytime()) + YELLOW + " on the server.");
                        if (Bukkit.getOfflinePlayer(playerId).isOnline())
                            sender.sendMessage(GREEN + playerName + " is online right now! Say hey!");
                    }
                else
                    sender.sendMessage(YELLOW + playerName + " has never been here before.");
            } else
                sender.sendMessage(RED + "Sorry bud, " + args[0] + " is not a Minecraft account. Did you make a typo?");
        } else
            sender.sendMessage(RED + "Usage: /seen <username>");

        return true;
    }
}


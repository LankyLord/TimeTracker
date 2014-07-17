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
package com.cedeel.timetracker;

import com.cedeel.timetracker.Util.Util;
import com.cedeel.timetracker.storage.FileStorage;
import com.cedeel.timetracker.storage.Storage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TimeTracker {

    // In-memory set of logged in players
    private ConcurrentMap<UUID, TrackedPlayer> players;
    // File storage
    private Storage storage;
    // Date formatting
    private boolean alwaysDate = false;
    private int daysBeforeDate = 30;

    public TimeTracker(Plugin plugin) {
        // TODO: Change storage
        storage = new FileStorage(plugin.getDataFolder());
        players = new ConcurrentHashMap<UUID, TrackedPlayer>();
        storage.saveData();

        alwaysDate = plugin.getConfig().getBoolean("AlwaysDate", false);
        daysBeforeDate = plugin.getConfig().getInt("DaysBeforeDate", 30);
    }

    private static String dateTime(long start) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date = new Date(start);

        return df.format(date);
    }

    public String sinceString(long start, long end) {
        long daysSince = (end -start) / 86400000L;

        if (daysSince > daysBeforeDate || alwaysDate)
            return dateTime(start) + " (" + Util.humanTime(start, end) + " ago)";
        else return Util.humanTime(start, end) + " ago";
    }

    protected void addPlayer(final UUID id) {
        if (!getPlayers().containsKey(id)) {
            TrackedPlayer player = storage.getPlayer(id);
            long time = (new Date()).getTime();
            if (player.getLastSeen() == Util.UNINITIALISED_TIME || player.getFirstJoined() == Util.UNINITIALISED_TIME) {
                getPlayers().put(player.getPlayerID(), new TrackedPlayer(player.getPlayerID(), time, time, player.getPlaytime()));
            } else {
                getPlayers().put(player.getPlayerID(), new TrackedPlayer(player.getPlayerID(), player.getFirstJoined(), time, player.getPlaytime()));
            }
        }
    }

    protected boolean isFirstSession(final UUID id) {
        TrackedPlayer target = getPlayer(id);
        return target.getFirstJoined() == target.getLastSeen();
    }

    protected boolean removePlayer(final UUID id) {
        TrackedPlayer tracked = players.get(id);
        if (tracked != null) {
            long time = (new Date()).getTime();
            long playtime = (time - tracked.getLastSeen()) + tracked.getPlaytime();
            storage.pushPlayer(new TrackedPlayer(tracked.getPlayerID(), tracked.getFirstJoined(), time, playtime));
            getPlayers().remove(id);
            return true;
        }
        return false;
    }

    /**
     * Retrieve a player
     * @param id UUID of the player
     * @return Tracked player
     */
    public TrackedPlayer getPlayer(UUID id) {
        if (id != null) {
            if (players.containsKey(id)) {
                return players.get(id);
            } else {
                return storage.getPlayer(id);
            }
        }
        return null;
    }

    /**
     * Get the list of players
     *
     * @return Get the list of players
     */
    public ConcurrentMap<UUID, TrackedPlayer> getPlayers() {
        return players;
    }

    public void shutDown() {
        for (UUID id : getPlayers().keySet()) {
            removePlayer(id);
        }
        storage.saveData();
    }
}

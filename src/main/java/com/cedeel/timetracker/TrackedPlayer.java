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

import java.util.UUID;

public class TrackedPlayer {
    private long firstJoined, lastSeen, playtime;
    private UUID id;

    /**
     * A player that is tracked by this plugin
     * @param id The UUID of the player
     * @param firstJoined The time, in milliseconds, of their first join
     * @param lastSeen The time, in milliseconds, of when they last left the server
     * @param playtime The total play time of the player
     */
    public TrackedPlayer(UUID id, long firstJoined, long lastSeen, long playtime) {
        this.id = id;
        this.firstJoined = firstJoined;
        this.lastSeen = lastSeen;
        this.playtime = playtime;
    }

    /**
     * Get the time in milliseconds of the first join of a given player
     * @return The time in milliseconds of the first join
     */
    public long getFirstJoined() {
        return firstJoined;
    }

    /**
     * Get the time in milliseconds of the last time a given player left the server
     * @return The time in milliseconds of the last time they left the server
     */
    public long getLastSeen() {
        return lastSeen;
    }

    /**
     * Get the time a player has spent on the server
     * @return The time, in milliseconds, the player has spent on the server
     */
    public long getPlaytime() {
        return playtime;
    }

    /**
     * Get the UUID of the player
     * @return The UUID of the player
     */
    public UUID getPlayerID() {
        return id;
    }
}

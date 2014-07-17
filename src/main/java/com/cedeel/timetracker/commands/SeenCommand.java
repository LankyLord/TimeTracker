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
import com.cedeel.timetracker.Util;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Callable;

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

                sender.sendMessage(YELLOW + playerName + " has never been here before.");
            } else
                sender.sendMessage(RED + "Sorry bud, " + args[0] + " is not a Minecraft account. Did you make a typo?");
        } else
            sender.sendMessage(RED + "Usage: /seen <username>");

        return true;
    }
}

class UUIDFetcher implements Callable<Map<String, UUID>> {
    private static final double PROFILES_PER_REQUEST = 100;
    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
    private final JSONParser jsonParser = new JSONParser();
    private final List<String> names;
    private final boolean rateLimiting;

    public UUIDFetcher(List<String> names, boolean rateLimiting) {
        this.names = ImmutableList.copyOf(names);
        this.rateLimiting = rateLimiting;
    }

    public UUIDFetcher(List<String> names) {
        this(names, true);
    }

    public Map<String, UUID> call() throws Exception {
        Map<String, UUID> uuidMap = new HashMap<String, UUID>();
        int requests = (int) Math.ceil(names.size() / PROFILES_PER_REQUEST);
        for (int i = 0; i < requests; i++) {
            HttpURLConnection connection = createConnection();
            String body = JSONArray.toJSONString(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));
            writeBody(connection, body);
            JSONArray array = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            for (Object profile : array) {
                JSONObject jsonProfile = (JSONObject) profile;
                String id = (String) jsonProfile.get("id");
                String name = (String) jsonProfile.get("name");
                UUID uuid = UUIDFetcher.getUUID(id);
                uuidMap.put(name, uuid);
            }
            if (rateLimiting && i != requests - 1) {
                Thread.sleep(100L);
            }
        }
        return uuidMap;
    }

    private static void writeBody(HttpURLConnection connection, String body) throws Exception {
        OutputStream stream = connection.getOutputStream();
        stream.write(body.getBytes());
        stream.flush();
        stream.close();
    }

    private static HttpURLConnection createConnection() throws Exception {
        URL url = new URL(PROFILE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private static UUID getUUID(String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    public static byte[] toBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static UUID fromBytes(byte[] array) {
        if (array.length != 16) {
            throw new IllegalArgumentException("Illegal byte array length: " + array.length);
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        long mostSignificant = byteBuffer.getLong();
        long leastSignificant = byteBuffer.getLong();
        return new UUID(mostSignificant, leastSignificant);
    }

    public static UUID getUUIDOf(String name) throws Exception {
        return new UUIDFetcher(Arrays.asList(name)).call().get(getNameOf(name));
    }

    public static String getNameOf(String name) throws Exception {
        Map<String, UUID> results = new UUIDFetcher(Arrays.asList(name)).call();
        String result = null;
        for (Map.Entry<String, UUID> entry : results.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name))
                result = entry.getKey();
        }
        return result;
    }
}

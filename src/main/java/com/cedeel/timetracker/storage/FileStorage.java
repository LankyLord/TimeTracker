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
package com.cedeel.timetracker.storage;

import com.cedeel.timetracker.TrackedPlayer;
import com.cedeel.timetracker.Util.UUIDFetcher;
import com.cedeel.timetracker.Util.Util;
import com.google.common.io.Files;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * An implementation of storage to YAML file.
 */
public class FileStorage implements Storage {

    private File dataFolder;
    private YamlConfiguration Data = null;
    private File DataFile = null;
    private static final String DATAFILENAME = "playertimes.yml";

    // Configuration section names
    private static final String FIRST_JOIN = "first";
    private static final String LAST_SEEN = "last";
    private static final String PLAYTIME = "playtime";

    /**
     * Constructor for the file storage
     * @param path The path where the data file will be stored
     */
    public FileStorage(File path) {
        dataFolder = path;
        if (new File(path, "Data.yml").exists() && !(new File(path, DATAFILENAME).exists()))
            new Converter(path).convert();
    }

    /**
     * Get the data from file
     *
     * @return The player data
     */
    private YamlConfiguration getData() {
        if (Data == null)
            this.reloadData();
        return Data;
    }

    @Override
    public boolean saveData() {
        if (Data == null || DataFile == null)
            return false;
        try {
            getData().save(DataFile);
            return true;
        } catch (IOException ex) {
            //instance.getLogger().log(Level.SEVERE, "Could not save config to " + DataFile, ex);

            return false;
        }
    }

    /** Reload the data file */
    private void reloadData() {
        if (DataFile == null)
            DataFile = new File(dataFolder, DATAFILENAME);
        Data = YamlConfiguration.loadConfiguration(DataFile);
    }

    @Override
    public TrackedPlayer getPlayer(UUID id) {
        TrackedPlayer result;
        if (this.getData().isConfigurationSection(id.toString())) {
            ConfigurationSection section = this.getData().getConfigurationSection(id.toString());
            result = new TrackedPlayer(id, section.getLong(FIRST_JOIN), section.getLong(LAST_SEEN), section.getLong(PLAYTIME));
        } else {
            result = new TrackedPlayer(id, Util.UNINITIALISED_TIME, Util.UNINITIALISED_TIME, Util.UNINITIALISED_TIME);
        }
        return result;
    }

    @Override
    public boolean pushPlayer(TrackedPlayer player) {
        ConfigurationSection section = getData().createSection(player.getPlayerID().toString());
        section.set(FIRST_JOIN, player.getFirstJoined());
        section.set(LAST_SEEN, player.getLastSeen());
        section.set(PLAYTIME, player.getPlaytime());
        return saveData();
    }
}

class Converter {
    File file, nfile;

    Converter(File path) {
        file = new File(path, "Data.yml");
        nfile = new File(path, "playertimes.yml");
    }

    YamlConfiguration convert() {
        System.out.println("Starting conversion. This might take a while!");
        YamlConfiguration result = new YamlConfiguration();
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            Map<String, UUID> users = new HashMap<String, UUID>();
            try {
                users = new UUIDFetcher(new ArrayList<String>(config.getKeys(false))).call();
            } catch (Exception ignored) {}
            Map<String, UUID> lusers = new HashMap<String, UUID>(users.size());
            for ( Map.Entry<String, UUID> e : users.entrySet())
                lusers.put(e.getKey().toLowerCase(), e.getValue());

            for ( String key : config.getKeys(false)) {
                try {
                    ConfigurationSection se = result.createSection(lusers.get(key).toString());
                    se.set("first", config.get(key + ".first"));
                    se.set("last", config.get(key + ".last"));
                    se.set("playtime", config.get(key + ".playtime"));
                } catch (Exception e) {
                    System.err.println("User not found: " + key);;
                }
            }
            try {
                result.save(nfile);
                Files.move(file, new File(file.toString() + ".bak"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}

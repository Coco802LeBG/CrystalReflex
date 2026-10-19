package com.deathmotion.marlowcrystal.config;

import com.deathmotion.marlowcrystal.Log;
import com.deathmotion.marlowcrystal.config.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public final class SettingsFile {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path path;

    public SettingsFile(Path path) {
        this.path = path;
    }

    public Settings load() {
        Settings settings;
        block9: {
            if (Files.notExists(this.path, new LinkOption[0])) {
                return new Settings();
            }
            BufferedReader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8);
            try {
                Settings settings2 = (Settings)GSON.fromJson((Reader)reader, Settings.class);
                Settings settings3 = settings = settings2 != null ? settings2 : new Settings();
                if (reader == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            ((Reader)reader).close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (JsonParseException | IOException e) {
                    Log.warn("Could not read " + String.valueOf(this.path.getFileName()) + ", using defaults: " + e.getMessage());
                    return new Settings();
                }
            }
            ((Reader)reader).close();
        }
        return settings;
    }

    public synchronized void save(Settings settings) {
        try {
            Files.createDirectories(this.path.getParent(), new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8, new OpenOption[0]);){
                GSON.toJson((Object)settings, (Appendable)writer);
            }
        }
        catch (JsonParseException | IOException e) {
            Log.error("Could not save " + String.valueOf(this.path.getFileName()) + ": " + e.getMessage());
        }
    }
}


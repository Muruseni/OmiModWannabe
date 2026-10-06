package com.example.blacklistedlobbywatcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BlacklistManager {
    private final Minecraft mc;
    private final File file;
    private final List<String> blacklist = new ArrayList<String>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public BlacklistManager(Minecraft mc) {
        this.mc = mc;
        this.file = new File(mc.mcDataDir, "config/blacklisted_lobby_watcher.json");
    }

    public void load() {
        blacklist.clear();

        if (!file.exists()) {
            save();
            return;
        }

        try {
            Type type = new TypeToken<List<String>>() {}.getType();
            FileReader reader = new FileReader(file);
            List<String> loaded = gson.fromJson(reader, type);
            reader.close();

            if (loaded != null) {
                for (String name : loaded) {
                    add(name, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }

            FileWriter writer = new FileWriter(file);
            gson.toJson(blacklist, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean contains(String name) {
        if (name == null) {
            return false;
        }

        String normalized = name.trim().toLowerCase(Locale.ROOT);
        for (String entry : blacklist) {
            if (entry.toLowerCase(Locale.ROOT).equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    public void add(String name) {
        add(name, true);
    }

    private void add(String name, boolean save) {
        if (name == null) {
            return;
        }

        String cleaned = name.trim();
        if (cleaned.isEmpty() || contains(cleaned)) {
            return;
        }

        blacklist.add(cleaned);

        if (save) {
            save();
        }
    }

    public void remove(String name) {
        if (name == null) {
            return;
        }

        String normalized = name.trim().toLowerCase(Locale.ROOT);

        for (int i = blacklist.size() - 1; i >= 0; i--) {
            if (blacklist.get(i).toLowerCase(Locale.ROOT).equals(normalized)) {
                blacklist.remove(i);
            }
        }

        save();
    }

    public List<String> getEntries() {
        return Collections.unmodifiableList(new ArrayList<String>(blacklist));
    }
}

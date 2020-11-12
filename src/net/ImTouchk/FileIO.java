package net.ImTouchk;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FileIO {
    public static File dataFolder;

    public FileConfiguration config;
    public String path;
    public File file;

    public FileIO(String path) {
        open(path);
    }

    public void open(String path) {
        this.path = path;
        file = new File(dataFolder, this.path);
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void close() {
        try { config.save(file); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void write(String var, Object value) { config.set(var, value); }

    public Map<String, Object> getArray(String section) { return config.getConfigurationSection(section).getValues(false); }
    public Object getObject(String path, Class<Object> classz) { return config.getObject(path, classz); }
    public String getString(String path) { return config.getString(path); }
    public Boolean getBool(String path) { return config.getBoolean(path); }
    public Integer getInt(String path) { return config.getInt(path); }

    public Boolean exists(String var) { return config.contains(var); }
}

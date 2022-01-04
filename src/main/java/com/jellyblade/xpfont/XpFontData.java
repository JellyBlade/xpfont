package com.jellyblade.xpfont;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class XpFontData implements Serializable {

    private static transient final long serialVersionUID = -9163702578461779941L;
    public HashMap<String, Long> storedXp;
    public ArrayList<Location> fontLocations;

    public static enum Storagetype {
        SERIALIZED,
        YAML,
        SQL
    }

    public XpFontData(HashMap<String, Long> fValues, ArrayList<Location> fLocations) {
        this.storedXp = fValues;
        this.fontLocations = fLocations;
    }

    public XpFontData(XpFontData sxfv) {
        this.storedXp = sxfv.storedXp;
        this.fontLocations = sxfv.fontLocations;
    }

    public XpFontData() {
        this.storedXp = new HashMap<String, Long>();
        this.fontLocations = new ArrayList<Location>();
    }

    public void changeXPDataForPlayer(Player player, Long xpChange) {
        String id = player.getUniqueId().toString();
        if (storedXp.containsKey(id)) {
            storedXp.put(id, storedXp.get(id) + xpChange);
        }
    }

    public long getXPDataForPlayer(Player player) {
        String id = player.getUniqueId().toString();
        if (storedXp.containsKey(id)) {
            return storedXp.get(id);
        }
        else return 0;
    }

    public void initXPDataForPlayer(Player player) {
        String id = player.getUniqueId().toString();
        storedXp.put(id, 0l);
    }

    public void setXPDataForPlayer(Player player, long xpChange) {
        String id = player.getUniqueId().toString();
        storedXp.put(id, xpChange);
    }

    public boolean isPlayerInXPData(Player player) {
        String id = player.getUniqueId().toString();
        if (storedXp.containsKey(id)) {
            return true;
        }
        return false;
    }

    public void createFont(Location location) {
        if (!fontLocations.contains(location)) {
            fontLocations.add(location);
            
        }
    }

    public void destroyFont(Location location) {
        if (fontLocations.contains(location)) {
            fontLocations.remove(location);
        }
    }

    public boolean checkLocation(Location location) {
        if (fontLocations.contains(location)) {
            return true;
        }
        return false;
    }

    Location getLocationFromString(String sLoc) {
        if (sLoc == null || sLoc.trim() == "") {
            return null;
        }
        String[] sLocParts = sLoc.split(":");
        if (sLocParts.length == 4) {
            World w = Bukkit.getServer().getWorld(sLocParts[0]);
            Double x = Double.parseDouble(sLocParts[1]);
            Double y = Double.parseDouble(sLocParts[2]);
            Double z = Double.parseDouble(sLocParts[3]);
            return new Location(w, x, y, z);
        }
        return null;
    }

    String getStringFromLocation(Location loc) {
        if (loc == null) {
            return "";
        }
        return ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    public boolean saveDataYAML(File playerFonts, File fontBlocks) {
        try { 
            if (!playerFonts.exists()) {
                File parent = new File(playerFonts.getParent());
                parent.mkdirs();
                playerFonts.createNewFile();
            }
            if (!fontBlocks.exists()) {
                File parent = new File(fontBlocks.getParent());
                parent.mkdirs();
                fontBlocks.createNewFile();
            }
            FileConfiguration fBlocks = YamlConfiguration.loadConfiguration(fontBlocks);
            FileConfiguration pFonts = YamlConfiguration.loadConfiguration(playerFonts);
            fBlocks.createSection("locs");
            ConfigurationSection fBlockLocs = fBlocks.getConfigurationSection("locs");
            for (int i = 0; i < fontLocations.size(); i++) {
                fBlockLocs.set(fontLocations.get(i).getWorld().getName() + "._" + i, getStringFromLocation(fontLocations.get(i)));
            }
            fBlocks.save(fontBlocks);
            for (Map.Entry<String, Long> playerXp : storedXp.entrySet()) {
                pFonts.set(playerXp.getKey(), playerXp.getValue());
            } 
            pFonts.save(playerFonts);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean loadPlayerFonts(File playerFonts) {
        FileConfiguration pFonts = YamlConfiguration.loadConfiguration(playerFonts);
        for (String uuid : pFonts.getKeys(false)) {
            storedXp.put(uuid, pFonts.getLong(uuid));
        }
        return true;
    }
    public boolean loadFontBlocks(File fontBlocks) {
        FileConfiguration fBlocks = YamlConfiguration.loadConfiguration(fontBlocks);
        ConfigurationSection fBlockWorlds = fBlocks.getConfigurationSection("locs");

        for (String world : fBlockWorlds.getKeys(false)) {
            ConfigurationSection fWorldLocs = fBlockWorlds.getConfigurationSection(world);
            for (String loc : fWorldLocs.getKeys(false)) {
                fontLocations.add(getLocationFromString(world + fWorldLocs.getString(loc)));
            }
        }

        return true;
    }
    
    public boolean saveDataSerialized(File file) {
        try {
            if (!file.exists()) {
                File parent = new File(file.getParent());
                parent.mkdirs();
                file.createNewFile();
            }
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new FileOutputStream(file));
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static XpFontData loadDataSerialized(File file) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new FileInputStream(file));
            XpFontData sxfv = (XpFontData) in.readObject();
            in.close();
            return sxfv;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

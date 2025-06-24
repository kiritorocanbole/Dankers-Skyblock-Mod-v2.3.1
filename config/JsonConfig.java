package me.Danker.config;

import com.google.gson.*;
import me.Danker.DankersSkyblockMod;
import me.Danker.features.*;
import me.Danker.features.loot.TrophyFishTracker;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class JsonConfig {

    public static void createNewJsonObject(String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(new JsonObject().toString());
        fileWriter.close();
    }

    public static void createNewJsonArray(String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(new JsonArray().toString());
        fileWriter.close();
    }

    public static JsonObject initJsonObject(String file) throws IOException {
        if (!(new File(file).exists())) createNewJsonObject(file);
        try {
            return new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        } catch (IllegalStateException | JsonSyntaxException corrupted) {
            corrupted.printStackTrace();
            System.out.println("Recreating " + file);
            createNewJsonObject(file);
            return new JsonParser().parse(new FileReader(file)).getAsJsonObject();
        }
    }

    public static <T> Object[] initJsonArray(String file, Class<T> clazz) throws IOException {
        if (!(new File(file).exists())) createNewJsonArray(file);
        Object[] arr = null;
        try {
            arr = new Gson().fromJson(new FileReader(file), (Type) clazz);
        } catch (JsonSyntaxException corrupted) {
            corrupted.printStackTrace();
            System.out.println("Recreating " + file);
            createNewJsonArray(file);
        }
        return arr;
    }

    public static void reloadConfig() {
        try {
            File directory = new File(DankersSkyblockMod.configDirectory + "/dsm");
            if (!directory.exists()) directory.mkdir();

            // migrate
            if (new File(DankersSkyblockMod.configDirectory + "/dsmalerts.json").exists()) {
                transferFile("/dsmalerts.json");
                transferFile("/dsmaliases.json");
                transferFile("/dsmminions.json");
                transferFile("/dsmtrophyfish.json");
                transferFile("/dsmmeter.json");
            }

            // Alerts
            Object[] alerts = initJsonArray(Alerts.configFile, Alerts.Alert[].class);
            if (alerts != null) Alerts.alerts = new ArrayList<>(Arrays.asList((Alerts.Alert[]) alerts));

            // Aliases
            Object[] aliases = initJsonArray(ChatAliases.configFile, ChatAliases.Alias[].class);
            if (aliases != null) ChatAliases.aliases = new ArrayList<>(Arrays.asList((ChatAliases.Alias[]) aliases));

            // Minions
            Object[] minions = initJsonArray(MinionLastCollected.configFile, MinionLastCollected.Minion[].class);
            if (minions != null) MinionLastCollected.minions = new ArrayList<>(Arrays.asList((MinionLastCollected.Minion[]) minions));

            // Trophy Fish
            TrophyFishTracker.fish = initJsonObject(TrophyFishTracker.configFile);
            MeterTracker.meter = initJsonObject(MeterTracker.configFile);
            DungeonTimer.timer = initJsonObject(DungeonTimer.configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void transferFile(String file) throws IOException {
        File path = new File(DankersSkyblockMod.configDirectory + file);
        if (path.exists()) {
            File dest = new File(DankersSkyblockMod.configDirectory + "/dsm");
            FileUtils.moveFileToDirectory(path, dest, true);
        }
    }

}

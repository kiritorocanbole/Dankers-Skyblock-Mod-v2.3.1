package me.Danker.utils;

import com.google.gson.*;
import me.Danker.DankersSkyblockMod;
import me.Danker.config.CfgConfig;
import me.Danker.config.ModConfig;
import me.Danker.features.ColouredNames;
import me.Danker.features.GoldenEnchants;
import me.Danker.handlers.APIHandler;
import me.Danker.handlers.ScoreboardHandler;
import me.Danker.locations.DungeonFloor;
import me.Danker.locations.Location;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.*;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    
    public static boolean inSkyblock = false;
    public static Location currentLocation = Location.NONE;
    public static DungeonFloor currentFloor = DungeonFloor.NONE;
    public static int[] skillXPPerLevel = {0, 50, 125, 200, 300, 500, 750, 1000, 1500, 2000, 3500, 5000, 7500, 10000, 15000, 20000, 30000, 50000,
                                           75000, 100000, 200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000, 1100000,
                                           1200000, 1300000, 1400000, 1500000, 1600000, 1700000, 1800000, 1900000, 2000000, 2100000, 2200000,
                                           2300000, 2400000, 2500000, 2600000, 2750000, 2900000, 3100000, 3400000, 3700000, 4000000, 4300000,
                                           4600000, 4900000, 5200000, 5500000, 5800000, 6100000, 6400000, 6700000, 7000000};
    static int[] dungeonsXPPerLevel = {0, 50, 75, 110, 160, 230, 330, 470, 670, 950, 1340, 1890, 2665, 3760, 5260, 7380, 10300, 14400,
                                      20000, 27600, 38000, 52500, 71500, 97000, 132000, 180000, 243000, 328000, 445000, 600000, 800000,
                                      1065000, 1410000, 1900000, 2500000, 3300000, 4300000, 5600000, 7200000, 9200000, 12000000, 15000000,
                                      19000000, 24000000, 30000000, 38000000, 48000000, 60000000, 75000000, 93000000, 116250000};
    static int[] expertiseKills = {50, 100, 250, 500, 1000, 2500, 5500, 10000, 15000};
    static Pattern boldPattern = Pattern.compile("(?i)\\u00A7L");
    static Map<Character, Integer> romanNumerals = new HashMap<Character, Integer>(){{
        put('I', 1);
        put('V', 5);
        put('X', 10);
        put('L', 50);
        put('C', 100);
        put('D', 500);
        put('M', 1000);
    }};
    
    public static int getItems(String item) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        AxisAlignedBB scan = new AxisAlignedBB(x - 6, y - 6, z - 6, x + 6, y + 6, z + 6);
        List<EntityItem> items = mc.theWorld.getEntitiesWithinAABB(EntityItem.class, scan);
        
        for (EntityItem i : items) {
            String itemName = StringUtils.stripControlCodes(i.getEntityItem().getDisplayName());
            if (itemName.equals(item)) return i.getEntityItem().stackSize;
        }
        // No items found
        return 0;
    }
    
    public static String returnGoldenEnchants(String line) {
        Matcher matcher = GoldenEnchants.t6EnchantPattern.matcher(line);
        StringBuffer out = new StringBuffer();
        
        while (matcher.find()) {
            matcher.appendReplacement(out, GoldenEnchants.t6Enchants.get(matcher.group(1)));
        }
        matcher.appendTail(out);
        
        return out.toString();
    }
    
    public static List<String> getMatchingPlayers(String arg) {
        List<String> matchingPlayers = new ArrayList<>();
        Collection<NetworkPlayerInfo> players = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
        
        for (NetworkPlayerInfo player : players) {
            String playerName = player.getGameProfile().getName();
            if (playerName.startsWith("!")) continue; // New tablist
            if (playerName.toLowerCase().startsWith(arg.toLowerCase())) {
                matchingPlayers.add(playerName);
            }
        }
        
        return matchingPlayers;
    }
    
    public static void createTitle(String text, int seconds) {
        Minecraft.getMinecraft().thePlayer.playSound(ModConfig.alertNoise, 1, (float) 0.5);
        DankersSkyblockMod.titleTimer = seconds * 20;
        DankersSkyblockMod.showTitle = true;
        DankersSkyblockMod.titleText = text;
    }

    public static boolean isOnHypixel() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.theWorld != null && !mc.isSingleplayer()) {
            if (mc.thePlayer != null && mc.thePlayer.getClientBrand() != null && mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel")) {
                return true;
            }
            if (mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
                return true;
            }
        }
        return false;
    }

    public static void checkForSkyblock() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.theWorld != null && !mc.isSingleplayer()) {
            ScoreObjective scoreboardObj = mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
            if (scoreboardObj != null) {
                String scObjName = ScoreboardHandler.cleanSB(scoreboardObj.getDisplayName());
                if (scObjName.contains("SKYBLOCK")) {
                    inSkyblock = true;
                    return;
                }
            }
        }
        inSkyblock = false;
    }

    public static void checkTabLocation() {
        if (inSkyblock) {
            Collection<NetworkPlayerInfo> players = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
            for (NetworkPlayerInfo player : players) {
                if (player == null || player.getDisplayName() == null) continue;
                String text = player.getDisplayName().getUnformattedText();
                if (text.startsWith("Area: ") || text.startsWith("Dungeon: ")) {
                    currentLocation = Location.fromTab(text.substring(text.indexOf(":") + 2));
                    return;
                }
            }
        }
        currentLocation = Location.NONE;
    }

    public static boolean isInDungeons() { // update this whenever new dungeons come out
        return currentLocation == Location.CATACOMBS;
    }

    public static void checkForDungeonFloor() {
        if (isInDungeons()) {
            List<String> scoreboard = ScoreboardHandler.getSidebarLines();

            for (String s : scoreboard) {
                String sCleaned = ScoreboardHandler.cleanSB(s);

                if (sCleaned.contains("The Catacombs (")) {
                    String floor = sCleaned.substring(sCleaned.indexOf("(") + 1, sCleaned.indexOf(")"));

                    try {
                        currentFloor = DungeonFloor.valueOf(floor);
                    } catch (IllegalArgumentException ex) {
                        currentFloor = DungeonFloor.NONE;
                        ex.printStackTrace();
                    }

                    break;
                }
            }
        } else {
            currentFloor = DungeonFloor.NONE;
        }
    }

    public static boolean isInScoreboard(String text) {
        List<String> scoreboard = ScoreboardHandler.getSidebarLines();
        for (String s : scoreboard) {
            String sCleaned = ScoreboardHandler.cleanSB(s);
            if (sCleaned.contains(text)) return true;
        }
        return false;
    }
    
    public static String capitalizeString(String string) {
        if (string == null) return null;
        String[] words = string.split("_");
        
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() < 2) words[i] = words[i].toUpperCase();
            else words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }
        
        return String.join(" ", words);
    }
    
    public static double getPercentage(int num1, int num2) {
        if (num2 == 0) return 0D;
        double result = ((double) num1 * 100D) / (double) num2;
        result = Math.round(result * 100D) / 100D;
        return result;
    }
    
    public static String getTimeBetween(double timeOne, double timeTwo) {
        double secondsBetween = Math.floor(timeTwo - timeOne);
        
        String timeFormatted;
        int days;
        int hours;
        int minutes;
        int seconds;
        
        if (secondsBetween > 86400) {
            // More than 1d, display #d#h
            days = (int) (secondsBetween / 86400);
            hours = (int) (secondsBetween % 86400 / 3600);
            timeFormatted = days + "d" + hours + "h";
        } else if (secondsBetween > 3600) {
            // More than 1h, display #h#m
            hours = (int) (secondsBetween / 3600);
            minutes = (int) (secondsBetween % 3600 / 60);
            timeFormatted = hours + "h" + minutes + "m";
        } else {
            // Display #m#s
            minutes = (int) (secondsBetween / 60);
            seconds = (int) (secondsBetween % 60);
            timeFormatted = minutes + "m" + seconds + "s";
        }
        
        return timeFormatted;
    }
    
    public static String getMoneySpent(double coins) {
        double coinsSpentMillions = coins / 1000000D;
        coinsSpentMillions = Math.floor(coinsSpentMillions * 100D) / 100D;
        return coinsSpentMillions + "M";
    }
    
    public static double xpToSkillLevel(double xp, int limit) {
        for (int i = 0, xpAdded = 0; i < limit + 1; i++) {
            xpAdded += skillXPPerLevel[i];
            if (xp < xpAdded) {
                return (i - 1) + (xp - (xpAdded - skillXPPerLevel[i])) / skillXPPerLevel[i];
            }
        }
        return limit;
    }

    public static int skillLevelToXp(int level) {
        int sum = 0;

        for (int i = 1; i <= level; i++) {
            sum += skillXPPerLevel[i];
        }

        return sum;
    }
    
    public static double xpToDungeonsLevel(double xp) {
        for (int i = 0, xpAdded = 0; i < dungeonsXPPerLevel.length; i++) {
            xpAdded += dungeonsXPPerLevel[i];
            if (xp < xpAdded) {
                double level =  (i - 1) + (xp - (xpAdded - dungeonsXPPerLevel[i])) / dungeonsXPPerLevel[i];
                return (double) Math.round(level * 100D) / 100;
            }
        }
        return 50D + MathHelper.clamp_double(Math.round((xp - 569809640D) / 200000000D * 100D) / 100D, 0D, 49D);
    }
    
    public static int expertiseKillsLeft(int kills) {
        for (int i = 0; i < expertiseKills.length; i++) {
            if (kills < expertiseKills[i]) {
                return expertiseKills[i] - kills;
            }
        }
        return -1;
    }

    // Only used when over limit
    public static int getPastXpEarned(int currentLevelXp, int limit) {
        if (currentLevelXp == 0) {
            int xpAdded = 0;
            for (int i = 1; i <= limit; i++) {
                xpAdded += skillXPPerLevel[i];
            }
            return xpAdded;
        }
        for (int i = 1, xpAdded = 0; i <= limit; i++) {
            xpAdded += skillXPPerLevel[i - 1];
            if (currentLevelXp == skillXPPerLevel[i]) return xpAdded;
        }
        return 0;
    }

    public static double getTotalXpEarned(int currentLevel, double percentage) {
        double progress = 0;
        if (currentLevel < 60) progress = skillXPPerLevel[currentLevel + 1] * (percentage / 100D);
        double xpAdded = 0;
        for (int i = 1; i <= currentLevel; i++) {
            xpAdded += skillXPPerLevel[i];
        }
        return xpAdded + progress;
    }
    
    public static String getColouredBoolean(boolean bool) {
        return bool ? EnumChatFormatting.GREEN + "On" : EnumChatFormatting.RED + "Off";
    }

    // Taken from SkyblockAddons
    public static List<String> getItemLore(ItemStack itemStack) {
        final int NBT_INTEGER = 3;
        final int NBT_STRING = 8;
        final int NBT_LIST = 9;
        final int NBT_COMPOUND = 10;

        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("display", NBT_COMPOUND)) {
            NBTTagCompound display = itemStack.getTagCompound().getCompoundTag("display");

            if (display.hasKey("Lore", NBT_LIST)) {
                NBTTagList lore = display.getTagList("Lore", NBT_STRING);

                List<String> loreAsList = new ArrayList<>();
                for (int lineNumber = 0; lineNumber < lore.tagCount(); lineNumber++) {
                    loreAsList.add(lore.getStringTagAt(lineNumber));
                }

                return Collections.unmodifiableList(loreAsList);
            }
        }

        return Collections.emptyList();
    }

    public static boolean hasRightClickAbility(ItemStack itemStack) {
        return Utils.getItemLore(itemStack).stream().anyMatch(line -> {
            String stripped = StringUtils.stripControlCodes(line);
            return stripped.startsWith("Item Ability:") && stripped.endsWith("RIGHT CLICK");
        });
    }
    
    public static BlockPos getFirstBlockPosAfterVectors(Minecraft mc, Vec3 pos1, Vec3 pos2, int strength, int distance) {
        double x = pos2.xCoord - pos1.xCoord;
        double y = pos2.yCoord - pos1.yCoord;
        double z = pos2.zCoord - pos1.zCoord;
        
        for (int i = strength; i < distance * strength; i++) { // Start at least 1 strength away
            double newX = pos1.xCoord + ((x / strength) * i);
            double newY = pos1.yCoord + ((y / strength) * i);
            double newZ = pos1.zCoord + ((z / strength) * i);
            
            BlockPos newBlock = new BlockPos(newX, newY, newZ);
            if (mc.theWorld.getBlockState(newBlock).getBlock() != Blocks.air) {
                return newBlock;
            }
        }
        
        return null;
    }
    
    public static BlockPos getNearbyBlock(Minecraft mc, BlockPos pos, Block... blockTypes) {
        if (pos == null) return null;
        BlockPos pos1 = new BlockPos(pos.getX() - 2, pos.getY() - 3, pos.getZ() - 2);
        BlockPos pos2 = new BlockPos(pos.getX() + 2, pos.getY() + 3, pos.getZ() + 2);
        
        BlockPos closestBlock = null;
        double closestBlockDistance = 99;
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(pos1, pos2);
        
        for (BlockPos block : blocks) {
            for (Block blockType : blockTypes) {
                if (mc.theWorld.getBlockState(block).getBlock() == blockType && block.distanceSq(pos) < closestBlockDistance) {
                    closestBlock = block;
                    closestBlockDistance = block.distanceSq(pos);
                }
            }
        }
        
        return closestBlock;
    }
    
    public static BlockPos getBlockUnderItemFrame(EntityItemFrame itemFrame) {
        switch (itemFrame.facingDirection) {
            case NORTH:
                return new BlockPos(itemFrame.posX, itemFrame.posY, itemFrame.posZ + 1);
            case EAST:
                return new BlockPos(itemFrame.posX - 1, itemFrame.posY, itemFrame.posZ - 0.5);
            case SOUTH:
                return new BlockPos(itemFrame.posX, itemFrame.posY, itemFrame.posZ - 1);
            case WEST:
                return new BlockPos(itemFrame.posX + 1, itemFrame.posY, itemFrame.posZ - 0.5);
            default:
                return null;
        }
    }

    public static boolean isRealPlayer(EntityPlayer player) {
        return player.getUniqueID().version() == 4 && !player.isPlayerSleeping();
    }

    public static String removeBold(String text) {
        return boldPattern.matcher(text).replaceAll("");
    }

    public static int getIntFromString(String text, boolean romanNumeral) {
        if (text.matches(".*\\d.*")) {
            return Integer.parseInt(StringUtils.stripControlCodes(text).replaceAll("\\D", ""));
        } else if (romanNumeral) {
            int number = 0;

            for (int i = 0; i < text.length(); i++) {
                if (!romanNumerals.containsKey(text.charAt(i))) continue;
                int roman = romanNumerals.get(text.charAt(i));

                if (i != text.length() - 1 && romanNumerals.containsKey(text.charAt(i + 1)) && roman < romanNumerals.get(text.charAt(i + 1))) {
                    number += romanNumerals.get(text.charAt(i + 1)) - roman;
                    i++;
                } else {
                    number += roman;
                }
            }

            return number;
        }

        return -1;
    }

    public static boolean skillsInitialized() {
        return DankersSkyblockMod.miningLevel != -1;
    }

    public static int initializeSkill(ItemStack skillStack, String configValue) {
        int level = -1;

        if (skillStack != null) {
            String display = skillStack.getDisplayName();
            if (display.startsWith("§a")) {
                if (display.contains(" ")) {
                    level = Utils.getIntFromString(display.substring(display.indexOf(" ") + 1), true);
                } else {
                    level = 0;
                }
            }
        }

        CfgConfig.writeIntConfig("skills", configValue, level);
        return level;
    }

    public static void refreshRepo() {
        DankersSkyblockMod.data = APIHandler.getResponse("https://raw.githubusercontent.com/bowser0000/SkyblockMod-REPO/main/data.json", false);
        System.out.println("Loaded data from GitHub?: " + (DankersSkyblockMod.data != null && DankersSkyblockMod.data.has("trivia")));
        ColouredNames.users = DankersSkyblockMod.data.getAsJsonObject("colourednames").entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
        System.out.println("Refreshed DSM repo at " + System.currentTimeMillis());
    }

    public static int getCooldownFromAbility(String ability) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        boolean foundAbility = false;

        List<ItemStack> itemsToSearch = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            ItemStack hotbarItem = player.inventory.getStackInSlot(i);
            if (hotbarItem == null) continue;
            itemsToSearch.add(hotbarItem);
        }

        for (int i = 0; i < 4; i++) {
            ItemStack armorItem = player.inventory.armorItemInSlot(0);
            if (armorItem == null) continue;
            itemsToSearch.add(armorItem);
        }

        for (ItemStack item : itemsToSearch) {
            if (foundAbility) break;
            List<String> tooltip = item.getTooltip(player, false);

            for (String line : tooltip) {
                if (line.contains(EnumChatFormatting.GOLD + "Ability: ") || line.contains(EnumChatFormatting.GOLD + "Full Set Bonus: ")) {
                    if (line.contains(EnumChatFormatting.GOLD + "Ability: " + ability)) {
                        foundAbility = true;
                        continue;
                    } else if (foundAbility) {
                        break;
                    }
                }

                if (foundAbility && line.contains(EnumChatFormatting.DARK_GRAY + "Cooldown: ")) {
                    return Integer.parseInt(StringUtils.stripControlCodes(line).replaceAll("\\D", ""));
                }
            }
        }

        return 0;
    }

    public static double getCooldownReductionFromLevel(int level) {
        return (Math.floor(level / 2D) + 25) / 100D;
    }

    public static void desktopNotification(String name, String title, String text, TrayIcon.MessageType messageType) {
        try {
            final SystemTray tray = SystemTray.getSystemTray();
            final Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            final TrayIcon trayIcon = new TrayIcon(image, name);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip(name);
            tray.add(trayIcon);
            trayIcon.displayMessage(title, text, messageType);
            tray.remove(trayIcon);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static JsonObject deepCopy(JsonObject obj) {
        JsonObject newObj = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            newObj.add(entry.getKey(), entry.getValue());
        }

        return newObj;
    }

    public static JsonArray deepCopy(JsonArray array) {
        JsonArray newArray = new JsonArray();

        for (JsonElement element : array) {
            if (element.isJsonObject()) {
                newArray.add(deepCopy(element.getAsJsonObject()));
            } else if (element.isJsonArray()) {
                newArray.add(deepCopy(element.getAsJsonArray()));
            } else  {
                newArray.add(element);
            }
        }

        return newArray;
    }

    // https://github.com/BiscuitDevelopment/SkyblockAddons/blob/main/src/main/java/codes/biscuit/skyblockaddons/utils/ItemUtils.java#L139-L148
    public static NBTTagCompound getExtraAttributes(ItemStack item) {
        if (item == null || !item.hasTagCompound()) return null;
        return item.getSubCompound("ExtraAttributes", false);
    }

    // https://github.com/BiscuitDevelopment/SkyblockAddons/blob/main/src/main/java/codes/biscuit/skyblockaddons/utils/ItemUtils.java#L116-L131
    public static String getSkyblockItemID(ItemStack item) {
        if (item == null) return null;

        NBTTagCompound extraAttributes = getExtraAttributes(item);
        if (extraAttributes == null || !extraAttributes.hasKey("id", 8)) return null;

        return extraAttributes.getString("id");
    }

    public static JsonObject getTrophyFromAPI(JsonObject obj, String name) {
        JsonObject tiers = new JsonObject();

        tiers.addProperty("BRONZE", obj.has(name + "_bronze") ? obj.get(name + "_bronze").getAsInt() : 0);
        tiers.addProperty("SILVER", obj.has(name + "_silver") ? obj.get(name + "_silver").getAsInt() : 0);
        tiers.addProperty("GOLD", obj.has(name + "_gold") ? obj.get(name + "_gold").getAsInt() : 0);
        tiers.addProperty("DIAMOND", obj.has(name + "_diamond") ? obj.get(name + "_diamond").getAsInt() : 0);

        return tiers;
    }

    public static boolean isJson(String obj) {
        try {
            new JsonParser().parse(obj);
        } catch (JsonSyntaxException ex) {
            return false;
        }
        return true;
    }

    public static int parseInt(String integer){
        String cleanInt = integer.replaceAll("\\D", "");
        return Integer.parseInt(cleanInt);
    }

    public static JsonObject getObjectFromPath(JsonObject obj, String path) {
        if (obj == null) return null;

        String[] split = path.split("\\.");
        JsonObject newObj = obj;

        for (String id : split) {
            if (!newObj.has(id)) {
                System.out.println(id + " does not exist in path " + path);
                return null;
            }

            newObj = newObj.getAsJsonObject(id);
        }

        return newObj;
    }

    public static int getSkillMaxLevel(String skill) {
        if (DankersSkyblockMod.data == null) return 50;
        return DankersSkyblockMod.data.getAsJsonObject("skills").get(skill).getAsInt();
    }

}

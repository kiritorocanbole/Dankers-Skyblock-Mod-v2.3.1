package me.Danker.commands.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.Danker.config.ModConfig;
import me.Danker.handlers.APIHandler;
import me.Danker.handlers.HypixelAPIHandler;
import me.Danker.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class GuildOfCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "guildof";
    }

    @Override
    public String getCommandUsage(ICommandSender arg0) {
        return "/" + getCommandName() + " [name]";
    }

    public static String usage(ICommandSender arg0) {
        return new GuildOfCommand().getCommandUsage(arg0);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return Utils.getMatchingPlayers(args[0]);
        }
        return null;
    }
    
    @Override
    public void processCommand(ICommandSender arg0, String[] arg1) throws CommandException {
        // MULTI THREAD DRIFTING
        new Thread(() -> {
            EntityPlayer player = (EntityPlayer) arg0;
            
            // Get UUID for Hypixel API requests
            String username;
            String uuid;
            if (arg1.length == 0) {
                username = player.getName();
                uuid = player.getUniqueID().toString().replaceAll("[\\-]", "");
            } else {
                username = arg1[0];
                uuid = APIHandler.getUUID(username);
            }
            player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.mainColour) + "Checking guild of " + ModConfig.getColour(ModConfig.secondaryColour) + username + ModConfig.getColour(ModConfig.mainColour) + " using Polyfrost's API."));
            
            // Find guild ID
            System.out.println("Fetching guild...");
            JsonObject guildResponse = HypixelAPIHandler.getJsonObjectAuth(HypixelAPIHandler.URL + "guild/" + uuid);

            if (guildResponse == null) {
                player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.errorColour) + "Could not connect to API."));
                return;
            }
            if (!guildResponse.get("success").getAsBoolean()) {
                String reason = guildResponse.get("cause").getAsString();
                player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.errorColour) + "Failed with reason: " + reason));
                return;
            }
            
            System.out.println("Fetching guild stats and members...");
            
            String guildName = "N/A";
            String guildMaster = "N/A";
            int players = 0;
            // Check if player is in guild
            if (!guildResponse.get("guild").isJsonNull()) {
                guildName = guildResponse.getAsJsonObject("guild").get("name").getAsString();
                
                // Loop through members to find guildmaster
                JsonArray guildMembers = guildResponse.getAsJsonObject("guild").getAsJsonArray("members");
                players = guildMembers.size();
                for (JsonElement member : guildMembers) {
                    JsonObject memberObject = member.getAsJsonObject();
                    String memberRank = memberObject.get("rank").getAsString();
                    
                    if (memberRank.equals("GUILDMASTER") || memberRank.equals("Guild Master")) {
                        // Get username from UUID
                        String gmUUID = memberObject.get("uuid").getAsString();
                        guildMaster = APIHandler.getName(gmUUID);
                        break;
                    }
                }
            }
            
            player.addChatMessage(new ChatComponentText(ModConfig.getDelimiter() + "\n" +
                                                        EnumChatFormatting.AQUA + " " + username + "'s Guild:\n" +
                                                        ModConfig.getColour(ModConfig.typeColour) + " Guild: " + ModConfig.getColour(ModConfig.valueColour) + EnumChatFormatting.BOLD + guildName + "\n" +
                                                        ModConfig.getColour(ModConfig.typeColour) + " Guildmaster: " + ModConfig.getColour(ModConfig.valueColour) + EnumChatFormatting.BOLD + guildMaster + "\n" +
                                                        ModConfig.getColour(ModConfig.typeColour) + " Members: " + ModConfig.getColour(ModConfig.valueColour) + EnumChatFormatting.BOLD + players + "\n" +
                                                        ModConfig.getDelimiter()));
        }).start();
    }

}

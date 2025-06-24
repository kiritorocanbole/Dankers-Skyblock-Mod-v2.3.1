package me.Danker.commands;

import me.Danker.DankersSkyblockMod;
import me.Danker.commands.api.*;
import me.Danker.commands.loot.DisplayCommand;
import me.Danker.commands.loot.ImportFishingCommand;
import me.Danker.commands.loot.LootCommand;
import me.Danker.commands.loot.ResetLootCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class DHelpCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "dhelp";
    }

    @Override
    public String getCommandUsage(ICommandSender arg0) {
        return "/" + getCommandName();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    @Override
    public void processCommand(ICommandSender arg0, String[] arg1) throws CommandException {
        EntityPlayer player = (EntityPlayer) arg0;
        
        player.addChatMessage(new ChatComponentText("\n" + EnumChatFormatting.GOLD + " " + DankersSkyblockMod.MODID + " Version " + DankersSkyblockMod.VERSION + "\n" +
                                                    EnumChatFormatting.AQUA + " <> = Mandatory parameter. [] = Optional parameter.\n" +
                                                    EnumChatFormatting.GOLD + " Commands, " + EnumChatFormatting.GREEN + " Keybinds.\n" +
                                                    EnumChatFormatting.GOLD + getCommandUsage(arg0) + EnumChatFormatting.AQUA + " - Returns this message.\n" +
                                                    EnumChatFormatting.GOLD + DankerGuiCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Opens the GUI for Danker's Skyblock Mod.\n" +
                                                    EnumChatFormatting.GOLD + LootCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Returns loot received from slayer quests or fishing stats. /loot fishing winter returns winter sea creatures instead.\n" +
                                                    EnumChatFormatting.GOLD + DisplayCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Text display for trackers. /display fishing winter displays winter sea creatures instead. /display auto automatically displays the loot for the slayer quest you have active.\n" +
                                                    EnumChatFormatting.GOLD + ResetLootCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Resets loot for trackers. /resetloot confirm confirms the reset.\n" +
                                                    EnumChatFormatting.GOLD + SlayerCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to get slayer xp of a person. If no name is provided, it checks yours.\n" +
                                                    EnumChatFormatting.GOLD + SkillsCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to get skill levels of a person. If no name is provided, it checks yours.\n" +
                                                    EnumChatFormatting.GOLD + LobbySkillsCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to find the average skills of the lobby, as well the three players with the highest skill average.\n" +
                                                    EnumChatFormatting.GOLD + GuildOfCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to get guild name and guild master of a person. If no name is provided, it checks yours.\n" +
                                                    EnumChatFormatting.GOLD + PetsCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to get pets of a person. If no name is provided, it checks yours.\n" +
                                                    EnumChatFormatting.GOLD + BankCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to get bank and purse coins of a person. If no name is provided, it checks yours.\n" +
                                                    EnumChatFormatting.GOLD + ArmourCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to get armor of a person. If no name is provided, it checks yours.\n" +
                                                    EnumChatFormatting.GOLD + DungeonsCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to get dungeon levels of a person. If no name is provided, it checks yours.\n" +
                                                    EnumChatFormatting.GOLD + WeightCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to get weight of a person. If no name is provided, it checks yours. Adding lily uses lily's weight instead of Senither's.\n" +
                                                    EnumChatFormatting.GOLD + ImportFishingCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Imports your fishing stats from your latest profile to your fishing tracker using the API.\n" +
                                                    EnumChatFormatting.GOLD + SkyblockPlayersCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to find how many players are on each Skyblock island.\n" +
                                                    EnumChatFormatting.GOLD + SkillTrackerCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Text display for skill xp/hour.\n" +
                                                    EnumChatFormatting.GOLD + LobbyBankCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to find the average bank total of the lobby, as well the three players with the highest total money in the bank(and purse).\n" +
                                                    EnumChatFormatting.GOLD + RepartyCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Disbands and reparties all members in the party.\n" +
                                                    EnumChatFormatting.GOLD + PlayerCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to find skills, slayers, coins and weight of a player.\n" +
                                                    EnumChatFormatting.GOLD + ReloadConfigCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Reloads Danker's Skyblock Mod config.\n" +
                                                    EnumChatFormatting.GOLD + ReloadRepoCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Reloads Danker's Skyblock Mod repository.\n" +
                                                    EnumChatFormatting.GOLD + HOTMCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to find total powder and HotM tree of a person. If no name is provided, it checks yours.\n" +
                                                    EnumChatFormatting.GOLD + TrophyFishCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Uses API to find trophy fish stats of a person. If no name is provided, it checks yours.\n" +
                                                    EnumChatFormatting.GOLD + StopLobbyCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Stops currently running /lobbyskills or /lobbybank command.\n" +
                                                    EnumChatFormatting.GOLD + PowderTrackerCommand.usage(arg0) + EnumChatFormatting.AQUA + " - Text display for powder/hour.\n" +
                                                    EnumChatFormatting.GREEN + "Open Maddox Menu" + EnumChatFormatting.AQUA + " - M by default.\n" +
                                                    EnumChatFormatting.GREEN + "Regular Ability" + EnumChatFormatting.AQUA + " - Numpad 4 by default.\n" +
                                                    EnumChatFormatting.GREEN + "Start/Stop Skill Tracker" + EnumChatFormatting.AQUA + " - Numpad 5 by default.\n" +
                                                    EnumChatFormatting.GREEN + "Create Waypoint" + EnumChatFormatting.AQUA + " - Numpad 6 by default.\n" +
                                                    EnumChatFormatting.GREEN + "Start/Stop Powder Tracker" + EnumChatFormatting.AQUA + " - Numpad 8 by default.\n"));
    }

}

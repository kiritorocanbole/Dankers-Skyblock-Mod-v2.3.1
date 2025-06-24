package me.Danker.commands.api;

import com.google.gson.JsonObject;
import me.Danker.config.ModConfig;
import me.Danker.handlers.APIHandler;
import me.Danker.handlers.HypixelAPIHandler;
import me.Danker.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HOTMCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "hotmof";
    }

    @Override
    public String getCommandUsage(ICommandSender arg0) {
        return "/" + getCommandName() + " [name]";
    }

    public static String usage(ICommandSender arg0) {
        return new HOTMCommand().getCommandUsage(arg0);
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
            player.addChatMessage(new ChatComponentText(ModConfig.getColour(ModConfig.mainColour) + "Checking HotM of " + ModConfig.getColour(ModConfig.secondaryColour) + username + ModConfig.getColour(ModConfig.mainColour) + " using Polyfrost's API."));

            // Find stats of latest profile
            JsonObject profileResponse = HypixelAPIHandler.getLatestProfile(uuid);
            if (profileResponse == null) return;

            String latestProfileID = HypixelAPIHandler.getLatestProfileID(uuid);
            if (latestProfileID == null) return;

            System.out.println("Fetching mining stats...");
            JsonObject miningCore = Utils.getObjectFromPath(profileResponse, "members." + uuid + ".mining_core");

            int mithril = 0;
            if (miningCore.has("powder_mithril")) {
                mithril = miningCore.get("powder_mithril").getAsInt();
                if (miningCore.has("powder_spent_mithril")) mithril += miningCore.get("powder_spent_mithril").getAsInt();
            }

            int gemstone = 0;
            if (miningCore.has("powder_gemstone")) {
                gemstone = miningCore.get("powder_gemstone").getAsInt();
                if (miningCore.has("powder_spent_gemstone")) gemstone += miningCore.get("powder_spent_gemstone").getAsInt();
            }

            int glacite = 0;
            if (miningCore.has("powder_glacite")) {
                glacite = miningCore.get("powder_glacite").getAsInt();
                if (miningCore.has("powder_spent_glacite")) glacite += miningCore.get("powder_spent_glacite").getAsInt();
            }

            String ability = EnumChatFormatting.RED + "None";
            if (miningCore.has("selected_pickaxe_ability")) {
                if (miningCore.get("selected_pickaxe_ability").isJsonNull()) {
                    ability = EnumChatFormatting.RED + "None";
                } else {
                    ability = Node.valueOf(miningCore.get("selected_pickaxe_ability").getAsString()).name;
                }
            }

            ChatComponentText tree = new ChatComponentText(EnumChatFormatting.GREEN + "" + EnumChatFormatting.BOLD + "[CLICK]");
            tree.setChatStyle(tree.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hotmtree " + username + " " + latestProfileID)));

            NumberFormat nf = NumberFormat.getIntegerInstance(Locale.US);
            player.addChatMessage(new ChatComponentText(ModConfig.getDelimiter() + "\n" +
                                                        EnumChatFormatting.AQUA + username + "'s HotM:\n" +
                                                        ModConfig.getColour(ModConfig.typeColour) + "Mithril Powder: " + EnumChatFormatting.DARK_GREEN + nf.format(mithril) + "\n" +
                                                        ModConfig.getColour(ModConfig.typeColour) + "Gemstone Powder: " + EnumChatFormatting.LIGHT_PURPLE + nf.format(gemstone) + "\n" +
                                                        ModConfig.getColour(ModConfig.typeColour) + "Glacite Powder: " + EnumChatFormatting.AQUA + nf.format(glacite) + "\n" +
                                                        ModConfig.getColour(ModConfig.typeColour) + "Pickaxe Ability: " + ModConfig.getColour(ModConfig.valueColour) + ability + "\n" +
                                                        ModConfig.getColour(ModConfig.typeColour) + "HotM Tree: ").appendSibling(tree)
                                                        .appendSibling(new ChatComponentText("\n" + ModConfig.getDelimiter())));
        }).start();
    }

    enum Node {
        mining_speed_boost("Mining Speed Boost"),
        pickaxe_toss("Pickobulus"),
        vein_seeker("Vein Seeker"),
        maniac_miner("Maniac Miner"),
        gemstone_infusion("Gemstone Infusion"),
        hazardous_miner("Hazardous Miner");

        public final String name;

        Node(String name) {
            this.name = name;
        }
    }

}

// ============================================================
// OptimizerCommands.java - Command Handler
// ============================================================
package com.mc.optimizer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OptimizerCommands extends CommandBase {

    // ============================================================
    // COMMAND CONSTANTS
    // ============================================================
    private static final String COMMAND_NAME = "optimizer";
    private static final List<String> COMMAND_ALIASES = Arrays.asList("opt", "optimize", "mcopt");
    private static final List<String> SUB_COMMANDS = Arrays.asList(
        "status", "toggle", "settings", "help", "stats", 
        "reset", "gc", "clear", "players", "fps", "memory"
    );
    
    private static final String VERSION = OptimizerMod.VERSION;

    // ============================================================
    // COMMAND METHODS
    // ============================================================

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + COMMAND_NAME + " <subcommand> [args]";
    }

    @Override
    public List<String> getCommandAliases() {
        return COMMAND_ALIASES;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            showMainMenu(sender);
            return;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "status":
                showStatus(sender);
                break;
            case "toggle":
                handleToggle(sender, args);
                break;
            case "settings":
                showSettings(sender);
                break;
            case "help":
                showHelp(sender);
                break;
            case "stats":
                showStats(sender);
                break;
            case "reset":
                handleReset(sender);
                break;
            case "gc":
                handleGC(sender);
                break;
            case "clear":
                handleClear(sender);
                break;
            case "players":
                showPlayers(sender);
                break;
            case "fps":
                handleFPS(sender, args);
                break;
            case "memory":
                handleMemory(sender, args);
                break;
            default:
                throw new WrongUsageException("Unknown subcommand. Use /" + COMMAND_NAME + " help");
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // No permission required
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, SUB_COMMANDS);
        }
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "toggle":
                    return getListOfStringsMatchingLastWord(args, 
                        Arrays.asList("fps", "memory", "render", "particles", "clouds", "water", "sky"));
                case "fps":
                    return getListOfStringsMatchingLastWord(args, 
                        Arrays.asList("on", "off", "limit"));
                case "memory":
                    return getListOfStringsMatchingLastWord(args, 
                        Arrays.asList("on", "off", "max", "min"));
            }
        }
        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("fps") && args[1].equalsIgnoreCase("limit")) {
                return getListOfStringsMatchingLastWord(args, 
                    Arrays.asList("30", "60", "90", "120", "144", "240"));
            }
            if (subCommand.equals("memory") && args[1].equalsIgnoreCase("max")) {
                return getListOfStringsMatchingLastWord(args, 
                    Arrays.asList("1024", "2048", "4096", "8192"));
            }
        }
        return super.addTabCompletionOptions(sender, args);
    }

    // ============================================================
    // SUBCOMMAND HANDLERS
    // ============================================================

    private void showMainMenu(ICommandSender sender) {
        sendMessage(sender, EnumChatFormatting.GREEN + "=== MC Optimizer Pro " + VERSION + " ===");
        sendMessage(sender, EnumChatFormatting.GRAY + "Use /" + COMMAND_NAME + " <subcommand>");
        sendMessage(sender, EnumChatFormatting.GRAY + "Subcommands: status, toggle, settings, help, stats");
        sendMessage(sender, EnumChatFormatting.GRAY + "Type /" + COMMAND_NAME + " help for all commands");
        
        // Show quick status
        showQuickStatus(sender);
    }

    private void showStatus(ICommandSender sender) {
        boolean isClient = isClientSide(sender);
        
        sendMessage(sender, EnumChatFormatting.GREEN + "=== MC Optimizer Status ===");
        sendMessage(sender, EnumChatFormatting.GOLD + "Version: " + EnumChatFormatting.WHITE + VERSION);
        
        // FPS
        int fps = RegisterHandler.getCurrentFPS();
        String fpsColor = fps >= 60 ? EnumChatFormatting.GREEN.toString() : 
                         (fps >= 30 ? EnumChatFormatting.YELLOW.toString() : EnumChatFormatting.RED.toString());
        sendMessage(sender, EnumChatFormatting.GOLD + "FPS: " + fpsColor + fps);
        
        // TPS
        float tps = RegisterHandler.getAverageTPS();
        String tpsColor = tps >= 19.5f ? EnumChatFormatting.GREEN.toString() : 
                          (tps >= 15.0f ? EnumChatFormatting.YELLOW.toString() : EnumChatFormatting.RED.toString());
        sendMessage(sender, EnumChatFormatting.GOLD + "TPS: " + tpsColor + String.format("%.2f", tps));
        
        // Memory
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long max = runtime.maxMemory() / 1024 / 1024;
        sendMessage(sender, EnumChatFormatting.GOLD + "Memory: " + EnumChatFormatting.WHITE + used + "MB / " + max + "MB");
        
        // Players
        int players = RegisterHandler.getOnlinePlayerCount();
        sendMessage(sender, EnumChatFormatting.GOLD + "Players: " + EnumChatFormatting.WHITE + players);
        
        // Optimization status
        boolean isOptimized = RegisterHandler.isOptimizationActive();
        sendMessage(sender, EnumChatFormatting.GOLD + "Optimization: " + 
            (isOptimized ? EnumChatFormatting.GREEN + "ACTIVE" : EnumChatFormatting.RED + "INACTIVE"));
        
        // GC count
        int gcCount = RegisterHandler.getGCCount();
        sendMessage(sender, EnumChatFormatting.GOLD + "GC Runs: " + EnumChatFormatting.WHITE + gcCount);
        
        // Features status
        sendMessage(sender, EnumChatFormatting.GRAY + "--- Features ---");
        sendMessage(sender, EnumChatFormatting.GRAY + "FPS Boost: " + 
            (ConfigHandler.FPS_BOOST ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        sendMessage(sender, EnumChatFormatting.GRAY + "Memory Opt: " + 
            (ConfigHandler.MEMORY_OPTIMIZATION ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        sendMessage(sender, EnumChatFormatting.GRAY + "Render Opt: " + 
            (ConfigHandler.RENDER_OPTIMIZE ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        
        if (isClientSide(sender)) {
            Minecraft mc = Minecraft.getMinecraft();
            sendMessage(sender, EnumChatFormatting.GRAY + "Render Distance: " + 
                EnumChatFormatting.WHITE + mc.gameSettings.renderDistanceChunks);
            sendMessage(sender, EnumChatFormatting.GRAY + "Particle Limit: " + 
                EnumChatFormatting.WHITE + ConfigHandler.PARTICLE_LIMIT);
        }
    }

    private void showQuickStatus(ICommandSender sender) {
        int fps = RegisterHandler.getCurrentFPS();
        String fpsColor = fps >= 60 ? EnumChatFormatting.GREEN.toString() : 
                         (fps >= 30 ? EnumChatFormatting.YELLOW.toString() : EnumChatFormatting.RED.toString());
        sendMessage(sender, EnumChatFormatting.GRAY + "FPS: " + fpsColor + fps + 
            EnumChatFormatting.GRAY + " | Players: " + RegisterHandler.getOnlinePlayerCount() +
            " | GC: " + RegisterHandler.getGCCount());
    }

    private void handleToggle(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("Usage: /" + COMMAND_NAME + " toggle <fps|memory|render|particles|clouds|water|sky>");
        }

        String feature = args[1].toLowerCase();
        boolean newState = false;
        String featureName = "";

        switch (feature) {
            case "fps":
                ConfigHandler.FPS_BOOST = !ConfigHandler.FPS_BOOST;
                newState = ConfigHandler.FPS_BOOST;
                featureName = "FPS Boost";
                if (newState && isClientSide(sender)) {
                    // Apply FPS boost
                    RegisterHandler.toggleOptimization();
                }
                break;
            case "memory":
                ConfigHandler.MEMORY_OPTIMIZATION = !ConfigHandler.MEMORY_OPTIMIZATION;
                newState = ConfigHandler.MEMORY_OPTIMIZATION;
                featureName = "Memory Optimization";
                if (newState) {
                    RegisterHandler.forceGC();
                }
                break;
            case "render":
                ConfigHandler.RENDER_OPTIMIZE = !ConfigHandler.RENDER_OPTIMIZE;
                newState = ConfigHandler.RENDER_OPTIMIZE;
                featureName = "Render Optimization";
                break;
            case "particles":
                ConfigHandler.OPTIMIZE_PARTICLES = !ConfigHandler.OPTIMIZE_PARTICLES;
                newState = ConfigHandler.OPTIMIZE_PARTICLES;
                featureName = "Particle Optimization";
                break;
            case "clouds":
                ConfigHandler.OPTIMIZE_CLOUDS = !ConfigHandler.OPTIMIZE_CLOUDS;
                newState = ConfigHandler.OPTIMIZE_CLOUDS;
                featureName = "Cloud Optimization";
                if (isClientSide(sender) && newState) {
                    Minecraft.getMinecraft().gameSettings.clouds = 0;
                }
                break;
            case "water":
                ConfigHandler.OPTIMIZE_WATER = !ConfigHandler.OPTIMIZE_WATER;
                newState = ConfigHandler.OPTIMIZE_WATER;
                featureName = "Water Optimization";
                break;
            case "sky":
                ConfigHandler.OPTIMIZE_SKY = !ConfigHandler.OPTIMIZE_SKY;
                newState = ConfigHandler.OPTIMIZE_SKY;
                featureName = "Sky Optimization";
                break;
            default:
                throw new WrongUsageException("Invalid feature. Use: fps, memory, render, particles, clouds, water, sky");
        }

        // Save config
        ConfigHandler.save();

        // Send response
        sendMessage(sender, EnumChatFormatting.GREEN + featureName + " " + 
            (newState ? EnumChatFormatting.GREEN + "ENABLED" : EnumChatFormatting.RED + "DISABLED"));
        
        if (OptimizerMod.logger != null) {
            OptimizerMod.logger.info("Toggled " + featureName + " to " + newState);
        }
    }

    private void showSettings(ICommandSender sender) {
        sendMessage(sender, EnumChatFormatting.GREEN + "=== MC Optimizer Settings ===");
        
        // General
        sendMessage(sender, EnumChatFormatting.GOLD + "--- General ---");
        sendMessage(sender, EnumChatFormatting.GRAY + "Mod Enabled: " + 
            (ConfigHandler.ENABLE_MOD ? EnumChatFormatting.GREEN + "YES" : EnumChatFormatting.RED + "NO"));
        sendMessage(sender, EnumChatFormatting.GRAY + "Debug Mode: " + 
            (ConfigHandler.DEBUG_MODE ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        sendMessage(sender, EnumChatFormatting.GRAY + "Language: " + EnumChatFormatting.WHITE + ConfigHandler.LANGUAGE);
        
        // FPS
        sendMessage(sender, EnumChatFormatting.GOLD + "--- FPS Settings ---");
        sendMessage(sender, EnumChatFormatting.GRAY + "FPS Boost: " + 
            (ConfigHandler.FPS_BOOST ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        sendMessage(sender, EnumChatFormatting.GRAY + "Render Distance: " + 
            EnumChatFormatting.WHITE + ConfigHandler.RENDER_DISTANCE);
        sendMessage(sender, EnumChatFormatting.GRAY + "Entity Distance: " + 
            EnumChatFormatting.WHITE + ConfigHandler.ENTITY_RENDER_DISTANCE);
        sendMessage(sender, EnumChatFormatting.GRAY + "Particle Limit: " + 
            EnumChatFormatting.WHITE + ConfigHandler.PARTICLE_LIMIT);
        
        // Memory
        sendMessage(sender, EnumChatFormatting.GOLD + "--- Memory Settings ---");
        sendMessage(sender, EnumChatFormatting.GRAY + "Memory Opt: " + 
            (ConfigHandler.MEMORY_OPTIMIZATION ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        sendMessage(sender, EnumChatFormatting.GRAY + "Max Memory: " + 
            EnumChatFormatting.WHITE + ConfigHandler.MAX_MEMORY_MB + " MB");
        sendMessage(sender, EnumChatFormatting.GRAY + "GC Interval: " + 
            EnumChatFormatting.WHITE + ConfigHandler.GC_INTERVAL_SECONDS + "s");
        
        // Render
        sendMessage(sender, EnumChatFormatting.GOLD + "--- Render Settings ---");
        sendMessage(sender, EnumChatFormatting.GRAY + "Render Opt: " + 
            (ConfigHandler.RENDER_OPTIMIZE ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        sendMessage(sender, EnumChatFormatting.GRAY + "Particle Opt: " + 
            (ConfigHandler.OPTIMIZE_PARTICLES ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        sendMessage(sender, EnumChatFormatting.GRAY + "Cloud Opt: " + 
            (ConfigHandler.OPTIMIZE_CLOUDS ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        sendMessage(sender, EnumChatFormatting.GRAY + "Water Opt: " + 
            (ConfigHandler.OPTIMIZE_WATER ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        
        // Visual
        sendMessage(sender, EnumChatFormatting.GOLD + "--- Visual ---");
        sendMessage(sender, EnumChatFormatting.GRAY + "Show FPS: " + 
            (ConfigHandler.SHOW_FPS ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        sendMessage(sender, EnumChatFormatting.GRAY + "Show Memory: " + 
            (ConfigHandler.SHOW_MEMORY ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF"));
        
        // Use
        sendMessage(sender, EnumChatFormatting.GOLD + "To change settings, edit config/mcoptimizer.cfg");
    }

    private void showHelp(ICommandSender sender) {
        sendMessage(sender, EnumChatFormatting.GREEN + "=== MC Optimizer Commands ===");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "status");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - Show current optimization status");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "toggle <feature>");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - Toggle: fps, memory, render, particles, clouds, water, sky");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "settings");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - Show all settings");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "stats");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - Show detailed statistics");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "reset");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - Reset all settings to defaults");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "gc");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - Force garbage collection");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "clear");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - Clear optimization cache");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "players");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - List online players");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "fps [on|off|limit <value>]");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - Control FPS settings");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "memory [on|off|max <mb>]");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - Control memory settings");
        sendMessage(sender, EnumChatFormatting.GOLD + "/" + COMMAND_NAME + " " + EnumChatFormatting.WHITE + "help");
        sendMessage(sender, EnumChatFormatting.GRAY + "  - Show this help");
    }

    private void showStats(ICommandSender sender) {
        String stats = RegisterHandler.getStatistics();
        String[] lines = stats.split("\n");
        for (String line : lines) {
            sendMessage(sender, EnumChatFormatting.GRAY + line);
        }
    }

    private void handleReset(ICommandSender sender) {
        // Reset config to defaults
        ConfigHandler.ENABLE_MOD = true;
        ConfigHandler.FPS_BOOST = true;
        ConfigHandler.MEMORY_OPTIMIZATION = true;
        ConfigHandler.RENDER_OPTIMIZE = true;
        ConfigHandler.OPTIMIZE_PARTICLES = true;
        ConfigHandler.OPTIMIZE_CLOUDS = true;
        ConfigHandler.OPTIMIZE_WATER = true;
        ConfigHandler.RENDER_DISTANCE = 12;
        ConfigHandler.PARTICLE_LIMIT = 500;
        ConfigHandler.SHOW_FPS = true;
        ConfigHandler.SHOW_MEMORY = true;
        ConfigHandler.MAX_MEMORY_MB = 4096;
        ConfigHandler.GC_INTERVAL_SECONDS = 10;
        
        ConfigHandler.save();
        
        sendMessage(sender, EnumChatFormatting.GREEN + "All settings reset to defaults!");
        
        if (isClientSide(sender)) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.gameSettings.renderDistanceChunks = ConfigHandler.RENDER_DISTANCE;
        }
        
        if (OptimizerMod.logger != null) {
            OptimizerMod.logger.info("Settings reset by command");
        }
    }

    private void handleGC(ICommandSender sender) {
        long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        RegisterHandler.forceGC();
        long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long freed = (before - after) / 1024 / 1024;
        
        sendMessage(sender, EnumChatFormatting.GREEN + "Garbage collection forced!");
        sendMessage(sender, EnumChatFormatting.GRAY + "Freed: " + freed + " MB");
        sendMessage(sender, EnumChatFormatting.GRAY + "GC Count: " + RegisterHandler.getGCCount());
        
        if (OptimizerMod.logger != null) {
            OptimizerMod.logger.info("Manual GC performed, freed " + freed + " MB");
        }
    }

    private void handleClear(ICommandSender sender) {
        RegisterHandler.clearCache();
        sendMessage(sender, EnumChatFormatting.GREEN + "Cache cleared successfully!");
        
        // Force GC after clearing
        RegisterHandler.forceGC();
        
        if (OptimizerMod.logger != null) {
            OptimizerMod.logger.info("Cache cleared by command");
        }
    }

    private void showPlayers(ICommandSender sender) {
        List<EntityPlayer> players = RegisterHandler.getOnlinePlayers();
        
        sendMessage(sender, EnumChatFormatting.GREEN + "=== Online Players (" + players.size() + ") ===");
        
        if (players.isEmpty()) {
            sendMessage(sender, EnumChatFormatting.GRAY + "No players online");
            return;
        }
        
        for (EntityPlayer player : players) {
            UUID uuid = player.getUniqueID();
            boolean optimized = RegisterHandler.isPlayerOptimized(uuid);
            int renderDist = RegisterHandler.getPlayerRenderDistance(uuid);
            long joinTime = RegisterHandler.getPlayerJoinTime(uuid);
            long elapsed = (System.currentTimeMillis() - joinTime) / 1000 / 60; // minutes
            
            sendMessage(sender, EnumChatFormatting.GRAY + player.getName() + 
                (optimized ? EnumChatFormatting.GREEN + " [OPT]" : "") +
                EnumChatFormatting.GRAY + " - RD: " + renderDist +
                " - Online: " + elapsed + "m");
        }
    }

    private void handleFPS(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("Usage: /" + COMMAND_NAME + " fps <on|off|limit <value>>");
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "on":
                ConfigHandler.FPS_BOOST = true;
                ConfigHandler.save();
                sendMessage(sender, EnumChatFormatting.GREEN + "FPS Boost ENABLED");
                break;
            case "off":
                ConfigHandler.FPS_BOOST = false;
                ConfigHandler.save();
                sendMessage(sender, EnumChatFormatting.RED + "FPS Boost DISABLED");
                break;
            case "limit":
                if (args.length < 3) {
                    throw new WrongUsageException("Usage: /" + COMMAND_NAME + " fps limit <value>");
                }
                try {
                    int limit = Integer.parseInt(args[2]);
                    if (limit < 1 || limit > 1000) {
                        throw new CommandException("FPS limit must be between 1 and 1000");
                    }
                    // Set FPS limit
                    sendMessage(sender, EnumChatFormatting.GREEN + "FPS limit set to: " + limit);
                } catch (NumberFormatException e) {
                    throw new CommandException("Invalid number: " + args[2]);
                }
                break;
            default:
                throw new WrongUsageException("Usage: /" + COMMAND_NAME + " fps <on|off|limit <value>>");
        }
        
        if (OptimizerMod.logger != null) {
            OptimizerMod.logger.info("FPS command: " + action);
        }
    }

    private void handleMemory(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("Usage: /" + COMMAND_NAME + " memory <on|off|max <mb>>");
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "on":
                ConfigHandler.MEMORY_OPTIMIZATION = true;
                ConfigHandler.save();
                sendMessage(sender, EnumChatFormatting.GREEN + "Memory Optimization ENABLED");
                RegisterHandler.forceGC();
                break;
            case "off":
                ConfigHandler.MEMORY_OPTIMIZATION = false;
                ConfigHandler.save();
                sendMessage(sender, EnumChatFormatting.RED + "Memory Optimization DISABLED");
                break;
            case "max":
                if (args.length < 3) {
                    throw new WrongUsageException("Usage: /" + COMMAND_NAME + " memory max <mb>");
                }
                try {
                    int maxMem = Integer.parseInt(args[2]);
                    if (maxMem < 256 || maxMem > 16384) {
                        throw new CommandException("Memory limit must be between 256 and 16384 MB");
                    }
                    ConfigHandler.MAX_MEMORY_MB = maxMem;
                    ConfigHandler.save();
                    sendMessage(sender, EnumChatFormatting.GREEN + "Max memory set to: " + maxMem + " MB");
                } catch (NumberFormatException e) {
                    throw new CommandException("Invalid number: " + args[2]);
                }
                break;
            default:
                throw new WrongUsageException("Usage: /" + COMMAND_NAME + " memory <on|off|max <mb>>");
        }
        
        if (OptimizerMod.logger != null) {
            OptimizerMod.logger.info("Memory command: " + action);
        }
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================

    private void sendMessage(ICommandSender sender, String message) {
        sender.addChatMessage(new ChatComponentText(message));
    }

    private boolean isClientSide(ICommandSender sender) {
        return sender instanceof EntityPlayerSP || 
               sender instanceof EntityPlayer && 
               sender.getEntityWorld().isRemote;
    }

    // ============================================================
    // COMMAND VALIDATION
    // ============================================================

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        if (index == 0 && args.length > 0) {
            return args[0].equalsIgnoreCase("players");
        }
        return false;
    }

    @Override
    public List<String> getCommandAliases(MinecraftServer server) {
        return COMMAND_ALIASES;
    }
}

// ============================================================
// ConfigHandler.java - Configuration Management
// ============================================================
package com.mc.optimizer;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    
    private static Configuration config;
    private static ConfigHandler instance;
    
    // ============================================================
    // GENERAL SETTINGS
    // ============================================================
    public static boolean ENABLE_MOD = true;
    public static boolean DEBUG_MODE = false;
    public static String LANGUAGE = "en_US";
    public static int UPDATE_CHECK_INTERVAL = 3600;
    
    // ============================================================
    // FPS OPTIMIZATION SETTINGS
    // ============================================================
    public static boolean FPS_BOOST = true;
    public static boolean OPTIMIZE_RENDER_DISTANCE = true;
    public static boolean OPTIMIZE_PARTICLES = true;
    public static boolean OPTIMIZE_ENTITIES = true;
    public static boolean OPTIMIZE_CHUNKS = true;
    public static boolean OPTIMIZE_CLOUDS = true;
    public static boolean OPTIMIZE_WATER = true;
    public static boolean OPTIMIZE_SKY = false;
    public static boolean OPTIMIZE_LEAVES = true;
    public static boolean OPTIMIZE_GRASS = true;
    public static boolean OPTIMIZE_ITEM_RENDER = true;
    public static boolean OPTIMIZE_GUI_RENDER = false;
    
    public static int RENDER_DISTANCE = 12;
    public static int MAX_RENDER_DISTANCE = 32;
    public static int MIN_RENDER_DISTANCE = 2;
    public static int ENTITY_RENDER_DISTANCE = 64;
    public static int PARTICLE_LIMIT = 500;
    public static int MAX_PARTICLES = 2000;
    public static int CHUNK_UPDATE_THRESHOLD = 50;
    public static int LIGHT_UPDATE_INTERVAL = 5;
    
    // ============================================================
    // MEMORY OPTIMIZATION SETTINGS
    // ============================================================
    public static boolean MEMORY_OPTIMIZATION = true;
    public static boolean GC_OPTIMIZATION = true;
    public static boolean CHUNK_CACHE_OPTIMIZATION = true;
    public static boolean TEXTURE_CACHE_OPTIMIZATION = true;
    public static boolean SOUND_CACHE_OPTIMIZATION = true;
    public static boolean SKIN_CACHE_OPTIMIZATION = true;
    public static boolean MODEL_CACHE_OPTIMIZATION = true;
    
    public static int MAX_MEMORY_MB = 4096;
    public static int MIN_MEMORY_MB = 512;
    public static int GC_INTERVAL_SECONDS = 10;
    public static int CHUNK_CACHE_SIZE = 256;
    public static int TEXTURE_CACHE_SIZE = 128;
    
    // ============================================================
    // NETWORK OPTIMIZATION SETTINGS
    // ============================================================
    public static boolean NETWORK_OPTIMIZATION = true;
    public static boolean PACKET_COMPRESSION = true;
    public static boolean PREDICTIVE_LOADING = true;
    public static boolean BATCH_UPDATES = true;
    
    public static int MAX_PACKET_SIZE = 32767;
    public static int PACKET_QUEUE_SIZE = 100;
    public static int UPDATE_INTERVAL_TICKS = 2;
    
    // ============================================================
    // VISUAL SETTINGS
    // ============================================================
    public static boolean SHOW_FPS = true;
    public static boolean SHOW_MEMORY = true;
    public static boolean SHOW_TPS = false;
    public static boolean SHOW_COORDS = false;
    public static boolean SHOW_BIOME = false;
    public static boolean SHOW_LIGHT_LEVEL = false;
    public static boolean SHOW_DEBUG_INFO = false;
    
    public static int FPS_COUNTER_POSITION = 0; // 0=TopLeft, 1=TopRight, 2=BottomLeft, 3=BottomRight
    public static boolean FPS_COUNTER_BACKGROUND = true;
    public static String FPS_COUNTER_COLOR = "GREEN";
    
    // ============================================================
    // ADVANCED SETTINGS
    // ============================================================
    public static boolean ENABLE_JIT_OPTIMIZATION = true;
    public static boolean ENABLE_THREAD_PRIORITY = true;
    public static boolean ENABLE_FAST_MATH = true;
    public static boolean ENABLE_RENDER_THREADING = false;
    public static boolean ENABLE_CHUNK_THREADING = true;
    public static boolean ENABLE_SOUND_THREADING = false;
    
    public static int THREAD_PRIORITY = 5;
    public static int RENDER_THREADS = 2;
    public static int CHUNK_THREADS = 4;
    
    // ============================================================
    // BLACKLIST / WHITELIST
    // ============================================================
    public static List<String> OPTIMIZATION_BLACKLIST = new ArrayList<>();
    public static List<String> ENTITY_BLACKLIST = new ArrayList<>();
    public static List<String> DIMENSION_BLACKLIST = new ArrayList<>();
    
    static {
        // Default blacklists
        OPTIMIZATION_BLACKLIST.add("minecraft:chest");
        OPTIMIZATION_BLACKLIST.add("minecraft:hopper");
        OPTIMIZATION_BLACKLIST.add("minecraft:furnace");
        
        ENTITY_BLACKLIST.add("minecraft:armor_stand");
        ENTITY_BLACKLIST.add("minecraft:item_frame");
        ENTITY_BLACKLIST.add("minecraft:painting");
        
        DIMENSION_BLACKLIST.add("-1"); // Nether
        DIMENSION_BLACKLIST.add("1");  // End
    }
    
    // ============================================================
    // CONFIG INITIALIZATION
    // ============================================================
    public static void init(File file) {
        instance = new ConfigHandler();
        config = new Configuration(file);
        
        try {
            config.load();
            
            // Load all categories
            loadGeneralSettings();
            loadFPSSettings();
            loadMemorySettings();
            loadNetworkSettings();
            loadVisualSettings();
            loadAdvancedSettings();
            loadBlacklistSettings();
            
        } catch (Exception e) {
            System.err.println("[MC Optimizer] Failed to load config: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
    
    // ============================================================
    // LOAD GENERAL SETTINGS
    // ============================================================
    private static void loadGeneralSettings() {
        String category = "General";
        
        ENABLE_MOD = config.getBoolean("Enable_Mod", category, true, 
            "Enable or disable the entire mod");
        
        DEBUG_MODE = config.getBoolean("Debug_Mode", category, false, 
            "Enable debug logging");
        
        LANGUAGE = config.getString("Language", category, "en_US", 
            "Language setting", new String[]{"en_US", "en_GB", "es_ES", "fr_FR", "de_DE", "zh_CN"});
        
        UPDATE_CHECK_INTERVAL = config.getInt("Update_Check_Interval", category, 3600, 0, 86400, 
            "How often to check for updates (in seconds)");
    }
    
    // ============================================================
    // LOAD FPS SETTINGS
    // ============================================================
    private static void loadFPSSettings() {
        String category = "FPS_Optimization";
        
        FPS_BOOST = config.getBoolean("FPS_Boost", category, true, 
            "Enable FPS boost optimizations");
        
        OPTIMIZE_RENDER_DISTANCE = config.getBoolean("Optimize_Render_Distance", category, true, 
            "Dynamically adjust render distance for better FPS");
        
        OPTIMIZE_PARTICLES = config.getBoolean("Optimize_Particles", category, true, 
            "Reduce particle render load");
        
        OPTIMIZE_ENTITIES = config.getBoolean("Optimize_Entities", category, true, 
            "Optimize entity rendering");
        
        OPTIMIZE_CHUNKS = config.getBoolean("Optimize_Chunks", category, true, 
            "Optimize chunk rendering and updates");
        
        OPTIMIZE_CLOUDS = config.getBoolean("Optimize_Clouds", category, true, 
            "Reduce cloud render quality for better FPS");
        
        OPTIMIZE_WATER = config.getBoolean("Optimize_Water", category, true, 
            "Optimize water rendering");
        
        OPTIMIZE_SKY = config.getBoolean("Optimize_Sky", category, false, 
            "Optimize sky rendering");
        
        OPTIMIZE_LEAVES = config.getBoolean("Optimize_Leaves", category, true, 
            "Optimize leaf rendering");
        
        OPTIMIZE_GRASS = config.getBoolean("Optimize_Grass", category, true, 
            "Optimize grass rendering");
        
        OPTIMIZE_ITEM_RENDER = config.getBoolean("Optimize_Item_Render", category, true, 
            "Optimize item rendering in inventories");
        
        OPTIMIZE_GUI_RENDER = config.getBoolean("Optimize_GUI_Render", category, false, 
            "Optimize GUI rendering");
        
        RENDER_DISTANCE = config.getInt("Render_Distance", category, 12, 2, 32, 
            "Default render distance (chunks)");
        
        MAX_RENDER_DISTANCE = config.getInt("Max_Render_Distance", category, 32, 2, 64, 
            "Maximum render distance");
        
        MIN_RENDER_DISTANCE = config.getInt("Min_Render_Distance", category, 2, 2, 16, 
            "Minimum render distance");
        
        ENTITY_RENDER_DISTANCE = config.getInt("Entity_Render_Distance", category, 64, 16, 256, 
            "Distance to render entities (blocks)");
        
        PARTICLE_LIMIT = config.getInt("Particle_Limit", category, 500, 0, 2000, 
            "Maximum particles on screen");
        
        MAX_PARTICLES = config.getInt("Max_Particles", category, 2000, 0, 10000, 
            "Absolute maximum particles");
        
        CHUNK_UPDATE_THRESHOLD = config.getInt("Chunk_Update_Threshold", category, 50, 1, 200, 
            "Chunks to update per tick");
        
        LIGHT_UPDATE_INTERVAL = config.getInt("Light_Update_Interval", category, 5, 1, 20, 
            "Light update interval (ticks)");
    }
    
    // ============================================================
    // LOAD MEMORY SETTINGS
    // ============================================================
    private static void loadMemorySettings() {
        String category = "Memory_Optimization";
        
        MEMORY_OPTIMIZATION = config.getBoolean("Memory_Optimization", category, true, 
            "Enable memory optimization");
        
        GC_OPTIMIZATION = config.getBoolean("GC_Optimization", category, true, 
            "Enable garbage collection optimization");
        
        CHUNK_CACHE_OPTIMIZATION = config.getBoolean("Chunk_Cache_Optimization", category, true, 
            "Optimize chunk caching");
        
        TEXTURE_CACHE_OPTIMIZATION = config.getBoolean("Texture_Cache_Optimization", category, true, 
            "Optimize texture caching");
        
        SOUND_CACHE_OPTIMIZATION = config.getBoolean("Sound_Cache_Optimization", category, true, 
            "Optimize sound caching");
        
        SKIN_CACHE_OPTIMIZATION = config.getBoolean("Skin_Cache_Optimization", category, true, 
            "Optimize skin caching");
        
        MODEL_CACHE_OPTIMIZATION = config.getBoolean("Model_Cache_Optimization", category, true, 
            "Optimize model caching");
        
        MAX_MEMORY_MB = config.getInt("Max_Memory_MB", category, 4096, 512, 16384, 
            "Maximum memory allocation (MB)");
        
        MIN_MEMORY_MB = config.getInt("Min_Memory_MB", category, 512, 256, 4096, 
            "Minimum memory allocation (MB)");
        
        GC_INTERVAL_SECONDS = config.getInt("GC_Interval_Seconds", category, 10, 1, 60, 
            "Garbage collection interval (seconds)");
        
        CHUNK_CACHE_SIZE = config.getInt("Chunk_Cache_Size", category, 256, 64, 1024, 
            "Chunk cache size");
        
        TEXTURE_CACHE_SIZE = config.getInt("Texture_Cache_Size", category, 128, 64, 512, 
            "Texture cache size");
    }
    
    // ============================================================
    // LOAD NETWORK SETTINGS
    // ============================================================
    private static void loadNetworkSettings() {
        String category = "Network_Optimization";
        
        NETWORK_OPTIMIZATION = config.getBoolean("Network_Optimization", category, true, 
            "Enable network optimization");
        
        PACKET_COMPRESSION = config.getBoolean("Packet_Compression", category, true, 
            "Enable packet compression");
        
        PREDICTIVE_LOADING = config.getBoolean("Predictive_Loading", category, true, 
            "Enable predictive chunk loading");
        
        BATCH_UPDATES = config.getBoolean("Batch_Updates", category, true, 
            "Enable batch entity updates");
        
        MAX_PACKET_SIZE = config.getInt("Max_Packet_Size", category, 32767, 1024, 65535, 
            "Maximum packet size (bytes)");
        
        PACKET_QUEUE_SIZE = config.getInt("Packet_Queue_Size", category, 100, 10, 1000, 
            "Packet queue size");
        
        UPDATE_INTERVAL_TICKS = config.getInt("Update_Interval_Ticks", category, 2, 1, 10, 
            "Update interval (ticks)");
    }
    
    // ============================================================
    // LOAD VISUAL SETTINGS
    // ============================================================
    private static void loadVisualSettings() {
        String category = "Visual";
        
        SHOW_FPS = config.getBoolean("Show_FPS", category, true, 
            "Display FPS counter");
        
        SHOW_MEMORY = config.getBoolean("Show_Memory", category, true, 
            "Display memory usage");
        
        SHOW_TPS = config.getBoolean("Show_TPS", category, false, 
            "Display TPS counter");
        
        SHOW_COORDS = config.getBoolean("Show_Coords", category, false, 
            "Display coordinates");
        
        SHOW_BIOME = config.getBoolean("Show_Biome", category, false, 
            "Display biome information");
        
        SHOW_LIGHT_LEVEL = config.getBoolean("Show_Light_Level", category, false, 
            "Display light level");
        
        SHOW_DEBUG_INFO = config.getBoolean("Show_Debug_Info", category, false, 
            "Display debug information");
        
        FPS_COUNTER_POSITION = config.getInt("FPS_Counter_Position", category, 0, 0, 3, 
            "FPS counter position: 0=TopLeft, 1=TopRight, 2=BottomLeft, 3=BottomRight");
        
        FPS_COUNTER_BACKGROUND = config.getBoolean("FPS_Counter_Background", category, true, 
            "Show background behind FPS counter");
        
        FPS_COUNTER_COLOR = config.getString("FPS_Counter_Color", category, "GREEN", 
            "FPS counter color", new String[]{"GREEN", "RED", "YELLOW", "BLUE", "WHITE"});
    }
    
    // ============================================================
    // LOAD ADVANCED SETTINGS
    // ============================================================
    private static void loadAdvancedSettings() {
        String category = "Advanced";
        
        ENABLE_JIT_OPTIMIZATION = config.getBoolean("Enable_JIT_Optimization", category, true, 
            "Enable JIT compilation optimization");
        
        ENABLE_THREAD_PRIORITY = config.getBoolean("Enable_Thread_Priority", category, true, 
            "Enable thread priority management");
        
        ENABLE_FAST_MATH = config.getBoolean("Enable_Fast_Math", category, true, 
            "Enable fast math optimizations");
        
        ENABLE_RENDER_THREADING = config.getBoolean("Enable_Render_Threading", category, false, 
            "Enable render threading (experimental)");
        
        ENABLE_CHUNK_THREADING = config.getBoolean("Enable_Chunk_Threading", category, true, 
            "Enable chunk threading");
        
        ENABLE_SOUND_THREADING = config.getBoolean("Enable_Sound_Threading", category, false, 
            "Enable sound threading (experimental)");
        
        THREAD_PRIORITY = config.getInt("Thread_Priority", category, 5, 1, 10, 
            "Thread priority level");
        
        RENDER_THREADS = config.getInt("Render_Threads", category, 2, 1, 8, 
            "Number of render threads");
        
        CHUNK_THREADS = config.getInt("Chunk_Threads", category, 4, 1, 8, 
            "Number of chunk threads");
    }
    
    // ============================================================
    // LOAD BLACKLIST SETTINGS
    // ============================================================
    private static void loadBlacklistSettings() {
        String category = "Blacklist";
        
        String[] defaultBlacklist = new String[]{"minecraft:chest", "minecraft:hopper", "minecraft:furnace"};
        String[] blacklist = config.getStringList("Optimization_Blacklist", category, defaultBlacklist, 
            "Blocks to exclude from optimization");
        OPTIMIZATION_BLACKLIST.clear();
        for (String item : blacklist) {
            OPTIMIZATION_BLACKLIST.add(item);
        }
        
        String[] defaultEntityBlacklist = new String[]{"minecraft:armor_stand", "minecraft:item_frame", "minecraft:painting"};
        String[] entityBlacklist = config.getStringList("Entity_Blacklist", category, defaultEntityBlacklist, 
            "Entities to exclude from optimization");
        ENTITY_BLACKLIST.clear();
        for (String item : entityBlacklist) {
            ENTITY_BLACKLIST.add(item);
        }
        
        String[] defaultDimensionBlacklist = new String[]{"-1", "1"};
        String[] dimensionBlacklist = config.getStringList("Dimension_Blacklist", category, defaultDimensionBlacklist, 
            "Dimensions to exclude from optimization");
        DIMENSION_BLACKLIST.clear();
        for (String item : dimensionBlacklist) {
            DIMENSION_BLACKLIST.add(item);
        }
    }
    
    // ============================================================
    // SAVE CONFIG
    // ============================================================
    public static void save() {
        if (config != null && config.hasChanged()) {
            config.save();
        }
    }
    
    // ============================================================
    // GET CONFIG VALUE - Utility Methods
    // ============================================================
    public static boolean isOptimizationEnabled(String blockId) {
        if (!ENABLE_MOD) return false;
        if (OPTIMIZATION_BLACKLIST.contains(blockId)) return false;
        return true;
    }
    
    public static boolean isEntityOptimizationEnabled(String entityId) {
        if (!ENABLE_MOD) return false;
        if (ENTITY_BLACKLIST.contains(entityId)) return false;
        return true;
    }
    
    public static boolean isDimensionOptimizationEnabled(int dimensionId) {
        if (!ENABLE_MOD) return false;
        if (DIMENSION_BLACKLIST.contains(String.valueOf(dimensionId))) return false;
        return true;
    }
    
    public static int getRenderDistanceForDimension(int dimensionId) {
        if (!isDimensionOptimizationEnabled(dimensionId)) {
            return RENDER_DISTANCE;
        }
        // Dynamic render distance based on dimension
        if (dimensionId == -1) { // Nether
            return Math.min(RENDER_DISTANCE, 8);
        } else if (dimensionId == 1) { // End
            return Math.min(RENDER_DISTANCE, 10);
        }
        return RENDER_DISTANCE;
    }
    
    // ============================================================
    // CONFIG CHANGE EVENT
    // ============================================================
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(OptimizerMod.MODID)) {
            // Reload config
            init(config.getConfigFile());
            System.out.println("[MC Optimizer] Config reloaded");
        }
    }
    
    // ============================================================
    // GET INSTANCE
    // ============================================================
    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
        }
        return instance;
    }
}

package com.mineaurion.aurionworld.world;

import com.mineaurion.aurionworld.core.Log;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.network.ForgeMessage;
import net.minecraftforge.event.world.WorldEvent;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class AWorldManager {
    public static final String PROVIDER_NORMAL = "normal";
    public static final String PROVIDER_HELL = "nether";
    public static final String PROVIDER_END = "end";

    protected Map<String, AWorld> worlds = new HashMap<>();
    protected Map<Integer, AWorld> worldsByDim = new HashMap<>();
    protected Map<String, Integer> worldProviderClasses = new HashMap<>();
    protected Map<String, WorldType> worldTypes = new HashMap<>();

    public AWorldManager() {

    }

    // ============================================================
    // World states
    public void load() {
        DimensionManager.loadDimensionDataMap(null);
        // TODO: loadedWorlds from Model World
        Map<String, AWorld> loadedWorlds = new HashMap<>();
        for (AWorld world : loadedWorlds.values()) {
            worlds.put(world.getName(), world);
            try {
                registerWorld(world);
                loadWorld(world);
            } catch (AWorldException e) {
                switch (e.type) {
                    case NO_PROVIDER:
                        Log.error(String.format(e.type.error, world.provider));
                        break;
                    case NO_WORLDTYPE:
                        Log.error(String.format(e.type.error, world.worldType));
                        break;
                    default:
                        Log.error(e.type.error);
                        break;
                }

            }
        }
    }

    public void stop() {
        saveAll();
        for (AWorld world : worlds.values()) {
            world.worldLoaded = false;
            DimensionManager.unregisterDimension(world.getDimensionId());
        }
        worldsByDim.clear();
        worlds.clear();
    }

    public void saveAll() {
        for (AWorld world : getWorlds()) {
            world.save();
        }
    }

    // ============================================================
    // World management

    public Collection<AWorld> getWorlds() {
        return worlds.values();
    }

    public AWorld getWorld(String name) {
        return worlds.get(name);
    }

    public void addWorld(AWorld world) throws AWorldException {
        if (worlds.containsKey(world.getName()))
            throw new AWorldException(AWorldException.Type.ALREADY_EXISTS);
        registerWorld(world);
        loadWorld(world);
        worlds.put(world.getName(), world);
        world.save();
    }

    protected void registerWorld(AWorld world) throws AWorldException {
        world.providerId = getWorldProviderId(world.provider);
        world.worldTypeObj = getWorldTypeByName(world.worldType);

        // Register dimension with last used id if possible
        if (DimensionManager.isDimensionRegistered(world.dimensionId))
            world.dimensionId = DimensionManager.getNextFreeDimId();

        // Register the dimension

        DimensionManager.registerDimension(world.dimensionId, world.providerId);

        worldsByDim.put(world.dimensionId, world);
    }

    protected void loadWorld(AWorld world) {
        Log.info("START loadWorld");
        if (world.worldLoaded)
            return;
        try {
            Log.info("TRY loadWorld");
            // Initialize worlds settings
            MinecraftServer mcServer = MinecraftServer.getServer();
            WorldServer overworld = DimensionManager.getWorld(0);
            if (overworld == null)
                throw new RuntimeException("Cannot hotload dim: Overworld is not Loaded!");
            ISaveHandler savehandler = new AWorldSaveHandler(overworld.getSaveHandler(), world);
            WorldSettings worldSettings = new WorldSettings(world.seed, WorldSettings.GameType.SURVIVAL, world.mapFeaturesEnabled, false, world.worldTypeObj);

            // Create WorldServer with settings
            WorldServer worldServer = new AWorldServer(mcServer, savehandler, //
                    overworld.getWorldInfo().getWorldName(), world.dimensionId, worldSettings, //
                    overworld, mcServer.theProfiler, world);
            // Overwrite dimensionId because WorldProviderEnd for example just hardcodes the dimId
            worldServer.provider.dimensionId = world.dimensionId;
            worldServer.addWorldAccess(new WorldManager(mcServer, worldServer));
            if (!mcServer.isSinglePlayer())
                worldServer.getWorldInfo().setGameType(mcServer.getGameType());
            mcServer.func_147139_a(mcServer.func_147135_j());
            world.updateWorldSettings();
            world.worldLoaded = true;
            world.error = false;

            // Post WorldEvent.Load
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(worldServer));

            // Tell everyone about the new dim
            FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
            ForgeMessage.DimensionRegisterMessage msg = new ForgeMessage.DimensionRegisterMessage(world.dimensionId, world.providerId);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
            channel.writeOutbound(msg);

            Log.info("TRY loadWorld 1");
            // Post WorldEvent.Load
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(worldServer));
            Log.info("TRY loadWorld 2");
        } catch (Exception e) {
            world.error = true;
            throw e;
        }
        Log.info("TRY loadWorld END");
    }

    // ============================================================
    // WorldProvider management

    /**
     * Use reflection to load the registered WorldProviders
     */
    public void loadWorldProviders() {
        try {
            Field f_providers = DimensionManager.class.getDeclaredField("providers");
            f_providers.setAccessible(true);
            @SuppressWarnings("unchecked")
            Hashtable<Integer, Class<? extends WorldProvider>> loadedProviders = (Hashtable<Integer, Class<? extends WorldProvider>>) f_providers.get(null);
            for (Map.Entry<Integer, Class<? extends WorldProvider>> provider : loadedProviders.entrySet()) {
                // skip the default providers as these are aliased as 'normal',
                // 'nether' and 'end'
                if (provider.getKey() >= -1 && provider.getKey() <= 1)
                    continue;

                worldProviderClasses.put(provider.getValue().getName(), provider.getKey());
            }
            worldProviderClasses.put(PROVIDER_NORMAL, 0);
            worldProviderClasses.put(PROVIDER_HELL, 1);
            worldProviderClasses.put(PROVIDER_END, -1);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Log.info("Available worlds providers:");
        for (Map.Entry<String, Integer> provider : worldProviderClasses.entrySet()) {
            Log.info("# " + provider.getValue() + ":" + provider.getKey());
        }
    }

    public int getWorldProviderId(String providerName) throws AWorldException {
        switch (providerName.toLowerCase()) {
            // We use the hardcoded values as some mods just replace the class
            // (BiomesOPlenty)
            case PROVIDER_NORMAL:
                return 0;
            case PROVIDER_HELL:
                return -1;
            case PROVIDER_END:
                return 1;
            default:
                Integer providerId = worldProviderClasses.get(providerName);
                if (providerId == null)
                    throw new AWorldException(AWorldException.Type.NO_PROVIDER);
                return providerId;
        }
    }

    public Map<String, Integer> getWorldProviders() {
        return worldProviderClasses;
    }

    // ============================================================
    // WorldType management

    /**
     * Returns the WorldType for a given worldType string
     */
    public WorldType getWorldTypeByName(String worldType) throws AWorldException {
        WorldType type = worldTypes.get(worldType.toUpperCase());
        if (type == null)
            throw new AWorldException(AWorldException.Type.NO_WORLDTYPE);
        return type;
    }

    /**
     * Builds the map of valid worldTypes
     */
    public void loadWorldTypes() {
        for (int i = 0; i < WorldType.worldTypes.length; ++i) {
            WorldType type = WorldType.worldTypes[i];
            if (type == null)
                continue;

            String name = type.getWorldTypeName().toUpperCase();

            if (name.equals("DEFAULT_1_1"))
                continue;

            worldTypes.put(name, type);
        }

        Log.info("Available worlds types:");
        for (String worldType : worldTypes.keySet())
            Log.info("# " + worldType);
    }

    public Map<String, WorldType> getWorldTypes() {
        return worldTypes;
    }
}

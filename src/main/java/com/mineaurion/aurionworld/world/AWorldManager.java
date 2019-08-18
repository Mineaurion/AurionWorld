package com.mineaurion.aurionworld.world;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.Log;
import com.mineaurion.aurionworld.core.misc.point.WorldPoint;
import com.mineaurion.aurionworld.core.models.WorldModel;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.network.ForgeMessage;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class AWorldManager {
    public static final String PROVIDER_NORMAL = "normal";
    public static final String PROVIDER_HELL = "nether";
    public static final String PROVIDER_END = "end";

    protected Map<String, AWorld> worlds = new HashMap<>();

    protected Map<String, Integer> worldProviderClasses = new HashMap<>();
    protected Map<String, WorldType> worldTypes = new HashMap<>();

    public AWorldManager() {

    }

    /*private void checkMissingWorlds() {
        Integer[] dimensionIDs = DimensionManager.getStaticDimensionIDs();

        for (Integer dimId : dimensionIDs) {
            if (getWorld(dimId) == null && ) {
                String name = DimensionManager.getProvider(dimId).getDimensionName().toLowerCase();
                String provider = DimensionManager.getWorld(dimId).getProviderName().toLowerCase();
                String type = "";

                AWorld world = new AWorld(name, provider, type);
                world.save();
            }
        }
    }*/

    // ============================================================
    // WorldModel states

    private HashMap<String, AWorld> loadAllWorldFromDb() {
        List<WorldModel> worldModels = WorldModel.findAll();
        HashMap<String, AWorld> results = new HashMap<>();
        for (WorldModel wm : worldModels) {
            results.put((String) wm.get("name"), new AWorld(wm));
        }
        return results;
    }

    public void load() {
        DimensionManager.loadDimensionDataMap(null);
        // TODO: loadedWorlds from Model WorldModel
        Map<String, AWorld> worldsToLoad = loadAllWorldFromDb();
        for (AWorld world : worldsToLoad.values()) {
            worlds.put(world.getName(), world);
            loadWorld(world);
        }
    }

    public void unloadWorld(AWorld world) {
        world.worldLoaded = false;
        world.worldLoadIt = false;
        world.removeAllPlayersFromWorld();
        DimensionManager.unloadWorld(world.getDimensionId());
        Log.info("World " + world.getName() + " is now unloaded! All players has been teleported at overworld's spawn");
        world.save();
    }

    public void stop() {
        for (AWorld world : worlds.values()) {
            world.worldLoaded = false;
            world.save();
            DimensionManager.unregisterDimension(world.getDimensionId());
        }
        worlds.clear();
    }

    // ============================================================
    // WorldModel management

    public Collection<AWorld> getWorlds() {
        return worlds.values();
    }

    public Collection<AWorld> getPlayerOwnedWorlds(String playerName) {
        Collection<AWorld> results = new ArrayList<>();
        EntityPlayerMP player = AurionWorld.getEntityPlayer(playerName);

        if (player == null)
            return results;

        for (AWorld world : worlds.values()) {
            if (world.ownerUuid.equals(player.getUniqueID().toString()))
                results.add(world);
        }
        return results;
    }

    public AWorld getWorld(String name) {
        return worlds.get(name);
    }

    public AWorld getWorld(Integer dimId) {
        for (AWorld world : getWorlds()) {
            if (world.dimensionId == dimId)
                return world;
        }
        return null;
    }

    public void deleteWorld(AWorld world) {
        WorldServer ws = world.getWorldServer();
        worlds.remove(world.getName());
        unloadWorld(world);
        if (DimensionManager.getWorld(world.dimensionId) == null) {
            Log.info("START TRY TO DELETE");
            try {
                if (DimensionManager.isDimensionRegistered(ws.provider.dimensionId))
                    DimensionManager.unregisterDimension(ws.provider.dimensionId);

                File path = ws.getChunkSaveLocation(); // new
                // File(world.getSaveHandler().getWorldDirectory(),
                // world.provider.getSaveFolder());
                FileUtils.deleteDirectory(path);
            } catch (IOException e) {
                Log.warn("Error deleting dimension files");
            }
            world.delete();
        }
    }

    public void addWorld(AWorld world) throws AWorldException {
        registerWorld(world, true);
        loadWorld(world, true);
        worlds.put(world.getName(), world);
        world.save();
    }

    protected void registerWorld(AWorld world, boolean newWorld) throws AWorldException {
        world.providerId = getWorldProviderId(world.provider);
        world.worldTypeObj = getWorldTypeByName(world.worldType);

        if (newWorld) {
            world.dimensionId = DimensionManager.getNextFreeDimId();
            world.name += "-" + world.dimensionId;
        }

        DimensionManager.registerDimension(world.dimensionId, world.providerId);
    }

    public void loadWorld(AWorld world) {
        try {
            registerWorld(world, false);
            loadWorld(world, false);
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

    protected void loadWorld(AWorld world, boolean newWorld) {
        if (world.worldLoaded)
            return;
        try {
            // Initialize worlds settings
            MinecraftServer mcServer = MinecraftServer.getServer();
            WorldServer overworld = DimensionManager.getWorld(0);
            if (overworld == null)
                throw new RuntimeException("Cannot hotload dim: Overworld is not Loaded!");
            ISaveHandler savehandler = new AWorldSaveHandler(overworld.getSaveHandler(), world);
            WorldSettings worldSettings = new WorldSettings(
                    world.seed,
                    WorldSettings.GameType.SURVIVAL,
                    world.structures,
                    false,
                    world.worldTypeObj)
                    .func_82750_a(world.generator);
            // Create WorldServer with settings
            WorldServer worldServer = new AWorldServer(mcServer, savehandler,
                    overworld.getWorldInfo().getWorldName(), world.dimensionId, worldSettings,
                    overworld, mcServer.theProfiler, world);
            // Overwrite dimensionId because WorldProviderEnd for example just hardcodes the dimId
            worldServer.provider.dimensionId = world.dimensionId;

            if (newWorld)
                world.setSpawn(
                        worldServer.getSpawnPoint().posX,
                        worldServer.getSpawnPoint().posY,
                        worldServer.getSpawnPoint().posZ
                );
            else
                world.setSpawn();

            //worldServer.provider.setS
            worldServer.addWorldAccess(new WorldManager(mcServer, worldServer));
            if (!mcServer.isSinglePlayer())
                worldServer.getWorldInfo().setGameType(mcServer.getGameType());
            // SetDifficulty
            mcServer.func_147139_a(mcServer.func_147135_j());
            world.updateWorldSettings();
            world.worldLoaded = true;
            world.worldLoadIt = true;
            world.error = false;

            // Post WorldEvent.Load
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(worldServer));

            // Tell everyone about the new dim
            FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
            ForgeMessage.DimensionRegisterMessage msg = new ForgeMessage.DimensionRegisterMessage(world.dimensionId, world.providerId);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
            channel.writeOutbound(msg);

            // Post WorldEvent.Load
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(worldServer));
        } catch (Exception e) {
            world.error = true;
            throw e;
        }
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

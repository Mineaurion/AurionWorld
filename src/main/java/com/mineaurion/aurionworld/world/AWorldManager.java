package com.mineaurion.aurionworld.world;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.misc.output.Log;
import com.mineaurion.aurionworld.core.models.WorldModel;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
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

    protected ArrayList<WorldServer> worldsToUnregister = new ArrayList<>();
    protected ArrayList<WorldServer> worldsToDelete = new ArrayList<>();


    protected Map<String, Integer> worldProviderClasses = new HashMap<>();
    protected Map<String, WorldType> worldTypes = new HashMap<>();

    /**
     * Event handler for new clients that need to know about our worlds
     */
    protected AWorldEventHandler eventHandler = new AWorldEventHandler(this);

    public AWorldManager() {

    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START)
            return;

        unregisterWorldsInQueue();
        deleteWorldsInQueue();
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
    // Worlds management
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

        Map<String, AWorld> worldsToLoad = loadAllWorldFromDb();
        for (AWorld world : worldsToLoad.values()) {
            worlds.put(world.getName(), world);
            try {
                registerWorld(world);
                if (world.worldLoadIt)
                    loadWorld(world, false);
            } catch (AWorldException e) {
                Log.error(e.getMessage());
            }
        }
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
    // World Global management

    public Collection<AWorld> getWorlds() {
        return worlds.values();
    }

    public Optional<AWorld> getWorld(String name) {
        AWorld world = worlds.get(name);
        return Optional.ofNullable(world);
    }

    public Optional<AWorld> getWorld(Integer dimId) {
        for (AWorld world : getWorlds()) {
            if (world.dimensionId == dimId)
                return Optional.of(world);
        }
        return Optional.empty();
    }

    public void addWorld(AWorld world) throws AWorldException {
        registerWorld(world);
        loadWorld(world, true);
        worlds.put(world.getName(), world);
        world.save();
    }

    protected void registerWorld(AWorld world) throws AWorldException {
        world.providerId = getWorldProviderId(world.provider);
        world.worldTypeObj = getWorldTypeByName(world.worldType);

        if (world.isNew) {
            world.dimensionId = DimensionManager.getNextFreeDimId();
            world.name += "-" + world.dimensionId;
            world.isNew = false;
        }

        DimensionManager.registerDimension(world.dimensionId, world.providerId);
    }

    public void loadWorld(AWorld world, boolean newWorld) {
        if (world.worldLoaded)
            return;
        try {
            // Initialize worlds settings
            MinecraftServer mcServer = MinecraftServer.getServer();
            WorldServer overworld = DimensionManager.getWorld(0);
            if (overworld == null)
                throw new AWorldException("Cannot hotload dim: Overworld is not Loaded!");
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
        } catch (Exception e) {
            world.error = true;
            throw e;
        }
    }

    public void unloadWorld(AWorld world, boolean unregisterIt) {
        world.worldLoaded = false;
        world.worldLoadIt = false;
        world.removeAllPlayersFromWorld();
        DimensionManager.unloadWorld(world.getDimensionId());
        if (unregisterIt) {
            worldsToUnregister.add(DimensionManager.getWorld(world.getDimensionId()));
            worlds.remove(world.getName());
        }
        Log.info("World " + world.getName() + " is now unloaded! All players has been teleported at overworld's spawn");
        world.save();
    }

    public void deleteWorld(AWorld world) {
        unloadWorld(world, true);
        world.delete();
        worldsToDelete.add(DimensionManager.getWorld(world.getDimensionId()));
    }

    public void unregisterWorldsInQueue() {
        for (Iterator<WorldServer> it = worldsToUnregister.iterator(); it.hasNext(); ) {
            WorldServer world = it.next();
            if (DimensionManager.getWorld(world.provider.dimensionId) == null) {
                if (DimensionManager.isDimensionRegistered(world.provider.dimensionId))
                    DimensionManager.unregisterDimension(world.provider.dimensionId);
                it.remove();
            }

        }
    }

    public void deleteWorldsInQueue() {
        for (Iterator<WorldServer> it = worldsToDelete.iterator(); it.hasNext(); ) {
            WorldServer world = it.next();
            // Check with DimensionManager, whether the world is still loaded
            if (DimensionManager.getWorld(world.provider.dimensionId) == null) {
                try {
                    if (DimensionManager.isDimensionRegistered(world.provider.dimensionId))
                        DimensionManager.unregisterDimension(world.provider.dimensionId);

                    File p = world.getChunkSaveLocation();
                    FileUtils.deleteDirectory(p);

                } catch (IOException e) {
                    Log.warn(e.getMessage());
                    Log.warn("Error deleting dimension files");
                }
                it.remove();
            }
        }
    }

    public Collection<AWorld> getPlayerWorlds(UUID uuid) {
        Collection<AWorld> results = new ArrayList<>();

        for (AWorld world : worlds.values()) {
            if (world.ownerUuid.equals(uuid))
                results.add(world);
        }
        return results;
    }
    // ============================================================
    // WorldProvider management

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
                    throw new AWorldException(String.format(AWorldException.NO_PROVIDER, providerName));
                return providerId;
        }
    }

    public Map<String, Integer> getWorldProviders() {
        return worldProviderClasses;
    }

    // ============================================================
    // WorldType management

    public WorldType getWorldTypeByName(String worldType) throws AWorldException {
        WorldType type = worldTypes.get(worldType.toUpperCase());
        if (type == null)
            throw new AWorldException(String.format(AWorldException.NO_WORLDTYPE, worldType));
        return type;
    }

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

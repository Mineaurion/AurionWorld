package com.mineaurion.aurionworld.world;

import com.mineaurion.aurionworld.core.Log;
import com.mineaurion.aurionworld.core.misc.WorldUtil;
import com.mineaurion.aurionworld.core.misc.point.WarpPoint;
import com.mineaurion.aurionworld.core.misc.teleporter.TeleportHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;

import java.util.Random;

public class AWorld {
    protected int dimensionId;

    protected String name;
    protected String provider;
    protected String worldType;
    protected long seed;

    protected boolean mapFeaturesEnabled = true;

    protected int providerId;
    protected WorldType worldTypeObj;

    protected boolean worldLoaded = false;
    protected boolean error;

    public AWorld(String name, String provider, String worldType, long seed) {
        this.name = name;
        this.provider = provider;
        this.worldType = worldType;
        this.seed = seed;
    }

    public AWorld(String name, String provider, String worldType) {
        this(name, provider, worldType, new Random().nextLong());
    }

    protected void save() {
        //DataManager.getInstance().save(this, this.name);
        //TODO: save with Model World in DB
        Log.info("Save worlds " + name + " (" + dimensionId + ")");
    }

    public void updateWorldSettings() {
        if (!worldLoaded)
            return;
        //WorldServer worldServer = getWorldServer();
        // worldServer.difficultySetting = difficulty;
        // worldServer.setAllowedSpawnTypes(allowHostileCreatures, allowPeacefulCreatures);
    }

    // ============================================================
    // Teleporter Management

    /**
     * Teleport the player to the multiworld
     */
    public void teleport(EntityPlayerMP player, boolean instant)
    {
        teleport(player, getWorldServer(), instant);
    }

    /**
     * Teleport the player to the multiworld
     */
    public static void teleport(EntityPlayerMP player, WorldServer world, boolean instant)
    {
        teleport(player, world, player.posX, player.posY, player.posZ, instant);
    }

    /**
     * Teleport the player to the multiworld
     */
    public static void teleport(EntityPlayerMP player, WorldServer world, double x, double y, double z, boolean instant)
    {
        boolean worldChange = player.worldObj.provider.dimensionId != world.provider.dimensionId;
        if (worldChange)
            displayDepartMessage(player);

        y = WorldUtil.placeInWorld(world, (int) x, (int) y, (int) z);
        WarpPoint target = new WarpPoint(world.provider.dimensionId, x, y, z, player.rotationPitch, player.rotationYaw);
        if (instant)
            TeleportHelper.checkedTeleport(player, target);
        else
            TeleportHelper.teleport(player, target);

        if (worldChange)
            displayWelcomeMessage(player);
    }

    public static void displayDepartMessage(EntityPlayerMP player)
    {
        // String msg = player.worldObj.provider.getDepartMessage();
        // if (msg == null)
        // msg = "Leaving the Overworld.";
        // if (player.dimension > 1 || player.dimension < -1)
        // msg += " (#" + player.dimension + ")";
        // ChatOutputHandler.sendMessage(player, new ChatComponentText(msg));
    }

    public static void displayWelcomeMessage(EntityPlayerMP player)
    {
        // String msg = player.worldObj.provider.getWelcomeMessage();
        // if (msg == null)
        // msg = "Entering the Overworld.";
        // if (player.dimension > 1 || player.dimension < -1)
        // msg += " (#" + player.dimension + ")";
        // ChatOutputHandler.sendMessage(player, new ChatComponentText(msg));
    }

    // ============================================================
    // Getters and Setters

    public WorldServer getWorldServer() {
        return MinecraftServer.getServer().worldServerForDimension(dimensionId);
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(int dimensionId) {
        this.dimensionId = dimensionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getWorldType() {
        return worldType;
    }

    public void setWorldType(String worldType) {
        this.worldType = worldType;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public boolean isMapFeaturesEnabled() {
        return mapFeaturesEnabled;
    }

    public void setMapFeaturesEnabled(boolean mapFeaturesEnabled) {
        this.mapFeaturesEnabled = mapFeaturesEnabled;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public WorldType getWorldTypeObj() {
        return worldTypeObj;
    }

    public void setWorldTypeObj(WorldType worldTypeObj) {
        this.worldTypeObj = worldTypeObj;
    }

    public boolean isWorldLoaded() {
        return worldLoaded;
    }

    public void setWorldLoaded(boolean worldLoaded) {
        this.worldLoaded = worldLoaded;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}

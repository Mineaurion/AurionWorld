package com.mineaurion.aurionworld.world;

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

    protected boolean worldLoaded;
    protected boolean error;

    public AWorld(String name, String provider, String worldType, long seed) {
        this.name = name;
        this.provider = provider;
        this.worldType = worldType;
        this.seed = seed;
    }

    public AWorld(String name, String provider, String worldType)
    {
        this(name, provider, worldType, new Random().nextLong());
    }

    public void save() {

    }

    public void updateWorldSettings()
    {
        if (!worldLoaded)
            return;
        //WorldServer worldServer = getWorldServer();
        // worldServer.difficultySetting = difficulty;
        // worldServer.setAllowedSpawnTypes(allowHostileCreatures, allowPeacefulCreatures);
    }

    public WorldServer getWorldServer()
    {
        return MinecraftServer.getServer().worldServerForDimension(dimensionId);
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
}

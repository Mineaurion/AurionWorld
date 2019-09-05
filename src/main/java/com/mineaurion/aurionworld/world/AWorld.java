package com.mineaurion.aurionworld.world;

import com.mineaurion.aurionworld.AurionWorld;
import com.mineaurion.aurionworld.core.misc.output.Log;
import com.mineaurion.aurionworld.core.misc.WorldUtil;
import com.mineaurion.aurionworld.core.misc.point.WarpPoint;
import com.mineaurion.aurionworld.core.misc.point.WorldPoint;
import com.mineaurion.aurionworld.core.misc.teleporter.TeleportHelper;
import com.mineaurion.aurionworld.core.models.WorldMemberModel;
import com.mineaurion.aurionworld.core.models.WorldModel;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;

import java.util.ArrayList;
import java.util.UUID;

public class AWorld {
    public WorldModel model;

    public static int LEVEL_MEMBER = 1;
    public static int LEVEL_MEMBER_OWNER = 2;

    // Model Attributes
    protected int id;
    protected String name;
    protected int dimensionId;
    protected UUID ownerUuid;
    protected String worldType;
    protected String provider;
    protected long seed;
    protected String generator;
    protected boolean structures;
    protected boolean worldLoadIt;
    protected WorldPoint spawnPoint = null;
    protected ArrayList<WorldMemberModel> members = new ArrayList<>();


    // Helper
    protected boolean worldLoaded = false;
    protected int providerId;
    protected WorldType worldTypeObj;

    protected boolean error;

    public AWorld(String name, UUID ownerUuid, String provider, String worldType, long seed, String generator, boolean structures) {
        this.model = new WorldModel();

        this.name = name;
        this.ownerUuid = ownerUuid;
        this.provider = provider;
        this.worldType = worldType;
        this.seed = seed;
        this.generator = generator;
        this.structures = structures;
        this.worldLoadIt = true;
    }

    public AWorld(WorldModel model) {
        this.model = model;

        this.id = (Integer) model.get("id");
        this.dimensionId = (Integer) model.get("dimension_id");
        this.name = (String) model.get("name");
        this.ownerUuid = UUID.fromString((String)model.get("owner_uuid"));
        this.provider = (String) model.get("provider");
        this.worldType = (String) model.get("type");
        this.seed = Long.parseLong((String)model.get("seed"));
        this.generator = (String) model.get("generator");
        this.structures = (Integer) model.get("structures") == 1;
        this.worldLoadIt = (Integer) model.get("load_it") == 1;
    }

    public void setSpawn(int x, int y, int z) {
        this.spawnPoint = new WorldPoint(getWorldServer(), x, y, z);
        getWorldServer().setSpawnLocation(
            spawnPoint.getX(),
            spawnPoint.getY(),
            spawnPoint.getZ()
        );
    }

    public void setSpawn() {
        this.spawnPoint = new WorldPoint(
                getWorldServer(),
                (Integer)model.get("spawn_x"),
                (Integer)model.get("spawn_y"),
                (Integer)model.get("spawn_z")
        );
        getWorldServer().setSpawnLocation(
                spawnPoint.getX(),
                spawnPoint.getY(),
                spawnPoint.getZ()
        );
    }

    public void save() {
        // Set all Attribute Model
        this.model.set("name", name);
        this.model.set("dimension_id", dimensionId);
        this.model.set("owner_uuid", ownerUuid.toString());
        this.model.set("type", worldType);
        this.model.set("provider", provider);
        this.model.set("seed", seed);
        this.model.set("generator", generator);
        this.model.set("structures", (structures) ? 1 : 0);
        this.model.set("load_it", (worldLoadIt) ? 1 : 0);
        this.model.set("spawn_x", spawnPoint.getX());
        this.model.set("spawn_y", spawnPoint.getY());
        this.model.set("spawn_z", spawnPoint.getZ());
        // Database save
        this.model.save();
        Log.info("Save world " + name + " (" + dimensionId + ") in database");
    }

    public void delete() {
        this.model.delete();
        Log.info("World " + name + " has been deleted!");
    }

    public void updateWorldSettings() {
        if (!worldLoaded)
            return;
    }

    public void removeAllPlayersFromWorld() {
        WorldServer overworld = MinecraftServer.getServer().worldServerForDimension(0);
        for (EntityPlayerMP player : AurionWorld.getPlayerList()) {
            if (player.dimension == dimensionId) {
                teleport(player, overworld, true);
            }
        }
    }

    // ============================================================
    // Teleporter Management

    public void teleport(EntityPlayerMP player, boolean instant) {
        teleport(player, getWorldServer(), instant);
    }

    public void teleport(EntityPlayerMP player, WorldServer world, boolean instant) {
        teleport(player, world, world.getSpawnPoint().posX, world.getSpawnPoint().posY, world.getSpawnPoint().posZ, instant);
    }

    public static void teleport(EntityPlayerMP player, WorldServer world, double x, double y, double z, boolean instant) {
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

    public static void displayDepartMessage(EntityPlayerMP player) {
        // String msg = player.worldObj.provider.getDepartMessage();
        // if (msg == null)
        // msg = "Leaving the Overworld.";
        // if (player.dimension > 1 || player.dimension < -1)
        // msg += " (#" + player.dimension + ")";
        // ChatOutputHandler.sendMessage(player, new ChatComponentText(msg));
    }

    public static void displayWelcomeMessage(EntityPlayerMP player) {
        // String msg = player.worldObj.provider.getWelcomeMessage();
        // if (msg == null)
        // msg = "Entering the Overworld.";
        // if (player.dimension > 1 || player.dimension < -1)
        // msg += " (#" + player.dimension + ")";
        // ChatOutputHandler.sendMessage(player, new ChatComponentText(msg));
    }

    // ============================================================
    // Member Management
    public boolean isOwner(UUID uuid) {
        return true;
    }

    public boolean isMember(UUID uuid) {
        return true;
    }

    public boolean isUniqueOwner(UUID uuid) {
        return (uuid).equals(this.ownerUuid);
    }

    // ============================================================
    // Permission Actions Management
    public boolean canDoMemberAction(ICommandSender sender, boolean beInside) {
        if (AurionWorld.isServer(sender) && !beInside)
            return true;
        if (!AurionWorld.isPlayer(sender))
            return false;

        EntityPlayerMP player = (EntityPlayerMP)sender;
        if (AurionWorld.isOp(sender))
            return true;

        return isMember(player.getUniqueID());
    }

    public boolean canDoOwnerAction(ICommandSender sender, boolean beInside) {
        if (AurionWorld.isServer(sender) && !beInside)
            return true;
        if (!AurionWorld.isPlayer(sender))
            return false;
        EntityPlayerMP player = (EntityPlayerMP)sender;
        if (beInside && isInside(player))
        if (AurionWorld.isOp(sender))
            return true;

        return isOwner(player.getUniqueID());
    }

    public boolean isInside(EntityPlayerMP player) {
        return player.dimension == this.dimensionId;
    }


    // ============================================================
    // Getters and Setters

    public WorldServer getWorldServer() {
        return MinecraftServer.getServer().worldServerForDimension(dimensionId);
    }

    public int getDimensionId() {
        return dimensionId;
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

    public String getWorldType() {
        return worldType;
    }

    public boolean isLoaded() {
        return worldLoaded;
    }
}

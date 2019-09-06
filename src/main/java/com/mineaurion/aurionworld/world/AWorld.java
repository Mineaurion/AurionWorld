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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AWorld {
    public WorldModel model;


    // Model Attributes
    protected int id;

    protected String name;
    protected int dimensionId;
    protected UUID ownerUuid;
    protected String worldType;
    protected String provider;
    protected boolean isNew = false;


    protected long seed;
    protected String generator;
    protected boolean structures;
    protected boolean worldLoadIt;
    protected WorldPoint spawnPoint = null;
    protected HashMap<UUID, AWorldMember> members;

    // Helper
    protected boolean worldLoaded = false;
    protected int providerId;
    protected WorldType worldTypeObj;

    protected boolean error;

    public AWorld(String name, UUID ownerUuid, String provider, String worldType, long seed, String generator, boolean structures) {
        this.model = new WorldModel();
        isNew = true;

        this.name = name;
        this.ownerUuid = ownerUuid;
        this.provider = provider;
        this.worldType = worldType;
        this.seed = seed;
        this.generator = generator;
        this.structures = structures;
        worldLoadIt = true;
    }

    public AWorld(WorldModel model) {
        this.model = model;
        isNew = false;

        id = (Integer) model.get("id");
        dimensionId = (Integer) model.get("dimension_id");
        name = (String) model.get("name");
        ownerUuid = UUID.fromString((String) model.get("owner_uuid"));
        provider = (String) model.get("provider");
        worldType = (String) model.get("type");
        seed = Long.parseLong((String) model.get("seed"));
        generator = (String) model.get("generator");
        structures = (Integer) model.get("structures") == 1;
        worldLoadIt = (Integer) model.get("load_it") == 1;
    }

    protected HashMap<String, AWorldMember> loadWorldMembersFromDb() {
        List<WorldMemberModel> worldMembersModel = WorldMemberModel.findAll();
        HashMap<String, AWorldMember> results = new HashMap<>();
        for (WorldMemberModel wmm : worldMembersModel) {
            results.put(wmm.getString("name"), new AWorldMember(wmm));
        }
        return results;
    }

    public void setSpawn(int x, int y, int z) {
        spawnPoint = new WorldPoint(getWorldServer(), x, y, z);
        getWorldServer().setSpawnLocation(
                spawnPoint.getX(),
                spawnPoint.getY(),
                spawnPoint.getZ()
        );
    }

    public void setSpawn() {
        spawnPoint = new WorldPoint(
                getWorldServer(),
                (Integer) model.get("spawn_x"),
                (Integer) model.get("spawn_y"),
                (Integer) model.get("spawn_z")
        );
        getWorldServer().setSpawnLocation(
                spawnPoint.getX(),
                spawnPoint.getY(),
                spawnPoint.getZ()
        );
    }

    public void save() {
        // Set all Attribute Model
        model.set("name", name);
        model.set("dimension_id", dimensionId);
        model.set("owner_uuid", ownerUuid.toString());
        model.set("type", worldType);
        model.set("provider", provider);
        model.set("seed", seed);
        model.set("generator", generator);
        model.set("structures", (structures) ? 1 : 0);
        model.set("load_it", (worldLoadIt));
        model.set("spawn_x", spawnPoint.getX());
        model.set("spawn_y", spawnPoint.getY());
        model.set("spawn_z", spawnPoint.getZ());
        // Database save
        model.save();
        Log.info("Save world " + name + " (" + dimensionId + ") in database");
    }

    public void delete() {
        model.delete();
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

    public boolean isStructures() {
        return structures;
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

        y = WorldUtil.placeInWorld(world, (int) x, (int) y, (int) z);
        WarpPoint target = new WarpPoint(world.provider.dimensionId, x, y, z, player.rotationPitch, player.rotationYaw);
        if (instant)
            TeleportHelper.checkedTeleport(player, target);
        else
            TeleportHelper.teleport(player, target);
    }

    // ============================================================
    // Trust Management


    public boolean isUniqueOwner(UUID uuid) {
        return (uuid).equals(ownerUuid);
    }

    public boolean isMember(UUID uuid) {
        return members.containsKey(uuid) || isUniqueOwner(uuid);
    }

    public boolean isMemberOwner(UUID uuid) {
        AWorldMember member = members.get(uuid);
        if (member == null) {
            return isUniqueOwner(uuid);
        }

        return (member.getLevel() == AWorldMember.TRUST_OWNER);
    }

    public void addMember(UUID uuid, int level) {
        WorldMemberModel wmm = new WorldMemberModel();
        wmm.set("world_id", id);
        wmm.set("uuid", uuid);
        wmm.set("level", level);
        wmm.save();

        AWorldMember member = new AWorldMember(wmm);
        members.put(uuid, member);
    }

    public void removeMember(UUID uuid) {
        AWorldMember wmm = members.get(uuid);
        if (wmm != null) {
            wmm.delete();
            members.remove(uuid);
        }
    }

    private void clearMembers() {
        members.forEach((uuid, member) -> member.delete());
        members.clear();
    }


    // ============================================================
    // Permission Actions Management
    public boolean canDoMemberAction(ICommandSender sender, boolean beInside) {
        if (AurionWorld.isServer(sender) && !beInside)
            return true;
        if (!AurionWorld.isPlayer(sender))
            return false;

        EntityPlayerMP player = (EntityPlayerMP) sender;
        if (AurionWorld.isOp(sender))
            return true;

        return isMember(player.getUniqueID());
    }

    public boolean canDoOwnerAction(ICommandSender sender, boolean beInside) {
        if (AurionWorld.isServer(sender) && !beInside)
            return true;
        if (!AurionWorld.isPlayer(sender))
            return false;

        EntityPlayerMP player = (EntityPlayerMP) sender;
        if (beInside && isInside(player))
            if (AurionWorld.isOp(sender))
                return true;

        return isMemberOwner(player.getUniqueID());
    }

    public boolean isInside(EntityPlayerMP player) {
        return player.dimension == dimensionId;
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

    public long getSeed() {
        return seed;
    }

    public String getGenerator() {
        return generator;
    }

    public boolean isLoaded() {
        return worldLoaded;
    }

    public boolean isLoadIt() {
        return worldLoadIt;
    }

    public HashMap<UUID, AWorldMember> getMembers() {
        return members;
    }

    public Optional<AWorldMember> getMember(UUID uuid) {
        AWorldMember member = members.get(uuid);
        return Optional.ofNullable(member);
    }

    public int getId() {
        return id;
    }
}

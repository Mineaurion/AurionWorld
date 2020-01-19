package com.mineaurion.aurionworld.world;

import com.mineaurion.aurionworld.core.misc.output.Log;
import com.mineaurion.aurionworld.core.models.WorldMemberModel;

import java.math.BigInteger;
import java.util.UUID;

public class AWorldMember {
    private WorldMemberModel model;

    public static int TRUST_MEMBER = 1;
    public static int TRUST_OWNER = 2;

    protected int id;
    protected int worldId;
    protected UUID uuid;
    protected int level;
    protected boolean isNew;
    public AWorld world;

    // When add new member
    public AWorldMember(AWorld world, UUID uuid, int level) {
        model = new WorldMemberModel();
        isNew = true;

        this.world = world;
        this.worldId = this.world.getId();
        this.uuid = uuid;
        this.level = level;
        save();
    }

    // When load from DB
    public AWorldMember(AWorld world, WorldMemberModel wmm) {
        model = wmm;
        isNew = false;

        this.world = world;
        id = model.getInteger("id");
        worldId = model.getInteger("world_id");
        uuid = UUID.fromString(model.getString("uuid"));
        level = model.getInteger("level");
    }

    public void save() {
        model.setInteger("world_id", world.getId());
        model.setString("uuid", uuid.toString());
        model.setInteger("level", level);
        model.save();
        if (isNew) {
            id = model.getInteger("id");
            isNew = false;
        }
        Log.info(uuid.toString() + " has been added to " + world.getName() + " world!");
    }

    public void delete() {
        model.delete();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

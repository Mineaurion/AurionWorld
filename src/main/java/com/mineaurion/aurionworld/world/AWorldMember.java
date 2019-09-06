package com.mineaurion.aurionworld.world;

import com.mineaurion.aurionworld.core.models.WorldMemberModel;

import java.util.UUID;

public class AWorldMember {
    private WorldMemberModel model;

    public static int TRUST_MEMBER = 1;
    public static int TRUST_OWNER = 2;

    private int id;
    private int worldId;
    private UUID uuid;
    private int level;

    public AWorld world;

    public AWorldMember(WorldMemberModel wmm) {
        model = wmm;

        id = model.getInteger("id");
        worldId = model.getInteger("world_id");
        uuid = UUID.fromString(model.getString("uuid"));
        level = model.getInteger("level");
    }

    public AWorldMember(AWorld world, UUID uuid, int level) {
        model = new WorldMemberModel();
        this.world = world;
        this.worldId = this.world.getId();
        this.uuid = uuid;
        this.level = level;

        model.save();
    }

    public void save() {
        model.setInteger("world_id", world.getId());
        model.setString("uuid", uuid.toString());
        model.setInteger("level", level);
        model.save();
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

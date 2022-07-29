package me.limeglass.beamer.protocol;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.google.common.collect.Lists;

public class PacketFactory_1_19_R1 implements IPacketFactory {

	private static Entity fakeSquid;
	private static Entity fakeGuardian;

	static {
		fakeSquid = (Entity) Accessors.getConstructorAccessor(
				MinecraftReflection.getCraftBukkitClass("entity.CraftSquid"),
				MinecraftReflection.getCraftBukkitClass("CraftServer"),
				MinecraftReflection.getMinecraftClass("world.entity.animal.EntitySquid")
		).invoke(Bukkit.getServer(), Accessors.getConstructorAccessor(
				MinecraftReflection.getMinecraftClass("world.entity.animal.EntitySquid"),
				MinecraftReflection.getMinecraftClass("world.entity.EntityTypes"),
				MinecraftReflection.getNmsWorldClass()
		).invoke(new Object[] {BukkitConverters.getEntityTypeConverter().getGeneric(EntityType.SQUID),
				BukkitUnwrapper.getInstance().unwrapItem(Bukkit.getWorlds().get(0))}));

		fakeGuardian = (Entity) Accessors.getConstructorAccessor(
				MinecraftReflection.getCraftBukkitClass("entity.CraftGuardian"),
				MinecraftReflection.getCraftBukkitClass("CraftServer"),
				MinecraftReflection.getMinecraftClass("world.entity.monster.EntityGuardian")
		).invoke(Bukkit.getServer(), Accessors.getConstructorAccessor(
				MinecraftReflection.getMinecraftClass("world.entity.monster.EntityGuardian"),
				MinecraftReflection.getMinecraftClass("world.entity.EntityTypes"),
				MinecraftReflection.getNmsWorldClass()
				).invoke(new Object[] {BukkitConverters.getEntityTypeConverter().getGeneric(EntityType.GUARDIAN),
						BukkitUnwrapper.getInstance().unwrapItem(Bukkit.getWorlds().get(0))}));
	}

	public WrappedBeamPacket createPacketSquidSpawn(Location location) {
		PacketContainer container = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
		int entityID = EIDGen.generateEID();
		container.getIntegers().write(0, entityID);
		container.getUUIDs().write(0, UUID.randomUUID());
		container.getEntityTypeModifier().write(0, EntityType.SQUID);
		container.getDoubles().write(0, location.getX());
		container.getDoubles().write(1, location.getY());
		container.getDoubles().write(2, location.getZ());
		container.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
		container.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));

		WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata();
		WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(fakeSquid);
		// Invisible
		watcher.setObject(0, Registry.get(Byte.class), (byte) 0x20);
		wrapper.setMetadata(watcher.getWatchableObjects());
		wrapper.setEntityID(entityID);
		return new WrappedBeamPacket(container, wrapper);
	}

	public WrappedBeamPacket createPacketGuardianSpawn(Location location, WrappedBeamPacket squidPacket) {
		PacketContainer container = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
		int entityID = EIDGen.generateEID();
		container.getIntegers().write(0, entityID);
		container.getUUIDs().write(0, UUID.randomUUID());
		container.getEntityTypeModifier().write(0, EntityType.GUARDIAN);
		container.getDoubles().write(0, location.getX());
		container.getDoubles().write(1, location.getY());
		container.getDoubles().write(2, location.getZ());
		container.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
		container.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));

		WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata();
		WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(fakeGuardian);
		// Invisible
		watcher.setObject(0, Registry.get(Byte.class), (byte) 0x20);
		// Is retracting spikes
		watcher.setObject(16, false);
		// Target EID
		watcher.setObject(17, squidPacket.getHandle().getIntegers().read(0));
		wrapper.setMetadata(watcher.getWatchableObjects());
		wrapper.setEntityID(entityID);
		return new WrappedBeamPacket(container, wrapper);
	}

	public WrappedBeamPacket modifyPacketEntitySpawn(WrappedBeamPacket entitySpawnPacket, Location location) {
		PacketContainer container = entitySpawnPacket.getHandle();
		container.getIntegers().write(2, (int) Math.floor(location.getX() * 32.0));
		container.getIntegers().write(3, (int) Math.floor(location.getY() * 32.0));
		container.getIntegers().write(4, (int) Math.floor(location.getZ() * 32.0));
		container.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
		container.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
		return entitySpawnPacket;
	}

	public WrappedBeamPacket createPacketEntityMove(WrappedBeamPacket entityPacket) {
		PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
		container.getIntegers().write(0, entityPacket.getHandle().getIntegers().read(0));
		return new WrappedBeamPacket(container);
	}

	public WrappedBeamPacket modifyPacketEntityMove(WrappedBeamPacket entityMovePacket, Location location) {
		PacketContainer container = entityMovePacket.getHandle();
		container.getIntegers().write(1, (int) Math.floor(location.getX() * 32.0D));
		container.getIntegers().write(2, (int) Math.floor(location.getY() * 32.0D));
		container.getIntegers().write(3, (int) Math.floor(location.getZ() * 32.0D));
		container.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
		container.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
		return entityMovePacket;
	}

	public WrappedBeamPacket createPacketRemoveEntities(WrappedBeamPacket squidPacket, WrappedBeamPacket guardianPacket) {
		PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
		container.getIntLists().write(0, Lists.newArrayList(
				squidPacket.getHandle().getIntegers().read(0),
				guardianPacket.getHandle().getIntegers().read(0)));
		return new WrappedBeamPacket(container);
	}

}

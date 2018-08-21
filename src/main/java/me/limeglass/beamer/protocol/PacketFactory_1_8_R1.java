/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Jaxon A Brown
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 *  persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 *  OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package me.limeglass.beamer.protocol;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import static com.comphenix.protocol.PacketType.Play.Server.*;

public class PacketFactory_1_8_R1 implements IPacketFactory {

	private static Entity fakeSquid;
	private static Entity fakeGuardian;

	static {
		fakeSquid = (Entity) Accessors.getConstructorAccessor(
				MinecraftReflection.getCraftBukkitClass("entity.CraftSquid"),
				MinecraftReflection.getCraftBukkitClass("CraftServer"),
				MinecraftReflection.getMinecraftClass("EntitySquid")
		).invoke(null, Accessors.getConstructorAccessor(
				MinecraftReflection.getMinecraftClass("EntitySquid"),
				MinecraftReflection.getNmsWorldClass()
		).invoke(new Object[] {null}));

		fakeGuardian = (Entity) Accessors.getConstructorAccessor(
				MinecraftReflection.getCraftBukkitClass("entity.CraftGuardian"),
				MinecraftReflection.getCraftBukkitClass("CraftServer"),
				MinecraftReflection.getMinecraftClass("EntityGuardian")
		).invoke(null, Accessors.getConstructorAccessor(
				MinecraftReflection.getMinecraftClass("EntityGuardian"),
				MinecraftReflection.getNmsWorldClass()
		).invoke(new Object[] {null}));
	}

	public WrappedBeamPacket createPacketSquidSpawn(Location location) {
		PacketContainer container = new PacketContainer(SPAWN_ENTITY_LIVING);
		container.getIntegers().write(0, EIDGen.generateEID());
		container.getIntegers().write(1, 94);
		container.getIntegers().write(2, (int) Math.floor(location.getX() * 32.0));
        container.getIntegers().write(3, (int) Math.floor(location.getY() * 32.0));
        container.getIntegers().write(4, (int) Math.floor(location.getZ() * 32.0));
		container.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
		container.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
		WrappedDataWatcher wrapper = WrappedDataWatcher.getEntityWatcher(fakeSquid);
		wrapper.setObject(0, (byte) 32);
		container.getDataWatcherModifier().write(0, wrapper);
		return new WrappedBeamPacket(container);
	}

	public WrappedBeamPacket createPacketGuardianSpawn(Location location, WrappedBeamPacket squidPacket) {
		PacketContainer container = new PacketContainer(SPAWN_ENTITY_LIVING);
		container.getIntegers().write(0, EIDGen.generateEID());
		container.getIntegers().write(1, 68);
		container.getIntegers().write(2, (int) Math.floor(location.getX() * 32.0));
        container.getIntegers().write(3, (int) Math.floor(location.getY() * 32.0));
        container.getIntegers().write(4, (int) Math.floor(location.getZ() * 32.0));
		container.getBytes().write(0, (byte) (location.getYaw() * 256.0F / 360.0F));
		container.getBytes().write(1, (byte) (location.getPitch() * 256.0F / 360.0F));
		WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(fakeGuardian);
		watcher.setObject(0, (byte) 32);
		watcher.setObject(16, 0);
        watcher.setObject(17, squidPacket.getHandle().getIntegers().read(0));
		container.getDataWatcherModifier().write(0, watcher);
		return new WrappedBeamPacket(container);
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
		PacketContainer container = new PacketContainer(ENTITY_TELEPORT);
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
		PacketContainer container = new PacketContainer(ENTITY_DESTROY);
		container.getIntegerArrays().write(0, new int[] {
				squidPacket.getHandle().getIntegers().read(0),
				guardianPacket.getHandle().getIntegers().read(0)
		});
		return new WrappedBeamPacket(container);
	}

}

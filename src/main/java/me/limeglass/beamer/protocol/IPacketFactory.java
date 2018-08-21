package me.limeglass.beamer.protocol;

import org.bukkit.Location;

public interface IPacketFactory {

	/**
	 * Creates a packet to spawn a squid at the location.
	 * @param location location to spawn the squid.
	 * @return beam packet used to spawn for players.
	 */
	public WrappedBeamPacket createPacketSquidSpawn(Location location);
	
	/**
	 * Creates a packet to move an entity. Doesn't include where to move it to.
	 * @param entityPacket SquidSpawn or GuardianSpawn packet for the entity.
	 * @return Skeleton packet for the given entity.
	 */
	public WrappedBeamPacket createPacketEntityMove(WrappedBeamPacket entityPacket);
	
	/**
	 * Creates a packet to spawn a guardian at the location.
	 * @param location location to spawn the guardian.
	 * @param squidPacket squid the guardian will target.
	 * @return beam packet used to spawn for players.
	 */
	public WrappedBeamPacket createPacketGuardianSpawn(Location location, WrappedBeamPacket squidPacket);
	
	/**
	 * Adds location information to a packet to move an entity.
	 * @param entityMovePacket EntityMove packet to add location information to.
	 * @param location location to move the entity to.
	 * @return Finished packet to teleport the given entity.
	 */
	public WrappedBeamPacket modifyPacketEntityMove(WrappedBeamPacket entityMovePacket, Location location);
	
	/**
	 * Modifies location information of the given Spawn Packet.
	 * @param entitySpawnPacket SquidSpawn or GuardianSpawn packet for the entity.
	 * @param location location the entity should be spawned at.
	 * @return beam packet used to spawn for players.
	 */
	public WrappedBeamPacket modifyPacketEntitySpawn(WrappedBeamPacket entitySpawnPacket, Location location);
	
	/**
	 * Creates a packet to remove the guardian and squid entities.
	 * @param squidPacket SquidSpawn of the entity to remove
	 * @param guardianPacket GuardianSpawn of the entity to remove
	 * @return Packet to remove the guardian and squid when sent to a player.
	 */
	public WrappedBeamPacket createPacketRemoveEntities(WrappedBeamPacket squidPacket, WrappedBeamPacket guardianPacket);

}

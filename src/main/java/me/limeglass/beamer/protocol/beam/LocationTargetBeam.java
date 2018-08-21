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
package me.limeglass.beamer.protocol.beam;

import com.google.common.base.Preconditions;

import me.limeglass.beamer.Beamer;
import me.limeglass.beamer.protocol.IPacketFactory;
import me.limeglass.beamer.protocol.WrappedBeamPacket;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Creates a guardian beam between two locations.
 * This uses ProtocolLib to send two entities: A guardian and a squid.
 * The guardian is then set to target the squid.
 * Be sure to run #cleanup for any players you #start.
 * @author Jaxon A Brown
 */
public class LocationTargetBeam {
	private final WrappedBeamPacket packetSquidSpawn;
	private final WrappedBeamPacket packetSquidMove;
	private final WrappedBeamPacket packetGuardianSpawn;
	private final WrappedBeamPacket packetGuardianMove;
	private final WrappedBeamPacket packetRemoveEntities;

	/**
	 * Create a guardian beam. This sets up the packets.
	 * @param startingPosition Position to start the beam, or the position which the effect 'moves towards'.
	 * @param endingPosition Position to stop the beam, or the position which the effect 'moves away from'.
	 */
	public LocationTargetBeam(Location startingPosition, Location endingPosition) {
		Preconditions.checkNotNull(startingPosition, "startingPosition cannot be null");
		Preconditions.checkNotNull(endingPosition, "endingPosition cannot be null");
		Preconditions.checkState(startingPosition.getWorld().equals(endingPosition.getWorld()), "startingPosition and endingPosition must be in the same world");
		
		IPacketFactory factory = Beamer.getInstance().getPacketFactory();
		this.packetSquidSpawn = factory.createPacketSquidSpawn(startingPosition);
		this.packetSquidMove = factory.createPacketEntityMove(this.packetSquidSpawn);
		this.packetGuardianSpawn = factory.createPacketGuardianSpawn(endingPosition, this.packetSquidSpawn);
		this.packetGuardianMove = factory.createPacketEntityMove(this.packetGuardianSpawn);
		this.packetRemoveEntities = factory.createPacketRemoveEntities(this.packetSquidSpawn, this.packetGuardianSpawn);
	}

	/**
	 * Send the packets to create the beam to the player.
	 * @param player player to whom the beam will be sent.
	 */
	public void start(Player player) {
		this.packetSquidSpawn.send(player);
		this.packetGuardianSpawn.send(player);
	}

	/**
	 * Sets the position of the beam which the effect 'moves away from'.
	 * @param player player who should receive the update. They MUST have been showed the beam already.
	 * @param location location of the new position.
	 */
	public void setStartingPosition(Player player, Location location) {
		IPacketFactory factory = Beamer.getInstance().getPacketFactory();
		factory.modifyPacketEntitySpawn(this.packetSquidSpawn, location);
		factory.modifyPacketEntityMove(this.packetSquidMove, location).send(player);
	}

	/**
	 * Sets the position of the beam which the effect 'moves towards'.
	 * @param player player who should receive the update. They MUST have been showed the beam already.
	 * @param location location of the new position.
	 */
	public void setEndingPosition(Player player, Location location) {
		IPacketFactory factory = Beamer.getInstance().getPacketFactory();
		factory.modifyPacketEntitySpawn(this.packetGuardianSpawn, location);
		factory.modifyPacketEntityMove(this.packetGuardianMove, location).send(player);
	}

	/**
	 * Cleans up the entities on the player's side.
	 * @param player player who needs the cleanup.
	 */
	public void cleanup(Player player) {
		this.packetRemoveEntities.send(player);
	}
}

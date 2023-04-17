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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import me.limeglass.beamer.Beamer;

public class Beam {

	protected final Set<Player> viewers = new HashSet<>();
	protected final Set<Player> playing = new HashSet<>();
	protected final LocationTargetBeam beam;
	protected Location starting, ending;
	protected final UUID worldUUID;
	protected final double radius;
	protected final long delay;
	protected BukkitTask task;
	protected boolean active;

	public Beam(Location startingPosition, Location endingPosition) {
		this(startingPosition, endingPosition, 100D, 5);
	}

	public Beam(Location starting, Location ending, double radius, long delay) {
		Preconditions.checkState(starting.getWorld().equals(ending.getWorld()), "starting position and ending position must be in the same world");
		Preconditions.checkArgument(delay >= 1, "update delay must be a natural number");
		Preconditions.checkArgument(radius > 0, "viewing radius must be positive");
		Preconditions.checkNotNull(starting, "starting position may not be null");
		Preconditions.checkNotNull(ending, "ending position may not be null");

		this.beam = new LocationTargetBeam(starting, ending);
		this.worldUUID = starting.getWorld().getUID();
		this.radius = radius * radius;
		this.starting = starting;
		this.ending = ending;
		this.delay = delay;
	}

	public double getRadius() {
		return radius;
	}

	public long getUpdateDelay() {
		return delay;
	}

	public Set<Player> getViewers() {
		return viewers;
	}

	public Set<Player> getPlaying() {
		return playing;
	}

	public Location getStartingPosition() {
		return starting;
	}

	public boolean hasViewers(Player... players) {
		for (Player player : players) {
			if (!viewers.contains(player)) return false;
		}
		return true;
	}

	public boolean isPlaying(Player player) {
		return playing.contains(player);
	}

	public void addViewers(Player... players) {
		viewers.addAll(Sets.newHashSet(players));
	}

	public void setStartingPosition(Location location) {
		Preconditions.checkArgument(location.getWorld().getUID().equals(worldUUID), "location must be in the same world as this beam");
		Iterator<Player> iterator = viewers.iterator();
		while (iterator.hasNext()) {
			Player player = iterator.next();
			if (!player.isOnline() || !player.getWorld().getUID().equals(worldUUID) || !isClose(player.getLocation())) {
				iterator.remove();
				continue;
			}
			beam.setStartingPosition(player, location);
		}
		this.starting = location;
	}

	public Location getEndingPosition() {
		return ending;
	}

	public void setEndingPosition(Location location) {
		Preconditions.checkArgument(location.getWorld().getUID().equals(worldUUID), "location must be in the same world as this beam");
		Iterator<Player> iterator = viewers.iterator();
		while (iterator.hasNext()) {
			Player player = iterator.next();
			if (!player.isOnline() || !player.getWorld().getUID().equals(worldUUID) || !isClose(player.getLocation())) {
				iterator.remove();
				continue;
			}
			beam.setEndingPosition(player, location);
		}
		this.ending = location;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void start() {
		Preconditions.checkState(!active, "The beam must be disabled in order to start it");
		this.active = true;
		this.task = Bukkit.getScheduler().runTaskTimer(Beamer.getInstance(), new Runnable() {
			@Override
			public void run() {
				update();
			}
		}, 0, delay);
	}

	public void stop() {
		Preconditions.checkState(active, "The beam must be enabled in order to stop it");
		active = false;
		for (Player player : viewers) {
			if (player.getWorld().getUID().equals(worldUUID) && isClose(player.getLocation())) {
				beam.cleanup(player);
			}
		}
		if (task != null) task.cancel();
		viewers.clear();
		playing.clear();
		task = null;
	}

	public void update() {
		if (!active) {
			task.cancel();
			return;
		}
		for (Player player : viewers) {
			if (!player.getWorld().getUID().equals(worldUUID)) {
				viewers.remove(player);
				beam.cleanup(player);
				return;
			}
			if (isClose(player.getLocation())) {
				if (!playing.contains(player)) {
					playing.add(player);
					beam.start(player);
				}
			} else if (playing.contains(player)) {
				playing.remove(player);
				beam.cleanup(player);
			}
		}
	}

	protected boolean isClose(Location location) {
		return starting.distanceSquared(location) <= radius || ending.distanceSquared(location) <= radius;
	}

}

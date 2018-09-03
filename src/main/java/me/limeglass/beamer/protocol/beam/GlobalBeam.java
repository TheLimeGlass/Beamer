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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.base.Preconditions;

import me.limeglass.beamer.Beamer;

public class GlobalBeam extends Beam {

	private BukkitTask playerTask;
	
	public GlobalBeam(Location starting, Location ending) {
		this(starting, ending, 100D, 5);
	}

	public GlobalBeam(Location starting, Location ending, double radius, long delay) {
		super(starting, ending, radius, delay);
		playerTask = Bukkit.getScheduler().runTaskTimer(Beamer.getInstance(), new Runnable() {
			@Override
			public void run() {
				viewers.addAll(Bukkit.getOnlinePlayers());
			}
		}, 0, getUpdateDelay());
	}
	
	@Override
	public void stop() {
		Preconditions.checkState(active, "The beam must be enabled in order to stop it");
		active = false;
		for (Player player : viewers) {
			if (player.getWorld().getUID().equals(worldUUID) && isClose(player.getLocation())) {
				beam.cleanup(player);
			}
		}
		if (playerTask != null) playerTask.cancel();
		if (task != null) task.cancel();
		viewers.clear();
		playing.clear();
		playerTask = null;
		task = null;
	}

}

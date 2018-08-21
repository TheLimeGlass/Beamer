package me.limeglass.beamer.elements.effects;

import org.bukkit.Location;
import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.beamer.lang.BeamerEffect;
import me.limeglass.beamer.protocol.beam.ClientBeam;
import me.limeglass.beamer.utils.annotations.Patterns;

@Name("Beam start and ending locations")
@Description("Set the starting or ending location of the beam.")
@Patterns("set [the] (1¦starting|2¦ending) (location|postions)[s] of [(all [[of] the]|the)] [guardian] [beam[s]] %beams% to %location%")
public class EffStartEndLocationBeam extends BeamerEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		for (ClientBeam beam : expressions.getAll(event, ClientBeam.class)) {
			if (patternMark == 1) {
				beam.setStartingPosition(expressions.getSingle(event, Location.class));
			} else {
				beam.setEndingPosition(expressions.getSingle(event, Location.class));
			}
		}
	}

}

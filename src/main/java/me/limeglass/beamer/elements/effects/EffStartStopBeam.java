package me.limeglass.beamer.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.beamer.lang.BeamerEffect;
import me.limeglass.beamer.protocol.beam.Beam;
import me.limeglass.beamer.utils.annotations.Patterns;

@Name("Beam start and stop")
@Description("Starts or stops a Guardian Beam.")
@Patterns("(1¦start|2¦stop) [guardian] [beam[s]] %beams%")
public class EffStartStopBeam extends BeamerEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		for (Beam beam : expressions.getAll(event, Beam.class)) {
			if (patternMark == 1) {
				beam.start();
			} else if (patternMark == 2) {
				beam.stop();
			}
		}
	}

}

package me.limeglass.beamer.elements.conditions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.beamer.lang.BeamerCondition;
import me.limeglass.beamer.protocol.beam.Beam;
import me.limeglass.beamer.utils.annotations.Patterns;

@Name("Beam - is active")
@Description("Check if the beam is active.")
@Patterns("%beam% (1¦is|2¦is(n't|not)) active")
public class CondBeamIsActive extends BeamerCondition {

	public boolean check(Event event) {
		if (areNull(event)) return !isNegated();
		return expressions.getSingle(event, Beam.class).isActive() ? isNegated() : isNegated();
	}

}

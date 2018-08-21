package me.limeglass.beamer.elements.conditions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.beamer.lang.BeamerCondition;
import me.limeglass.beamer.protocol.beam.ClientBeam;
import me.limeglass.beamer.utils.annotations.Patterns;

@Name("Beam - is viewing")
@Description("Check if the player is currently viewing the beam.")
@Patterns("%beam% (1¦is|2¦is(n't|not)) [currently] viewable")
public class CondBeamIsViewing extends BeamerCondition {

	public boolean check(Event event) {
		if (isNull(event, 0)) return !isNegated();
		return expressions.getSingle(event, ClientBeam.class).isViewing() ? isNegated() : isNegated();
	}

}

package me.limeglass.beamer.elements.expressions;

import org.bukkit.Location;
import org.bukkit.event.Event;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.beamer.lang.BeamerPropertyExpression;
import me.limeglass.beamer.protocol.beam.Beam;
import me.limeglass.beamer.utils.annotations.Changers;
import me.limeglass.beamer.utils.annotations.Properties;
import me.limeglass.beamer.utils.annotations.PropertiesAddition;

@Name("Beam start and ending locations")
@Description("Returns/Sets the starting or ending location of the beam.")
@Properties({"beams", "(1¦starting|2¦ending) (location|postions)[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[guardian] [beam[s]]")
@Changers(ChangeMode.SET)
public class ExprBeamLocation extends BeamerPropertyExpression<Beam, Location> {
	
	@Override
	protected Location[] get(Event event, Beam[] beams) {
		for (Beam beam : beams) {
			if (patternMark == 1) collection.add(beam.getStartingPosition());
			else collection.add(beam.getEndingPosition());
		}
		return collection.toArray(new Location[collection.size()]);
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		if (isNull(event) || delta == null) return;
		Location location = (Location) delta[0];
		for (Beam beam : expressions.getAll(event, Beam.class)) {
			if (patternMark == 1) {
				beam.setStartingPosition(location);
			} else {
				beam.setEndingPosition(location);
			}
		}
	}

}

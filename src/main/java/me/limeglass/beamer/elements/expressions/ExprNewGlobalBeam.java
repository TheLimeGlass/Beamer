package me.limeglass.beamer.elements.expressions;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.util.coll.CollectionUtils;
import me.limeglass.beamer.lang.BeamerExpression;
import me.limeglass.beamer.protocol.beam.GlobalBeam;
import me.limeglass.beamer.utils.annotations.Patterns;
import me.limeglass.beamer.utils.annotations.Single;

@Name("GuardianBeam - New beam")
@Description("Creates a new beam.")
@Patterns("[a] [new] [global] beam (between|from) %location% (to|and) %location% [[(with|in)] radius %-number% [and] update delay %-number%]")
@Single
public class ExprNewGlobalBeam extends BeamerExpression<GlobalBeam> {

	@Override
	@Nullable
	protected GlobalBeam[] get(Event event) {
		Location starting = expressions.getSingle(event, Location.class, 0);
		Location ending = expressions.getSingle(event, Location.class, 1);
		if (starting == null || ending == null) return null;
		Number radius = expressions.getSingle(event, Number.class, 0);
		Number update = expressions.getSingle(event, Number.class, 1);
		if (radius == null && update == null) {
			return CollectionUtils.array(new GlobalBeam(starting, ending));
		} else {
			return CollectionUtils.array(new GlobalBeam(starting, ending, radius.doubleValue(), update.longValue()));
		}
	}

}

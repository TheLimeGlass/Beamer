package me.limeglass.beamer.elements.expressions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.util.coll.CollectionUtils;
import me.limeglass.beamer.lang.BeamerExpression;
import me.limeglass.beamer.protocol.beam.ClientBeam;
import me.limeglass.beamer.utils.annotations.Patterns;
import me.limeglass.beamer.utils.annotations.Single;

@Name("GuardianBeam - new beam")
@Description("Creates a new beam.")
@Patterns("[a] [new] beam (between|from) %location% (to|and) %location% for %player% [[(with|in)] radius %-number% [and] update delay %-number%]")
@Single
public class ExprNewBeam extends BeamerExpression<ClientBeam> {

	@Override
	@Nullable
	protected ClientBeam[] get(Event event) {
		Location location1 = expressions.getSingle(event, Location.class, 0);
		Location location2 = expressions.getSingle(event, Location.class, 1);
		Player player = expressions.getSingle(event, Player.class);
		if (location1 == null || location2 == null || player == null) return null;
		Number radius = expressions.getSingle(event, Number.class, 0);
		Number update = expressions.getSingle(event, Number.class, 1);
		if (radius == null && update == null) {
			return CollectionUtils.array(new ClientBeam(player, location1, location2));
		} else {
			return CollectionUtils.array(new ClientBeam(player, location1, location2, radius.doubleValue(), update.longValue()));
		}
	}

}

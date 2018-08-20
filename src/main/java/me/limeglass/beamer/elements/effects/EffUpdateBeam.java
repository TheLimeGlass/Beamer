package me.limeglass.beamer.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.beamer.lang.BeamerEffect;
import me.limeglass.beamer.utils.annotations.Patterns;
import net.jaxonbrown.guardianBeam.beam.ClientBeam;

@Name("Beam update")
@Description("Update Guardian Beam.")
@Patterns("update [guardian] [beam[s]] %beams%")
public class EffUpdateBeam extends BeamerEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		for (ClientBeam beam : expressions.getAll(event, ClientBeam.class)) {
			beam.update();
		}
	}

}

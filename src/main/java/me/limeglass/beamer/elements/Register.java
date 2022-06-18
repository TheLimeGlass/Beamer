package me.limeglass.beamer.elements;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.beamer.Beamer;
import me.limeglass.beamer.Syntax;
import me.limeglass.beamer.utils.ReflectionUtil;
import me.limeglass.beamer.utils.annotations.Disabled;
import me.limeglass.beamer.utils.annotations.ExpressionProperty;
import me.limeglass.beamer.utils.annotations.Patterns;
import me.limeglass.beamer.utils.annotations.Properties;
import me.limeglass.beamer.utils.annotations.PropertiesAddition;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Register {
	
	static {
		run : for (Class clazz : ReflectionUtil.getClasses(Beamer.getInstance(), Beamer.getInstance().getPackageName())) {
			if (clazz.getName().contains("serverinstances") && !Beamer.getInstance().getConfiguration("config").getBoolean("ServerInstances", false)) continue run;
			if (!clazz.isAnnotationPresent(Disabled.class)) {
				String[] syntax = null;
				ExpressionType type = ExpressionType.COMBINED;
				if (clazz.isAnnotationPresent(Patterns.class)) {
					syntax = Syntax.register(clazz, ((Patterns)clazz.getAnnotation(Patterns.class)).value());
				} else if (PropertyExpression.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Properties.class)) {
					type = ExpressionType.PROPERTY;
					String[] properties = ((Properties)clazz.getAnnotation(Properties.class)).value();
					String additions = (clazz.isAnnotationPresent(PropertiesAddition.class)) ? " " + ((PropertiesAddition) clazz.getAnnotation(PropertiesAddition.class)).value() + " " : " ";
					String input1 = "[the] ", input2 = "";
					if (properties.length > 2 && properties[2] != null) {
						int var = Integer.parseInt(properties[2].substring(1, 2));
						if (var == 1) input1 = properties[2].substring(3, properties[2].length());
						else input2 = properties[2].substring(3, properties[2].length());
					}
					String[] values = new String[]{Beamer.getNameplate() + input1 + " " + properties[1] + " (of|from|in)" + additions + "%" + properties[0] + "%", Beamer.getNameplate() + input2 + "%" + properties[0] + "%'[s]"  + additions.replace("[the] ", "") + properties[1]};
					syntax = Syntax.register(clazz, values);
					if (syntax == null) Beamer.debugMessage("&cThere was an issue registering the syntax for " + clazz.getName() + ". Make sure that the SyntaxToggles.yml is set for this syntax.");
				} else {
					continue run;
				}
				if (syntax != null) {
					if (Effect.class.isAssignableFrom(clazz)) {
						Skript.registerEffect(clazz, syntax);
						Beamer.debugMessage("&5Registered Effect " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
					} else if (Condition.class.isAssignableFrom(clazz)) {
						Skript.registerCondition(clazz, syntax);
						Beamer.debugMessage("&5Registered Condition " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
					} else if (Expression.class.isAssignableFrom(clazz)) {
						if (clazz.isAnnotationPresent(ExpressionProperty.class)) type = ((ExpressionProperty) clazz.getAnnotation(ExpressionProperty.class)).value();
						try {
							Skript.registerExpression(clazz, ((Expression) clazz.getConstructor().newInstance()).getReturnType(), type, syntax);
							Beamer.debugMessage("&5Registered Expression " + type.toString() + " " + clazz.getSimpleName() + " (" + clazz.getCanonicalName() + ") with syntax " + Arrays.toString(syntax));
						} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
							Beamer.consoleMessage("&cFailed to register expression " + clazz.getCanonicalName());
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

}

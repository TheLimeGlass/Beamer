package me.limeglass.beamer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Version;
import me.limeglass.beamer.elements.Register;
import me.limeglass.beamer.protocol.IPacketFactory;
import me.limeglass.beamer.protocol.PacketFactory_1_12_R1;
import me.limeglass.beamer.protocol.PacketFactory_1_19_R1;
import me.limeglass.beamer.protocol.PacketFactory_1_8_R1;
import me.limeglass.beamer.protocol.beam.Beam;
import me.limeglass.beamer.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class Beamer extends JavaPlugin {
	
	private static Map<String, FileConfiguration> files = new HashMap<String, FileConfiguration>();
	private String packageName = "me.limeglass.beamer.elements";
	private static String prefix = "&7[&bBeamer&7] &9";
	private static String nameplate = "[Beamer] ";
	private static IPacketFactory factory;
	private static Beamer instance;
	private SkriptAddon addon;
	
	public void onEnable(){
		addon = Skript.registerAddon(this);
		instance = this;
		Classes.registerClass(new ClassInfo<Beam>(Beam.class, "beam"));
		saveDefaultConfig();
		File config = new File(getDataFolder(), "config.yml");
		if (!getDescription().getVersion().equals(getConfig().getString("version"))) {
			//Syntax changes
			if (getConfig().getString("version").equals("1.0.2") || getConfig().getString("version").equals("1.0.1") || getConfig().getString("version").equals("1.0.0")) {
				File file = new File(getDataFolder(), "syntax.yml");
				if (file.exists()) file.delete();
			}
			consoleMessage("&dNew update found! Updating files now...");
			if (config.exists()) new SpigotConfigSaver(this).execute();
		}
		for (String name : Arrays.asList("config", "syntax")) { //replace config with future files here
			File file = new File(getDataFolder(), name + ".yml");
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				saveResource(file.getName(), false);
			}
			FileConfiguration configuration = new YamlConfiguration();
			try {
				configuration.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
			files.put(name, configuration);
		}
		if (Skript.getMinecraftVersion().equals(new Version(1, 8))) {
			factory = new PacketFactory_1_8_R1();
		} else if (Skript.getMinecraftVersion().isSmallerThan(new Version(1, 8))) {
			consoleMessage("Guardian's don't exist in 1.7 silly, thus this addon is useless. Please update your Spigot version in order to use this addon.");
			this.setEnabled(false);
			return;
		} else if (Skript.getMinecraftVersion().equals(new Version(1, 9))) {
			consoleMessage("Beamer does not support 1.9 at the moment. Please update your Spigot version to latest 1.12 in order to use this addon.");
			this.setEnabled(false);
			return;
		} else if (Skript.getMinecraftVersion().equals(new Version(1, 12))) {
			factory = new PacketFactory_1_12_R1();
		// Future additions
//		} else if (Skript.getMinecraftVersion().isLargerThan(new Version(1, 17))) {
//			factory = new PacketFactory_1_19_R1();
		} else {
			factory = new PacketFactory_1_19_R1();
		}
		new Register();
		if (!getConfig().getBoolean("DisableRegisteredInfo", false)) Bukkit.getLogger().info(nameplate + "has been enabled!");
	}
	
	public SkriptAddon getAddonInstance() {
		return addon;
	}
	
	public IPacketFactory getPacketFactory() {
		return factory;
	}
	
	public static String getNameplate() {
		return nameplate;
	}
	
	public static Beamer getInstance() {
		return instance;
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	//Grabs a FileConfiguration of a defined name. The name can't contain .yml in it.
	public FileConfiguration getConfiguration(String file) {
		return (files.containsKey(file)) ? files.get(file) : null;
	}
	
	public static void save(String configuration) {
		try {
			File configurationFile = new File(instance.getDataFolder(), configuration + ".yml");
			instance.getConfiguration(configuration).save(configurationFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void debugMessage(@Nullable String... messages) {
		if (instance.getConfig().getBoolean("debug")) {
			for (String text : messages) consoleMessage("&b" + text);
		}
	}
	
	public static void infoMessage(@Nullable String... messages) {
		if (messages != null && messages.length > 0) {
			for (String text : messages) Bukkit.getLogger().info(getNameplate() + text);
		} else {
			Bukkit.getLogger().info("");
		}
	}

	public static void consoleMessage(@Nullable String... messages) {
		if (instance.getConfig().getBoolean("DisableConsoleMessages", false)) return;
		if (messages != null && messages.length > 0) {
			for (String text : messages) {
				if (instance.getConfig().getBoolean("DisableConsoleColour", false)) infoMessage(ChatColor.stripColor(Utils.cc(text)));
				else Bukkit.getConsoleSender().sendMessage(Utils.cc(prefix + text));
			}
		} else {
			Bukkit.getLogger().info("");
		}
	}

}
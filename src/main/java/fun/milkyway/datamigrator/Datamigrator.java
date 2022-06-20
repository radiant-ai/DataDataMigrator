package fun.milkyway.datamigrator;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Datamigrator extends JavaPlugin {

    private static MigrationsProvider migrationsProvider;
    private static Datamigrator plugin;

    @Override
    public void onEnable() {
        migrationsProvider = new MigrationsProvider(this);
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new CommandRunner());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MigrationsProvider getMigrationsProvider() {
        return migrationsProvider;
    }

    public static Datamigrator getInstance() {
        return plugin;
    }
}

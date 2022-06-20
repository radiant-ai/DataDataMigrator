package fun.milkyway.datamigrator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@CommandAlias("migrate")
public class CommandRunner extends BaseCommand {


    @Subcommand("files")
    @CommandPermission("datamigrator.migrate.files")
    @CommandCompletion("uuid_from uuid_to")
    public void migrateFiles(CommandSender sender, String uuidFrom, String uuidTo) {
        var result = Datamigrator.getMigrationsProvider().migrateFiles(UUID.fromString(uuidFrom), UUID.fromString(uuidTo));
        if (result.isSuccess()) {
            sender.sendMessage("Migrated files from " + uuidFrom + " to " + uuidTo);
        } else {
            sender.sendMessage("Failed to migrate files from " + uuidFrom + " to " + uuidTo + ": " + result.getMessage());
        }
    }

    @Subcommand("regions")
    @CommandPermission("datamigrator.migrate.regions")
    @CommandCompletion("uuid_from uuid_to world_name")
    public void migrateRegions(CommandSender sender, String uuidFrom, String uuidTo, String world) {
        var result = Datamigrator.getMigrationsProvider().migrateRegions(UUID.fromString(uuidFrom), UUID.fromString(uuidTo), world);
        if (result.isSuccess()) {
            sender.sendMessage("Migrated regions from " + uuidFrom + " to " + uuidTo + " in world " + world);
        } else {
            sender.sendMessage("Failed to migrate regions from " + uuidFrom + " to " + uuidTo + " in world " + world + ": " + result.getMessage());
        }
    }

    @Subcommand("clans")
    @CommandPermission("datamigrator.migrate.clans")
    @CommandCompletion("uuid_from uuid_to")
    public void migrateClans(CommandSender sender, String uuidFrom, String uuidTo) {
        var result = Datamigrator.getMigrationsProvider().replaceClanMember(UUID.fromString(uuidFrom), UUID.fromString(uuidTo));
        if (result.isSuccess()) {
            sender.sendMessage("Migrated clan from " + uuidFrom + " to " + uuidTo);
        } else {
            sender.sendMessage("Failed to migrate clan from " + uuidFrom + " to " + uuidTo + ": " + result.getMessage());
        }
    }
}

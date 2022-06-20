package fun.milkyway.datamigrator;

import com.github.radiant.ezclans.core.ClanMember;
import com.github.radiant.ezclans.core.Clans;
import com.google.common.io.Files;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MigrationsProvider {
    private Datamigrator plugin;
    public MigrationsProvider(Datamigrator plugin) {
        this.plugin = plugin;
    }
    //replace player member in clan
    public Result replaceClanMember(UUID uuidFrom, UUID uuidTo) {
        var member = Clans.getMember(uuidFrom);
        if (member == null) {
            return new Result(false, "Member not found");
        }
        var clan = member.getClan();

        var newMember = new ClanMember(uuidTo, "", clan);
        var result = clan.addMember(newMember);
        if (!result) {
            return new Result(false, "Failed to add new member");
        }
        if (member.isLeader()) {
            clan.setLeader(newMember);
        }
        else if (member.isModerator()) {
            newMember.setModerator();
        }

        clan.removeMember(member);
        return new Result(true, "Success");
    }

    public Result migrateRegions(UUID uuidFrom, UUID uuidTo, String worldName) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var world = Bukkit.getWorld(worldName);
        if (world == null) {
            return new Result(false, "World not found");
        }
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));
        if (regionManager == null) {
            return new Result(false, "RegionManager not found");
        }
        var regions = regionManager.getRegions().values();
        for (var region : regions) {
            if (region.getOwners().contains(uuidFrom)) {
                region.getOwners().removePlayer(uuidFrom);
                region.getOwners().addPlayer(uuidTo);
            }
            if (region.getMembers().contains(uuidFrom)) {
                region.getMembers().removePlayer(uuidFrom);
                region.getMembers().addPlayer(uuidTo);
            }
            try {
                Flag<String> flag = (Flag<String>) WorldGuard.getInstance().getFlagRegistry().get("creator");
                var value = region.getFlag(flag);
                if (value != null && value.equals(uuidFrom.toString())) {
                    region.setFlag(flag, uuidTo.toString());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return new Result(false, "Exception occured, check console");
            }
        }
        return new Result(true, "Regions migrated");
    }

    //copy all player data, advancements and statistics to another player
    public Result migrateFiles(UUID uuidFrom, UUID uuidTo) {
        File worldFolder = new File(plugin.getDataFolder().getAbsoluteFile().getParentFile().getParentFile(), "world");

        File advancementsFolder = new File(worldFolder, "advancements");
        File playerdataFolder = new File(worldFolder, "playerdata");
        File statsFolder = new File(worldFolder, "stats");

        File advancementsFile = new File(advancementsFolder, uuidFrom+".json");
        File playerdataFile = new File(playerdataFolder, uuidFrom+".dat");
        File statsFile = new File(statsFolder, uuidFrom+".json");

        File advancementsFileNew = new File(advancementsFolder, uuidTo+".json");
        File playerdataFileNew = new File(playerdataFolder, uuidTo+".dat");
        File statsFileNew = new File(statsFolder, uuidTo+".json");

        try {
            Files.copy(advancementsFile, advancementsFileNew);
            Files.copy(playerdataFile, playerdataFileNew);
            Files.copy(statsFile, statsFileNew);
            return new Result(true, "Files migrated");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Result(false, "Files not migrated");
    }
}

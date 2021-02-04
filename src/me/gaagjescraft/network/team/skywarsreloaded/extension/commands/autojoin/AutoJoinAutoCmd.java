package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.autojoin;

import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Party;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.BaseCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.features.AutoRejoin;
import org.bukkit.ChatColor;

public class AutoJoinAutoCmd extends BaseCmd {

    public AutoJoinAutoCmd(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "auto";
        alias = new String[]{};
        argLength = 1;
    }

    @Override
    public boolean run() {
        GameMap map = MatchManager.get().getPlayerMap(player);
        if (map == null) {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("not_ingame")));
            return true;
        }
        else if (!map.getSpectators().contains(player.getUniqueId()) && map.getMatchState() != MatchState.ENDING) {
            player.sendMessage(ChatColor.RED + "You can't use auto join right now. Try again later.");
            return true;
        }

        AutoRejoin autoRejoin = AutoRejoin.fromPlayer(player);
        int result = -1;
        if (autoRejoin == null) {
            Party party = Party.getParty(player);
            if (party != null && !party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You must be the party leader to do this.");
                return true;
            }
            autoRejoin = new AutoRejoin(player, party, (map.getTeamSize() == 1 ? GameType.SINGLE : GameType.TEAM));
            result = autoRejoin.attemptJoin(true);
        }
        else {
            if (!autoRejoin.isOwner(player)) {
                player.sendMessage(ChatColor.RED + "You must be the party leader to do this.");
                return true;
            }
            Party party = Party.getParty(player);
            autoRejoin.setParty(party);
            autoRejoin.setType(map.getTeamSize() == 1 ? GameType.SINGLE : GameType.TEAM);
            result = autoRejoin.attemptJoin(true);
        }

        if (result == -1) {
            player.sendMessage(ChatColor.RED + "Something went wrong with finding you a new game.");
        }
        else if (result == 0) {
            if (autoRejoin.getParty() == null) player.sendMessage(ChatColor.RED + "We couldn't find an arena to fit you in.");
            else player.sendMessage(ChatColor.RED + "We couldn't find an arena to fit you and your party members in.");
        }
        else if (result == 1) {
            player.sendMessage(ChatColor.RED + "Waiting for your party members to finish their game before rejoining another one.");
        }
        else if (result == 2) {
            player.sendMessage(ChatColor.GREEN + "Successfully found a new game to join!");
        }
        return true;
    }
}

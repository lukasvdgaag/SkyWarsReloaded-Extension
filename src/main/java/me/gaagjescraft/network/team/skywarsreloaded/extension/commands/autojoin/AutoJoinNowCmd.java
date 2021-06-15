package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.autojoin;

import com.walrusone.skywarsreloaded.enums.GameType;
import com.walrusone.skywarsreloaded.enums.MatchState;
import com.walrusone.skywarsreloaded.game.GameMap;
import com.walrusone.skywarsreloaded.managers.MatchManager;
import com.walrusone.skywarsreloaded.utilities.Party;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import me.gaagjescraft.network.team.skywarsreloaded.extension.commands.BaseCmd;
import me.gaagjescraft.network.team.skywarsreloaded.extension.features.autojoin.AutoRejoin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoJoinNowCmd extends BaseCmd {

    public AutoJoinNowCmd(String t) {
        type = t;
        forcePlayer = true;
        cmdName = "now";
        alias = new String[]{};
        argLength = 1;
    }

    @Override
    public boolean run(CommandSender sender, Player player, String[] args) {
        GameMap map = MatchManager.get().getPlayerMap(player);
        if (map == null) {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("not_ingame")));
            return true;
        } else if (!map.getSpectators().contains(player.getUniqueId()) && map.getMatchState() != MatchState.ENDING) {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.cannot_use_now")));
            return true;
        }

        AutoRejoin autoRejoin = AutoRejoin.fromPlayer(player);
        if (autoRejoin == null) {
            Party party = Party.getParty(player);
            if (party != null && !party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.must_be_leader")));
                return true;
            }
            autoRejoin = new AutoRejoin(player, party, (map.getTeamSize() == 1 ? GameType.SINGLE : GameType.TEAM));
            autoRejoin.attemptJoin(true);

            AutoRejoin.autoRejoins.remove(autoRejoin);
        } else {
            if (!autoRejoin.isOwner(player)) {
                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("autojoin.must_be_leader")));
                return true;
            }
            Party party = Party.getParty(player);
            autoRejoin.setParty(party);
            autoRejoin.setType(map.getTeamSize() == 1 ? GameType.SINGLE : GameType.TEAM);
            autoRejoin.attemptJoin(true);

            AutoRejoin.autoRejoins.remove(autoRejoin);
        }
        return true;
    }

}

package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.player;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.database.DataStorage;
import com.walrusone.skywarsreloaded.managers.PlayerStat;
import com.walrusone.skywarsreloaded.menus.playeroptions.*;
import me.gaagjescraft.network.team.skywarsreloaded.extension.SWExtension;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectCosmeticCmd extends BaseCmd {
    
    public SelectCosmeticCmd(String t ) {
        type = t;
        forcePlayer = true;
        cmdName = "select";
        alias = new String[]{};
        argLength = 3;
    }

    @Override
    public boolean run(CommandSender sender, Player player, String[] args) {
        if (player.hasPermission("sw.select")) {

            String type = args[1];
            String selected = args[2];

            if (type.equalsIgnoreCase("winsound")) {
                WinSoundOption po = (WinSoundOption) WinSoundOption.getPlayerOptionByKey(selected);
                if (po != null) {
                    if (!player.hasPermission(po.getPermission())) {
                        player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                        return true;
                    }

                    PlayerStat ps = PlayerStat.getPlayerStats(player);
                    ps.setWinSound(selected);
                    DataStorage.get().saveStats(ps);
                    player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                    return true;
                }

                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_found").replace("%type%", type)));
                return true;
            } else if (type.equalsIgnoreCase("killsound")) {
                KillSoundOption po = (KillSoundOption) KillSoundOption.getPlayerOptionByKey(selected);
                if (po != null) {
                    if (!player.hasPermission(po.getPermission())) {
                        player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                        return true;
                    }

                    PlayerStat ps = PlayerStat.getPlayerStats(player);
                    ps.setKillSound(selected);
                    DataStorage.get().saveStats(ps);
                    player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                    return true;
                }

                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_found").replace("%type%", type)));
                return true;
            } else if (type.equalsIgnoreCase("glass") || type.equalsIgnoreCase("cage") || type.equalsIgnoreCase("glasscolor")) {
                GlassColorOption po = (GlassColorOption) GlassColorOption.getPlayerOptionByKey(selected);
                if (po != null) {
                    if (!player.hasPermission(po.getPermission())) {
                        player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                        return true;
                    }

                    PlayerStat ps = PlayerStat.getPlayerStats(player);
                    ps.setGlassColor(selected);
                    DataStorage.get().saveStats(ps);
                    player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                    return true;
                }

                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_found").replace("%type%", type)));
                return true;
            } else if (type.equalsIgnoreCase("taunt")) {
                TauntOption po = (TauntOption) TauntOption.getPlayerOptionByKey(selected);
                if (po != null) {
                    if (!player.hasPermission(po.getPermission())) {
                        player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                        return true;
                    }

                    PlayerStat ps = PlayerStat.getPlayerStats(player);
                    ps.setTaunt(selected);
                    DataStorage.get().saveStats(ps);
                    player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                    return true;
                }

                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_found").replace("%type%", type)));
                return true;
            } else if (type.equalsIgnoreCase("particle") || type.equalsIgnoreCase("particleeffect")) {
                ParticleEffectOption po = (ParticleEffectOption) ParticleEffectOption.getPlayerOptionByKey(selected);
                if (po != null) {
                    if (!player.hasPermission(po.getPermission())) {
                        player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                        return true;
                    }

                    PlayerStat ps = PlayerStat.getPlayerStats(player);
                    ps.setParticleEffect(selected);
                    DataStorage.get().saveStats(ps);
                    player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                    return true;
                }

                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_found").replace("%type%", type)));
                return true;
            } else if (type.equalsIgnoreCase("projectile")) {
                ProjectileEffectOption po = (ProjectileEffectOption) ProjectileEffectOption.getPlayerOptionByKey(selected);
                if (po != null) {
                    if (!player.hasPermission(po.getPermission())) {
                        player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                        return true;
                    }

                    PlayerStat ps = PlayerStat.getPlayerStats(player);
                    ps.setProjectileEffect(selected);
                    DataStorage.get().saveStats(ps);
                    player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                    return true;
                }

                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_not_found").replace("%type%", type)));
                return true;
            } else {
                player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("select_cosmetic_invalid_type")));
                return true;
            }
        }
        else {
            player.sendMessage(SWExtension.c(SWExtension.get().getConfig().getString("no_permission")));
            return true;
        }
    }
}

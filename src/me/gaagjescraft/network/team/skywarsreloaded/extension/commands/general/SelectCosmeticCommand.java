package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.general;

import com.walrusone.skywarsreloaded.database.DataStorage;
import com.walrusone.skywarsreloaded.managers.PlayerStat;
import com.walrusone.skywarsreloaded.menus.playeroptions.*;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;
import me.gaagjescraft.network.team.skywarsreloaded.extension.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SelectCosmeticCommand implements Listener {

    private String c(String a) {
        return ChatColor.translateAlternateColorCodes('&',a);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (StringUtils.startsWithIgnoreCase(e.getMessage(), "/sw select") || StringUtils.startsWithIgnoreCase(e.getMessage(),"/skywars select")) {
            e.setCancelled(true);
            String[] a = e.getMessage().split(" ");

            if (e.getPlayer().hasPermission("sw.select")) {

                if (a.length == 4) {
                    String type = a[2];
                    String selected = a[3];

                    if (type.equalsIgnoreCase("winsound")) {
                        WinSoundOption po = (WinSoundOption) WinSoundOption.getPlayerOptionByKey(selected);
                        if (po != null) {
                            if (!e.getPlayer().hasPermission(po.getPermission())) {
                                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                                return;
                            }

                            PlayerStat ps = PlayerStat.getPlayerStats(e.getPlayer());
                            ps.setWinSound(selected);
                            DataStorage.get().saveStats(ps);
                            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                            return;
                        }

                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_comsetic_not_found").replace("%type%", type)));
                        return;
                    } else if (type.equalsIgnoreCase("killsound")) {
                        KillSoundOption po = (KillSoundOption) KillSoundOption.getPlayerOptionByKey(selected);
                        if (po != null) {
                            if (!e.getPlayer().hasPermission(po.getPermission())) {
                                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                                return;
                            }

                            PlayerStat ps = PlayerStat.getPlayerStats(e.getPlayer());
                            ps.setKillSound(selected);
                            DataStorage.get().saveStats(ps);
                            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                            return;
                        }

                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_comsetic_not_found").replace("%type%", type)));
                        return;
                    } else if (type.equalsIgnoreCase("glass") || type.equalsIgnoreCase("cage") || type.equalsIgnoreCase("glasscolor")) {
                        GlassColorOption po = (GlassColorOption) GlassColorOption.getPlayerOptionByKey(selected);
                        if (po != null) {
                            if (!e.getPlayer().hasPermission(po.getPermission())) {
                                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                                return;
                            }

                            PlayerStat ps = PlayerStat.getPlayerStats(e.getPlayer());
                            ps.setGlassColor(selected);
                            DataStorage.get().saveStats(ps);
                            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                            return;
                        }

                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_comsetic_not_found").replace("%type%", type)));
                        return;
                    } else if (type.equalsIgnoreCase("taunt")) {
                        TauntOption po = (TauntOption) TauntOption.getPlayerOptionByKey(selected);
                        if (po != null) {
                            if (!e.getPlayer().hasPermission(po.getPermission())) {
                                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                                return;
                            }

                            PlayerStat ps = PlayerStat.getPlayerStats(e.getPlayer());
                            ps.setTaunt(selected);
                            DataStorage.get().saveStats(ps);
                            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                            return;
                        }

                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_comsetic_not_found").replace("%type%", type)));
                        return;
                    } else if (type.equalsIgnoreCase("particle") || type.equalsIgnoreCase("particleeffect")) {
                        ParticleEffectOption po = (ParticleEffectOption) ParticleEffectOption.getPlayerOptionByKey(selected);
                        if (po != null) {
                            if (!e.getPlayer().hasPermission(po.getPermission())) {
                                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                                return;
                            }

                            PlayerStat ps = PlayerStat.getPlayerStats(e.getPlayer());
                            ps.setParticleEffect(selected);
                            DataStorage.get().saveStats(ps);
                            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                            return;
                        }

                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_comsetic_not_found").replace("%type%", type)));
                        return;
                    } else if (type.equalsIgnoreCase("projectile")) {
                        ProjectileEffectOption po = (ProjectileEffectOption) ProjectileEffectOption.getPlayerOptionByKey(selected);
                        if (po != null) {
                            if (!e.getPlayer().hasPermission(po.getPermission())) {
                                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_not_allowed").replace("%type%", type)));
                                return;
                            }

                            PlayerStat ps = PlayerStat.getPlayerStats(e.getPlayer());
                            ps.setProjectileEffect(selected);
                            DataStorage.get().saveStats(ps);
                            e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_successful").replace("%type%", type).replace("%name%", po.getName())));
                            return;
                        }

                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_not_found").replace("%type%", type)));
                        return;
                    } else {
                        e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_invalid_type")));
                        return;
                    }
                }

                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("select_cosmetic_incorrect_usage")));
            }
            else {
                e.getPlayer().sendMessage(c(Main.get().getConfig().getString("no_permission")));
            }
        }
    }

}

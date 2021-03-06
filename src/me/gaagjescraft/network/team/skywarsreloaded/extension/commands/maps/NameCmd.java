package me.gaagjescraft.network.team.skywarsreloaded.extension.commands.maps;

import com.walrusone.skywarsreloaded.commands.BaseCmd;
import com.walrusone.skywarsreloaded.game.GameMap;
import me.gaagjescraft.network.team.skywarsreloaded.extension.Main;

public class NameCmd extends BaseCmd {

    public NameCmd(String t) {
        type = t;
        forcePlayer = false;
        cmdName = "name";
        alias = new String[]{"display", "displayname", "n"};
        argLength = 3;
    }

    @Override
    public boolean run() {
        String mapName = args[1];
        GameMap map = GameMap.getMap(mapName);

        if (map == null) {
            sender.sendMessage(Main.c(Main.get().getConfig().getString("invalid_arena")));
            return true;
        }

        StringBuilder b = new StringBuilder();
        for (int i = 2;i<args.length;i++) {
            b.append(i > 2 ? " " + args[i] : args[i]);
        }

        map.setDisplayName(b.toString().trim());
        sender.sendMessage(Main.c(Main.get().getConfig().getString("display_name_set").replace("%map%",map.getName()).replace("%name%", b.toString())));
        return true;
    }
}

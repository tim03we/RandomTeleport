package tim03we.randomteleport.commands;

/*
 * This software is distributed under "GNU General Public License v3.0".
 * This license allows you to use it and/or modify it but you are not at
 * all allowed to sell this plugin at any cost. If found doing so the
 * necessary action required would be taken.
 *
 * RandomTeleport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License v3.0 for more details.
 *
 * You should have received a copy of the GNU General Public License v3.0
 * along with this program. If not, see
 * <https://opensource.org/licenses/GPL-3.0>.
 */

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import tim03we.randomteleport.Language;
import tim03we.randomteleport.RandomTeleport;

public class RandomTeleportCommand extends Command {

    public RandomTeleportCommand() {
        super("randomteleport", "Teleport to a random place in a world", "/randomteleport [level]");
        setAliases(new String[]{"randomtp", "rtp"});
        setPermission("randomteleport.use");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!sender.hasPermission(getPermission())) {
            sender.sendMessage(Language.translate(true, "no.permission"));
            return true;
        }
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Level level = player.getLevel();
            if(args.length > 0) {
                Level targetLevel = Server.getInstance().getLevelByName(args[0]);
                if(targetLevel != null && Server.getInstance().isLevelLoaded(args[0])) {
                    level = targetLevel;
                } else {
                    player.sendMessage(Language.translate(true, "world.not.found"));
                    return true;
                }
            }
            Level finalLevel = level;
            RandomTeleport.findRandomSafePosition(player.getLevel(), position -> {
                player.teleport(position.setLevel(finalLevel));
                if(RandomTeleport.instance.getConfig().getBoolean("position.found.message")) {
                    player.sendMessage(Language.translate(true, "position.found"));
                }
            });
        }
        return false;
    }
}

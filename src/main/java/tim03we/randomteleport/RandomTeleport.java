package tim03we.randomteleport;

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

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import tim03we.randomteleport.commands.RandomTeleportCommand;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RandomTeleport extends PluginBase {

    public static RandomTeleport instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Language.init();
        getServer().getCommandMap().register("randomteleport", new RandomTeleportCommand());
    }

    public static void findRandomSafePosition(Level level, Consumer<Position> callback) {

        CompletableFuture.runAsync(() -> callback.accept(randomPos(new Position(0, 0, 0, level))));

    }

    private static Position randomPos(Position base) {
        Random random = new Random();
        int x = random.nextInt(instance.getConfig().getInt("radius")) - instance.getConfig().getInt("radius");
        int z = random.nextInt(instance.getConfig().getInt("radius")) - instance.getConfig().getInt("radius");

        base.setComponents(x, 67, z);

        int cx = base.getChunkX(), cz = base.getChunkZ();

        while (!base.getLevel().isChunkGenerated(cx, cz) || !base.getLevel().isChunkLoaded(cx, cz)) {
            base.getLevel().generateChunk(cx, cz, true);
            base.getLevel().loadChunk(cx, cz, true);
        }

        for (int i = 20; i < 120; i++) {
            base.setComponents(x, i, z);
            Block ground = base.getLevel().getBlock(base);
            Block body = base.getLevel().getBlock(base.setComponents(x, i + 1, z));
            Block head = base.getLevel().getBlock(base.setComponents(x, i + 2, z));
            if (head.getId() == 0 && body.getId() == 0) {
                if(ground.getId() != Block.LAVA || ground.getId() != Block.STILL_LAVA && ground.isSolid()) {
                    return base.setComponents(x, i + 1, z);
                }
            }
        }

        return randomPos(base);
    }
}

package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.ChunkRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "setupSpawn", at = @At(value = "NEW", target = "(J)Ljava/util/Random;", remap = false))
    private static Random redirectRandomCreation(long seed, ServerWorld world) {
        Globals.setupGlobals(world);
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.SPAWN_POINT_SALT, 0);
        return rand;
    }
}

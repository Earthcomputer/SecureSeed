package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.carver.Carver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;
import java.util.function.Function;

@Mixin(Carver.class)
public class MixinCarver {
    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "carveRegion", at = @At(value = "NEW", target = "(J)Ljava/util/Random;"))
    private Random redirectCreateRandom(long s, Chunk chunk, Function<BlockPos, Biome> posToBiome, long seed, int seaLevel, int chunkX, int chunkZ) {
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, chunkX, chunkZ, Globals.dimension.get(), Globals.REGION_CARVER_SALT, seed);
        return rand;
    }
}

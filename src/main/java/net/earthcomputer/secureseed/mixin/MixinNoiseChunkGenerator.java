package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NoiseChunkGenerator.class)
public class MixinNoiseChunkGenerator {
    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "<init>(Lnet/minecraft/world/biome/source/BiomeSource;Lnet/minecraft/world/biome/source/BiomeSource;JLjava/util/function/Supplier;)V", at = @At(value = "NEW", target = "(J)Lnet/minecraft/world/gen/ChunkRandom;", ordinal = 0))
    private ChunkRandom redirectChunkRandom0(long seed) {
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.TERRAIN_NOISE_SALT, 0);
        return rand;
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "<init>(Lnet/minecraft/world/biome/source/BiomeSource;Lnet/minecraft/world/biome/source/BiomeSource;JLjava/util/function/Supplier;)V", at = @At(value = "NEW", target = "(J)Lnet/minecraft/world/gen/ChunkRandom;", ordinal = 1))
    private ChunkRandom redirectChunkRandom1(long seed) {
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.TERRAIN_NOISE_SALT, 1);
        return rand;
    }
}

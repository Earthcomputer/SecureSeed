package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PillagerOutpostFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PillagerOutpostFeature.class)
public class MixinPillagerOutpostFeature {
    @ModifyVariable(method = "shouldStartAt", at = @At(value = "LOAD", ordinal = 1), ordinal = 0)
    private ChunkRandom redirectSetSeed(ChunkRandom rand, ChunkGenerator chunkGenerator, BiomeSource biomeSource, long worldSeed, ChunkRandom random, int chunkX, int chunkZ) {
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, chunkX >> 4, chunkZ >> 4, Globals.dimension.get(), Globals.PILLAGER_OUTPOST_SALT, 0);
        return rand;
    }
}

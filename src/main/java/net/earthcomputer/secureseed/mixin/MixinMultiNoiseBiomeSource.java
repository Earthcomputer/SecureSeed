package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MultiNoiseBiomeSource.class)
public class MixinMultiNoiseBiomeSource {
    @Shadow @Final private long seed;

    @ModifyArg(method = "<init>(JLjava/util/List;Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource$NoiseParameters;Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource$NoiseParameters;Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource$NoiseParameters;Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource$NoiseParameters;Ljava/util/Optional;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/noise/DoublePerlinNoiseSampler;method_30846(Lnet/minecraft/world/gen/ChunkRandom;ILit/unimi/dsi/fastutil/doubles/DoubleList;)Lnet/minecraft/util/math/noise/DoublePerlinNoiseSampler;"), index = 0)
    private ChunkRandom modifyChunkRandom(ChunkRandom rand) {
        IChunkRandom iChunkRandom = (IChunkRandom) rand;
        long salt = iChunkRandom.secureseed_getInsecureSeed() - seed;
        iChunkRandom.secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.MULTI_NOISE_BIOME_SALT, salt);
        return rand;
    }
}

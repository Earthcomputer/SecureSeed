package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(OctavePerlinNoiseSampler.class)
public class MixinOctavePerlinNoiseSampler {
    @ModifyVariable(method = "<init>(Lnet/minecraft/world/gen/ChunkRandom;Lcom/mojang/datafixers/util/Pair;)V", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
    private ChunkRandom seedRandom(ChunkRandom rand) {
        IChunkRandom iChunkRandom = (IChunkRandom) rand;
        iChunkRandom.secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.OCTAVE_NOISE_SALT, iChunkRandom.secureseed_getInsecureSeed());
        return rand;
    }
}

package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Biome.class)
public class MixinBiome {
    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/noise/OctaveSimplexNoiseSampler;<init>(Lnet/minecraft/world/gen/ChunkRandom;Ljava/util/List;)V"), index = 0)
    private static ChunkRandom modifyChunkRandom(ChunkRandom rand) {
        ((IChunkRandom) rand).secureseed_setInsecure();
        return rand;
    }
}

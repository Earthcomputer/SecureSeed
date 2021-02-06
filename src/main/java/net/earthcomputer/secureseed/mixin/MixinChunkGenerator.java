package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Random;

@Mixin(ChunkGenerator.class)
public class MixinChunkGenerator {
    @ModifyVariable(method = "generateStrongholdPositions", at = @At(value = "INVOKE", target = "Ljava/util/Random;setSeed(J)V", remap = false, shift = At.Shift.AFTER), ordinal = 0)
    private Random replaceRandom(Random rand) {
        ChunkRandom newRand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) newRand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.STRONGHOLD_LOCATION_SALT, 0);
        return newRand;
    }
}

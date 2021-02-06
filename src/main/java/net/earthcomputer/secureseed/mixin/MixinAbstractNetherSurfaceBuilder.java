package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.surfacebuilder.AbstractNetherSurfaceBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractNetherSurfaceBuilder.class)
public class MixinAbstractNetherSurfaceBuilder {
    @ModifyVariable(method = "initSeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/surfacebuilder/AbstractNetherSurfaceBuilder;getSurfaceStates()Lcom/google/common/collect/ImmutableList;", ordinal = 0), ordinal = 0)
    private long resetSeed(long seed) {
        return 0;
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "initSeed", at = @At(value = "NEW", target = "(J)Lnet/minecraft/world/gen/ChunkRandom;"))
    private ChunkRandom redirectCreateChunkRandom1(long seed) {
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.SURFACE_BUILDER_SALT, seed);
        return rand;
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "createNoisesForStates", at = @At(value = "NEW", target = "(J)Lnet/minecraft/world/gen/ChunkRandom;"))
    private static ChunkRandom redirectCreateChunkRandom2(long seed) {
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.SURFACE_BUILDER_SALT, seed);
        return rand;
    }

}

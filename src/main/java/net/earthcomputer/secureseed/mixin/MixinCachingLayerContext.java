package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.gen.ChunkRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(CachingLayerContext.class)
public class MixinCachingLayerContext {
    @Unique private final ChunkRandom rand = new ChunkRandom(0);
    @Unique private long salt;

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "<init>", at = @At(value = "NEW", target = "(J)Ljava/util/Random;"))
    private Random createBiomeNoiseRandom(long seed) {
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, 0, 0, Globals.dimension.get(), Globals.BIOME_NOISE_SALT, 0);
        return rand;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int cacheCapacity, long seed, long salt, CallbackInfo ci) {
        this.salt = salt;
    }

    /**
     * @author Earthcomputer
     */
    @Overwrite
    public void initSeed(long x, long y) {
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, (int) x, (int) y, Globals.dimension.get(), Globals.BIOME_SALT, salt);
    }

    /**
     * @author Earthcomputer
     */
    @Overwrite
    public int nextInt(int bound) {
        return rand.nextInt(bound);
    }
}

package net.earthcomputer.secureseed.mixin;

import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.Hashing;
import net.earthcomputer.secureseed.IChunkRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.ChunkRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Random;

@Mixin(ChunkRandom.class)
public class MixinChunkRandom extends Random implements IChunkRandom {
    // world seed (64 bytes) = 64 bytes
    // x (4 bytes), z (4 bytes), dimension (4 bytes), type salt (4 bytes), salt (8 bytes), counter (8 bytes), padding (32 bytes) = 64 bytes

    // hash the world seed to guard against badly chosen world seeds
    @Unique private static final long[] HASHED_ZERO_SEED = Hashing.hashWorldSeed(new long[Globals.WORLD_SEED_LONGS]);
    @Unique private static final ThreadLocal<long[]> LAST_SEEN_WORLD_SEED = ThreadLocal.withInitial(() -> new long[Globals.WORLD_SEED_LONGS]);
    @Unique private static final ThreadLocal<long[]> HASHED_WORLD_SEED = ThreadLocal.withInitial(() -> HASHED_ZERO_SEED);

    @Unique private final long[] worldSeed = new long[Globals.WORLD_SEED_LONGS];
    @Unique private final long[] randomBits = new long[8];
    @Unique private int randomBitIndex;
    @Unique private static final int MAX_RANDOM_BIT_INDEX = 64 * 8;
    @Unique private static final int LOG2_MAX_RANDOM_BIT_INDEX = 9;
    @Unique private long counter = 0;
    @Unique private final long[] message = new long[16];
    @Unique private final long[] cachedInternalState = new long[16];

    @Unique private boolean secure = true;
    @Unique private long insecureSeed;

    // sanity check
    @Unique private boolean secureSeeded = false;

    @Inject(method = "<init>(J)V", at = @At("RETURN"))
    private void captureInsecureSeed(long insecureSeed, CallbackInfo ci) {
        this.insecureSeed = insecureSeed;
    }

    @Unique
    private long[] getHashedWorldSeed() {
        if (!Arrays.equals(worldSeed, LAST_SEEN_WORLD_SEED.get())) {
            HASHED_WORLD_SEED.set(Hashing.hashWorldSeed(worldSeed));
            System.arraycopy(worldSeed, 0, LAST_SEEN_WORLD_SEED.get(), 0, Globals.WORLD_SEED_LONGS);
        }
        return HASHED_WORLD_SEED.get();
    }

    @Unique
    private void moreRandomBits() {
        message[3] = counter++;
        System.arraycopy(getHashedWorldSeed(), 0, randomBits, 0, 8);
        Hashing.hash(message, randomBits, cachedInternalState, 64, true);
    }

    @Override
    public void secureseed_setSeed(long[] worldSeed, int x, int z, int dimension, int typeSalt, long salt) {
        System.arraycopy(worldSeed, 0, this.worldSeed, 0, Globals.WORLD_SEED_LONGS);
        message[0] = ((long) x << 32) | ((long) z & 0xffffffffL);
        message[1] = ((long) dimension << 32) | ((long) salt & 0xffffffffL);
        message[2] = typeSalt;
        message[3] = counter = 0;
        randomBitIndex = MAX_RANDOM_BIT_INDEX;
        secureSeeded = true;
    }

    @Override
    public long secureseed_getInsecureSeed() {
        return insecureSeed;
    }

    @Override
    public void secureseed_setInsecure() {
        secure = false;
    }

    @Unique
    private long getBits(int count) {
        if (!secureSeeded) {
            throw new IllegalStateException("Using unseeded ChunkRandom");
        }

        if (randomBitIndex >= MAX_RANDOM_BIT_INDEX) {
            moreRandomBits();
            randomBitIndex -= MAX_RANDOM_BIT_INDEX;
        }

        int alignment = randomBitIndex & 63;
        if ((randomBitIndex >>> 6) == ((randomBitIndex + count) >>> 6)) {
            long result = (randomBits[randomBitIndex >>> 6] >>> alignment) & ((1L << count) - 1);
            randomBitIndex += count;
            return result;
        } else {
            long result = (randomBits[randomBitIndex >>> 6] >>> alignment) & ((1L << (64 - alignment)) - 1);
            randomBitIndex += count;
            if (randomBitIndex >= MAX_RANDOM_BIT_INDEX) {
                moreRandomBits();
                randomBitIndex -= MAX_RANDOM_BIT_INDEX;
            }
            alignment = randomBitIndex & 63;
            result <<= alignment;
            result |= (randomBits[randomBitIndex >>> 6] >>> (64 - alignment)) & ((1L << alignment) - 1);

            return result;
        }
    }

    @Redirect(method = "next", at = @At(value = "INVOKE", target = "Ljava/util/Random;next(I)I"))
    private int secureNext(Random self, int bits) {
        if (secure) {
            return (int) getBits(bits);
        } else {
            return super.next(bits);
        }
    }

    /**
     * @author Earthcomputer
     * @reason Very different
     */
    @Overwrite
    public void consume(int count) {
        if (!secure) {
            for (int i = 0; i < count; i++) {
                next(1);
            }
            return;
        }

        randomBitIndex += count;
        if (randomBitIndex >= MAX_RANDOM_BIT_INDEX * 2) {
            randomBitIndex -= MAX_RANDOM_BIT_INDEX;
            counter += randomBitIndex >>> LOG2_MAX_RANDOM_BIT_INDEX;
            randomBitIndex &= MAX_RANDOM_BIT_INDEX - 1;
            randomBitIndex += MAX_RANDOM_BIT_INDEX;
        }
    }

    @Override
    public int nextInt(int bound) {
        if (!secure) return super.nextInt(bound);

        int bits = MathHelper.log2DeBruijn(bound);
        int result;
        do {
            result = (int) getBits(bits);
        } while (result >= bound);

        return result;
    }

    @Override
    public long nextLong() {
        if (!secure) return super.nextLong();

        return getBits(64);
    }

    @Override
    public double nextDouble() {
        if (!secure) return super.nextDouble();

        return getBits(53) * 0x1.0p-53;
    }

    @Override
    public synchronized void setSeed(long seed) {
        secureSeeded = false;
        super.setSeed(seed);
    }

    /**
     * @author Earthcomputer
     * @reason Very different
     */
    @Overwrite
    public long setTerrainSeed(int chunkX, int chunkZ) {
        secureseed_setSeed(Globals.worldSeed, chunkX, chunkZ, Globals.dimension.get(), Globals.TERRAIN_SALT, 0);
        return 0;
    }

    /**
     * @author Earthcomputer
     * @reason Very different
     */
    @Overwrite
    public long setPopulationSeed(long worldSeed, int blockX, int blockZ) {
        secureseed_setSeed(Globals.worldSeed, blockX, blockZ, Globals.dimension.get(), Globals.POPULATION_SALT, 0);
        return ((long) blockX << 32) | ((long) blockZ & 0xffffffffL);
    }

    /**
     * @author Earthcomputer
     * @reason Very different
     */
    @Overwrite
    public long setDecoratorSeed(long populationSeed, int index, int step) {
        secureseed_setSeed(Globals.worldSeed, (int) (populationSeed >> 32), (int) populationSeed, Globals.dimension.get(), Globals.DECORATION_SALT, index + 10000 * step);
        return 0;
    }

    /**
     * @author Earthcomputer
     * @reason Very different
     */
    @Overwrite
    public long setCarverSeed(long worldSeed, int chunkX, int chunkZ) {
        secureseed_setSeed(Globals.worldSeed, chunkX, chunkZ, Globals.dimension.get(), Globals.CARVER_SALT, 0);
        return 0;
    }

    /**
     * @author Earthcomputer
     * @reason Very different
     */
    @Overwrite
    public long setRegionSeed(long worldSeed, int regionX, int regionZ, int salt) {
        secureseed_setSeed(Globals.worldSeed, regionX, regionZ, Globals.dimension.get(), Globals.REGION_SALT, salt);
        return 0;
    }

    /**
     * @author Earthcomputer
     * @reason Very different
     */
    @Overwrite
    public static Random getSlimeRandom(int chunkX, int chunkZ, long worldSeed, long scrambler) {
        ChunkRandom rand = new ChunkRandom(0);
        //noinspection ConstantConditions
        ((IChunkRandom) rand).secureseed_setSeed(Globals.worldSeed, chunkX, chunkZ, Globals.dimension.get(), Globals.SLIME_CHUNK_SALT, 0);
        return rand;
    }
}

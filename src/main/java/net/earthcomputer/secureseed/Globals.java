package net.earthcomputer.secureseed;

import com.google.common.collect.Iterables;
import net.minecraft.server.world.ServerWorld;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Globals {
    public static final int WORLD_SEED_LONGS = 16;
    public static final int WORLD_SEED_BITS = WORLD_SEED_LONGS * 64;

    public static final long[] worldSeed = new long[WORLD_SEED_LONGS];
    public static final ThreadLocal<Integer> dimension = ThreadLocal.withInitial(() -> 0);

    public static final int TERRAIN_SALT = 0;
    public static final int POPULATION_SALT = 1;
    public static final int DECORATION_SALT = 2;
    public static final int CARVER_SALT = 3;
    public static final int REGION_SALT = 4;
    public static final int SLIME_CHUNK_SALT = 5;
    public static final int BIOME_SALT = 6;
    public static final int BIOME_NOISE_SALT = 7;
    public static final int SPAWN_POINT_SALT = 8;
    public static final int END_PILLAR_SALT = 9;
    public static final int STRONGHOLD_LOCATION_SALT = 10;
    public static final int PILLAGER_OUTPOST_SALT = 11;
    public static final int END_GATEWAY_ORDER_SALT = 12;
    public static final int REGION_CARVER_SALT = 13;
    public static final int CAVE_CARVER_SALT = 14;
    public static final int RAVINE_CARVER_SALT = 15;
    public static final int OCTAVE_NOISE_SALT = 16;
    public static final int OCTAVE_SIMPLEX_NOISE_SALT = 17;
    public static final int MULTI_NOISE_BIOME_SALT = 18;
    public static final int END_BIOME_NOISE_SALT = 19;
    public static final int TERRAIN_NOISE_SALT = 20;
    public static final int SURFACE_BUILDER_SALT = 21;

    public static void setupGlobals(ServerWorld world) {
        long[] seed = ((IGeneratorOptions) world.getServer().getSaveProperties().getGeneratorOptions()).secureseed_getSeed();
        System.arraycopy(seed, 0, worldSeed, 0, WORLD_SEED_LONGS);
        int worldIndex = Iterables.indexOf(world.getServer().getWorldRegistryKeys(), it -> it == world.getRegistryKey());
        if (worldIndex == -1) worldIndex = world.getServer().getWorldRegistryKeys().size(); // if we are in world construction it may not have been added to the map yet
        dimension.set(worldIndex);
    }

    public static long[] createRandomWorldSeed() {
        long[] seed = new long[WORLD_SEED_LONGS];
        SecureRandom rand = new SecureRandom();
        for (int i = 0; i < WORLD_SEED_LONGS; i++) {
            seed[i] = rand.nextLong();
        }
        return seed;
    }

    public static long[] parseSeed(String seedStr) {
        long[] seed = new long[WORLD_SEED_LONGS];
        try {
            BigInteger seedBigInt = new BigInteger(seedStr);
            if (seedBigInt.signum() < 0) {
                seedBigInt = seedBigInt.and(BigInteger.ONE.shiftLeft(WORLD_SEED_BITS).subtract(BigInteger.ONE));
            }
            for (int i = 0; i < WORLD_SEED_LONGS; i++) {
                BigInteger[] divRem = seedBigInt.divideAndRemainder(BigInteger.ONE.shiftLeft(64));
                seed[i] = divRem[1].longValue();
                seedBigInt = divRem[0];
            }
        } catch (NumberFormatException e) {
            seed[0] = seedStr.hashCode();
        }

        return seed;
    }

    public static String seedToString(long[] seed) {
        BigInteger seedBigInt = BigInteger.ZERO;
        for (int i = WORLD_SEED_LONGS - 1; i >= 0; i--) {
            BigInteger val = BigInteger.valueOf(seed[i]);
            if (val.signum() < 0) {
                val = val.add(BigInteger.ONE.shiftLeft(64));
            }
            seedBigInt = seedBigInt.shiftLeft(64).add(val);
        }

        return seedBigInt.toString();
    }
}

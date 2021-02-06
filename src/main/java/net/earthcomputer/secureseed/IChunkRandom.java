package net.earthcomputer.secureseed;

public interface IChunkRandom {
    void secureseed_setSeed(long[] worldSeed, int x, int z, int dimension, int typeSalt, long salt);
    long secureseed_getInsecureSeed();
    void secureseed_setInsecure();
}

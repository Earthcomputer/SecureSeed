package net.earthcomputer.secureseed;

public interface IGeneratorOptions {
    long[] secureseed_getSeed();
    void secureseed_setSeed(long[] seed);
}

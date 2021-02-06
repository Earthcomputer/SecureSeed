package net.earthcomputer.secureseed.mixin;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.earthcomputer.secureseed.Globals;
import net.earthcomputer.secureseed.IGeneratorOptions;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mixin(GeneratorOptions.class)
public class MixinGeneratorOptions implements IGeneratorOptions {
    @Unique private long[] secureSeed;

    @Override
    public long[] secureseed_getSeed() {
        return secureSeed;
    }

    @Override
    public void secureseed_setSeed(long[] seed) {
        this.secureSeed = seed;
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "*()Lnet/minecraft/world/gen/GeneratorOptions;", at = @At("RETURN"))
    private void onCloneGeneratorOptions(CallbackInfoReturnable<GeneratorOptions> ci) {
        ((IGeneratorOptions) ci.getReturnValue()).secureseed_setSeed(secureSeed);
    }

    @SuppressWarnings({"UnresolvedMixinReference", "unchecked"})
    @Redirect(method = "method_28606(Lcom/mojang/serialization/codecs/RecordCodecBuilder$Instance;)Lcom/mojang/datafixers/kinds/App;",
            at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/Products$P5;apply(Lcom/mojang/datafixers/kinds/Applicative;Lcom/mojang/datafixers/kinds/App;)Lcom/mojang/datafixers/kinds/App;"))
    private static <T1, T2, T3, T4, T5, R> App<RecordCodecBuilder.Mu<GeneratorOptions>, R> redirectApply(
            Products.P5<RecordCodecBuilder.Mu<GeneratorOptions>, T1, T2, T3, T4, T5> product, Applicative<RecordCodecBuilder.Mu<GeneratorOptions>, ?> instance,
            App<RecordCodecBuilder.Mu<GeneratorOptions>, Function5<T1, T2, T3, T4, T5, R>> function
    ) {
        return product.and(Codec.LONG.listOf()
                .xmap(list -> list.stream().mapToLong(Long::longValue).toArray(), array -> {
                    if (array == null) array = Globals.createRandomWorldSeed();
                    return Arrays.stream(array).boxed().collect(Collectors.toList());
                })
                .orElseGet(Globals::createRandomWorldSeed)
                .xmap(seed -> {
                    if (seed.length != Globals.WORLD_SEED_LONGS) {
                        seed = Globals.createRandomWorldSeed();
                    }
                    return seed;
                }, Function.identity())
                .fieldOf("secureSeed")
                .stable()
                .forGetter(options -> ((IGeneratorOptions) options).secureseed_getSeed())
        ).apply(instance, RecordCodecBuilder.stable((seed, generateStructures, bonusChest, simpleRegistry, legacyCustomOptions, secureSeed) -> {
            GeneratorOptions options = GeneratorOptionsAccessor.createGeneratorOptions((Long) seed, (Boolean) generateStructures, (Boolean) bonusChest, (SimpleRegistry<DimensionOptions>) simpleRegistry, (Optional<String>) legacyCustomOptions);
            ((IGeneratorOptions) options).secureseed_setSeed(secureSeed);
            return (R) options;
        }));
    }

    @Inject(method = "fromProperties", at = @At("RETURN"))
    private static void parseSecureSeedFromLevelProperties(DynamicRegistryManager registryManager, Properties properties, CallbackInfoReturnable<GeneratorOptions> ci) {
        String seedStr = properties.getProperty("level-seed");
        long[] seed = seedStr.isEmpty() ? Globals.createRandomWorldSeed() : Globals.parseSeed(seedStr);
        ((IGeneratorOptions) ci.getReturnValue()).secureseed_setSeed(seed);
    }
}

package net.earthcomputer.secureseed.mixin;

import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(GeneratorOptions.class)
public interface GeneratorOptionsAccessor {
    @Invoker
    static GeneratorOptions createGeneratorOptions(long seed, boolean generateStructures, boolean bonusChest,
                                                   SimpleRegistry<DimensionOptions> simpleRegistry,
                                                   Optional<String> legacyCustomOptions) {
        throw new UnsupportedOperationException();
    }
}

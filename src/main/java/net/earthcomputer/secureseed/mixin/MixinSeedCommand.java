package net.earthcomputer.secureseed.mixin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.earthcomputer.secureseed.Globals;
import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SeedCommand.class)
public class MixinSeedCommand {

    /**
     * @author Earthcomputer
     * @reason Very different
     */
    @SuppressWarnings({"OverwriteTarget", "target"})
    @Overwrite
    private static int method_13617(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Globals.setupGlobals(context.getSource().getWorld());
        String seedStr = Globals.seedToString(Globals.worldSeed);
        Text result = Texts.bracketed(new LiteralText(seedStr)).styled(style -> {
            return style.withColor(Formatting.GREEN)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, seedStr))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click")))
                    .withInsertion(seedStr);
        });
        context.getSource().sendFeedback(new TranslatableText("commands.seed.success", result), false);
        return (int) Globals.worldSeed[0];
    }

}

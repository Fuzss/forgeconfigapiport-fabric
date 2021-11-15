package net.minecraftforge;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraftforge.network.config.ConfigSync;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.server.command.EnumArgument;
import net.minecraftforge.server.command.ModIdArgument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ForgeConfigAPIPort implements ModInitializer {
    public static final String MODID = "forgeconfigapiport";
    public static final String NAME = "Forge Config API Port";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final Marker CORE = MarkerManager.getMarker("CORE");

    @Override
    public void onInitialize() {
        ConfigSync.INSTANCE.init();
        // loaded immediately on fabric as no mod loading stages exist
//        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, FabricEnvironment.getConfigDir());
        FMLConfig.loadDefaultConfigPath();
        this.registerArgumentTypes();
        this.registerCallbacks();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerArgumentTypes() {
        ArgumentTypes.register(new ResourceLocation(MODID, "enum").toString(), EnumArgument.class, (ArgumentSerializer) new EnumArgument.Serializer());
        ArgumentTypes.register(new ResourceLocation(MODID, "modid").toString(), ModIdArgument.class, new EmptyArgumentSerializer<>(ModIdArgument::modIdArgument));
    }

    private void registerCallbacks() {
        CommandRegistrationCallback.EVENT.register((CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) -> {
            if (!dedicated) ConfigCommand.register(dispatcher);
        });
        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleHooks::handleServerAboutToStart);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerLifecycleHooks::handleServerStopped);
    }
}
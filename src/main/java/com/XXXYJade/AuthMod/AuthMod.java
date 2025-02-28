package com.XXXYJade.AuthMod;

import com.XXXYJade.AuthMod.Command.CommandManager;
import com.XXXYJade.AuthMod.Eventlistener.EventLiseners;
import com.XXXYJade.AuthMod.Password.PasswordManager;
import com.XXXYJade.AuthMod.Player.PlayerManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AuthMod.MODID)
public class AuthMod {
    private static AuthMod intance;

    public static final String MODID = "config/authmod";
    private static final Logger LOGGER = LogManager.getLogger("config/authmod");

    private static PasswordManager passwordManager;
    private static PlayerManager playerManager;
    private static EventLiseners eventLiseners;
    private static CommandManager commandManager;

    public AuthMod(IEventBus modEventBus, ModContainer modContainer) {
        intance = this;
        passwordManager = PasswordManager.getInstance();
        playerManager = new PlayerManager();
        eventLiseners = new EventLiseners(this);
        commandManager = new CommandManager(this);

        NeoForge.EVENT_BUS.register(eventLiseners);
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> CommandManager.registerAllCommands(commandManager, event.getServer().getCommands().getDispatcher()));
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }
    public static AuthMod getInstance() {
        return intance;
    }

    public static PasswordManager getPasswordManager() {
        return passwordManager;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static EventLiseners getEventLiseners() {
        return eventLiseners;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

}

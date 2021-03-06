package me.morpheus.dtpunishment;

import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

import me.morpheus.dtpunishment.configuration.MainConfig;
import me.morpheus.dtpunishment.data.DataStore;
import me.morpheus.dtpunishment.data.FileDataStore;
import me.morpheus.dtpunishment.data.MySqlDataStore;

public class DTPunishmentModule implements Module {

    MainConfig config;

    public DTPunishmentModule(MainConfig config) {
        this.config = config;
    }

    @Override
    public void configure(Binder binder) {
        // When we register the module, we need to satisfy the interface for the
        // data store
        // Choose it here based on the config
        if (config.database.enabled)
            binder.bind(DataStore.class).to(MySqlDataStore.class).in(Scopes.SINGLETON);
        else
            binder.bind(DataStore.class).to(FileDataStore.class).in(Scopes.SINGLETON);

        // Sponge deps that aren't in the container already
        binder.bind(Server.class).toInstance(Sponge.getServer());
        binder.bind(CommandManager.class).toInstance(Sponge.getCommandManager());
    }

}

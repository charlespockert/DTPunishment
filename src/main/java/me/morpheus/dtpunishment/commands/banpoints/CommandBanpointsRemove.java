package me.morpheus.dtpunishment.commands.banpoints;

import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

import me.morpheus.dtpunishment.data.DataStore;
import me.morpheus.dtpunishment.utils.Util;

public class CommandBanpointsRemove implements CommandExecutor {

    @Inject
    private DataStore dataStore;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        User user = args.<User>getOne("player").get();
        UUID uuid = user.getUniqueId();
        String name = user.getName();
        int actual = dataStore.getBanpoints(uuid);
        int amount = args.<Integer>getOne("amount").get();

        if (actual - amount < 0)
            amount = actual;
        int total = actual - amount;

        dataStore.removeBanpoints(uuid, amount);

        if (user.isOnline()) {
            user.getPlayer().get()
                    .sendMessage(Util.getWatermark().append(
                            Text.of(TextColors.AQUA, amount + " banpoints have been removed; you now have " + total))
                            .build());
        }

        Text adminMessage = Util.getWatermark()
                .append(Text.of(TextColors.AQUA, String.format(
                        "%s has removed %d banpoint(s) from %s; they now have %d", src.getName(), amount, name, total)))
                .build();

        if (src instanceof ConsoleSource)
            src.sendMessage(adminMessage);

        for (Player p : Sponge.getServer().getOnlinePlayers()) {
            if (p.hasPermission("dtpunishment.staff.notify") || p == src) {
                p.sendMessage(adminMessage);
            }
        }

        return CommandResult.success();
    }
}

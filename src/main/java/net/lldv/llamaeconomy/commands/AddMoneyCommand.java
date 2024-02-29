package net.lldv.llamaeconomy.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.ConfigSection;
import net.lldv.llamaeconomy.LlamaEconomy;
import net.lldv.llamaeconomy.components.event.PlayerAddMoneyEvent;
import net.lldv.llamaeconomy.components.language.Language;

import java.util.concurrent.CompletableFuture;

public class AddMoneyCommand extends PluginCommand<LlamaEconomy> {

    public AddMoneyCommand(LlamaEconomy owner, ConfigSection section) {
        super(section.getString("Name"), owner);
        setDescription(section.getString("Description"));
        setUsage(section.getString("Usage"));
        setAliases(section.getStringList("Aliases").toArray(new String[]{}));
        setPermission(section.getString("Permission"));
        final String[] params = section.getString("Parameters").split(";");
        addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType(params[0], false, CommandParamType.STRING),
                CommandParameter.newType(params[1], false, CommandParamType.FLOAT)
        });
        /*
        *         this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter(params[0], CommandParamType.FLOAT, false),
                new CommandParameter(params[1], CommandParamType.INT, true)
        });*/
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.hasPermission(getPermission())) return false;
        CompletableFuture.runAsync(() -> {
            if (args.length >= 2) {
                try {
                    String target = args[0];
                    Player playerTarget = getPlugin().getServer().getPlayer(target);
                    if (playerTarget != null) target = playerTarget.getName();

                    if (!LlamaEconomy.getAPI().hasAccount(target)) {
                        sender.sendMessage(Language.get("not-registered", target));
                        return;
                    }

                    double amt = Double.parseDouble(args[1]);

                    if (amt < 0) {
                        sender.sendMessage(Language.get("invalid-amount"));
                        return;
                    }

                    LlamaEconomy.getAPI().addMoney(target, amt);
                    if (sender.isPlayer())
                        Server.getInstance().getPluginManager().callEvent(new PlayerAddMoneyEvent((Player) sender, target, amt));
                    sender.sendMessage(Language.get("added-money", target, getPlugin().getMonetaryUnit(), this.getPlugin().getMoneyFormat().format(amt)));

                } catch (NumberFormatException ex) {
                    sender.sendMessage(Language.get("invalid-amount"));
                }
            } else sender.sendMessage(Language.get("usage", getUsage()));
        });
        return false;
    }
}

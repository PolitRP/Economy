package net.lldv.LlamaEconomy.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.LlamaEconomy.utils.Language;

import java.util.concurrent.CompletableFuture;

public class MoneyCommand extends Command {

    public MoneyCommand() {
        super("money", "Money command", "/money <optional: player>", new String[]{"mymoney", "getmoney", "bal", "balance"});
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        // Making the command async so it's more efficient when using real database providers
        CompletableFuture.runAsync(() -> {
            if (args.length >= 1) {
                String target = args[0];
                Player playerTarget = LlamaEconomy.instance.getServer().getPlayer(target);
                if (playerTarget != null) target = playerTarget.getName();

                if (!LlamaEconomy.getAPI().hasAccount(target)) {
                    sender.sendMessage(Language.getAndReplace("not-registered", target));
                    return;
                }

                double money = LlamaEconomy.getAPI().getMoney(target);
                sender.sendMessage(Language.getAndReplace("money-other", target, LlamaEconomy.monetaryUnit, money));
            } else {
                if (sender.isPlayer()) {
                    double money = LlamaEconomy.getAPI().getMoney(sender.getName());
                    sender.sendMessage(Language.getAndReplace("money", LlamaEconomy.monetaryUnit, money));
                }
            }
        });
        return false;
    }
}

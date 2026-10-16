package com.nick1416.wormholetech.knowledge;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Op-only test helper: /wormholetech research <planet> <percent> [player]. */
public class CommandResearch extends CommandBase {
    @Override public String getName() { return "wormholetech"; }
    @Override public String getUsage(ICommandSender s) { return "/wormholetech research <planet> <0-100> [player]"; }
    @Override public int getRequiredPermissionLevel() { return 2; }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3 || !"research".equals(args[0])) throw new WrongUsageException(getUsage(sender));
        int planet = Arrays.asList(Planets.KEYS).indexOf(args[1]);
        if (planet < 0) throw new CommandException("Unknown planet '" + args[1] + "'. Use: " + String.join(", ", Planets.KEYS));
        int pct = parseInt(args[2], 0, 100);
        EntityPlayerMP p = args.length > 3 ? getPlayer(server, sender, args[3]) : getCommandSenderAsPlayer(sender);
        KnowledgeData d = KnowledgeData.get(p.world);
        d.setPercent(p.getUniqueID(), planet, pct);
        KnowledgeEvents.checkUnlocks(p, d);
        com.nick1416.wormholetech.mode.ModeNetwork.CHANNEL.sendTo(new KnowledgeMessage(d.clientView(p.getUniqueID())), p);
        notifyCommandListener(sender, this, "Set %s research of %s to %s%%", args[1], p.getName(), d.percent(p.getUniqueID(), planet));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "research");
        if (args.length == 2) return getListOfStringsMatchingLastWord(args, Planets.KEYS);
        if (args.length == 3) return getListOfStringsMatchingLastWord(args, "0", "25", "50", "100");
        if (args.length == 4) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        return Collections.emptyList();
    }

    @Override public boolean isUsernameIndex(String[] args, int i) { return i == 3; }
}

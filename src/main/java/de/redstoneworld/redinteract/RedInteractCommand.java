package de.redstoneworld.redinteract;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RedInteractCommand implements CommandExecutor {

    private final RedInteract plugin;

    public RedInteractCommand(RedInteract plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if ("reload".equalsIgnoreCase(args[0]) && sender.hasPermission("redinteract.command.reload")) {
                if (plugin.loadConfig()) {
                    sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Error while reloading the config!");
                }
                return true;
            } else {
                try {
                    InteractRequest.Type type = InteractRequest.Type.valueOf(args[0].toUpperCase());
                    if (sender.hasPermission("redinteract.command." + type.toString().toLowerCase())) {

                        if (args.length > 4) {
                            World world = plugin.getServer().getWorld(args[1]);

                            if (world != null) {
                                int[] coords = new int[3];
                                for (int i = 0; i < 3; i++) {
                                    try {
                                        coords[i] = Integer.parseInt(args[i + 2]);
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage(ChatColor.RED + "The inputted number " + ChatColor.WHITE + args[i + 2] + ChatColor.RED + " is not a valid integer!");
                                        return true;
                                    }
                                }

                                LocationInfo loc = new LocationInfo(world.getName(), coords[0], coords[1], coords[2]);

                                if (plugin.execute(type, loc)) {
                                    sender.sendMessage(ChatColor.GREEN + type.toString() + "ED " + loc.toString());
                                } else {
                                    sender.sendMessage(ChatColor.RED + loc.toString() + " was " + (type == InteractRequest.Type.ADD ? "already" : "not") + " registered!");
                                }

                            } else {
                                sender.sendMessage(ChatColor.RED + "No world with the name " + ChatColor.WHITE + args[1] + ChatColor.RED + " found!");
                            }

                        } else if (sender instanceof Player){
                            plugin.addPendingRequest((Player) sender, type);

                        } else {
                            sender.sendMessage(ChatColor.RED + "To run this command from the console use "
                                    + ChatColor.WHITE + "/" + label + " " + type.toString().toLowerCase() + " <world> <x> <y> <z>");
                        }

                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have the permission "  + ChatColor.WHITE + "redinteract.command." + type.toString().toLowerCase());
                    }

                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
        }
        return false;
    }
}

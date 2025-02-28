package com.XXXYJade.AuthMod.Command;

import com.XXXYJade.AuthMod.AuthMod;
import com.XXXYJade.AuthMod.Config;
import com.XXXYJade.AuthMod.Password.PasswordManager;
import com.XXXYJade.AuthMod.Password.PasswordUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class CommandManager {
    public CommandManager(AuthMod authMod) {
    }

    public static void registerAllCommands(CommandManager commandManager, CommandDispatcher<CommandSourceStack> dispatcher) {
        commandManager.registerCommand(dispatcher);
        commandManager.loginCommand(dispatcher);
        commandManager.changePasswordCommand(dispatcher);
    }

    private void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> register = Commands.literal("register")
                .then(Commands.argument("password", StringArgumentType.word())
                        .then(Commands.argument("confirmPassword", StringArgumentType.word())
                                .executes(this::register)));
        dispatcher.register(register);

        LiteralArgumentBuilder<CommandSourceStack> reg = Commands.literal("reg")
                .then(Commands.argument("password", StringArgumentType.word())
                        .then(Commands.argument("confirmPassword", StringArgumentType.word())
                                .executes(this::register)));
        dispatcher.register(reg);
    }

    private int register(CommandContext<CommandSourceStack> context) {
        try {
            Player player = context.getSource().getPlayerOrException();
            String username = player.getName().getString();
            String password = StringArgumentType.getString(context, "password");
            String confirmPassword = StringArgumentType.getString(context, "confirmPassword");
            if (!PasswordManager.checkPasswordLenth(password)) {
                context.getSource().sendFailure(Component.literal("密码长度应为" + Config.getInstance().getPasswordMinLenth() + "-" + Config.getInstance().getPasswordMaxLenth() + "之间!"));
                return 0;
            } else if (!password.equals(confirmPassword)) {
                context.getSource().sendFailure(Component.literal("两次密码输入不一致!"));
                return 0;
            } else if (AuthMod.getPasswordManager().hasPassword(username)) {
                context.getSource().sendFailure(Component.literal("你已经注册过了!"));
                return 0;
            } else {
                context.getSource().sendSuccess(() -> Component.literal("注册成功"), false);
                AuthMod.getPasswordManager().addNewPassword(username, password);
                return 1;
            }
        } catch (RuntimeException | CommandSyntaxException e) {
            context.getSource().sendFailure(Component.literal("注册出现异常!"));
            AuthMod.getLOGGER().warn("注册时出现异常:", e);
            return 0;
        }
    }

    private void loginCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> login = Commands.literal("login")
                .then(Commands.argument("password", StringArgumentType.word())
                        .executes(this::login));
        dispatcher.register(login);
        LiteralArgumentBuilder<CommandSourceStack> l = Commands.literal("l")
                .then(Commands.argument("password", StringArgumentType.word())
                        .executes(this::login));
        dispatcher.register(l);
    }

    private int login(CommandContext<CommandSourceStack> context) {
        try {
            Player player = context.getSource().getPlayerOrException();
            String username = player.getName().getString();
            String password = StringArgumentType.getString(context, "password");
            String storedPassword = AuthMod.getPasswordManager().getPassword(username);
            if (!AuthMod.getPasswordManager().hasPassword(username)) {
                context.getSource().sendFailure(Component.literal("你还没有注册!"));
                return 0;
            } else if (!PasswordUtils.verifyPassword(password, storedPassword)) {
                context.getSource().sendFailure(Component.literal("密码错误!"));
                return 0;
            } else {
                context.getSource().sendSuccess(() -> Component.literal("登录成功!"), false);
                AuthMod.getLOGGER().info("玩家" + username + "登录成功");
                AuthMod.getPlayerManager().logIn(player);
                return 1;
            }
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(Component.literal("登录出现异常!"));
            AuthMod.getLOGGER().warn("登录时出现异常:", e);
            return 0;
        }
    }

    private void changePasswordCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> changePassword = Commands.literal("changepassword")
                .then(Commands.argument("old password", StringArgumentType.word())
                        .then(Commands.argument("new password", StringArgumentType.word())
                                .executes(this::changePassword)));
        dispatcher.register(changePassword);
    }

    private int changePassword(CommandContext<CommandSourceStack> context) {
        try {
            Player player = context.getSource().getPlayerOrException();
            String username = player.getName().getString();
            String oldPassword = StringArgumentType.getString(context, "old password");
            String newPassword = StringArgumentType.getString(context, "new password");
            if (!AuthMod.getPlayerManager().isLoggedIn(username)) {
                return 0;
            } else if (oldPassword.equals(newPassword)) {
                context.getSource().sendFailure(Component.literal("新密码与旧密码相同!"));
                return 0;
            } else {
                PasswordManager.changePassword(username, newPassword);
                context.getSource().sendSuccess(() -> Component.literal("修改密码成功!"), false);
                AuthMod.getLOGGER().info("玩家" + username + "修改密码成功");
                return 1;
            }
        } catch (CommandSyntaxException e) {
            AuthMod.getLOGGER().warn("修改密码时出现异常:", e);
            return 0;
        }
    }
}



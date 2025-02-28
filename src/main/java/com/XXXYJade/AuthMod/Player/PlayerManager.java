package com.XXXYJade.AuthMod.Player;

import com.XXXYJade.AuthMod.AuthMod;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerManager {
    private final Timer timer = new Timer(true);
    private static final Map<String, Boolean> playersLoggedIn = new HashMap<>();
    private static final ConcurrentMap<String, TimerTask> loginTasks = new ConcurrentHashMap<>();

    public PlayerManager() {
        loadPlayers();
    }

    private void loginTask(Player player) {
        String username = player.getName().getString();
        TimerTask task = new TimerTask() { // 创建一个计时器任务
            @Override
            public void run() {
                if (!isLoggedIn(username)) {
                    ((ServerPlayer) player).connection.disconnect(Component.literal("登陆超时"));
                    AuthMod.getLOGGER().info("玩家" + username + "登录超时");
                    loginTasks.remove(username);    //取消之前的登录任务
                }
            }
        };
        loginTasks.put(username, task);    //添加到计时器任务
        timer.schedule(task, 10 * 1000L); //设置计时器计划时间，如果玩家没有登录则退出游戏
    }

    public void loadPlayers() {
        Set<String> usernameSet = AuthMod.getPasswordManager().getPasswordMap().keySet();
        for (String username : usernameSet) {
            playersLoggedIn.put(username, false);
        }
    }

    public boolean isLoggedIn(String username) {
        if (!playersLoggedIn.containsKey(username)) {
            playersLoggedIn.put(username, false);
        }
        return playersLoggedIn.get(username);
    }

    public void storeSpawn(Player player) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        player.getPersistentData().putDouble("spawnX", x);
        player.getPersistentData().putDouble("spawnY", y);
        player.getPersistentData().putDouble("spawnZ", z);
    }

    public void moveToSpawn(Player player) {
        double x = player.getPersistentData().getDouble("spawnX");
        double y = player.getPersistentData().getDouble("spawnY");
        double z = player.getPersistentData().getDouble("spawnZ");
        player.teleportTo(x, y, z);
    }

    public void addPlayer(Player player) {
        String username = player.getName().getString();
        playersLoggedIn.put(username, false);
        loginTask(player);
        AuthMod.getLOGGER().info("玩家" + username + "加入游戏");
    }

    public void logIn(Player player) {
        playersLoggedIn.put(player.getName().getString(), true);
    }

    public void logout(Player player) {
        String username = player.getName().getString();
        playersLoggedIn.remove(username);
        cancelLoginTimer(username);
    }

    public void cancelLoginTimer(String username) {
        TimerTask task = loginTasks.remove(username);
        if (task != null) {
            task.cancel();
        }
        AuthMod.getLOGGER().info("玩家" + username + "退出游戏");
    }
}

package com.XXXYJade.AuthMod.Password;

import com.XXXYJade.AuthMod.AuthMod;
import com.XXXYJade.AuthMod.Config;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PasswordManager {
    private static PasswordManager instance;

    private static final Map<String, String> passwordMap = new HashMap<>();
    private static String passwordDataFilePath;
    private static final Gson gson = new Gson();
    private static int passwordMaxLenth;
    private static int passwordMinLenth;

    public static PasswordManager getInstance() {
        if (instance == null) {
            instance = new PasswordManager();
        }
        return instance;
    }

    public Map<String, String> getPasswordMap() {
        return passwordMap;
    }

    public PasswordManager() {
        passwordDataFilePath = Config.getInstance().getPasswordDataFilePath();
        passwordMaxLenth = Config.getInstance().getPasswordMaxLenth();
        passwordMinLenth = Config.getInstance().getPasswordMinLenth();
        loadPasswords();
    }

    public static void loadPasswords() {
        try {
            Path path = Path.of(passwordDataFilePath);
            if (Files.exists(path)) {   //文件如果存在
                try (FileReader reader = new FileReader(path.toFile())) {
                    Map<String, String> loadedPasswords = gson.fromJson(reader, new TypeToken<Map<String, String>>() {
                    }.getType());  //读取文件中的密码数据
                    if (loadedPasswords != null) {
                        passwordMap.putAll(loadedPasswords);  //如果文件不为空，将读取到的数据赋值给passwords
                        AuthMod.getLOGGER().info("密码文件读取成功!");
                    }
                }
            }
        } catch (IOException e) {
            AuthMod.getLOGGER().warn("密码文件加载时出现异常:", e);
        }
    }

    public static void savePasswords() {
        try {
            Path path = Path.of(passwordDataFilePath);
            Files.createDirectories(path.getParent());
            try (FileWriter writer = new FileWriter(path.toFile())) {
                gson.toJson(passwordMap, writer);
                AuthMod.getLOGGER().info("密码文件更新成功!");
            }
        } catch (IOException e) {
            AuthMod.getLOGGER().warn("密码文件更新成功时出现异常:", e);
        }
    }

    public void addNewPassword(String username, String password) {
        String encryptedPassword = PasswordUtils.encryptPassword(password);
        try {
            passwordMap.put(username, encryptedPassword);
            AuthMod.getLOGGER().info("玩家" + username + "注册成功");
        } catch (RuntimeException e) {
            AuthMod.getLOGGER().warn("添加新密码时出现异常:", e);
        }
        savePasswords();
    }

    public boolean hasPassword(String username) {
        if (passwordMap.get(username) == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getPassword(String username) {
        return passwordMap.get(username);
    }

    public static boolean checkPasswordLenth(String password) {
        if (password.length() < passwordMinLenth || password.length() > passwordMaxLenth) {
            return false;
        } else {
            return true;
        }
    }

    public static void changePassword(String username, String newPassword) {
        String encryptedNewPassword = PasswordUtils.encryptPassword(newPassword);
        passwordMap.put(username, encryptedNewPassword);
        savePasswords();
    }
}

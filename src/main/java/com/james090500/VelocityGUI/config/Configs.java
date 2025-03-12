package com.james090500.VelocityGUI.config;

import com.james090500.VelocityGUI.VelocityGUI;
import com.james090500.VelocityGUI.helpers.PlaceholderParser;
import com.moandjiezana.toml.Toml;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;

public class Configs {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    @Getter private static HashMap<String, Panel> panels = new HashMap<>();

    /**
     * Loads the config files.
     * @param velocityGUI
     */
    public static void loadConfigs(VelocityGUI velocityGUI) {
        //Create data directory
        Path dataDirectory = velocityGUI.getDataDirectory();
        if(!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdir();
        }

        //Create default config
        File configFile = new File(dataDirectory + "/config.toml");
        if(!configFile.exists()) {
            try (InputStream in = VelocityGUI.class.getResourceAsStream("/config.toml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //Load config
        config = new Toml().read(configFile).to(Config.class);


        //Create panel directory
        File panelDir = new File(dataDirectory.toFile() + "/panels");
        if(!panelDir.exists()) {
            panelDir.mkdir();
        }
        //Load default example
        if(panelDir.listFiles().length == 0) {
            try (InputStream in = VelocityGUI.class.getResourceAsStream("/example.toml")) {
                Files.copy(in, new File(panelDir + "/example.toml").toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Load config
        for(File file : panelDir.listFiles()) {
            Panel panel = new Toml().read(file).to(Panel.class);
            panels.put(panel.getName(), panel);
        }

        //Create lang directory
        File langDir = new File(dataDirectory.toFile() + "/lang");
        if(!langDir.exists()) {
            langDir.mkdir();
        }
        //load default data
        for(String lang : new String[]{"en-us", "it-it"}){ //default supported languages
            File langFile = new File(langDir + "/"+lang+".toml");
            if(!langFile.exists()) {
                try (InputStream in = VelocityGUI.class.getResourceAsStream("/lang/"+lang+".toml")) {
                    Files.copy(in, langFile.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //load lang
        lang = new Lang();
        Toml tomLang = new Toml().read(new File(langDir + "/"+config.getLang()+".toml"));
        //check all keys and load missing ones, then translate all strings
        for (Field field : Lang.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                String value = tomLang.getString(field.getName());
                if(value == null){
                    velocityGUI.getLogger().error("Missing lang string: \"{}\"! This will result in errors.", field.getName());
                } else {
                    //edit color chars
                    field.set(lang, miniMessage.deserialize(PlaceholderParser.convertLegacyToMiniMessage(value)));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Getter private static Config config;
    @Getter private static Lang lang;

    public class Config {
        @Getter private String lang;
    }

    public static class Lang {
        @Getter private Component alreadyConnected;
        @Getter private Component availablePanels;
        @Getter private Component onlyPlayersCanRunThis;
        @Getter private Component panelNotFound;
        @Getter private Component playerNotFound;
        @Getter private Component pluginReloaded;
        @Getter private Component unknownArgs;
        @Getter private Component unknownServer;
    }

    public class Panel {

        @Getter private String name;
        @Getter private String perm;
        @Getter private int rows ;
        @Getter private String title;
        @Getter private String empty;
        @Getter private String sound;
        @Getter private String emptysound;
        @Getter private String[] commands;
        @Getter private HashMap<Integer, Item> items;
        @Getter private String[] servers;

        @Override
        public String toString() {
            return "Panel{" +
                    "name='" + name + '\'' +
                    ", perm='" + perm + '\'' +
                    ", rows=" + rows +
                    ", title='" + title + '\'' +
                    ", empty='" + empty + '\'' +
                    ", sound='" + sound + '\'' +
                    ", emptysound='" + emptysound + '\'' +
                    ", items=" + items +
                    ", servers=" + Arrays.toString(servers) +
                    '}';
        }
    }

    public class Item {

        private String name;
        @Getter private String material;
        private byte stack;
        @Getter private String[] lore;
        @Getter private boolean enchanted;
        @Getter private String[] commands;

        /**
         * Return name or make empty if missed from config
         * @return
         */
        public String getName() {
            return (name != null) ? name : "&f";
        }

        /**
         * If stack is missed from config make it 1
         * @return
         */
        public byte getStack() {
            return (stack > 0) ? stack : 1;
        }

        @Override
        public String toString() {
            return "GuiItem{" +
                    "name='" + name + '\'' +
                    ", material='" + material + '\'' +
                    ", stack=" + stack +
                    ", lore=" + Arrays.toString(lore) +
                    ", enchanted=" + enchanted +
                    '}';
        }
    }

}

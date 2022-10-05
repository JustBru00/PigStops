package me.pigalala.pitminigame;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import me.pigalala.pitminigame.commands.CommandPit;
import me.pigalala.pitminigame.enums.PitGame;
import me.pigalala.pitminigame.listeners.PitListener;
import me.pigalala.pitminigame.pit.Pit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public final class PigStops extends JavaPlugin {

    private static PigStops plugin;

    private Material pitBlock;

    @Override
    public void onEnable() {
        plugin = this;

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        new PitListener();

        setupCommands();
        loadPitBlock();
        loadPitGame();
    }

    @Override
    public void onDisable(){
        Bukkit.getOnlinePlayers().forEach(Pit::reset);
    }

    private void loadPitBlock(){
        try {
            setPitBlock(Material.valueOf(getConfig().getString("pitBlock").toUpperCase()));
        } catch (IllegalArgumentException e) {
            getLogger().log(Level.SEVERE, "'pitBlock' in PigStops plugin config does not exist");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadPitGame(){
        try {
            setDefaultPitGame(PitGame.valueOf(getConfig().getString("pitGame").toUpperCase()));
        } catch (IllegalArgumentException e) {
            getLogger().log(Level.SEVERE, "'pitGame' in PigStops plugin config does not exist");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void setupCommands(){
        PaperCommandManager commandManager = new PaperCommandManager(plugin);
        commandManager.registerCommand(new CommandPit());

        commandManager.getCommandCompletions().registerAsyncCompletion("pits", c -> {
            List<String> games = new ArrayList<>();
            Arrays.stream(PitGame.values()).toList().forEach(e -> {
                games.add(e.toString());
            });
            return ImmutableList.copyOf(games.toArray(new String[0]));
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("blocks", c-> {
            List<String> blocks = new ArrayList<>();
            Arrays.stream(Material.values()).toList().forEach(e -> {
                if(!e.isSolid()) return;
                if(!e.isBlock()) return;
                blocks.add(e.toString().toLowerCase(Locale.ROOT));
            });
            return ImmutableList.copyOf(blocks.toArray(new String[0]));
        });
    }

    public static PigStops getPlugin() {return plugin;}

    public void setDefaultPitGame(PitGame pit) {
        getConfig().set("pitGame", pit.name());
        saveConfig();
        reloadConfig();
    }

    public PitGame getDefaultPitGame() {
        return PitGame.valueOf(getConfig().getString("pitGame"));
    }

    public void setPitBlock(Material block) {
        pitBlock = block;
        getConfig().set("pitBlock", pitBlock.name().toLowerCase());
        saveConfig();
        reloadConfig();
    }
    public Material getPitBlock() {return pitBlock;}
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

import audio.AudioPlayer;
import audio.Playlist;
import audio.SoundManager;
import audio.Source;
import audio.Track;
import java.util.ArrayList;

/**
 *
 * @author Kyle
 */
public class AudioManager implements AudioPlayerIntf {
    
    private SoundManager am;
    private ArrayList<Track> tracks = new ArrayList<>();
    
    public static String FIRE = "FIRE";
    public static String GAME = "GAME";
    public static String MENU = "MENU";
    public static String MOTHERSHIP = "MOTHERSHIP";
    public static String MOTHERSHIP_WARNING = "MOTHERSHIP_WARNING";
    public static String POWER_UP = "POWER_UP";
    public static String LOSE_POWER_UP = "LOSE_POWER_UP";
    public static String HURT_ALIEN = "HURT_ALIEN";
    public static String KILL_ALIEN = "KILL_ALIEN";
    public static String HURT_SHIP = "HURT_SHIP";
    public static String MOTHERSHIP_EXPLODE = "MOTHERSHIP_EXPLODE";
    public static String LEVEL_UP = "LEVEL_UP";

    
    {
        tracks.add(new Track(GAME, Source.RESOURCE, "/spaceinvaders/game.wav"));
        tracks.add(new Track(MOTHERSHIP, Source.RESOURCE, "/spaceinvaders/mothership.wav"));
        tracks.add(new Track(MOTHERSHIP_WARNING, Source.RESOURCE, "/spaceinvaders/mothership_warning.wav"));
        tracks.add(new Track(FIRE, Source.RESOURCE, "/spaceinvaders/fire.wav"));
        tracks.add(new Track(MENU, Source.RESOURCE, "/spaceinvaders/menu.wav"));
        tracks.add(new Track(POWER_UP, Source.RESOURCE, "/spaceinvaders/powerup.wav"));
        tracks.add(new Track(LOSE_POWER_UP, Source.RESOURCE, "/spaceinvaders/lose_powerup.wav"));
        tracks.add(new Track(LEVEL_UP, Source.RESOURCE, "/spaceinvaders/levelup.wav"));
        tracks.add(new Track(HURT_ALIEN, Source.RESOURCE, "/spaceinvaders/hurt_alien.wav"));
        tracks.add(new Track(KILL_ALIEN, Source.RESOURCE, "/spaceinvaders/kill_alien.wav"));
        tracks.add(new Track(HURT_SHIP, Source.RESOURCE, "/spaceinvaders/hurt_ship.wav"));
        tracks.add(new Track(MOTHERSHIP_EXPLODE, Source.RESOURCE, "/spaceinvaders/mothership_explode.wav"));

        am = new SoundManager(new Playlist(tracks));
    }
    
    @Override
    public void playAudio(String name, boolean loop) {
        if (loop == true) {
            am.play(name, Integer.MAX_VALUE);
        } else {
            am.play(name);
        }
    }

    @Override
    public void stopAudio(String name) {
        am.stop(name);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spaceinvaders;

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
    public static String POWER_UP = "POWER_UP";
    public static String LOSE_POWER_UP = "LOSE_POWER_UP";
    public static String HURT_ALIEN = "HURT_ALIEN";
    public static String KILL_ALIEN = "KILL_ALIEN";

    
    {
        tracks.add(new Track(GAME, Source.RESOURCE, "/spaceinvaders/game.wav"));
        tracks.add(new Track(FIRE, Source.RESOURCE, "/spaceinvaders/fire.wav"));
        tracks.add(new Track(MENU, Source.RESOURCE, "/spaceinvaders/menu.wav"));
        tracks.add(new Track(POWER_UP, Source.RESOURCE, "/spaceinvaders/powerup.wav"));
        tracks.add(new Track(LOSE_POWER_UP, Source.RESOURCE, "/spaceinvaders/lose_powerup.wav"));
        tracks.add(new Track(HURT_ALIEN, Source.RESOURCE, "/spaceinvaders/hurt_alien.wav"));
        tracks.add(new Track(KILL_ALIEN, Source.RESOURCE, "/spaceinvaders/kill_alien.wav"));

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

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
    
    private SoundManager sm;
    private ArrayList<Track> tracks = new ArrayList<>();
    
    {
        tracks.add(new Track("GAME", Source.RESOURCE, "/spaceinvaders/game_new.wav"));
        tracks.add(new Track("FIRE", Source.RESOURCE, "/spaceinvaders/fire.wav"));
        sm = new SoundManager(new Playlist(tracks));
    }
    
    @Override
    public void playAudio(String name, int loopCount) {
        sm.play(name, loopCount);
    }

    @Override
    public void stopAudio(String name) {
        sm.stop(name);
    }
    
    
    
}

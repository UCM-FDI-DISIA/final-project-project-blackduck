package ui;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Manages all audio playback including sound effects and background music.
 * Handles volume control and music looping.
 */
public class AudioManager {
    private static AudioManager instance;

    private int volumeLevel = 2; // 0=Off, 1=Low, 2=Medium, 3=High
    private boolean musicEnabled = true;
    private Clip backgroundMusicClip;

    // Volume multipliers for each level
    private static final float[] VOLUME_MULTIPLIERS = {
        0.0f,   // Off
        0.3f,   // Low
        0.6f,   // Medium
        1.0f    // High
    };

    private AudioManager() {
        // Private constructor for singleton
    }

    /**
     * Get singleton instance
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Set volume level (0=Off, 1=Low, 2=Medium, 3=High)
     */
    public void setVolumeLevel(int level) {
        if (level < 0 || level > 3) {
            level = 2; // Default to medium
        }
        this.volumeLevel = level;

        // Update background music volume if playing
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            setClipVolume(backgroundMusicClip, VOLUME_MULTIPLIERS[level]);
        }
    }

    /**
     * Get current volume level
     */
    public int getVolumeLevel() {
        return volumeLevel;
    }

    /**
     * Set whether background music is enabled
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;

        if (enabled) {
            playBackgroundMusic();
        } else {
            stopBackgroundMusic();
        }
    }

    /**
     * Check if background music is enabled
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Play a sound effect (click, win, lose, etc.)
     */
    public void playSoundEffect(String filename) {
        if (volumeLevel == 0) {
            return; // Volume is off
        }

        new Thread(() -> {
            try {
                File soundFile = new File(filename);
                if (!soundFile.exists()) {
                    return; // File not found, silently fail
                }

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);

                // Set volume
                setClipVolume(clip, VOLUME_MULTIPLIERS[volumeLevel]);

                // Play and dispose when done
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                // Silently fail - audio is optional
            }
        }).start();
    }

    /**
     * Play bet sound effect
     */
    public void playBetSound() {
        playSoundEffect("src/data/audio/bet.wav");
    }

    /**
     * Play win sound effect
     */
    public void playWinSound() {
        playSoundEffect("src/data/audio/win.wav");
    }

    /**
     * Play lose sound effect
     */
    public void playLoseSound() {
        playSoundEffect("src/data/audio/lose.wav");
    }

    /**
     * Play push (tie) sound effect
     */
    public void playPushSound() {
        playSoundEffect("src/data/audio/push.wav");
    }

    /**
     * Play background music (loops continuously)
     */
    public void playBackgroundMusic() {
        if (!musicEnabled || volumeLevel == 0) {
            System.out.println("Music not playing: musicEnabled=" + musicEnabled + ", volumeLevel=" + volumeLevel);
            return;
        }

        // Stop existing music if playing
        stopBackgroundMusic();

        new Thread(() -> {
            try {
                File musicFile = new File("src/data/audio/background_music.wav");
                if (!musicFile.exists()) {
                    System.err.println("Background music file not found: " + musicFile.getAbsolutePath());
                    return; // File not found, silently fail
                }

                System.out.println("Starting background music...");
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(musicFile);
                backgroundMusicClip = AudioSystem.getClip();
                backgroundMusicClip.open(audioIn);

                // Set volume
                setClipVolume(backgroundMusicClip, VOLUME_MULTIPLIERS[volumeLevel] * 0.5f); // Music at 50% of effect volume

                // Loop continuously
                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusicClip.start();
                System.out.println("Background music started successfully!");
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Error playing background music: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Stop background music
     */
    public void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
            backgroundMusicClip = null;
        }
    }

    /**
     * Set the volume of a clip using gain control
     */
    private void setClipVolume(Clip clip, float volume) {
        if (clip == null) {
            return;
        }

        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Convert linear volume (0.0 to 1.0) to decibels
            float dB = (float) (Math.log(Math.max(0.0001, volume)) / Math.log(10.0) * 20.0);

            // Clamp to valid range
            dB = Math.max(gainControl.getMinimum(), Math.min(dB, gainControl.getMaximum()));

            gainControl.setValue(dB);
        } catch (IllegalArgumentException e) {
            // Gain control not supported, ignore
        }
    }
}

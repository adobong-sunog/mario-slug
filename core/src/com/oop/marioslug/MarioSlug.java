package com.oop.marioslug;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.oop.marioslug.Screens.PlayScreen;

public class MarioSlug extends Game {

	// Height and width of the game
	public static final int VWidth = 400;
	public static final int VHeight = 208;

	// Scale for the game
	public static final float PPM = 100f;

	// Set bit for filtering which objects mario and other entities can collide with
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short BUFFSHROOM_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short GOONS_BIT = 64;
	public static final short GOONS_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	public static final short FIREBALL_BIT = 1024;
	public static final short FINISH_BIT = 2048;
	public SpriteBatch batch;
	Texture img;

	public static AssetManager manager;

	@Override
	public void create () {
		batch = new SpriteBatch();

		// Load bgm and sfx
		manager = new AssetManager();
		manager.load("audio/music/mario_music.ogg", Music.class);
		manager.load("audio/sounds/coin.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.load("audio/sounds/breakblock.wav", Sound.class);
		manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
		manager.load("audio/sounds/powerup.wav", Sound.class);
		manager.load("audio/sounds/powerdown.wav", Sound.class);
		manager.load("audio/sounds/mariodie.wav", Sound.class);
		manager.load("audio/sounds/stomp.wav", Sound.class);
		manager.finishLoading();
		setScreen(new PlayScreen(this));
	}

	// Dispose files to prevent memory leaks
	@Override
	public void dispose() {
		super.dispose();
		manager.dispose();
	}

	@Override
	public void render () {
		super.render();
	}

}

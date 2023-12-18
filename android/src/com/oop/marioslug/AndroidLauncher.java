package com.oop.marioslug;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Screens.PlayScreen;

public class AndroidLauncher extends AndroidApplication {
	private MarioSlug game;
	private boolean inGameScreen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inGameScreen = true;

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		game = new MarioSlug();
		initialize(game, config);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (game != null) {
			game.resume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (game != null) {
			game.pause();
		}
	}

	@Override
	public void onBackPressed() {
		if (inGameScreen) {
			// If in the game screen, go back to the main menu
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish(); // Optional: Close the current activity if you don't want to keep it in the back stack
		} else {
			// Default back button action
			super.onBackPressed();
		}
	}

	public void setInGameScreen(boolean inGameScreen) {
		this.inGameScreen = inGameScreen;
	}

}

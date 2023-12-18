package com.oop.marioslug.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Scenes.HUD;

public class GameOver implements Screen {

    private Viewport viewport;
    private Stage stage;
    private Game game;

    public static String message;
    public GameOver(Game game) {
        this.game = game;
        viewport = new FitViewport(MarioSlug.VWidth, MarioSlug.VHeight, new OrthographicCamera());
        stage = new Stage(viewport, ((MarioSlug) game).batch);
        message = "GAME OVER";

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label lblGameOverTitle = new Label(message, font);
        Label lblScoreTitle = new Label("| SCORE |", font);
        Label lblScore = HUD.lblScore;
        Label lblPlayAgain = new Label("Click anywhere to play again", font);

        table.add(lblGameOverTitle).expandX();
        table.row();
        table.add(lblScoreTitle).expandX().padTop(10f);
        table.row();
        table.add(lblScore).expandX().padTop(10f);
        table.row();
        table.add(lblPlayAgain).expandX().padTop(10f);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            game.setScreen(new PlayScreen((MarioSlug) game));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

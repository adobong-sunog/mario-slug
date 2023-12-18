package com.oop.marioslug.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.oop.marioslug.MarioSlug;

public class HUD implements Disposable {

    public Stage stage;
    private Viewport viewport;
    public static Integer worldTimer;
    private Float timeCount;
    public static Integer score;
    public static Integer charges;
    public static Label lblCharges;
    public static Label lblScore;
    private Label lblCountDown;
    private Label lblTopCharges;
    private Label lblTopScore;
    private Label lblTopTimer;

    public  HUD(SpriteBatch sb) {
        worldTimer = 200;
        timeCount = 0.0F;
        score = charges = 0;

        viewport = new FitViewport(MarioSlug.VWidth, MarioSlug.VHeight, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        // Initialize stage layout using a table
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        // Set labels
        lblCharges = new Label(String.format("%03d", charges), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        lblScore = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        lblCountDown = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        lblTopCharges = new Label(String.format("CHARGES"), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        lblTopScore = new Label(String.format("SCORE"), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        lblTopTimer = new Label(String.format("TIME"), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // Set stats on top of the screen
        table.add(lblTopCharges).expandX().padTop(10);
        table.add(lblTopScore).expandX().padTop(10);
        table.add(lblTopTimer).expandX().padTop(10);
        table.row();
        table.add(lblCharges).expandX();
        table.add(lblScore).expandX();
        table.add(lblCountDown).expandX();

        stage.addActor(table);
    }

    // Timer
    public void update(float dt) {
        timeCount += dt;
        if (timeCount >= 1) {
            worldTimer--;
            lblCountDown.setText(String.format("%03d", worldTimer));
            timeCount = 0f;
        }
    }

    // Score
    public static void addScore(int value){
        score += value;
        lblScore.setText(String.format("%06d", score));
    }

    public static void addCharges(int value){
        charges += value;
        lblCharges.setText(String.format("%03d", charges));
    }

    public static void removeCharges(int value){
        charges -= value;
        lblCharges.setText(String.format("%03d", charges));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

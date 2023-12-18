package com.oop.marioslug.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Screens.PlayScreen;
import com.oop.marioslug.Scenes.HUD;

import Sprites.Mario;

public class OnScreenControls {
    Viewport viewport;
    Stage stage;
    boolean upPressed, throwPressed, leftPressed, rightPressed;
    OrthographicCamera cam;

    public OnScreenControls() {
        cam = new OrthographicCamera();
        viewport = new FitViewport(MarioSlug.VWidth, MarioSlug.VHeight, cam);
        stage = new Stage(viewport, PlayScreen.batch);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();

        table.left().bottom();

        Image imgUp = new Image(new Texture("screen_controls/btnJump.png"));
        imgUp.setSize(35, 35);
        imgUp.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        Image imgThrow = new Image(new Texture("screen_controls/btnThrow.png"));
        imgThrow.setSize(35, 35);
        imgThrow.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                throwPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                throwPressed = false;
            }
        });

        Image imgLeft = new Image(new Texture("screen_controls/btnLeft.png"));
        imgLeft.setSize(35, 35);
        imgLeft.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        Image imgRight = new Image(new Texture("screen_controls/btnRight.png"));
        imgRight.setSize(35, 35);
        imgRight.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        table.add();
        table.add(imgUp).size(imgUp.getWidth(), imgUp.getHeight());
        table.add().pad(0,5,5, 5);
        table.add(imgThrow).size(imgThrow.getWidth(), imgThrow.getHeight()).bottom().right().pad(0, 210, 0,0);
        table.row().pad(5, 5, 5, 5);
        table.add(imgLeft).size(imgLeft.getWidth(), imgLeft.getHeight());
        table.add();
        table.add(imgRight).size(imgRight.getWidth(), imgRight.getHeight());
        table.row().padBottom(5);
        table.add();
        table.add();
        table.add();

        stage.addActor(table);
    }

    public void draw() {
        stage.draw();
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isThrowPressed() {
        return throwPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}

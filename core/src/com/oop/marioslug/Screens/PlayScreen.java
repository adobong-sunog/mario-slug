package com.oop.marioslug.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Scenes.HUD;
import com.oop.marioslug.Scenes.OnScreenControls;

import java.util.concurrent.LinkedBlockingQueue;

import Sprites.BoxShrooms;
import Sprites.Goons;
import Sprites.Item;
import Sprites.ItemDefinition;
import Sprites.Mario;
import Tools.CollisionChecker;
import Tools.WorldCreator;


public class PlayScreen implements Screen {
    private MarioSlug game;
    private OrthographicCamera gameCam;
    public Viewport gamePort;
    private HUD hud;

    // Map
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // Audio
    private Music music;

    // World properties and entities
    private World world;
    private Stage stage;
    private Box2DDebugRenderer b2dr;
    private Mario player;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDefinition> itemsToSpawn;
    private WorldCreator creator;
    private TextureAtlas atlas;
    public VictoryScreen victoryScreen;

    // Controller
    public static SpriteBatch batch;
    private OnScreenControls onScreenControls;

    // Timer
    public static final float FIREBALL_COOLDOWN = 2.0f;
    private static final float GAME_TIMER = 200;
    private float gameTimer;
    public static float fireBallCoolDownTimer;

    // Keyboard input and onscreen controls
    public void handleInput(float dt){
        if (player.currentState != Mario.State.MARIOVER) {
            if ((Gdx.input.isKeyPressed(Input.Keys.SPACE)) || onScreenControls.isUpPressed()) {
                player.jump();
            }
            if ((Gdx.input.isKeyPressed(Input.Keys.D) || onScreenControls.isRightPressed()) && player.body.getLinearVelocity().x <= 0.6)
            {
                player.body.applyLinearImpulse(new Vector2(0.4f, 0), player.body.getWorldCenter(), true);
            }
            if ((Gdx.input.isKeyPressed(Input.Keys.A) || onScreenControls.isLeftPressed()) && player.body.getLinearVelocity().x >= -0.6) {
                player.body.applyLinearImpulse(new Vector2(-0.4f, 0), player.body.getWorldCenter(), true);
            }
            if ((Gdx.input.isKeyPressed(Input.Keys.S)) || onScreenControls.isThrowPressed()) {
                // Allow mario to only throw a fireball if he has charges and also after every 2 seconds
                if (HUD.charges > 0) {
                    if (fireBallCoolDownTimer <= 0) {
                        player.fire();
                        HUD.removeCharges(1);
                        fireBallCoolDownTimer = FIREBALL_COOLDOWN;
                    }
                }
            }
        }
    }



    public void update(float dt){
        handleInput(dt);
        handleSpawnItem();

        world.step(1/60f, 2, 2);

        player.update(dt);
        for (Goons goons : creator.getGoons()) {
            goons.update(dt);
            // Let the enemies move only when mario is near
            if(goons.getX() < player.getX() + 224 / MarioSlug.PPM){
                goons.body.setActive(true);
            }
        }

        for (Item item : items) {
            item.update(dt);
        }

        hud.update(dt);
        updateGameTimer(dt);

        // Move camera view only if mario is still alive
        if (player.currentState != Mario.State.MARIOVER) {
            gameCam.position.x = player.body.getPosition().x;
        }

        // Update fireball cooldown timer
        if (fireBallCoolDownTimer > 0) {
            fireBallCoolDownTimer -= dt;
        }

        gameCam.update();
        renderer.setView(gameCam);
    }

    // Set to game over once the timer has run out
    private void updateGameTimer(float dt) {
        if (gameTimer <= 0 && player.currentState != Mario.State.MARIOVER) {
            player.hit(null);
        }
        else {
            gameTimer -= dt;
        }
    }

    // Load the game and set its screen size
    public PlayScreen(MarioSlug game){
        atlas = new TextureAtlas("Entities.pack");
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(MarioSlug.VWidth / MarioSlug.PPM, MarioSlug.VHeight / MarioSlug.PPM, gameCam);
        hud = new HUD(game.batch);

        fireBallCoolDownTimer = 0;
        gameTimer = GAME_TIMER;

        // Load and render map
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("stage1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1f / MarioSlug.PPM);

        // Set coordinates so that the game doesn't default to 0, 0
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        batch = new SpriteBatch();

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        // Set object properties
        creator = new WorldCreator(this);

        // Create mario
        player = new Mario(this);

        onScreenControls = new OnScreenControls();

        world.setContactListener(new CollisionChecker());

        music = MarioSlug.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDefinition>();

    }

    public void spawnItem(ItemDefinition itemDef) {
        itemsToSpawn.add(itemDef);
    }

    public void handleSpawnItem() {
        if (!itemsToSpawn.isEmpty()) {
           ItemDefinition itemDef = itemsToSpawn.poll();
           if (itemDef.type == BoxShrooms.class) {
               items.add(new BoxShrooms(this, itemDef.position.x, itemDef.position.y));
           }

        }
    }
    public TextureAtlas getAtlas() {
        return atlas;
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void show() {

    }

    // Clear the screen before rendering the game
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render map and box2d object properties.
        renderer.render();
        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Goons goons : creator.getGoons()) {
            goons.draw(game.batch);
        }
        for (Item item : items) {
            item.draw(game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        onScreenControls.draw();

        // Always check if mario's state is over after every rendering to load the game over screen
        if (gameOver()) {
            game.setScreen(new GameOver(game));
            dispose();
        }

        if (gameWon()) {
            game.setScreen(new VictoryScreen(game));
            CollisionChecker.checkGameFinished = 0;
            dispose();
        }
    }

    public boolean gameWon() {
        if (CollisionChecker.checkGameFinished > 0) {
            return true;
        }
        return false;
    }
    public boolean gameOver() {
        if (player.currentState == Mario.State.MARIOVER && player.getStateTimer() > 2) {
            return true;
        }
        return false;
    }

    // Manual resizing test for desktop application resizing
    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        onScreenControls.resize(width, height);
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

    // Dispose components when quitting the game to preserve memory.
    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
        batch.dispose();
    }
}

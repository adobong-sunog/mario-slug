package Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Screens.PlayScreen;

public class Mario extends Sprite {

    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, MARIOVER };
    public State currentState;
    public State previousState;

    public World world;
    public Body body;
    private PlayScreen screen;

    private float stateTimer;
    private boolean runToRight;
    private boolean isBig;
    private boolean BigRunAnimation;
    private boolean timeToDefineBigState;
    private boolean setRedefineMario;
    private boolean itsMariover;

    private TextureRegion MarioStillPosition;
    private TextureRegion bigStillPosition;
    private TextureRegion jump;
    private TextureRegion bigJump;
    private TextureRegion mariOver;
    private Animation<TextureRegion> run;
    private Animation<TextureRegion> bigRun;
    private Animation<TextureRegion> growMario;

    private Array<FireBall> fireBalls;

    public Mario(PlayScreen screen) {
        this.screen = screen;
        this.world = screen.getWorld();

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runToRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        // Running animation
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        }
        run = new Animation(0.1f, frames);
        frames.clear();
        for (int i = 1; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        }
        bigRun = new Animation(0.1f, frames);
        frames.clear();

        // Growing animation
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<TextureRegion>(0.2f, frames);

        // Jumping animation
        jump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        // Game over texture
        mariOver = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        // Standing still texture
        MarioStillPosition = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16,16);
        bigStillPosition = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        defineMario();

        setBounds(0,0, 16 / MarioSlug.PPM, 16 / MarioSlug.PPM);
        setRegion(MarioStillPosition);

        fireBalls = new Array<FireBall>();
    }

    public void update(float dt) {
        // Lower down big mario's sprite as he's floating on the default position
        if (isBig) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2 - 6 / MarioSlug.PPM);
        }
        else {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        }
        setRegion(getFrame(dt));
        if (timeToDefineBigState) {
            defineBigMario();
        }
        if (setRedefineMario) {
            redefineMario();
        }

        // Set to game over if mario falls off the ground
        if (body.getPosition().y<0 && !itsMariover){
            body.setLinearVelocity(0,0);
            body.getPosition().y=0;
            hit(null);
        }

        // Remove fireballs after a certain amount of time or if it collides with an enemy
        for (FireBall ball : fireBalls) {
            ball.update(dt);
            if (ball.isDestroyed()) {
                fireBalls.removeValue(ball, true);
            }
        }
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case MARIOVER:
                region = mariOver;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer)) {
                    BigRunAnimation = false;
                }
                break;
            case JUMPING:
                region = isBig ? bigJump : jump;
                break;
            case RUNNING:
                region = isBig? bigRun.getKeyFrame(stateTimer, true) : run.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = isBig ? bigStillPosition : MarioStillPosition;
                break;
        }

        // Reverse mario's direction based on the control input
        if ((body.getLinearVelocity().x < 0 || !runToRight) && !region.isFlipX()) {
            region.flip(true, false);
            runToRight = false;
        }
        else if ((body.getLinearVelocity().x > 0 || runToRight) && region.isFlipX()) {
            region.flip(true, false);
            runToRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    // Check mario's current state
    public State getState() {
        if (itsMariover) {
            return State.MARIOVER;
        }
        else if (BigRunAnimation) {
            return State.GROWING;
        }
        else if (body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
            return State.JUMPING;
        }
        else if (body.getLinearVelocity().y < 0) {
            return State.FALLING;
        }
        else if (body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        }
        else
            return State.STANDING;
    }

    // Function to enlarge mario
    public void grow() {
        BigRunAnimation = true;
        isBig = true;
        timeToDefineBigState = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        MarioSlug.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    // Shrink big mario if he collides with an enemy and set the game to game over state then remove mario from the game if he collides again with an enemy in his normal state
    public void hit(Goons goons) {
        if (goons instanceof Turtles && ((Turtles) goons).getCurrentState() == Turtles.State.STILL_STOMPED) {
            ((Turtles) goons).kick(this.getX() <= goons.getX() ? Turtles.KICK_SPEED_R : Turtles.KICK_SPEED_L);
        }
        else {
            if (isBig) {
                isBig = false;
                setRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioSlug.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                MarioSlug.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                MarioSlug.manager.get("audio/sounds/mariodie.wav", Sound.class).play();

                itsMariover = true;
                Filter filter = new Filter();
                filter.maskBits = MarioSlug.NOTHING_BIT;
                for (Fixture fixture : body.getFixtureList()) {
                    fixture.setFilterData(filter);
                }
                // Set mario to go to the center then drop him to make him look like he fell off the world
                body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
            }
        }
    }

    // Prevent mario from being able to spam jump
    public void jump(){
        if ( currentState != State.JUMPING ) {
            body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }

    public void fire() {
        fireBalls.add(new FireBall(screen, body.getPosition().x, body.getPosition().y, runToRight ? true : false));
    }

    public void draw(Batch batch) {
        super.draw(batch);
        for(FireBall ball : fireBalls) {
            ball.draw(batch);
        }
    }

    public boolean isBig() {
        return isBig;
    }
    public boolean itsOver() {
        return itsMariover;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    // Reduce mario's size back to normal when he gets hit by an enemy
    public void redefineMario() {
        Vector2 position = body.getPosition();
        world.destroyBody(body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6f / MarioSlug.PPM);

        fixtureDef.filter.categoryBits = MarioSlug.MARIO_BIT;

        // Set objects that mario can collide with
        fixtureDef.filter.maskBits = MarioSlug.GROUND_BIT | MarioSlug.BUFFSHROOM_BIT | MarioSlug.BRICK_BIT | MarioSlug.OBJECT_BIT | MarioSlug.GOONS_BIT | MarioSlug.GOONS_HEAD_BIT | MarioSlug.ITEM_BIT | MarioSlug.FINISH_BIT;

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        // Give mario's head a "sensor" to detect whenever he bumps his heads into objects
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioSlug.PPM, 6 / MarioSlug.PPM), new Vector2(2 / MarioSlug.PPM, 6 / MarioSlug.PPM));
        fixtureDef.filter.categoryBits = MarioSlug.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);

        setRedefineMario = false;
    }
    public void defineMario() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32f / MarioSlug.PPM, 32f / MarioSlug.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6f / MarioSlug.PPM);

        fixtureDef.filter.categoryBits = MarioSlug.MARIO_BIT;

        // Set objects that mario can collide with
        fixtureDef.filter.maskBits = MarioSlug.GROUND_BIT | MarioSlug.BUFFSHROOM_BIT | MarioSlug.BRICK_BIT | MarioSlug.OBJECT_BIT | MarioSlug.GOONS_BIT | MarioSlug.GOONS_HEAD_BIT | MarioSlug.ITEM_BIT | MarioSlug.FINISH_BIT;

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        // Give mario's head a "sensor" to detect whenever he bumps his heads into objects
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioSlug.PPM, 6 / MarioSlug.PPM), new Vector2(2 / MarioSlug.PPM, 6 / MarioSlug.PPM));
        fixtureDef.filter.categoryBits = MarioSlug.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
    }

    public void defineBigMario() {
        Vector2 currentPosition = body.getPosition();
        world.destroyBody(body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition.add(0, 10 / MarioSlug.PPM));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6f / MarioSlug.PPM);

        fixtureDef.filter.categoryBits = MarioSlug.MARIO_BIT;

        // Set objects that big mario can collide with
        fixtureDef.filter.maskBits = MarioSlug.GROUND_BIT | MarioSlug.BUFFSHROOM_BIT | MarioSlug.BRICK_BIT | MarioSlug.OBJECT_BIT | MarioSlug.GOONS_BIT | MarioSlug.GOONS_HEAD_BIT | MarioSlug.ITEM_BIT | MarioSlug.FINISH_BIT;

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        shape.setPosition(new Vector2(0, -14 / MarioSlug.PPM));
        body.createFixture(fixtureDef).setUserData(this);

        // Give big mario's head a "sensor" to detect whenever he bumps his heads into objects
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioSlug.PPM, 6 / MarioSlug.PPM), new Vector2(2 / MarioSlug.PPM, 6 / MarioSlug.PPM));
        fixtureDef.filter.categoryBits = MarioSlug.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
        timeToDefineBigState = false;
    }

}

package Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Scenes.HUD;
import com.oop.marioslug.Screens.PlayScreen;

public class Turtles extends Goons {
    public enum State { WALKING, DEAD, STILL_STOMPED, MOVING_STOMPED }
    public State currentState;
    public State previousState;
    private float stateTimer;
    private float TurtleRotation;

    public static final int KICK_SPEED_L = -1;
    public static final int KICK_SPEED_R = 1;

    private TextureRegion shell;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frame;

    private boolean setDestroyGoon;
    private boolean destroyed;
    public Turtles(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frame = new Array<TextureRegion>();
        frame.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frame.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);
        walkAnimation = new Animation<>(0.2f, frame);
        currentState = previousState = State.WALKING;
        TurtleRotation = 0;

        setBounds(getX(), getY(), 16 / MarioSlug.PPM, 24 / MarioSlug.PPM);
    }

    private TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch (currentState) {
            case STILL_STOMPED:
            case MOVING_STOMPED:
                region = shell;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTimer, true);
                break;
        }

        if (velocity.x > 0 && region.isFlipX() == false) {
            region.flip(true, false);
        }
        if (velocity.x < 0 && region.isFlipX() == true) {
            region.flip(true, false);
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_STOMPED;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void killed() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioSlug.NOTHING_BIT;

        for (Fixture fixture : body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        body.applyLinearImpulse(new Vector2(0, 5f), body.getWorldCenter(), true);
    }
    public void onGoonsHit(Goons goons) {
        if (goons instanceof Turtles) {
            if (((Turtles) goons).currentState == State.MOVING_STOMPED && currentState != State.MOVING_STOMPED) {
                killed();
            }
            else if (currentState == State.MOVING_STOMPED && ((Turtles) goons).currentState == State.WALKING) {
                return;
            }
            else {
                reverseVelocity(true, false);
            }
        }
        else if (currentState != State.MOVING_STOMPED) {
            reverseVelocity(true, false);
        }

    }

    public void draw(Batch batch) {
        if (!destroyed) {
            super.draw(batch);
        }
    }
    @Override
    protected void defineGoon() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6f / MarioSlug.PPM);

        fixtureDef.filter.categoryBits = MarioSlug.GOONS_BIT;

        // Set objects that turtles can collide with
        fixtureDef.filter.maskBits = MarioSlug.GROUND_BIT | MarioSlug.BUFFSHROOM_BIT | MarioSlug.BRICK_BIT | MarioSlug.GOONS_BIT | MarioSlug.MARIO_BIT | MarioSlug.OBJECT_BIT | MarioSlug.FIREBALL_BIT;

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        // Turtle "head sensor" like mario's
        PolygonShape head = new PolygonShape();
        Vector2[] vertex = new Vector2[4];
        vertex[0] = new Vector2(-5, 8).scl(1 / MarioSlug.PPM);
        vertex[1] = new Vector2(5, 8).scl(1 / MarioSlug.PPM);
        vertex[2] = new Vector2(-3, 3).scl(1 / MarioSlug.PPM);
        vertex[3] = new Vector2(3, 3).scl(1 / MarioSlug.PPM);
        head.set(vertex);
        fixtureDef.shape = head;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = MarioSlug.GOONS_HEAD_BIT;

        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if (currentState == State.STILL_STOMPED && stateTimer > 5) {
            currentState = State.WALKING;
            velocity.x = 0.5f;
        }

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - 8 / MarioSlug.PPM);

        if (currentState == State.DEAD) {
            TurtleRotation += 3;
            rotate(TurtleRotation);
            if(stateTimer > 5 && !destroyed) {
                world.destroyBody(body);
                destroyed = true;
            }
        }
        else {
            body.setLinearVelocity(velocity);
        }
        body.setLinearVelocity(velocity);
    }

    public void onFireBallHit(FireBall fireBall) {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioSlug.NOTHING_BIT;

        for (Fixture fixture : body.getFixtureList()) {
            fixture.setFilterData(filter);
        }
        body.applyLinearImpulse(new Vector2(0, 5f), body.getWorldCenter(), true);
        HUD.addScore(50);
        MarioSlug.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }
    @Override
    public void hitOnHead(Mario mario) {
        if (currentState != State.STILL_STOMPED) {
            currentState = State.STILL_STOMPED;
            velocity.x = 0;
        }
        else {
            kick(mario.getX() <= this.getX() ? KICK_SPEED_R : KICK_SPEED_L);
        }
    }
}

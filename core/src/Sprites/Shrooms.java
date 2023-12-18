package Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Scenes.HUD;
import com.oop.marioslug.Screens.PlayScreen;

public class Shrooms extends Goons {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frame;
    private boolean setDestroyGoon;
    private boolean destroyed;
    public Shrooms(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        // Goomba's walking animation
        frame = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++) {
            frame.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        }
        walkAnimation = new Animation<TextureRegion>(0.4f, frame);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / MarioSlug.PPM, 16 / MarioSlug.PPM);

        setDestroyGoon = false;
        destroyed = false;
    }

    public void update(float dt) {
        stateTime += dt;

        // Check if goomba is not yet destroyed
        if(setDestroyGoon && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime = 0;
        }
        else if (!destroyed) {
            body.setLinearVelocity(velocity);
            // Subtract by half of the width because for some reason the goombas are offset a bit (temporary fix)
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    public void onFireBallHit(FireBall fireBall) {
            setDestroyGoon = true;
            HUD.addScore(50);
            MarioSlug.manager.get("audio/sounds/stomp.wav", Sound.class).play();
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

        // Set objects that goombas can collide with
        fixtureDef.filter.maskBits = MarioSlug.GROUND_BIT | MarioSlug.BUFFSHROOM_BIT | MarioSlug.BRICK_BIT | MarioSlug.GOONS_BIT | MarioSlug.MARIO_BIT | MarioSlug.OBJECT_BIT | MarioSlug.FIREBALL_BIT;

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        // Goomba "head sensor" like mario's
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

    public void draw(Batch batch) {
        if(!destroyed || stateTime < 1) {
            super.draw(batch);
        }
    }

    public void onGoonsHit(Goons goons) {
        if (goons instanceof Turtles && ((Turtles) goons).currentState == Turtles.State.MOVING_STOMPED) {
            setDestroyGoon = true;
        }
        else {
            reverseVelocity(true, false);
        }
    }
    @Override
    public void hitOnHead(Mario mario) {
        setDestroyGoon = true;
        MarioSlug.manager.get("audio/sounds/stomp.wav", Sound.class).play();
        HUD.addScore(25);
    }
}

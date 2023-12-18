package Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Screens.PlayScreen;

public class FireBall extends Sprite {
    PlayScreen screen;
    World world;

    Array<TextureRegion> frame;
    Animation<TextureRegion> fireBall;

    float stateTime;
    boolean isDestroyed;
    boolean setToDestroy;
    boolean fireToRight;

    Body body;
    public FireBall(PlayScreen screen, float x, float y, boolean fireToRight) {
        this.fireToRight = fireToRight;
        this.screen = screen;
        this.world = screen.getWorld();

        frame = new Array<TextureRegion>();

        for (int i = 0; i < 4; i++) {
            frame.add(new TextureRegion(screen.getAtlas().findRegion("fireball"), i * 8, 0, 8, 8));
        }
        fireBall = new Animation<TextureRegion>(0.2f , frame);
        setRegion(fireBall.getKeyFrame(0));
        setBounds(x, y, 6 / MarioSlug.PPM, 6 / MarioSlug.PPM);
        defineFireBall();
    }

    public void defineFireBall() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(fireToRight ? getX() + 12 / MarioSlug.PPM : getX() - 12 / MarioSlug.PPM, getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        if(!world.isLocked()) {
            body = world.createBody(bodyDef);
        }

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(3 / MarioSlug.PPM);
        fixtureDef.filter.categoryBits = MarioSlug.FIREBALL_BIT;
        fixtureDef.filter.maskBits = MarioSlug.GROUND_BIT | MarioSlug.BUFFSHROOM_BIT | MarioSlug.BRICK_BIT | MarioSlug.GOONS_BIT | MarioSlug.OBJECT_BIT;

        fixtureDef.shape = shape;
        fixtureDef.restitution = 1;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef).setUserData(this);
        body.setLinearVelocity(new Vector2(fireToRight ? 2 : -2, 2.5f));
    }

    public void update(float dt) {
        stateTime += dt;
        setRegion(fireBall.getKeyFrame(stateTime, true));
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        if ((stateTime > 3 || setToDestroy) && !isDestroyed) {
            world.destroyBody(body);
            isDestroyed = true;
        }
        if (body.getLinearVelocity().y > 2f) {
            body.setLinearVelocity(body.getLinearVelocity().x, 2f);
        }
        if ( ((fireToRight && body.getLinearVelocity().x < 0) || (!fireToRight && body.getLinearVelocity().x > 0))) {
            setToDestroy = true;
        }
    }

    public void setToDestroy() {
        setToDestroy = true;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}

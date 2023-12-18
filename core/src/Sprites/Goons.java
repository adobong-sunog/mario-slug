package Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.oop.marioslug.Screens.PlayScreen;

public abstract class Goons extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body body;
    public Vector2 velocity;

    public Goons(PlayScreen screen, float x, float y) {
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineGoon();
        velocity = new Vector2(0.5f, 0);
        body.setActive(false);
    }

    protected abstract void defineGoon();
    public abstract void update(float dt);
    public abstract void hitOnHead(Mario mario);
    public abstract void onGoonsHit(Goons goons);
    public abstract void onFireBallHit(FireBall fireBall);

    // Reverse direction of enemy sprite
    public void reverseVelocity(boolean x, boolean y) {
        if (x) {
            velocity.x = -velocity.x;
        }
        if (y) {
            velocity.y = -velocity.y;
        }
    }
}


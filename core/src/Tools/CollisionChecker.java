package Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Screens.VictoryScreen;

import Sprites.FireBall;
import Sprites.Goons;
import Sprites.InteractiveTileObject;
import Sprites.Item;
import Sprites.Mario;

public class CollisionChecker implements ContactListener {
    private MarioSlug game;
    public static int checkGameFinished;
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        checkGameFinished = 0;

        this.game = game;
        int checkCollision = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (checkCollision) {
            case MarioSlug.MARIO_HEAD_BIT | MarioSlug.BRICK_BIT:
            case MarioSlug.MARIO_HEAD_BIT | MarioSlug.BUFFSHROOM_BIT:
                if (fixA.getFilterData().categoryBits == MarioSlug.MARIO_HEAD_BIT) {
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
                }
                else {
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                }
                break;
            // End game once mario reaches the castle
            case MarioSlug.MARIO_BIT | MarioSlug.FINISH_BIT:
                checkGameFinished +=1;
                break;
            case MarioSlug.FIREBALL_BIT | MarioSlug.GOONS_BIT:
                if (fixA.getFilterData().categoryBits == MarioSlug.FIREBALL_BIT && fixB.getUserData() instanceof Goons) {
                    ((FireBall) fixA.getUserData()).setToDestroy();
                    ((Goons) fixB.getUserData()).onFireBallHit((FireBall) fixA.getUserData());
                }
                else if (fixB.getFilterData().categoryBits == MarioSlug.FIREBALL_BIT && fixA.getUserData() instanceof Goons) {
                    ((FireBall) fixB.getUserData()).setToDestroy();
                    ((Goons) fixA.getUserData()).onFireBallHit((FireBall) fixB.getUserData());
                }
                break;
            case MarioSlug.GOONS_HEAD_BIT | MarioSlug.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioSlug.GOONS_HEAD_BIT) {
                    ((Goons)fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
                }
                else {
                    ((Goons)fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
                }
                break;
            // Reverse enemy direction when hitting an object (e.g: pipe)
            case MarioSlug.GOONS_BIT | MarioSlug.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioSlug.GOONS_BIT) {
                    ((Goons)fixA.getUserData()).reverseVelocity(true, false);
                }
                else {
                    ((Goons)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            // Set to gameover if mario hits an enemy
            case MarioSlug.MARIO_BIT | MarioSlug.GOONS_BIT:
                Gdx.app.log("MARIO", "HIT");
                if (fixA.getFilterData().categoryBits == MarioSlug.MARIO_BIT) {
                    ((Mario) fixA.getUserData()).hit((Goons)fixB.getUserData());
                }
                else {
                    ((Mario) fixB.getUserData()).hit((Goons)fixA.getUserData());
                }
                break;
            // Set enemies to reverse direction when colliding with each other
            case MarioSlug.GOONS_BIT | MarioSlug.GOONS_BIT:
                ((Goons)fixA.getUserData()).onGoonsHit((Goons)fixB.getUserData());
                ((Goons)fixB.getUserData()).onGoonsHit((Goons)fixA.getUserData());
                break;
            case MarioSlug.ITEM_BIT | MarioSlug.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == MarioSlug.ITEM_BIT) {
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                }
                else {
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                }
                break;
            // Allow mario to "consume" the mushroom
            case MarioSlug.ITEM_BIT | MarioSlug.MARIO_BIT:
                if (fixA.getFilterData().categoryBits == MarioSlug.ITEM_BIT) {
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                }
                else {
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

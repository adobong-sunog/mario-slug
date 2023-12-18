package Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Screens.PlayScreen;

import Sprites.BuffShrooms;
import Sprites.Brick;
import Sprites.Goons;
import Sprites.Shrooms;
import Sprites.Turtles;

public class WorldCreator {
    private Array<Shrooms> walkingShrooms;
    private Array<Turtles> turtles;
    public WorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        Body body;

        /*
         *  Setting static objects for mario to be able to stand and move on.
         *  Each objects are looped based on the array index of each layer on the map.
         */
        // Set ground object
        for (MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioSlug.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioSlug.PPM);

            body = world.createBody(bodyDef);
            shape.setAsBox((rectangle.getWidth() / 2) / MarioSlug.PPM, (rectangle.getHeight() / 2) / MarioSlug.PPM);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }

        // Set green pipe objects
        for (MapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioSlug.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioSlug.PPM);

            body = world.createBody(bodyDef);
            shape.setAsBox((rectangle.getWidth() / 2) / MarioSlug.PPM, (rectangle.getHeight() / 2) / MarioSlug.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = MarioSlug.OBJECT_BIT;
            body.createFixture(fixtureDef);
        }

        // Set brick objects
        for (MapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {

            new Brick(screen, object);
        }

        // Set ammo box objects
        for (MapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {

            new BuffShrooms(screen, object);
        }

        // Set goombas
        walkingShrooms = new Array<Shrooms>();
        for (MapObject object: map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            walkingShrooms.add(new Shrooms(screen, rectangle.getX() / MarioSlug.PPM, rectangle.getY() / MarioSlug.PPM));
        }

        // Set turtles
        turtles = new Array<Turtles>();
        for (MapObject object: map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            turtles.add(new Turtles(screen, rectangle.getX() / MarioSlug.PPM, rectangle.getY() / MarioSlug.PPM));
        }

        // Set finish line object
        for (MapObject object: map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioSlug.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioSlug.PPM);

            body = world.createBody(bodyDef);
            shape.setAsBox((rectangle.getWidth() / 2) / MarioSlug.PPM, (rectangle.getHeight() / 2) / MarioSlug.PPM);
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = MarioSlug.FINISH_BIT;
            body.createFixture(fixtureDef);
        }
    }

    /* public Array<Shrooms> getWalkingShrooms() {
        return walkingShrooms;
    } */

    public Array<Goons> getGoons() {
        Array<Goons> goons = new Array<Goons>();
        goons.addAll(walkingShrooms);
        goons.addAll(turtles);
        return goons;
    }
}

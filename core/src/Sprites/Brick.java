package Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Scenes.HUD;
import com.oop.marioslug.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioSlug.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Brick", "Collision");
        // Allow any brick to be destroyed only if mario is in his "big" state
        if (mario.isBig()) {
            setCategoryFilter(MarioSlug.DESTROYED_BIT);
            getCell().setTile(null);
            HUD.addScore(150);
            HUD.addCharges(2);
            MarioSlug.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        MarioSlug.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }
}

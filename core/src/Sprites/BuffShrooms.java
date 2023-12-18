package Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.oop.marioslug.MarioSlug;
import com.oop.marioslug.Scenes.HUD;
import com.oop.marioslug.Screens.PlayScreen;

public class BuffShrooms extends InteractiveTileObject {

    private static TiledMapTileSet tileSet;
    private final int BLANK_BOX = 28;
    public BuffShrooms(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioSlug.BUFFSHROOM_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("AmmoBox", "Collision");
        // Check if box has already been bumped by mario
        if (getCell().getTile().getId() == BLANK_BOX) {
            MarioSlug.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }
        else {
            // Spawn a mushroom only if mario isn't big yet to prevent a very large mario
            if (object.getProperties().containsKey("mushroom")) {
                if (!mario.isBig()) {
                    screen.spawnItem(new ItemDefinition(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioSlug.PPM), BoxShrooms.class));
                    MarioSlug.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
                }
                else {
                    MarioSlug.manager.get("audio/sounds/coin.wav", Sound.class).play();
                }
            }
            else {
                MarioSlug.manager.get("audio/sounds/coin.wav", Sound.class).play();
            }
            HUD.addScore(70);
            HUD.addCharges(5);
        }
        getCell().setTile(tileSet.getTile(BLANK_BOX));
    }

}

package io.github.some_example_name;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Main extends Game {

    public static Random random = new Random();
    @Override
    public void create() {
        setScreen(new MenuScreen(this)); // Cambia a la pantalla de menú
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}

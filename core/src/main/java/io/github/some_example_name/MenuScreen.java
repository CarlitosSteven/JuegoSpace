package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.audio.Music;

public class MenuScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont; // Nueva fuente para el título

    private BitmapFont nombresFont; // Nueva fuente para el título

    private BitmapFont nombresFont_Kevin;

    private BitmapFont nombresFont_Esteban;


    private GlyphLayout layout;
    private Texture backgroundTexture; // Para la imagen de fondo
    private float buttonX, buttonY, buttonWidth, buttonHeight;

    private Music backgroundMusic;

    public MenuScreen(final Main game) {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("fondo_space.wav"));
        backgroundMusic.setLooping(true); // Hacer que el sonido se repita
        backgroundMusic.play(); // Comenzar a reproducir la música de fondo


        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(3); // Tamaño más grande para la fuente del botón

        titleFont = new BitmapFont(); // Fuente para el título
        titleFont.getData().setScale(6); // Aumentar el tamaño del título


        nombresFont = new BitmapFont(); // Fuente para el título
        nombresFont.getData().setScale(4); // Aumentar el tamaño del título

        nombresFont_Kevin = new BitmapFont(); // Fuente para el título
        nombresFont_Kevin.getData().setScale(4); // Aumentar el tamaño del título

        nombresFont_Esteban = new BitmapFont(); // Fuente para el título
        nombresFont_Esteban.getData().setScale(4); // Aumentar el tamaño del título


        layout = new GlyphLayout();

        // Cargar la imagen de fondo desde assets
        backgroundTexture = new Texture(Gdx.files.internal("fondo_cel.png"));

        // Definir las dimensiones y la posición del botón
        buttonWidth = 600;
        buttonHeight = 200;
        buttonX = (Gdx.graphics.getWidth() - buttonWidth) / 2; // Centrado en X
        buttonY = (Gdx.graphics.getHeight() - buttonHeight) / 2; // Centrado en Y
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Dibujar la imagen de fondo y escalarla para que ocupe toda la pantalla
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Dibujar el título
        String titleText = "Nebula Strike"; //
        layout.setText(titleFont, titleText);
        float titleWidth = layout.width;
        float titleHeight = layout.height;

        // Dibujar el título centrado en la parte superior
        titleFont.setColor(Color.WHITE); // Cambiar el color del título
        titleFont.draw(batch, titleText, (Gdx.graphics.getWidth() - titleWidth) / 2, Gdx.graphics.getHeight() - 400);


        String nombresText = "Rocio Felix"; //
        layout.setText(nombresFont, nombresText);
        float titleWidth_nombres = layout.width;
        float titleHeight_nombres = layout.height;

        // Dibujar el título centrado en la parte superior
        nombresFont.setColor(Color.WHITE); // Cambiar el color del título
        nombresFont.draw(batch, nombresText, (Gdx.graphics.getWidth() - titleWidth_nombres) / 2, Gdx.graphics.getHeight() - 1800);


        String nombresText_Kevin = "Kevin Zamaniego"; //El ser mas bello del planeta el cual es el amor de mi vida aunque este nojado conmigo. Te amo <3
        layout.setText(nombresFont_Kevin, nombresText_Kevin);
        float titleWidth_nombresKevin = layout.width;
        float titleHeight_nombresKevin = layout.height;
// Dibujar el título centrado en la parte superior
        nombresFont.setColor(Color.WHITE); // Cambiar el color del título
        nombresFont.draw(batch, nombresText_Kevin, (Gdx.graphics.getWidth() - titleWidth_nombresKevin) / 2, Gdx.graphics.getHeight() - 1950);


        String nombresText_Esteban = "Esteban Zamudio";
        layout.setText(nombresFont_Esteban, nombresText_Kevin);
        float titleWidth_nombresEsteban= layout.width;
        float titleHeight_nombresEsteban = layout.height;


        nombresFont_Esteban.setColor(Color.WHITE); // Cambiar el color del título
        nombresFont_Esteban.draw(batch, nombresText_Esteban, (Gdx.graphics.getWidth() - titleWidth_nombresEsteban) / 2, Gdx.graphics.getHeight() - 2100);

        // Dibujar el botón como un rectángulo
        batch.setColor(Color.BLUE); // Color del botón
        batch.draw(backgroundTexture, buttonX, buttonY, buttonWidth, buttonHeight); // Rectángulo del botón

        font.setColor(Color.WHITE); // Color del texto del botón

        // Medir el texto del botón
        layout.setText(font, "Nivel 1");
        float textWidth = layout.width;
        float textHeight = layout.height;

        // Dibujar el texto del botón centrado
        font.draw(batch, "Nivel 1", buttonX + (buttonWidth - textWidth) / 2, buttonY + (buttonHeight + textHeight) / 2);

        batch.end();

        // Manejar clics
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            touchPos.y = Gdx.graphics.getHeight() - touchPos.y; // Invertir y para el sistema de coordenadas

            // Verificar si el clic está dentro del área del botón
            if (touchPos.x >= buttonX && touchPos.x <= buttonX + buttonWidth &&
                touchPos.y >= buttonY && touchPos.y <= buttonY + buttonHeight) {
                backgroundMusic.stop();
                game.setScreen(new GameScreen()); // Cambiar a la pantalla del juego
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Ajustar según el tamaño de la ventana si es necesario
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        titleFont.dispose(); // Libera los recursos de la fuente del título
        backgroundTexture.dispose(); // Libera los recursos de la imagen de fondo
    }
}

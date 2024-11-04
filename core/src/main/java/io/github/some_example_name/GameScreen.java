package io.github.some_example_name;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.badlogic.gdx.audio.Sound;



import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

class GameScreen implements Screen {


    //screen
    private Camera camera;

    private Viewport viewport;
    //graphics
    private SpriteBatch batch;

    private TextureAtlas textureAtlas;
    private Texture explosionTexture;
    private TextureRegion[] backgrounds;
    private float backgroundHeight;

    private TextureRegion playerShipTextureRegion,playerShieldTextureRegion,enemyShipTextureRegion,enemyShieldTextureRegion,playerLaserTextureRegion,enemyLaserTextureRegion;






    //timing
    private float [] backgroundOffset= {0,0,0,0};
    private float backgroundMaxScrollingSpeed;
    private float timeBetweenEnemySpawns = 1f;
    private float enemySpawnTimer = 0;

    //world parameters
    private final float WORLD_WIDTH =72;
    private final float WORLD_HEIGHT =128;

    private final float TOUCH_MOVEMENT_THRESHOLD = 0.5f;

    //game object
    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipList;
    private LinkedList<Laser> playerLaserList;
    private LinkedList<Laser> enemyLaserList;
    private LinkedList<Explosion> explosionList;
    private int score = 0;

    //Heads-Up Display
    BitmapFont font;

    float hudVerticalMargin, hudLeftX, hudRightX, hudCentreX, hudRow1Y, hudRow2Y, hudSectionWidth;
    private Sound explosionSound;

    private Sound gameOverSound;


    private boolean gameOver = false;

    GameScreen(){

        //set up the texture atlas

        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("game_over.wav"));

        camera = new OrthographicCamera();



        viewport = new StretchViewport(WORLD_WIDTH,WORLD_HEIGHT,camera);

        textureAtlas= new TextureAtlas("Images.atlas");

        backgrounds = new TextureRegion[4];
        backgrounds[0] = textureAtlas.findRegion("Starscape00");
        backgrounds[1] = textureAtlas.findRegion("Starscape01");
        backgrounds[2] = textureAtlas.findRegion("Starscape02");
        backgrounds[3] = textureAtlas.findRegion("Starscape03");

        backgroundHeight = WORLD_HEIGHT * 2;
        backgroundMaxScrollingSpeed = (float)(WORLD_HEIGHT)/4;

        //inicializacion de texturas de regiobes


        playerShipTextureRegion = textureAtlas.findRegion("playerShip3_blue");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyBlack4");
        playerShieldTextureRegion = textureAtlas.findRegion("shield1");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield2");
        playerLaserTextureRegion = textureAtlas.findRegion("laserBlue01");
        enemyLaserTextureRegion = textureAtlas.findRegion("laserRed16");
        enemyShieldTextureRegion.flip(false,true);


        explosionTexture = new Texture("explosion.png");

    //Aqui van a hacer los cambios para quitarle el escudo mas rapido
        //set up game objects
        playerShip = new PlayerShip(WORLD_WIDTH / 2, WORLD_HEIGHT / 4,
            10, 10,
            48, 3,
            0.4f, 4, 45, 0.5f,
            playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);



        enemyShipList = new LinkedList<>();
        playerLaserList= new LinkedList<>();
        enemyLaserList = new LinkedList<>();
        explosionList = new LinkedList<>();
        batch= new SpriteBatch();
        prepareHUD();
    }

    private void prepareHUD() {
        // Crear un BitmapFont básico (LibGDX tiene una fuente por defecto)
        font = new BitmapFont(); // Cargar la fuente predeterminada de LibGDX

        // Escalar la fuente para ajustarla a tu juego
        font.getData().setScale(0.30f);

        // Configurar los márgenes del HUD y posiciones
        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2 / 3 - hudLeftX;
        hudCentreX = WORLD_WIDTH / 3;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;

        // Si quieres cambiar el color del texto, puedes hacerlo así
        font.setColor(1, 1, 1, 1); // Blanco
    }


    @Override
    public void render(float deltaTime) {
        batch.begin();
        //scrolling background

        renderBackground(deltaTime);
        if (!gameOver) {
        detectInput(deltaTime);
        playerShip.update(deltaTime);
        spawnEnemyShips(deltaTime);

        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            moveEnemy(enemyShip, deltaTime);
            enemyShip.update(deltaTime);
            enemyShip.draw(batch);
        }

        //player ship
        playerShip.draw(batch);

        //lasers
        renderLasers(deltaTime);

        //detect collisions between lasers and ships
        detectCollisions();

        //explosions
        updateAndsions(deltaTime);
        updateAndRenderHUD();

            if (score > 1000) {
                // Cambiar a la siguiente pantalla
                ((Main) Gdx.app.getApplicationListener()).setScreen(new Level2());
            }

        } else {
            // Mostrar el mensaje de Game Over
            font.draw(batch, "GAME OVER", WORLD_WIDTH / 2, WORLD_HEIGHT -50, 0, Align.center, true);

            // Reiniciar el juego si se presiona R
            if (Gdx.input.isTouched())  {
                restartGame();
            }
        }


        batch.end();


    }
    private void restartGame() {
        // Reiniciar las variables del juego
        playerShip.lives = 3; // O el número inicial de vidas
        score = 0;
        enemyShipList.clear();
        playerLaserList.clear();
        enemyLaserList.clear();
        explosionList.clear();
        gameOver = false;
        // Reiniciar cualquier otra lógica necesaria
    }
    private void updateAndRenderHUD() {
        //render top row labels
        font.draw(batch, "Score", hudLeftX, hudRow1Y, hudSectionWidth, Align.left, false);
        font.draw(batch, "Shield", hudCentreX, hudRow1Y, hudSectionWidth, Align.center, false);
        font.draw(batch, "Lives", hudRightX, hudRow1Y, hudSectionWidth, Align.right, false);
        //render second row values
        font.draw(batch, String.format(Locale.getDefault(), "%06d", score), hudLeftX, hudRow2Y, hudSectionWidth, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.shield), hudCentreX, hudRow2Y, hudSectionWidth, Align.center, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.lives), hudRightX, hudRow2Y, hudSectionWidth, Align.right, false);
    }

    private void spawnEnemyShips(float deltaTime) {
        enemySpawnTimer += deltaTime;

        if (enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyShipList.add(new EnemyShip(Main.random.nextFloat() * (WORLD_WIDTH - 10) + 5,
                WORLD_HEIGHT - 5,
                10, 10,
                48, 1,
                0.3f, 5, 50, 0.8f,
                enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion));
            enemySpawnTimer -= timeBetweenEnemySpawns;
        }
    }

    private void detectInput(float deltaTime){
        //keyboard input


        //strategy: determinar la maxima distancia que el barco pueda mover

        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = (float)WORLD_HEIGHT/2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0) {
            playerShip.translate(Math.min(playerShip.movementSpeed*deltaTime, rightLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0) {
            playerShip.translate( 0f, Math.min(playerShip.movementSpeed*deltaTime, upLimit));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0) {
            playerShip.translate(Math.max(-playerShip.movementSpeed*deltaTime, leftLimit), 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0) {
            playerShip.translate(0f, Math.max(-playerShip.movementSpeed*deltaTime, downLimit));
        }

        //touch input (also mouse)
        if (Gdx.input.isTouched()) {
            //get the screen position of the touch
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            //convert to world position
            Vector2 touchPoint = new Vector2(xTouchPixels, yTouchPixels);
            touchPoint = viewport.unproject(touchPoint);

            //calculate the x and y differences
            Vector2 playerShipCentre = new Vector2(
                playerShip.boundingBox.x + playerShip.boundingBox.width/2,
                playerShip.boundingBox.y + playerShip.boundingBox.height/2);

            float touchDistance = touchPoint.dst(playerShipCentre);

            if (touchDistance > TOUCH_MOVEMENT_THRESHOLD) {
                float xTouchDifference = touchPoint.x - playerShipCentre.x;
                float yTouchDifference = touchPoint.y - playerShipCentre.y;

                //scale to the maximum speed of the ship
                float xMove = xTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;
                float yMove = yTouchDifference / touchDistance * playerShip.movementSpeed * deltaTime;

                if (xMove > 0) xMove = Math.min(xMove, rightLimit);
                else xMove = Math.max(xMove,leftLimit);

                if (yMove > 0) yMove = Math.min(yMove, upLimit);
                else yMove = Math.max(yMove,downLimit);

                playerShip.translate(xMove,yMove);
            }
        }

    }

    private void moveEnemy(EnemyShip enemyShip, float deltaTime) {
        //strategy: determine the max distance the ship can move

        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -enemyShip.boundingBox.x;
        downLimit = (float) WORLD_HEIGHT / 2 - enemyShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        upLimit = WORLD_HEIGHT - enemyShip.boundingBox.y - enemyShip.boundingBox.height;

        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * deltaTime;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed * deltaTime;

        if (xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove, leftLimit);

        if (yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove, downLimit);

        enemyShip.translate(xMove, yMove);
    }

        //touch input

    private void detectCollisions() {
        //for each player laser, check whether it intersects an enemy ship
        ListIterator<Laser> laserListIterator = playerLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while (enemyShipListIterator.hasNext()) {
                EnemyShip enemyShip = enemyShipListIterator.next();

                if (enemyShip.intersects(laser.boundingBox)) {
                    //contact with enemy ship
                    if (enemyShip.hitAndCheckDestroyed(laser)) {
                        enemyShipListIterator.remove();
                        explosionList.add(
                            new Explosion(explosionTexture,
                                new Rectangle(enemyShip.boundingBox),
                                0.7f));
                        explosionSound.play(); // Reproduce el sonido
                        score += 100;
                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }
        //for each enemy laser, check whether it intersects the player ship
        laserListIterator = enemyLaserList.listIterator();
        while (laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.intersects(laser.boundingBox)) {
                //contact with player ship
                if (playerShip.hitAndCheckDestroyed(laser)) {
                    explosionList.add(
                        new Explosion(explosionTexture,
                            new Rectangle(playerShip.boundingBox),
                            1.6f));
                    playerShip.shield = 10;
                    playerShip.lives--;

                    if (playerShip.lives <= 0) {
                        gameOver = true;
                    }

                }
                laserListIterator.remove();
            }
        }
    }

    private void updateAndsions(float deltaTime) {
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext()) {
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if (explosion.isFinished()) {
                explosionListIterator.remove();
            } else {
                explosion.draw(batch);
            }
        }
    }


    private void renderLasers(float deltaTime) {
        //create new lasers
        //player lasers
        if (playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.fireLasers();
            playerLaserList.addAll(Arrays.asList(lasers));
        }
        //enemy lasers
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                enemyLaserList.addAll(Arrays.asList(lasers));
            }
        }
        //draw lasers
        //remove old lasers
        ListIterator<Laser> iterator = playerLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y > WORLD_HEIGHT) {
                iterator.remove();
            }
        }
        iterator = enemyLaserList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed * deltaTime;
            if (laser.boundingBox.y + laser.boundingBox.height < 0) {
                iterator.remove();
            }
        }
    }

    private void  renderBackground(float deltaTime) {

        backgroundOffset[0] += deltaTime * backgroundMaxScrollingSpeed / 8;
        backgroundOffset[1] += deltaTime * backgroundMaxScrollingSpeed / 4;
        backgroundOffset[2] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffset[3] += deltaTime * backgroundMaxScrollingSpeed;

        for (int layer = 0; layer < backgroundOffset.length; layer++) {
            if (backgroundOffset[layer] > WORLD_HEIGHT) {
                backgroundOffset[layer] = 0;
            }
            batch.draw(backgrounds[layer],
                0,
                -backgroundOffset[layer],
                WORLD_WIDTH, WORLD_HEIGHT);
            batch.draw(backgrounds[layer],
                0,
                -backgroundOffset[layer] + WORLD_HEIGHT,
                WORLD_WIDTH, WORLD_HEIGHT);
        }
    }




    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
    @Override
    public void show() {

    }

    @Override
    public void dispose() {
        explosionSound.dispose(); // Libera el sonido
        gameOverSound.dispose(); // Libera el sonido


    }
}

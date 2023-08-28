package dk.sdu.mmmi.cbse.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.managers.GameInputProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class Game implements ApplicationListener {

    private static OrthographicCamera cam;
    private ShapeRenderer sr;

    private final GameData gameData = new GameData();
    private List<IEntityProcessingService> entityProcessors = new ArrayList<>();
    private List<IGamePluginService> entityPlugins = new ArrayList<>();
    private World world = new World();
    private AnnotationConfigApplicationContext context;

    // gets called once, when the game it started.
    @Override
    public void create() {

        context = new AnnotationConfigApplicationContext();
        context.scan("dk.sdu.mmmi.cbse");
        context.refresh();

        gameData.setDisplayWidth(Gdx.graphics.getWidth());
        gameData.setDisplayHeight(Gdx.graphics.getHeight());

        cam = new OrthographicCamera(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        cam.translate(gameData.getDisplayWidth() / 2, gameData.getDisplayHeight() / 2);
        cam.update();

        sr = new ShapeRenderer();

        Gdx.input.setInputProcessor(
                new GameInputProcessor(gameData)
        );


        // Lookup all Game Plugins using ServiceLoader
        for (IGamePluginService iGamePlugin : context.getBeansOfType(IGamePluginService.class).values()) {
            iGamePlugin.start(gameData, world);
        }
    }

    // is called continuously in a loop. Game logic should go here.
    @Override
    public void render() {

        // clear screen to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /*
		When the game is updated, is passes a float "dt".
		getDeltaTime tells how long it has been, since last "render" call.
		We will use this to determine have long we should move the game forward.
		If it is a large amount of time, we should move the game farther ahead, than if it is a shorter amount of time.
		 */

        gameData.setDelta(Gdx.graphics.getDeltaTime());

        update();

        draw();

        gameData.getKeys().update();
    }

    private void update() {
        // Update
        for (IEntityProcessingService entityProcessorService : context.getBeansOfType(IEntityProcessingService.class).values()) {
            entityProcessorService.process(gameData, world);
        }
    }

    private void draw() {
        for (Entity entity : world.getEntities()) {

            sr.setColor(1, 1, 1, 1);

            sr.begin(ShapeRenderer.ShapeType.Line);

            float[] shapex = entity.getShapeX();
            float[] shapey = entity.getShapeY();

            for (int i = 0, j = shapex.length - 1;
                    i < shapex.length;
                    j = i++) {

                sr.line(shapex[i], shapey[i], shapex[j], shapey[j]);
            }

            sr.end();
        }
    }

    // gets called when you change the size of the window
    @Override
    public void resize(int width, int height) {
    }

    // gets called (for android og iphone) if fx someone calls
    @Override
    public void pause() {
    }

    // gets called when the phone call is ended.
    @Override
    public void resume() {
    }

    //gets called when the application exits
    @Override
    public void dispose() {
    }

//    private Collection<? extends IGamePluginService> getPluginServices() {
//        return SPILocator.locateAll(IGamePluginService.class);
//    }
//
//    private Collection<? extends IEntityProcessingService> getEntityProcessingServices() {
//        return SPILocator.locateAll(IEntityProcessingService.class);
//    }

}

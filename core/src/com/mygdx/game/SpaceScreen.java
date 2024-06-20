package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.sun.org.apache.bcel.internal.Const;

import java.text.Normalizer;

public class SpaceScreen implements Screen {
    MyGame game;

    OrthographicCamera camera;
    TextureRegion shipTex;
    TextureRegion meteorTex;
    Color bgColor;
    TextureRegion lazerTex;

    float MAX_SPEED = 500;
    Vector2 shipPos;
    Vector2 shipVel;
    float STEERING_FACTOR = 5;
    Array<Circle> meteorCircs;
    Array<Vector2> meteorVects;
    Array<Circle> lazerCircs;
    private static final int lazerSpeed = 800;
    int lifes;
    float meteorSpeed = 350;
    Vector2 meteorPos1 = new Vector2(-64, 240);
    Vector2 meteorPos2 = new Vector2(400, -64);
    Vector2 meteorPos3 = new Vector2(864, 240);
    Vector2 meteorPos4 = new Vector2(400, 544);
    Circle shipRect;
    Integer meteorTime = 0;
    Array<Vector2> lazerVects;


    public SpaceScreen(MyGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        bgColor = Color.valueOf("1C3D6E");
        shipTex = new TextureRegion(new Texture(Gdx.files.internal("ship_L.png")));
        shipPos = new Vector2();
        shipVel = new Vector2();
        meteorTex = new TextureRegion(new Texture(Gdx.files.internal("meteor_detailedLarge.png")));
        lazerTex = new TextureRegion(new Texture(Gdx.files.internal("star_small.png")));
        meteorCircs = new Array<Circle>();
        meteorVects = new Array<Vector2>();
        createMeteor();
        createMeteor();
        createMeteor();
        createMeteor();
        shipRect = new Circle(shipPos.x, shipPos.y, shipTex.getRegionWidth()/2f - 10);
        lifes = 1;
        lazerVects = new Array<Vector2>();
        lazerCircs = new Array<Circle>();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        camera.update();

        Vector2 dir = new Vector2();
        meteorTime++;







        for (int i = 0;i < meteorVects.size; i++) {
            Vector2 velocity = meteorVects.get(i);
            Circle meteor = meteorCircs.get(i);
            meteor.x += velocity.x * Gdx.graphics.getDeltaTime();
            meteor.y += velocity.y * Gdx.graphics.getDeltaTime();
        }

        for (int i = 0; i < lazerVects.size; i++) {
            Circle circle = lazerCircs.get(i);
            Vector2 vect = lazerVects.get(i);
            circle.x += vect.x * Gdx.graphics.getDeltaTime();
            circle.y += vect.y * Gdx.graphics.getDeltaTime();
        }




        //collision

        for (int i = 0; i < meteorCircs.size; i++) {
            if (meteorCircs.get(i).overlaps(shipRect)) {
                lifes--;
                System.out.println("lifes -1, lifes = " + lifes);
            }
        }

        for (int i = 0; i < lazerCircs.size; i++) {
            if (lazerCircs.get(i).overlaps(meteorCircs.get(i))) {
                meteorCircs.removeIndex(i);
            }
        }




        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            dir.add(0, 1);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            dir.add(0, -1);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            dir.add(-1, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            dir.add(1, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            Circle circle = new Circle(shipPos, 10);
            Vector2 vect = new Vector2(shipVel);
            vect.nor();
            vect.scl(lazerSpeed);
            lazerCircs.add(circle);
            lazerVects.add(vect);
        }

        dir.nor();

        Vector2 desiredVelocity = new Vector2();
        desiredVelocity.set(dir);
        desiredVelocity.scl(MAX_SPEED);

        Vector2 steeringVector = new Vector2();
        steeringVector.set(desiredVelocity).sub(shipVel);
        steeringVector.scl(Gdx.graphics.getDeltaTime());
        steeringVector.scl(STEERING_FACTOR);
        shipVel.add(steeringVector);

        Vector2 shipVelDelta = new Vector2();
        shipVelDelta.set(shipVel).scl(Gdx.graphics.getDeltaTime());

        float rotationRad = MathUtils.atan2(shipVelDelta.y, shipVelDelta.x); // rad
        shipPos.add(shipVelDelta);

        shipRect.setPosition(shipPos.x, shipPos.y);

        ScreenUtils.clear(bgColor);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (int i = 0; i < lazerCircs.size; i++) {
            game.batch.draw(lazerTex, lazerCircs.get(i).x, lazerCircs.get(i).y);
        }
        game.batch.draw(
            shipTex,
            shipPos.x, shipPos.y,
            shipTex.getRegionWidth() / 2.f, shipTex.getRegionHeight() / 2.f,
            shipTex.getRegionWidth(), shipTex.getRegionHeight(),
            1.0f, 1.0f,
            MathUtils.radiansToDegrees * rotationRad
        );

        for (int i = 0; i < meteorCircs.size; i++ ) {
            game.batch.draw(meteorTex, (meteorCircs.get(i)).x, (meteorVects.get(i)).y);
        }

        game.batch.end();

        if (meteorTime == 6000) {
            createMeteor();
            createMeteor();
            createMeteor();
            createMeteor();
            meteorTime = 0;
        }



    }


    @Override
    public void resize(int i, int i1) {

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
    public void dispose() {
        meteorTex.getTexture().dispose();

    }
    public void createMeteor() {
        int spawnPos = MathUtils.random(1, 4);
        int posDeg = 0;
        Vector2 meteorPos = new Vector2(0, 0);
        switch (spawnPos) {
            case 1:
                posDeg = MathUtils.random(135, 225);
                meteorPos.set(meteorPos1);
                break;
            case 2:
                posDeg = MathUtils.random(45, 135);
                meteorPos.set(meteorPos2);
                break;
            case 3:
                int posDeg3Option1 = MathUtils.random(315, 360);
                int posDeg3Option2 = MathUtils.random(0, 45);
                int posDeg3Decider = MathUtils.random(1, 2);
                if (posDeg3Decider == 1) {
                    posDeg = posDeg3Option1;
                }
                else {
                    posDeg = posDeg3Option2;
                }
                meteorPos.set(meteorPos3);
                break;
            case 4:
                posDeg = MathUtils.random(225, 315);
                meteorPos.set(meteorPos4);
                break;
        }
        float posRad = posDeg * MathUtils.degreesToRadians;
        Vector2 posVect = new Vector2(MathUtils.cos(posRad), MathUtils.sin(posRad));
        posVect = posVect.scl(meteorSpeed);
        meteorVects.add(posVect);
        Circle meteor = new Circle(meteorPos.x, meteorPos.y, 32);
        meteorCircs.add(meteor);


    }


}
//screen size:
//height 480px
//with 800px
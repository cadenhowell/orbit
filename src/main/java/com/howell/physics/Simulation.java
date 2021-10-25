package com.howell.physics;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * @author Caden Howell
 * The main class responsible for running the simulation
 *
 */
public class Simulation extends Application {

    private static final int WIDTH = 1400;
    private static final int HEIGHT = 900;
    private long delay;
    private double cameraX = 0;
    private double cameraY = 0;
    private double anchorX = 0;
    private double anchorY = 0;

    private ArrayList<PhysicalSphere> spheres = new ArrayList<PhysicalSphere>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Creates and sets parameters for camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-10000);
        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);

        //Creates group that holds sphere
        Group group = new Group();
        //Create subscene that holds group
        SubScene world = new SubScene(group, 3.0 * WIDTH / 4.0, HEIGHT, true, SceneAntialiasing.DISABLED);

        //Container for controls
        VBox controls = new VBox(10);
        controls.setAlignment(Pos.TOP_CENTER);
        controls.setPadding(new Insets(10, 0, 0, 0));

        //Slider that changes G value
        Label gLabel = new Label("Gravitational Constant");
        Slider gConstant = new Slider(.001, 100000, 6.67408 * Math.pow(10, 2));
        gConstant.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                VectorUpdater.G = newValue.doubleValue();
            }
        });

        //Slider that changes simulation speed
        Label timeLabel = new Label("Simulation Speed");
        Slider timeConstant = new Slider(0, 10, 0);
        timeConstant.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                delay = newValue.longValue();
            }
        });

        //Slider the controls zoom
        Label zoomLabel = new Label("Zoom");
        Slider zoom = new Slider(-50000, 10000, -10000);
        zoom.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                camera.setTranslateZ(newValue.doubleValue());
            }
        });

        //All control text fields
        TextField massField = new TextField("Mass");
        TextField radiusField = new TextField("Radius");
        TextField x = new TextField("X Coordinate");
        TextField y = new TextField("Y Coordinate");
        TextField z = new TextField("Z Coordinate");
        TextField vx = new TextField("X Velocity");
        TextField vy = new TextField("Y Velocity");
        TextField vz = new TextField("Z Velocity");
        Label pad = new Label();
        pad.setPrefHeight(40);

        //Control buttons
        Button addSphere = new Button("Add Sphere");
        HBox buttons = new HBox(5);
        Button toggle = new Button("Start");
        Button clear = new Button("Clear");
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(toggle, clear);

        //Adds items to control menu
        controls.getChildren().addAll(massField, radiusField, x, y, z, vx, vy, vz, addSphere, pad, gLabel, gConstant, timeLabel,
                timeConstant, zoomLabel, zoom, buttons);

        //Animates program
        AnimationTimer timer = new AnimationTimer() {
            long time = System.nanoTime();

            @Override
            public void handle(long now) {
                if ((now - time) * Math.pow(10, -7) > delay) {
                    VectorUpdater.updateVectors(spheres, 0.017);
                    for (PhysicalSphere sphere : spheres) {
                        double dx = sphere.getVelocityVector().getX() * 0.017;
                        double dy = sphere.getVelocityVector().getY() * 0.017;
                        double dz = sphere.getVelocityVector().getZ() * 0.017;
                        sphere.setTranslateX(sphere.getTranslateX() + dx);
                        sphere.setTranslateY(sphere.getTranslateY() + dy);
                        sphere.setTranslateZ(sphere.getTranslateZ() + dz);
                    }
                    time = now;
                }
            }
        };


        addSphere.setOnAction(event -> {
            //Checks all text fields and formats ones that are not numbers
            for (Node field : controls.getChildren()) {
                if (field instanceof TextField) {
                    try {
                        Double.parseDouble(((TextField) field).getText());
                    } catch (Exception e) {
                        ((TextField) field).setText("0");
                    }
                }
            }
            //Velocity vector
            Point3D velocity = new Point3D(Double.parseDouble(vx.getText()), Double.parseDouble(vy.getText()),
                    Double.parseDouble(vz.getText()));
            double mass = Double.parseDouble(massField.getText());
            double radius = Double.parseDouble(radiusField.getText());
            if(mass == 0)
                mass = 500;
            if(radius == 0)
                radius = 500;
            PhysicalSphere sphere = new PhysicalSphere(radius, mass, velocity);
            group.getChildren().add(sphere);
            sphere.setTranslateX(Double.parseDouble(x.getText()));
            sphere.setTranslateY(Double.parseDouble(y.getText()));
            sphere.setTranslateZ(Double.parseDouble(z.getText()));
            spheres.add(sphere);
            massField.setText("Mass");
            radiusField.setText("Radius");
            vx.setText("X Velocity");
            vy.setText("Y Velocity");
            vz.setText("Z Velocity");
            x.setText("X Coordinate");
            y.setText("Y Coordinate");
            z.setText("Z Coordinate");
        });

        //Clears spheres
        clear.setOnAction(event -> {
            group.getChildren().clear();
            spheres.clear();
        });

        //Toggles timer
        toggle.setOnAction(event -> {
            if (toggle.getText().equals("Start")) {
                timer.start();
                toggle.setText("Stop");
            } else {
                timer.stop();
                toggle.setText("Start");
            }
        });

        //Gets anchor
        world.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
        });

        //Adds drag amount to anchor
        world.setOnMouseDragged(event -> {
            camera.setTranslateX(cameraX + (anchorX - event.getSceneX()) * 10);
            camera.setTranslateY(cameraY + (anchorY - event.getSceneY()) * 10);
        });

        //Logs new camera location
        world.setOnMouseReleased(event -> {
            cameraX = camera.getTranslateX();
            cameraY = camera.getTranslateY();
        });

        //Create border layout
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(0, 10, 0, 10));
        layout.setCenter(world);
        layout.setRight(controls);

        //Create main scene and add layout
        Scene scene = new Scene(layout, WIDTH, HEIGHT, true);
        world.setFill(Color.LIGHTSTEELBLUE);
        world.setCamera(camera);

        //Make the main stage
        primaryStage.setTitle("3D Physics Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
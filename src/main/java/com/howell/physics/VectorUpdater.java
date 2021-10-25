package com.howell.physics;

import javafx.geometry.Point3D;

import java.util.ArrayList;

/**
 *
 * @author Caden Howell
 * A class to update the velocity vector of each sphere based on the forces of other objects
 *
 */
public class VectorUpdater {

    public static double G = 6.67408 * Math.pow(10, 2);

    public static void updateVectors(ArrayList<PhysicalSphere> spheres, double timeStep) {

        for (int i = 0; i < spheres.size(); i++) {

            Point3D netForceVector = Point3D.ZERO;
            PhysicalSphere sphere1 = spheres.get(i);

            for (int j = 0; j < spheres.size(); j++) {

                if (i != j) {

                    PhysicalSphere sphere2 = spheres.get(j);

                    Point3D loc1 = sphere1.getCenter();
                    Point3D loc2 = sphere2.getCenter();

                    double m1 = sphere1.getMass();
                    double m2 = sphere2.getMass();

                    double r = loc1.distance(loc2);

                    double force = G * m1 * m2 / Math.pow(r, 2);

                    double dx = loc2.getX() - loc1.getX();
                    double dy = loc2.getY() - loc1.getY();
                    double dz = loc2.getZ() - loc1.getZ();

                    double forceX = dx * force / r;
                    double forceY = dy * force / r;
                    double forceZ = dz * force / r;

                    netForceVector = netForceVector.add(forceX, forceY, forceZ);

                }
            }

            Point3D accelerationVector = netForceVector.multiply(1.0 / sphere1.getMass());
            Point3D velocityVector = sphere1.getVelocityVector().add(accelerationVector.multiply(timeStep));

            sphere1.setVelocityVector(velocityVector);

        }
    }

}

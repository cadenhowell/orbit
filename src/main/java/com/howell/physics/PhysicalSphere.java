package com.howell.physics;

import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.shape.Sphere;

/**
 *
 * @author Caden Howell
 * A class representation of a sphere with physical properties
 */
public class PhysicalSphere extends Sphere {

    private Point3D velocityVector;
    private double mass;

    /**
     * @param double radius
     * @param double mass
     * @param Point3D initial velocity
     */
    public PhysicalSphere(double radius, double mass, Point3D initialVelocity) {
        super(radius);
        this.mass = mass;
        velocityVector = initialVelocity;
    }

    /**
     * @return mass
     */
    public double getMass() {
        return mass;
    }

    /**
     * @return velocity vector
     */
    public Point3D getVelocityVector() {
        return velocityVector;
    }

    /**
     * @param Point3D velocity vector
     */
    public void setVelocityVector(Point3D vv) {
        velocityVector = vv;
    }

    /**
     * @return center of sphere
     */
    public Point3D getCenter() {
        Bounds bounds = getBoundsInParent();
        return new Point3D(bounds.getCenterX(), bounds.getCenterY(), bounds.getCenterZ());
    }

}

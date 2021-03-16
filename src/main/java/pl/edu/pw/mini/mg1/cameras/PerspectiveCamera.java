package pl.edu.pw.mini.mg1.cameras;

import org.joml.*;
import pl.edu.pw.mini.mg1.collisions.Ray;

import java.lang.Math;

public class PerspectiveCamera {
    private float aspect;
    private float near;
    private float far;
    private float fov;

    private final Vector3f position;
    private final Vector3f rotation;

    private final Matrix4f viewMatrix;
    private final Matrix4f projectionMatrix;
    private final Matrix4f viewProjectionMatrix;

    public PerspectiveCamera(float aspect, float near, float far, float fov) {
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        this.fov = fov;
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.viewMatrix = new Matrix4f();
        this.projectionMatrix = new Matrix4f();
        this.viewProjectionMatrix = new Matrix4f();
        calculateProjectionMatrix();
    }

    private void calculateViewMatrix() {
        viewMatrix.identity()
                .rotateZ((float) Math.toRadians(rotation.z))
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .translate(-position.x, -position.y, -position.z);
        viewProjectionMatrix.set(projectionMatrix)
                .mul(viewMatrix);
    }

    private void calculateProjectionMatrix() {
        projectionMatrix.setPerspective((float) Math.toRadians(fov), aspect, near, far);
        viewProjectionMatrix.set(projectionMatrix)
                .mul(viewMatrix);
    }

    public float getAspect() {
        return aspect;
    }

    public float getNear() {
        return near;
    }

    public float getFar() {
        return far;
    }

    public float getFov() {
        return fov;
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Vector3fc getRotation() {
        return rotation;
    }

    public Matrix4fc getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4fc getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4fc getViewProjectionMatrix() {
        return viewProjectionMatrix;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
        calculateProjectionMatrix();
    }

    public void setNear(float near) {
        this.near = near;
        calculateProjectionMatrix();
    }

    public void setFar(float far) {
        this.far = far;
        calculateProjectionMatrix();
    }

    public void setFov(float fov) {
        this.fov = fov;
        calculateProjectionMatrix();
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        calculateViewMatrix();
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.set(x, y, z);
        calculateViewMatrix();
    }

    public void move(float dx, float dy, float dz) {
        if ( dz != 0 ) {
            position.x -= (float)Math.sin(Math.toRadians(rotation.y)) * dz;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * dz;
        }
        if ( dx != 0) {
            position.x -= (float)Math.sin(Math.toRadians(rotation.y - 90.0f)) * dx;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90.0f)) * dx;
        }
        position.y += dy;
        calculateViewMatrix();
    }

    public void rotate(float dx, float dy, float dz) {
        rotation.x += dx;
        rotation.y += dy;
        rotation.z += dz;
        calculateViewMatrix();
    }

    public Ray getRay(float x, float y) {
        Vector3f origin = new Vector3f();
        Vector3f direction = new Vector3f();
        viewProjectionMatrix.unprojectRay(x, y, new int[]{0, 0, 1, 1}, origin, direction);
        return new Ray(origin, direction);
    }

    public Vector3f project(Vector3fc position) {
        return viewProjectionMatrix.project(position,  new int[]{0, 0, 1, 1}, new Vector3f());
    }
}

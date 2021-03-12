package pl.edu.pw.mini.mg1.cameras;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PerspectiveCamera {
    private float aspect;
    private float near;
    private float far;
    private float fov;

    private Vector3f position;
    private Vector3f rotation;

    private Matrix4f viewMatrix;
    private Matrix4f projectionMatrix;
    private Matrix4f viewProjectionMatrix;

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

    public void setPosition(Vector3f position) {
        this.position = position;
        calculateViewMatrix();
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
        calculateViewMatrix();
    }
}

package pl.edu.pw.mini.mg1.cameras;

import com.jogamp.opengl.math.FloatUtil;
import org.joml.*;
import pl.edu.pw.mini.mg1.collisions.Ray;

import static org.joml.Math.*;

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

    private final Vector2i resolution;

    private float focalLength = 1.0f;
    private float eyeSeparation = 0.05f;

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
        this.resolution = new Vector2i();
        calculateProjectionMatrix();
    }

    private void calculateViewMatrix() {
        Vector3f front = getFront();
        Vector3f up = getUp();
        viewMatrix.lookAt(position, position.add(front, new Vector3f()), up);
        viewMatrix.set(getStereoViewMatrix(0));
        System.out.println(viewMatrix);
        viewProjectionMatrix.set(projectionMatrix)
                .mul(viewMatrix);
    }

    private void calculateProjectionMatrix() {
        projectionMatrix.setPerspective(toRadians(fov), aspect, near, far);
        projectionMatrix.set(getStereoPerspectiveMatrix(0));
        viewProjectionMatrix.set(projectionMatrix)
                .mul(viewMatrix);
    }

    private Matrix4f getStereoViewMatrix(int eye) {
        Vector3f right = getRight();
        Vector3f front = getFront();
        Vector3f up = getUp();
        Vector3f pos = position.add(right.mul(eyeSeparation * 0.5f * eye, new Vector3f()), new Vector3f());
        return new Matrix4f().lookAt(pos, pos.add(front, new Vector3f()), up);
    }

    private Matrix4f getStereoPerspectiveMatrix(int eye) {
        float eyeOff = eye * (eyeSeparation * 0.5f) * (near / focalLength);
        float top = near * tan(toRadians(fov) * 0.5f);
        float right = aspect * top;
        return new Matrix4f().frustum(-right - eyeOff, right - eyeOff, -top, top, near, far);
    }

    public Matrix4fc getStereoViewProjectionMatrix(int eye) {
        return getStereoPerspectiveMatrix(eye).mul(getStereoViewMatrix(eye));
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

    public Vector3f getRight() {
        return getFront().cross(new Vector3f(0, 1, 0)).normalize();
    }

    public Vector3f getUp() {
        return getRight().cross(getFront());
    }

    public Vector3f getFront() {
        return new Vector3f(cos(toRadians(rotation.y)) * cos(toRadians(rotation.x)),
                sin(toRadians(rotation.y)), cos(toRadians(rotation.y)) * sin(toRadians(rotation.x))).normalize();
    }

    private void setAspect(float aspect) {
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
        Vector3f front = getFront();
        Vector3f right = getRight();
        position.add(front.mul(dz));
        position.add(right.mul(dx));
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
        return viewProjectionMatrix.project(position,  new int[]{0, 0, resolution.x, resolution.y}, new Vector3f());
    }

    public Vector3f unproject(Vector3fc winCoords) {
        return viewProjectionMatrix.unproject(winCoords,  new int[]{0, 0, resolution.x, resolution.y}, new Vector3f());
    }

    public Vector2ic getResolution() {
        return resolution;
    }

    public void setResolution(int width, int height) {
        this.resolution.set(width, height);
        setAspect((float) width / height);
    }

    public float getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(float focalLength) {
        this.focalLength = focalLength;
    }

    public float getEyeSeparation() {
        return eyeSeparation;
    }

    public void setEyeSeparation(float eyeSeparation) {
        this.eyeSeparation = eyeSeparation;
    }
}

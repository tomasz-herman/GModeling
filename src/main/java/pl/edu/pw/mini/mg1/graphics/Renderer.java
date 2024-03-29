package pl.edu.pw.mini.mg1.graphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.GLBuffers;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.models.*;

import java.nio.IntBuffer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

public class Renderer {
    private final Shader shader;
    private final Shader bezierShader;
    private final Shader stereoShader;
    private final Shader patchShader;
    private final Shader patchSplineShader;
    private final Shader patchGregoryShader;
    private final Shader torusShader;
    private final Shader phongShader;
    private final Shader terrainShader;

    private final Texture defaultTexture;

    private Function<PerspectiveCamera, Matrix4fc> viewProjectionFunction = PerspectiveCamera::getViewProjectionMatrix;
    private BiConsumer<GL4, Scene> renderFunction = this::render;
    private boolean grayscale = false;

    private int leftBufferID, rightBufferID;
    private int leftDepthTextureID, rightDepthTextureID;
    private int leftTextureID, rightTextureID;

    private int quadVBO;

    static float[] quad = {
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        -1.0f,  1.0f, 0.0f,
        -1.0f,  1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        1.0f,  1.0f, 0.0f,
    };

    public Renderer(GL4 gl) {
        shader = new Shader(gl, "/default.vert", "/default.frag");
        bezierShader = new Shader(gl, "/bezier.vert", "/bezier.frag", "/bezier.geom");
        stereoShader = new Shader(gl, "/stereo.vert", "/stereo.frag");
        patchShader = new Shader(gl, "/patch.vert", "/patch.frag", "/patch.tesc", "/patch.tese", "/patch.geom");
        patchSplineShader = new Shader(gl, "/patch.vert", "/patch.frag", "/patch.tesc", "/bspline.tese", "/patch.geom");
        patchGregoryShader = new Shader(gl, "/patch.vert", "/patch.frag", "/gregory.tesc", "/gregory.tese", "/patch.geom");
        torusShader = new Shader(gl, "/torus.vert", "/torus.frag", "/torus.tesc", "/torus.tese", "/torus.geom");
        phongShader = new Shader(gl, "/phong.vert", "/phong.frag");
        terrainShader = new Shader(gl, "/terrain.vert", "/terrain.frag");
        gl.glClearColor(0f, 0f, 0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glVertexAttrib3f(1, 1, 1, 1);
        gl.glPointSize(5.0f);
        createFrameBuffer(gl, fboID -> leftBufferID = fboID, texID -> leftTextureID = texID, texID -> leftDepthTextureID = texID);
        createFrameBuffer(gl, fboID -> rightBufferID = fboID, texID -> rightTextureID = texID, texID -> rightDepthTextureID = texID);
        createQuad(gl);
        defaultTexture = new Texture(gl, 1024, (i, j) -> new Vector3f(), false, false);
    }

    public void render(GL4 gl, Scene scene) {
        clearColorAndDepth(gl);
        setGrayScale(gl, grayscale);
        viewProjectionFunction = PerspectiveCamera::getViewProjectionMatrix;
        for (Model model : scene.getModelsAndPointers()) {
            model.validate(gl);
            model.render(gl, scene.getCamera(), this);
        }
    }

    public void renderStereoWithExtraSteps(GL4 gl, Scene scene) {
        setGrayScale(gl, grayscale);
        gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, leftBufferID);
        gl.glViewport(0,0,scene.getCamera().getResolution().x(),scene.getCamera().getResolution().y());
        clearColorAndDepth(gl);
        viewProjectionFunction = (camera -> camera.getStereoViewProjectionMatrix(-1));
        for (Model model : scene.getModelsAndPointers()) {
            model.validate(gl);
            model.render(gl, scene.getCamera(), this);
        }

        gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, rightBufferID);
        gl.glViewport(0,0,scene.getCamera().getResolution().x(),scene.getCamera().getResolution().y());
        clearColorAndDepth(gl);
        viewProjectionFunction = (camera -> camera.getStereoViewProjectionMatrix(1));
        for (Model model : scene.getModelsAndPointers()) {
            model.validate(gl);
            model.render(gl, scene.getCamera(), this);
        }

        gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, 0);
        gl.glViewport(0,0,scene.getCamera().getResolution().x(),scene.getCamera().getResolution().y());
        clearColorAndDepth(gl);

        gl.glUseProgram(stereoShader.getProgramID());

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, leftTextureID);

        gl.glActiveTexture(GL4.GL_TEXTURE1);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, rightTextureID);

        stereoShader.loadInteger(gl, "leftTexture", 0);
        stereoShader.loadInteger(gl, "rightTexture", 1);

        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, quadVBO);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT,false,0,0);

        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 6);

        gl.glDisableVertexAttribArray(0);
        gl.glUseProgram(0);
    }

    public void clearColorAndDepth(GL4 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    }

    public void renderStereo(GL4 gl, Scene scene) {
        clearColorAndDepth(gl);
        setGrayScale(gl, grayscale);
        gl.glColorMask(true, false, false, true);
        viewProjectionFunction = (camera -> camera.getStereoViewProjectionMatrix(-1));
        for (Model model : scene.getModelsAndPointers()) {
            model.validate(gl);
            model.render(gl, scene.getCamera(), this);
        }
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        gl.glColorMask(false, true, true, true);
        viewProjectionFunction = (camera -> camera.getStereoViewProjectionMatrix(1));
        for (Model model : scene.getModelsAndPointers()) {
            model.validate(gl);
            model.render(gl, scene.getCamera(), this);
        }
        gl.glColorMask(true, true, true, true);
    }

    public void render(GL4 gl, PerspectiveCamera camera, Model model) {
        gl.glLineWidth(model instanceof Pointer ? 3 : 1);

        Matrix4f mvp = viewProjectionFunction.apply(camera).get(new Matrix4f());
        mvp.mul(model.getModelMatrix());

        gl.glUseProgram(shader.getProgramID());

        shader.loadMatrix4f(gl, "mvp", mvp);

        gl.glBindVertexArray(model.getMesh().getVao());
        gl.glDrawElements(model.getMesh().getPrimitivesType(),
                model.getMesh().vertexCount(),
                GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    public void renderBezier(GL4 gl, PerspectiveCamera camera, Model model) {
        gl.glLineWidth(1);

        Matrix4f mvp = viewProjectionFunction.apply(camera).get(new Matrix4f());
        mvp.mul(model.getModelMatrix());

        gl.glUseProgram(bezierShader.getProgramID());

        bezierShader.loadMatrix4f(gl, "mvp", mvp);
        bezierShader.loadInteger(gl, "resolution", Math.max(camera.getResolution().x(), camera.getResolution().y()));

        gl.glBindVertexArray(model.getMesh().getVao());
        gl.glDrawElements(model.getMesh().getPrimitivesType(),
                model.getMesh().vertexCount(),
                GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    private void setGrayScale(GL4 gl, boolean grayScale) {
        gl.glUseProgram(shader.getProgramID());
        shader.loadInteger(gl, "grayscale", grayScale ? 1 : 0);
        gl.glUseProgram(0);
    }

    public void reshape(GL gl, int x, int y, int width, int height) {
        gl.glViewport(x, y, width, height);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, leftTextureID);
        gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGB, width, height, 0, GL4.GL_RGB, GL4.GL_UNSIGNED_BYTE, null);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, rightTextureID);
        gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGB, width, height, 0, GL4.GL_RGB, GL4.GL_UNSIGNED_BYTE, null);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, rightDepthTextureID);
        gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT24, width, height, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, leftDepthTextureID);
        gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT24, width, height, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, 0);
    }

    public void dispose(GL4 gl) {
        shader.dispose(gl);
        bezierShader.dispose(gl);
        stereoShader.dispose(gl);
        phongShader.dispose(gl);
        torusShader.dispose(gl);
        patchShader.dispose(gl);
        patchGregoryShader.dispose(gl);
        patchSplineShader.dispose(gl);
        terrainShader.dispose(gl);
        defaultTexture.dispose(gl);
        gl.glDeleteFramebuffers(2, new int[] {leftBufferID, rightBufferID}, 0);
        gl.glDeleteTextures(2, new int[] {leftTextureID, rightTextureID, rightDepthTextureID, leftDepthTextureID}, 0);
        gl.glDeleteBuffers(1, new int[]{quadVBO}, 0);
    }

    public BiConsumer<GL4, Scene> getRenderFunction() {
        return renderFunction;
    }

    public void setRenderFunction(BiConsumer<GL4, Scene> renderFunction) {
        this.renderFunction = renderFunction;
    }

    public boolean isGrayscale() {
        return grayscale;
    }

    public void setGrayscale(boolean grayscale) {
        this.grayscale = grayscale;
    }

    public void createFrameBuffer(GL4 gl, IntConsumer fboID, IntConsumer texID, IntConsumer depthTexID) {
        IntBuffer framebuffer = GLBuffers.newDirectIntBuffer(1);
        gl.glGenFramebuffers(1, framebuffer);
        gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, framebuffer.get(0));

        IntBuffer texture = GLBuffers.newDirectIntBuffer(1);
        gl.glGenTextures(1, texture);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, texture.get(0));

        gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_RGB, 800, 600, 0, GL4.GL_RGB, GL4.GL_UNSIGNED_BYTE, null);

        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);

        IntBuffer depthTexture = GLBuffers.newDirectIntBuffer(1);
        gl.glGenTextures(1, depthTexture);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, depthTexture.get(0));

        gl.glTexImage2D(GL4.GL_TEXTURE_2D, 0, GL4.GL_DEPTH_COMPONENT24, 800, 600, 0, GL4.GL_DEPTH_COMPONENT, GL4.GL_FLOAT, null);

        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, GL4.GL_LINEAR);
        gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, GL4.GL_LINEAR);

        gl.glFramebufferTexture2D(GL4.GL_FRAMEBUFFER, GL4.GL_COLOR_ATTACHMENT0, GL4.GL_TEXTURE_2D, texture.get(0), 0);
        gl.glFramebufferTexture(GL4.GL_FRAMEBUFFER, GL4.GL_DEPTH_ATTACHMENT, depthTexture.get(0), 0);
        gl.glDrawBuffers(1, new int[] {GL4.GL_COLOR_ATTACHMENT0}, 0);

        if(gl.glCheckFramebufferStatus(GL4.GL_FRAMEBUFFER) != GL4.GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Cannot create frame buffer");

        fboID.accept(framebuffer.get(0));
        texID.accept(texture.get(0));
        depthTexID.accept(depthTexture.get(0));
    }

    public void createQuad(GL4 gl) {
        IntBuffer vbo = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, vbo);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo.get(0));
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, (long) quad.length * Float.BYTES, GLBuffers.newDirectFloatBuffer(quad), GL4.GL_STATIC_DRAW);
        quadVBO = vbo.get(0);
    }

    public void renderPatch(GL4 gl, PerspectiveCamera camera, BezierPatchC0 patch) {
        gl.glLineWidth(1);

        Matrix4f mvp = viewProjectionFunction.apply(camera).get(new Matrix4f());

        gl.glUseProgram(patchShader.getProgramID());

        patchShader.loadMatrix4f(gl, "mvp", mvp);
        patchShader.loadInteger(gl, "divisionsU", patch.getDivisionsU());
        patchShader.loadInteger(gl, "divisionsV", patch.getDivisionsV());
        patchShader.loadInteger(gl, "U", patch.U());
        patchShader.loadInteger(gl, "V", patch.V());
        torusShader.loadInteger(gl, "trimming", 0);
        torusShader.loadInteger(gl, "right", patch.isRightSide() ? 1 : 0);
        torusShader.loadInteger(gl, "left", patch.isLeftSide() ? 1 : 0);
        patchShader.loadVector3f(gl, "color", patch.isSelected() ? new Vector3f(0.8f, 0.6f, 0.2f) : new Vector3f(1));

        if(patch.getTexture() != null) patch.getTexture().use(gl, 0);
        else defaultTexture.use(gl, 0);

        gl.glBindVertexArray(patch.getMesh().getVao());
        gl.glPatchParameteri(GL4.GL_PATCH_VERTICES, 16);
        gl.glDrawElements(patch.getMesh().getPrimitivesType(),
                patch.getMesh().vertexCount(),
                GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    public void renderSplinePatch(GL4 gl, PerspectiveCamera camera, BezierPatchC2 patch) {
        gl.glLineWidth(1);

        Matrix4f mvp = viewProjectionFunction.apply(camera).get(new Matrix4f());

        gl.glUseProgram(patchSplineShader.getProgramID());

        patchSplineShader.loadMatrix4f(gl, "mvp", mvp);
        patchSplineShader.loadInteger(gl, "divisionsU", patch.getDivisionsU());
        patchSplineShader.loadInteger(gl, "divisionsV", patch.getDivisionsV());
        patchSplineShader.loadInteger(gl, "U", patch.U());
        patchSplineShader.loadInteger(gl, "V", patch.V());
        torusShader.loadInteger(gl, "trimming", 0);
        torusShader.loadInteger(gl, "right", patch.isRightSide() ? 1 : 0);
        torusShader.loadInteger(gl, "left", patch.isLeftSide() ? 1 : 0);
        patchSplineShader.loadVector3f(gl, "color", patch.isSelected() ? new Vector3f(0.8f, 0.6f, 0.2f) : new Vector3f(1));

        if(patch.getTexture() != null) patch.getTexture().use(gl, 0);
        else defaultTexture.use(gl, 0);

        gl.glBindVertexArray(patch.getMesh().getVao());
        gl.glPatchParameteri(GL4.GL_PATCH_VERTICES, 16);
        gl.glDrawElements(patch.getMesh().getPrimitivesType(),
                patch.getMesh().vertexCount(),
                GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    public void renderGregoryPatch(GL4 gl, PerspectiveCamera camera, GregoryPatch patch) {
        gl.glLineWidth(1);

        Matrix4f mvp = viewProjectionFunction.apply(camera).get(new Matrix4f());

        gl.glUseProgram(patchGregoryShader.getProgramID());

        patchGregoryShader.loadMatrix4f(gl, "mvp", mvp);
        patchGregoryShader.loadInteger(gl, "divisionsU", patch.getDivisionsU());
        patchGregoryShader.loadInteger(gl, "divisionsV", patch.getDivisionsV());
        patchGregoryShader.loadInteger(gl, "U", patch.U());
        patchGregoryShader.loadInteger(gl, "V", patch.V());
        torusShader.loadInteger(gl, "right", 1);
        torusShader.loadInteger(gl, "left", 1);
        patchShader.loadVector3f(gl, "color", patch.isSelected() ? new Vector3f(0.8f, 0.6f, 0.2f) : new Vector3f(1));

        gl.glBindVertexArray(patch.getMesh().getVao());
        gl.glPatchParameteri(GL4.GL_PATCH_VERTICES, 20);
        gl.glDrawElements(patch.getMesh().getPrimitivesType(),
                patch.getMesh().vertexCount(),
                GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    public void renderTorus(GL4 gl, PerspectiveCamera camera, Torus torus) {
        gl.glLineWidth(1);

        Matrix4f mvp = viewProjectionFunction.apply(camera).get(new Matrix4f());
        mvp.mul(torus.getModelMatrix());

        gl.glUseProgram(torusShader.getProgramID());

        torusShader.loadMatrix4f(gl, "mvp", mvp);
        torusShader.loadInteger(gl, "divisionsU", torus.getInnerSegments());
        torusShader.loadInteger(gl, "divisionsV", torus.getOuterSegments());
        torusShader.loadInteger(gl, "trimming", 0);
        torusShader.loadInteger(gl, "right", torus.isRightSide() ? 1 : 0);
        torusShader.loadInteger(gl, "left", torus.isLeftSide() ? 1 : 0);
        torusShader.loadFloat(gl, "r", torus.getInnerRadius());
        torusShader.loadFloat(gl, "R", torus.getOuterRadius());

        torusShader.loadVector3f(gl, "color", torus.isSelected() ? new Vector3f(0.8f, 0.6f, 0.2f) : new Vector3f(1));

        if(torus.getTexture() != null) torus.getTexture().use(gl, 0);
        else defaultTexture.use(gl, 0);

        gl.glBindVertexArray(torus.getMesh().getVao());
        gl.glPatchParameteri(GL4.GL_PATCH_VERTICES, 1);
        gl.glDrawElements(torus.getMesh().getPrimitivesType(),
                torus.getMesh().vertexCount(),
                GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    public void renderTerrain(GL4 gl, PerspectiveCamera camera, Model model, Texture heights) {
        gl.glLineWidth(1);

        Matrix4f mvp = viewProjectionFunction.apply(camera).get(new Matrix4f());
        mvp.mul(model.getModelMatrix());

        gl.glUseProgram(terrainShader.getProgramID());

        terrainShader.loadMatrix4f(gl, "mvp", mvp);
        terrainShader.loadMatrix4f(gl, "model", model.getModelMatrix());
        terrainShader.loadInteger(gl, "heights", 0);
        terrainShader.loadVector3f(gl, "viewPos", camera.getPosition());

        if(heights != null) heights.use(gl, 0);
        else defaultTexture.use(gl, 0);

        gl.glBindVertexArray(model.getMesh().getVao());
        gl.glDrawElements(model.getMesh().getPrimitivesType(),
                model.getMesh().vertexCount(),
                GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    public void renderPhong(GL4 gl, PerspectiveCamera camera, Model model) {
        gl.glLineWidth(1);

        Matrix4f mvp = viewProjectionFunction.apply(camera).get(new Matrix4f());
        mvp.mul(model.getModelMatrix());

        gl.glUseProgram(phongShader.getProgramID());

        phongShader.loadMatrix4f(gl, "mvp", mvp);
        phongShader.loadMatrix4f(gl, "model", model.getModelMatrix());

        gl.glBindVertexArray(model.getMesh().getVao());
        gl.glDrawElements(model.getMesh().getPrimitivesType(),
                model.getMesh().vertexCount(),
                GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }
}

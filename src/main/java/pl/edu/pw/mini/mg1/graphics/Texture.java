package pl.edu.pw.mini.mg1.graphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.GLBuffers;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.function.BiFunction;

public class Texture {
    private final IntBuffer id = GLBuffers.newDirectIntBuffer(1);

    public Texture(GL4 gl, int size, BiFunction<Float, Float, Vector3f> pattern) {
        gl.glGenTextures(1, id);
        use(gl, 0);

        var pixels = new ArrayList<Byte>(4 * size * size);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float u = (float) y / size;
                float v = (float) x / size;
                Vector3f color = pattern.apply(u, v);
                int r = (int) (color.x * 255);
                int g = (int) (color.y * 255);
                int b = (int) (color.z * 255);
                pixels.add((byte) r);
                pixels.add((byte) g);
                pixels.add((byte) b);
                pixels.add((byte) 255);
            }
        }

        ByteBuffer buffer = GLBuffers.newDirectByteBuffer(ArrayUtils.toPrimitive(pixels.toArray(new Byte[0])));

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, size, size, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);

        gl.glTextureParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTextureParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        gl.glTextureParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        gl.glTextureParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);

        gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
    }

    public void use(GL4 gl, int unit) {
        gl.glActiveTexture(GL.GL_TEXTURE0 + unit);
        gl.glBindTexture(GL.GL_TEXTURE_2D, id.get(0));
    }

    public void dispose(GL4 gl) {
        gl.glDeleteTextures(1, id);
    }
}
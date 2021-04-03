package pl.edu.pw.mini.mg1.graphics;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.GLBuffers;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.utils.IOUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Shader {
    private final int programID;

    public Shader(GL4 gl, String vertexShaderPath, String fragmentShaderPath) {
        List<Integer> shaderList = new ArrayList<>();

        shaderList.add(createShader(gl, GL4.GL_VERTEX_SHADER, IOUtils.readResource(vertexShaderPath)));
        shaderList.add(createShader(gl, GL4.GL_FRAGMENT_SHADER, IOUtils.readResource(fragmentShaderPath)));

        programID = createProgram(gl, shaderList);

        shaderList.forEach(gl::glDeleteShader);
    }

    public Shader(GL4 gl, String vertexShaderPath, String fragmentShaderPath, String geometryShaderPath) {
        List<Integer> shaderList = new ArrayList<>();

        shaderList.add(createShader(gl, GL4.GL_VERTEX_SHADER, IOUtils.readResource(vertexShaderPath)));
        shaderList.add(createShader(gl, GL4.GL_FRAGMENT_SHADER, IOUtils.readResource(fragmentShaderPath)));
        shaderList.add(createShader(gl, GL4.GL_GEOMETRY_SHADER, IOUtils.readResource(geometryShaderPath)));

        programID = createProgram(gl, shaderList);

        shaderList.forEach(gl::glDeleteShader);
    }

    public void loadInteger(GL4 gl, String name, int value) {
        int location = gl.glGetUniformLocation(programID, name);
        gl.glUniform1i(location, value);
    }

    public void loadFloat(GL4 gl, String name, float value) {
        int location = gl.glGetUniformLocation(programID, name);
        gl.glUniform1f(location, value);
    }

    public void loadVector3f(GL4 gl, String name, Vector3fc vector) {
        int location = gl.glGetUniformLocation(programID, name);
        gl.glUniform3f(location, vector.x(), vector.y(), vector.z());
    }

    public void loadMatrix4f(GL4 gl, String name, Matrix4fc matrix) {
        int location = gl.glGetUniformLocation(programID, name);
        gl.glUniformMatrix4fv(location, 1, false,
                matrix.get(Buffers.newDirectFloatBuffer(16)));
    }

    private int createProgram(GL4 gl, List<Integer> shaderList) {

        int program = gl.glCreateProgram();

        shaderList.forEach(shader -> gl.glAttachShader(program, shader));

        gl.glLinkProgram(program);

        IntBuffer status = GLBuffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(program, GL4.GL_LINK_STATUS, status);
        if (status.get(0) == GL4.GL_FALSE) {

            IntBuffer infoLogLength = GLBuffers.newDirectIntBuffer(1);
            gl.glGetProgramiv(program, GL4.GL_INFO_LOG_LENGTH, infoLogLength);

            ByteBuffer bufferInfoLog = GLBuffers.newDirectByteBuffer(infoLogLength.get(0));
            gl.glGetProgramInfoLog(program, infoLogLength.get(0), null, bufferInfoLog);
            byte[] bytes = new byte[infoLogLength.get(0)];
            bufferInfoLog.get(bytes);
            String strInfoLog = new String(bytes);

            System.err.println("Linker failure: " + strInfoLog);
        }

        shaderList.forEach(shader -> gl.glDetachShader(program, shader));

        return program;
    }

    private int createShader(GL4 gl, int shaderType, String shaderFile) {

        int shader = gl.glCreateShader(shaderType);
        String[] lines = {shaderFile};
        IntBuffer length = GLBuffers.newDirectIntBuffer(new int[]{lines[0].length()});
        gl.glShaderSource(shader, 1, lines, length);

        gl.glCompileShader(shader);

        IntBuffer status = GLBuffers.newDirectIntBuffer(1);
        gl.glGetShaderiv(shader, GL4.GL_COMPILE_STATUS, status);
        if (status.get(0) == GL4.GL_FALSE) {

            IntBuffer infoLogLength = GLBuffers.newDirectIntBuffer(1);
            gl.glGetShaderiv(shader, GL4.GL_INFO_LOG_LENGTH, infoLogLength);

            ByteBuffer bufferInfoLog = GLBuffers.newDirectByteBuffer(infoLogLength.get(0));
            gl.glGetShaderInfoLog(shader, infoLogLength.get(0), null, bufferInfoLog);
            byte[] bytes = new byte[infoLogLength.get(0)];
            bufferInfoLog.get(bytes);
            String strInfoLog = new String(bytes);

            String strShaderType = switch (shaderType) {
                case GL4.GL_VERTEX_SHADER -> "vertex";
                case GL4.GL_GEOMETRY_SHADER -> "geometry";
                case GL4.GL_FRAGMENT_SHADER -> "fragment";
                default -> "";
            };
            System.err.println("Compiler failure in " + strShaderType + " shader: " + strInfoLog);
        }

        return shader;
    }

    public int getProgramID() {
        return programID;
    }

    public void dispose(GL4 gl) {
        gl.glDeleteProgram(programID);
    }
}

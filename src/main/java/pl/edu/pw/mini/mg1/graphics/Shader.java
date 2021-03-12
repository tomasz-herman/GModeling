package pl.edu.pw.mini.mg1.graphics;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.GLBuffers;
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

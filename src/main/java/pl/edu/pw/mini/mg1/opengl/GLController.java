package pl.edu.pw.mini.mg1.opengl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

public class GLController implements GLEventListener {
    private float rotateT = 0.0f;

    public void init(final GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
    }

    public void reshape(final GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
        System.out.println("Yay, a reshape " + width);
        GL2 gl = drawable.getGL().getGL2();
        final float aspect = (float) width / (float) height;
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();
        final float fh = 0.5f;
        final float fw = fh * aspect;
        gl.glFrustumf(-fw, fw, -fh, fh, 1.0f, 1000.0f);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(final GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glRotatef(rotateT, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateT, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(rotateT, 0.0f, 0.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f(0.0f, 1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 0.0f);
        gl.glVertex3f( 1.0f, 1.0f, 0.0f);
        gl.glVertex3f( 1.0f,-1.0f, 0.0f);
        gl.glVertex3f(-1.0f,-1.0f, 0.0f);
        gl.glEnd();
        rotateT += 0.2f;
    }

    public void dispose(final GLAutoDrawable drawable) {

    }

    public void setRotateT(float rotateT) {
        this.rotateT = rotateT;
    }
}

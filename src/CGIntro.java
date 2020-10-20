import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.PMVMatrix;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.glsl.ShaderState;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import static com.jogamp.opengl.GL.GL_VERSION;

public class CGIntro implements GLEventListener {

	/**
	 * CGIntro - a 10 second intro for CG.  Eric McCreath 2009, 2011, 2015, 2017
	 * 
	 * 
	 */

	JFrame jf;

	GLProfile profile;
	GLCapabilities caps;

	GLJPanel gljpanel;

	Dimension dim = new Dimension(800, 600);
	FPSAnimator animator;

	float time;

	PMVMatrix matrix;

	Texture cgtexture;
	float cgtextureAspect;

	static int fps = 20;
	static float introTime = 10.0f; // seconds

	int shaderprogram, vertexshader, fragshader;
	int vertexbuffer[];
	int colorbuffer[];
	int texbuffer[];

	public CGIntro() {
		jf = new JFrame("CG Intro");
		profile = GLProfile.getDefault();
		caps = new GLCapabilities(profile);
		gljpanel = new GLJPanel();
		gljpanel.addGLEventListener(this);
		gljpanel.requestFocusInWindow();
		jf.getContentPane().add(gljpanel);

		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		jf.setPreferredSize(dim);
		jf.pack();
		animator = new FPSAnimator(gljpanel, fps);
		time = 0.0f;
		animator.start();
	}

	public static void main(String[] args) {
		new CGIntro();
	}
	//todo for mac, i can only use the default shader version
	static final String vertstr[] = { "attribute vec4 vertex;\n" + "attribute vec2 texcoord;\n"
			+ "uniform mat4 mvMat, pMat;\n" + "varying vec2 tex_coord;\n" + "void main() {\n"
			+ "    tex_coord = texcoord;\n" + "    gl_Position = (pMat * mvMat) * vertex;\n" + "}\n" };

	static int vlens[] = new int[1];
	static int flens[] = new int[1];

	static final String fragstr[] = {  " uniform sampler2D texture;\n"
			+ " varying vec2 tex_coord;\n" + "void main() {\n"
			+ "   gl_FragColor = texture2D(texture,tex_coord);\n" + "}\n" };

	public void init(GLAutoDrawable dr) { // set up openGL for 2D drawing
		GL2 gl2 = dr.getGL().getGL2();
		GLU glu = new GLU();
		GLUT glut = new GLUT();
		System.out.println("GL_VERSION : " + gl2.glGetString(GL2.GL_VERSION));
		System.out.println("GL_SHADING_LANGUAGE_VERSION : " + gl2.glGetString(GL2.GL_SHADING_LANGUAGE_VERSION));
		matrix = new PMVMatrix();
		matrix.glMatrixMode(GL2.GL_PROJECTION);
		matrix.glFrustumf(-2.0f, 2.0f, -2.0f, 2.0f, 1.0f, 10.0f);
		matrix.glMatrixMode(GL2.GL_MODELVIEW);
		matrix.gluLookAt(0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

		// setup and load the vertex and fragment shader programs
		shaderprogram = gl2.glCreateProgram();
		vertexshader = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);
		vlens[0] = vertstr[0].length();
		gl2.glShaderSource(vertexshader, 1, vertstr, vlens, 0);
		gl2.glCompileShader(vertexshader);
		checkok(gl2, vertexshader, GL2.GL_COMPILE_STATUS);
		gl2.glAttachShader(shaderprogram, vertexshader);
		fragshader = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		flens[0] = fragstr[0].length();
		gl2.glShaderSource(fragshader, 1, fragstr, flens, 0);
		gl2.glCompileShader(fragshader);
		checkok(gl2, fragshader, GL2.GL_COMPILE_STATUS);
		gl2.glAttachShader(shaderprogram, fragshader);
		gl2.glLinkProgram(shaderprogram);
		checkok(gl2, shaderprogram, GL2.GL_LINK_STATUS);
		gl2.glValidateProgram(shaderprogram);
		checkok(gl2, shaderprogram, GL2.GL_VALIDATE_STATUS);
		gl2.glUseProgram(shaderprogram);

		gl2.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		try {
			cgtexture = TextureIO.newTexture(new File("images/compgraphicslogo.png"), true);
			cgtexture.enable(gl2);
			cgtextureAspect = ((float) cgtexture.getImageWidth()) / cgtexture.getImageHeight();
		} catch (GLException | IOException e) {
			e.printStackTrace();
		}

		// load the vertex and texture array
		float[] triangleArray = { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
				0.0f, 0.0f, 1.0f, 0.0f };

		FloatBuffer triangleVertexBuffer = Buffers.newDirectFloatBuffer(triangleArray);
		float[] texArray = { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f };
		FloatBuffer texCoordBuffer = Buffers.newDirectFloatBuffer(texArray);

		vertexbuffer = new int[1];
		gl2.glGenBuffers(1, vertexbuffer, 0);
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexbuffer[0]);
		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, (long) triangleArray.length * 4, triangleVertexBuffer,
				GL2.GL_STATIC_DRAW);

		texbuffer = new int[1];
		gl2.glGenBuffers(1, texbuffer, 0);
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, texbuffer[0]);
		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, (long) texArray.length * 4, texCoordBuffer, GL2.GL_STATIC_DRAW);
	}

	private void checkok(GL2 gl2, int program, int type) {
		IntBuffer intBuffer = IntBuffer.allocate(1);
		gl2.glGetProgramiv(program, type, intBuffer);
		if (intBuffer.get(0) != GL.GL_TRUE) {
			int[] len = new int[1];
			gl2.glGetProgramiv(program, GL2.GL_INFO_LOG_LENGTH, len, 0);
			if (len[0] != 0) {
				byte[] errormessage = new byte[len[0]];
				gl2.glGetProgramInfoLog(program, len[0], len, 0, errormessage, 0);
				System.err.println("problem\n" + new String(errormessage));
				gljpanel.destroy();
				jf.dispose();
				System.exit(0);
			}
		}
	} 
	
	
	public void display(GLAutoDrawable dr) {
		GL2 gl2 = dr.getGL().getGL2();
		GLU glu = new GLU();
		GLUT glut = new GLUT();

		float scale = 2.2f;
		float proportiondone = time / introTime;

		gl2.glUseProgram(shaderprogram);
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

		// set up the matrix transformation - the idea was to create the sign to move up
		// and rotate to front and center at the end
		matrix.glPushMatrix();
		matrix.glTranslatef(0.0f, -10.0f * (1.0f - proportiondone), 0.0f);
		matrix.glScalef(scale * cgtextureAspect, scale, scale);
		matrix.glTranslatef(-0.5f, 0.0f, 0.0f);
		matrix.glRotatef(-160.0f * (1.0f - proportiondone), 1.0f, 0.0f, 0.0f);

		// load the uniforms
		int mvMatrixID = gl2.glGetUniformLocation(shaderprogram, "mvMat");
		gl2.glUniformMatrix4fv(mvMatrixID, 1, false, matrix.glGetMvMatrixf());

		int pMatrixID = gl2.glGetUniformLocation(shaderprogram, "pMat");
		gl2.glUniformMatrix4fv(pMatrixID, 1, false, matrix.glGetPMatrixf());

		// set the buffers for drawing
		int posVAttrib = gl2.glGetAttribLocation(shaderprogram, "vertex");
		gl2.glEnableVertexAttribArray(posVAttrib);
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexbuffer[0]);
		gl2.glVertexAttribPointer(posVAttrib, 3, GL2.GL_FLOAT, false, 0, 0);

		int texAttrib = gl2.glGetAttribLocation(shaderprogram, "texcoord");
		gl2.glEnableVertexAttribArray(texAttrib);
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, texbuffer[0]);
		gl2.glVertexAttribPointer(texAttrib, 2, GL2.GL_FLOAT, false, 0, 0);

		
		gl2.glUniform1i(gl2.glGetUniformLocation(shaderprogram, "texture"), 0);
		gl2.glActiveTexture(GL2.GL_TEXTURE0);
		cgtexture.bind(gl2);

		// do the drawing
		gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, 6);

		gl2.glDisableVertexAttribArray(posVAttrib);
		gl2.glDisableVertexAttribArray(texAttrib);

		matrix.glPopMatrix();

		gl2.glFlush();

		if (time < introTime)
			time += 1.0f / fps;
	}

	public void dispose(GLAutoDrawable glautodrawable) {
	}

	public void reshape(GLAutoDrawable dr, int x, int y, int width, int height) {
	}
}

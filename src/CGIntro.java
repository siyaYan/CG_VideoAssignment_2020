
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;


import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import java.util.Random;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class CGIntro implements GLEventListener {

	/**
	 * CGIntro - a 10 second intro for CG.  Eric McCreath 2009, 2011, 2015, 2017
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 * random speed/direction/startPos/rotate & image/audio
	 */

	JFrame jf;
	GLProfile profile;
	GLCapabilities caps;
	GLJPanel gljpanel;
	Dimension dim = new Dimension(800, 800);
	FPSAnimator animator;
	float time;
	static int fps = 20;
	static float introTime = 50.0f; // seconds
	Texture cgtexture;
	Texture object;
	float cgtextureAspect;

	//we will have more than one objects for the following natures
	int[] rotate= new int[30];//rotate true/false for x,y,z
	int[] startpos=new int[10];//from -10 to 10
	int[] direct=new int[10];//-1 or 1(righe or left)
	int[] speed=new int[10];//-1,0,1(3 types)
	int[] starttime=new int[10];//0-10(10 types)
	boolean rote=false;

	float lightpos[] = { 3f, 4f, 10f, 1.0f };
	float ground[] = { 0.0f, 0.0f, -3.0f };
	float groundnormal[] = { 0.0f, 0.0f, -1.0f };

	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
		new CGIntro();
	}

	/**
	 * play background music function:
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 * todo choose a more suitable wav file
	 */
	public void play(String filePath) throws IOException, UnsupportedAudioFileException {
		AudioInputStream ais = AudioSystem.getAudioInputStream(new File(filePath));
		try {
			SourceDataLine line = AudioSystem.getSourceDataLine(ais.getFormat());
			line.open();
			line.start();
			int readBytes = 0;
			byte[] streamBuffer = new byte[512];
			while (true) {
				readBytes= ais.read(streamBuffer, 0, streamBuffer.length);
				if (readBytes <= 0)
					break;
				line.write(streamBuffer, 0, readBytes);
			}
			line.drain();
			line.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Random algorithms:(for one object, totally have 10)
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 * 3 options (1,0,-1) represent 3 types
	 */
	public void Random(int num) {
		rote=false;
		System.out.println("object "+num+": Random start!");
		Random rand = new Random();
		//ture or false for rotate x,y,z
		for (int index = 0; index < 3; index++) {
			//rotate[num+index]=(int)(2*Math.random());//random 0,1
			rotate[num+index]=rand.nextInt(3)-1;//random 1,0,-1
			if (rotate[num + index] == 1||rotate[num + index] == -1) {
				rote=true;
			}
		};
		//if x,y,z =0,x=1 default make object rotate to right in x axis
		if (!rote) {
			rotate[num]=1;
		}
		System.out.println("rotate:"+rotate[num]+","+rotate[num+1]+","+rotate[num+2]+",");
		//random from -10 to 10,note:-10 is the rightmost
		startpos[num]=rand.nextInt(21)-10;
		// do not have the same startPos
		if (num > 0) {
			for (int i = 0; i < num; i++) {
				while (startpos[i] == startpos[num]) {
					startpos[num]=rand.nextInt(21)-10;
				}
			}
		}
		System.out.println("startos:"+startpos[num]);
		//random speedDicrect can only be 1 or -1
		while (direct[num] == 0) {
			direct[num]=rand.nextInt(3)-1;//random -1,0,1
		}
		//make the falling more reasonale
		//if the start point is too right,make is falling to left
		if (startpos[num] <= -8) {
			direct[num]=1;
		}
		//converse
		if (startpos[num] >= 8) {
			direct[num]=-1;
		}
		System.out.println("direct:"+direct[num]);
		// 3 types of speed
		speed[num]=rand.nextInt(3)-1;//random -1,0,1
		System.out.println("speed:"+speed[num]);
		//start time from 0-9
		starttime[num]=rand.nextInt(10);//random 1,0,-1
		System.out.println("startTime:"+starttime[num]+"\n");
	}

	/**
	 * translation/rotation algorithms:
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 * todo add Acceleration of gravity(acc seems reality)
	 */
	public void transAndRotate(GL2 gl2,GLU glu, GLUT glut,int num) {
		float scale = 1;
		//falling down speed(10,20,30)-starttime(0-9)
		float speedDown = (speed[num]*10+20)*(time / introTime)-starttime[num];
		//speed for rotate and to left or right is default
		float speedRotate = 10*(time / introTime);
		float speedLeftOrRight = 8*(time / introTime);

		gl2.glPushMatrix();
		//if starttime haven't come
		if (speedDown < 0) {
			gl2.glTranslatef(0, 20 , 0.0f);
		} else {
			gl2.glTranslatef(startpos[num]+(speedLeftOrRight*direct[num]), (10.0f)-(speedDown), 0.0f);
		}
		gl2.glRotatef(180.0f * (speedRotate), 1.0f*rotate[num], 1.0f*rotate[num+1], 1.0f*rotate[num+2]);
		drawSphere(gl2,glu,glut);
		gl2.glPopMatrix();

		gl2.glPushMatrix();
		if (speedDown < 0) {
			gl2.glTranslatef(0,20,0);
		} else {
			gl2.glTranslatef(startpos[num]+(speedLeftOrRight*direct[num]), (10.0f)-(speedDown), 0.0f);
		}
		gl2.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
		gl2.glDisable(GL2.GL_LIGHTING);
		gl2.glPolygonOffset(-1.0f, -1.0f);
		gl2.glPushMatrix();
		float[] rgb=Color.darkGray.getColorComponents(null);
		gl2.glColor3d(rgb[0],rgb[1],rgb[2]);
		projectShadow(gl2, ground, groundnormal, lightpos);
		//gl2.glColor3d(1,0,0);
		drawShadow(gl2,glu,glut);
		gl2.glPopMatrix();
		gl2.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
		gl2.glEnable(GL2.GL_LIGHTING);
		gl2.glPopMatrix();
		//time change from 0.0 to 9.95, every step increase 0.05
		if (time < introTime) {
			//System.out.println(time);
			time += 1.0f / fps;
		}
	}

	/**
	 * using glusphere draw a simple sphere:
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 * todo texture binding to the sphere need to improve
	 */
	public void drawSphere(GL2 gl2, GLU glu, GLUT glut) {
		gl2.glPushMatrix();
		object.bind(gl2);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		GLUquadric sphere=glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(sphere,GLU.GLU_FILL);
		glu.gluQuadricTexture(sphere, true);
		glu.gluQuadricNormals(sphere, GLU.GLU_SMOOTH);
		glu.gluSphere(sphere,0.5,50,50);
		gl2.glPopMatrix();
	}
	public void drawShadow(GL2 gl2, GLU glu, GLUT glut) {
		object.disable(gl2);
		gl2.glPushMatrix();
		GLUquadric sphere=glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(sphere,GLU.GLU_FILL);
		glu.gluQuadricTexture(sphere, true);
		glu.gluQuadricNormals(sphere, GLU.GLU_SMOOTH);
		glu.gluSphere(sphere,0.5,50,50);
		gl2.glPopMatrix();
		object.enable(gl2);
	}
	/**
	 * draw the background:
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 * todo choose a more suitable image
	 */
	public void drawBackground(GL2 gl2, GLU glu, GLUT glut) {
		gl2.glPushMatrix();
		cgtexture.bind(gl2);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex3d(-20.0, -20.0, ground[2]);
		gl2.glTexCoord3d(0.0,0.0,ground[2]);
		gl2.glVertex3d(-20.0, 20.0, ground[2]);
		gl2.glTexCoord3d(1.0,0.0,ground[2]);
		gl2.glVertex3d(20.0, 20.0, ground[2]);
		gl2.glTexCoord3d(1.0,1.0,ground[2]);
		gl2.glVertex3d(20.0, -20.0, ground[2]);
		gl2.glTexCoord3d(0.0,1.0,ground[2]);
		gl2.glEnd();
		gl2.glDisable(GL2.GL_TEXTURE_2D);
		gl2.glPopMatrix();
	}

	public CGIntro() throws IOException, UnsupportedAudioFileException {
		for (int i = 0; i < 10; i++) {
			Random(i);
		}
		//Random(0);
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
		play("src/background.wav");
	}

	/**
	 * init the environment with 2 light and load file
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 *
	 */
	public void init(GLAutoDrawable dr) { // set up openGL for 2D drawing
		GL2 gl2 = dr.getGL().getGL2();
		GLU glu = new GLU();
		GLUT glut = new GLUT();
		gl2.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		gl2.glEnable(GL2.GL_DEPTH_TEST);

		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();

		glu.gluPerspective(60.0, 1.0, 1.0, 50.0);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		glu.gluLookAt(0.0, 0, 16.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

		gl2.glEnable(GL2.GL_LIGHTING);

		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[]{ 1f, 1f,1f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{0,0,0}, 0);
		gl2.glEnable(GL2.GL_LIGHT0);
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, new float[]{ 0.3f, 0.3f,0.3f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, new float[]{ 0.7f, 0.7f, 0.7f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT1,GL2.GL_SPECULAR, new float[]{ 0.6f, 0.6f, 0.6f, 1.0f },0);
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightpos, 0);
		gl2.glEnable(GL2.GL_LIGHT1);
		gl2.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, new float[]{ 0.3f, 0.3f,0.3f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, new float[]{ 0.7f, 0.7f, 0.7f, 1.0f }, 0);
		gl2.glLightfv(GL2.GL_LIGHT2,GL2.GL_SPECULAR, new float[]{ 0.6f, 0.6f, 0.6f, 1.0f },0);
		gl2.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, lightpos, 0);
		gl2.glEnable(GL2.GL_LIGHT2);

		try {
			cgtexture = TextureIO.newTexture(new File("src/images/backgound.jpg"), true);
			object = TextureIO.newTexture(new File("src/images/strawberry_2.jpg"), true);
			object.enable(gl2);
			cgtexture.enable(gl2);
			//cgtextureAspect = ((float) cgtexture.getImageWidth()) / cgtexture.getImageHeight();
		} catch (GLException | IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * using glusphere draw a simple sphere:
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 * todo modify the shadows for objects
	 */
	public void display(GLAutoDrawable dr) {
		GL2 gl2 = dr.getGL().getGL2();
		GLU glu = new GLU();
		GLUT glut = new GLUT();

		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		drawBackground(gl2,glu,glut);
		//10 objects
		for (int i = 0; i < 10; i++) {
			transAndRotate(gl2,glu,glut,i);
		}

		if (time < introTime) {
			//System.out.println(time);
			time += 1.0f / fps;
		}
		gl2.glFlush();
	}

	//	ShadowCup - this is a simple example of using a transformation to create shadows
	//	Eric McCreath 2009, 2011, 2015
	// multiply the current matrix with a projection matrix that will do the
	// shadow
	static void projectShadow(GL2 gl, float s[], float n[], float l[]) {
		float w, m;
		float mat[] = new float[4 * 4];

		w = (s[0] - l[0]) * n[0] + (s[1] - l[1]) * n[1] + (s[2] - l[2]) * n[2];
		m = l[0] * n[0] + l[1] * n[1] + l[2] * n[2];

		mat[index(0, 0)] = w + n[0] * l[0];
		mat[index(0, 1)] = n[1] * l[0];
		mat[index(0, 2)] = n[2] * l[0];
		mat[index(0, 3)] = -(w + m) * l[0];

		mat[index(1, 0)] = n[0] * l[1];
		mat[index(1, 1)] = w + n[1] * l[1];
		mat[index(1, 2)] = n[2] * l[1];
		mat[index(1, 3)] = -(w + m) * l[1];

		mat[index(2, 0)] = n[0] * l[2];
		mat[index(2, 1)] = n[1] * l[2];
		mat[index(2, 2)] = w + n[2] * l[2];
		mat[index(2, 3)] = -(w + m) * l[2];

		mat[index(3, 0)] = n[0];
		mat[index(3, 1)] = n[1];
		mat[index(3, 2)] = n[2];
		mat[index(3, 3)] = -m;

		gl.glMultMatrixf(mat, 0);

	}

	private static int index(int j, int i) {
		return j + 4 * i;
	}
	public void dispose(GLAutoDrawable glautodrawable) {
	}

	public void reshape(GLAutoDrawable dr, int x, int y, int width, int height) {
	}

}

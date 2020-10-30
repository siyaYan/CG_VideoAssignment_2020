
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
import java.util.Random;
import java.util.Vector;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class CGIntro implements GLEventListener {

	/**
	 * CGIntro - a 10 second intro for CG.  Eric McCreath 2009, 2011, 2015, 2017
	 *
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 * many objects with texture randomly from different position falling down with various moving track(have background music)
	 * Random: start position/falling speed/falling direction/rotate direction
	 */

	JFrame jf;
	GLProfile profile;
	GLCapabilities caps;
	GLJPanel gljpanel;
	Dimension dim = new Dimension(800, 800);
	FPSAnimator animator;

	float time;
	static int fps = 20;
	static float introTime = 120.0f; // 12 seconds

	float lightpos[] = { 2f, 2f, 30f, 1.0f };
	//size 40*40 pos(0,0,-15)
	float background[] = { 40.0f, 40.0f, -15.0f };
	Texture backgroundtexture;
	Texture object;
	Texture test;
	Texture ninjatextures;
	Texture fruittextures;

	//	File list for fruits
	String fruitFileName[] = {"src/fruit/Apple1.obj", "src/fruit/Watermelon1.obj", "src/fruit/Orange1.obj",
			"src/fruit/Apple1.obj","src/fruit/Watermelon1.obj","src/fruit/Orange1.obj",
			"src/fruit/Apple1.obj", "src/fruit/Watermelon1.obj", "src/fruit/Orange1.obj",
			"src/fruit/Apple1.obj", "src/fruit/Watermelon1.obj", "src/fruit/Orange1.obj",
			"src/fruit/Apple1.obj", "src/fruit/Watermelon1.obj", "src/fruit/Orange1.obj"};
	//number of fruit objects
	int objectNum=fruitFileName.length;
	//parameter for fruit objects
	int[] rotate= new int[3*objectNum];//rotate true/false for x,y,z(one object have to store 3 boolean state)
	int[] startposx=new int[objectNum];//from -10 to 10 in x axis
	int[] startposz=new int[objectNum];//from -1/0/1 to chose start pos in z axis
	int[] directx=new int[objectNum];//-1/0/1(go to righe/none/left in x axis)
	int[] directz=new int[objectNum];//-1/0/1(go to righe/none/left in z axis)
	int[] speed=new int[objectNum];//-1,0,1(3 types: slow/nomal/fast)
	int[] starttime=new int[objectNum];//0-10(10 types)
	boolean rote=false;

	//setting noraml speed for direction and rotation(can be change be random parameters)
	float xSpeed=8;
	float zSpeed=6;
	float rotateSpeed=6;

	//	File list for letters
	String letterFileName[] = {"src/letters/n-1.obj", "src/letters/i-1.obj", "src/letters/n-2.obj", "src/letters/j.obj", "src/letters/a.obj", "src/letters/p.obj",
			"src/letters/o.obj","src/letters/r.obj","src/letters/i-2.obj","src/letters/u.obj","src/letters/m.obj"};

	//number of letters
	int letterNum= letterFileName.length;
	//parameters for letters
	int[] speedLetter=new int[letterNum];//0,1(2 types: nomal/fast)
	int[] startTimeLetter=new int[letterNum];//0-2(3 types)

	//setting start/stop position of letters and rotate speed
	float[] startPosLetter={-6.25f,-5f,-3.75f,-2.5f,-1.25f,0f,1.25f,2.5f,3.75f,5f,6.25f};//11 letter in x axis
	float stopPosLetters=4;//letters stop position(screen can see 10 to -10 in both x & y axis)
	float rotateLetters=4;//rotate speed for letters

	//for shadow
	float groundShadow[] = { 0.0f, 0.0f, -13.0f };
	float groundnormal[] = { 0.0f, 0.0f, -10.0f };


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
	 * Random algorithms:(for [objectNum] fruits have 7 parameters each)
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 */
	public void randomFruits(int num) {
		rote=false;
		System.out.println("object "+num+": Random start!");
		Random rand = new Random();

		//object rotate direction
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

		//obejct start position(in x/z axis,y axis is fixed)
		//startposx from -10 to 10 in x axis
		startposx[num]=rand.nextInt(21)-10;
		//startposz from -1 to 1 in z axis
		startposz[num]=rand.nextInt(3)-1;

		// do not have the same startPos
		if (num > 0) {
			for (int i = 0; i < num; i++) {
				while (startposx[i] == startposx[num]) {
					startposx[num]=rand.nextInt(21)-10;
				}
				while (startposz[i] == startposz[num]) {
					startposz[num]=rand.nextInt(3)-1;
				}
			}
		}


		//object moving direction
		//random directx/directz from -1 to 1
		directx[num]=rand.nextInt(3)-1;//random -1,0,1
		directz[num]=rand.nextInt(3)-1;//random -1,0,1

		//make the falling more reasonale
		//if the start point is too right,make is falling to left
		if (startposx[num] <= -8) {
			directx[num]=1;
		}
		//converse
		if (startposx[num] >= 8) {
			directx[num]=-1;
		}
		//if the start point is too back,make is falling to front
		if (startposz[num] < 0) {
			directz[num]=1;
		}
		//converse
		if (startposz[num] > 0) {
			directz[num]=-1;
		}

		//object moving speed
		// 3 types of speed
		speed[num]=rand.nextInt(3)-1;//random -1,0,1


		//object moving start time
		//start time from 0-9
		starttime[num]=rand.nextInt(10);//random 0-9

		//leave the space for letter falling(letters are just on x axis,z=0)
		//fruits must be not z=0 And stright falling down But is fine if z=0 and falling down with other direction
		if (startposz[num] == 0 && directx[num] == 0 && directz[num] == 0) {
			startposz[num]=1;
		}

		//print the parameters
		System.out.println("speed:"+speed[num]);
		System.out.println("rotate:"+rotate[num]+","+rotate[num+1]+","+rotate[num+2]+",");
		System.out.println("startposx:"+startposx[num]);
		System.out.println("startposz:"+startposz[num]);
		System.out.println("directx:"+directx[num]);
		System.out.println("directz:"+directz[num]);
		System.out.println("startTime:"+starttime[num]+"\n");
	}

	/**
	 * Random algorithms:(for [letterNum] letters have 2 parameters each,just falling down in different time and speed)
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 */
	public void randomLetters(int num) {
		System.out.println("letter "+num+": Random start!");
		Random rand = new Random();
		//object moving speed
		// 2 types of speed
		speedLetter[num]=rand.nextInt(2);//random 0,1
		System.out.println("speed:"+speedLetter[num]);
		//object moving start time
		//start time from 0-2
		startTimeLetter[num]=rand.nextInt(3);//random 0,1,2
		System.out.println("startTime:"+startTimeLetter[num]+"\n");
	}

	/**
	 * translation/rotation algorithms for fruits
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 */
	public void transAndRotateFruits(GL2 gl2,GLU glu, GLUT glut,int num) {
		float scale = 0.8f;
		//falling down speed(10,20,30)-starttime(0-9)
		float speedDown = (speed[num]*10+20)*(time / introTime)-starttime[num];
		//speed for rotate and to left or right is default
		float speedLeftOrRight=xSpeed*(time / introTime);
		float speedBackOrFront=zSpeed*(time / introTime);
		float speedRotate=rotateSpeed * (time / introTime);

		gl2.glPushMatrix();
		//if it's not your turn stay in the pos (0,20,0)
		if (speedDown < 0) {
			gl2.glTranslatef(0, 20 , 0.0f);
		}
		else {
			gl2.glTranslatef(startposx[num]+(speedLeftOrRight*directx[num]), (10.0f)-(speedDown), startposz[num]+(speedBackOrFront*directz[num]));
		}
		//every rotateSpeed *(time / introTime) rotate half_circle
		gl2.glRotatef(180.0f * (speedRotate), 1.0f*rotate[num], 1.0f*rotate[num+1], 1.0f*rotate[num+2]);
		gl2.glScaled(scale,scale,scale);
		drawFruits(gl2, glu, glut, fruitFileName[num], 0.0f);
		gl2.glPopMatrix();

		gl2.glPushMatrix();
		gl2.glScaled(scale,scale,scale);
		gl2.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
		gl2.glDisable(GL2.GL_LIGHTING);
		gl2.glPolygonOffset(-0.5f, -0.5f);
		float[] rgb=Color.darkGray.getColorComponents(null);
		gl2.glColor3d(rgb[0],rgb[1],rgb[2]);
		projectShadow(gl2, groundShadow, groundnormal, lightpos);
		//if it's not your turn stay in the pos (0,20,0)
		if (speedDown < 0) {
			gl2.glTranslatef(0,20,0.0f);
		}
		else {
			gl2.glTranslatef(startposx[num]+(speedLeftOrRight*directx[num]), (10.0f)-(speedDown), startposz[num]+(speedBackOrFront*directz[num]));
		}
		gl2.glRotatef(180.0f * (speedRotate), 1.0f*rotate[num], 1.0f*rotate[num+1], 1.0f*rotate[num+2]);
		fruittextures.disable(gl2);
		gl2.glScalef(0.25f, 0.25f, 0.25f);
		drawShadow(gl2, glu, glut,fruitFileName[num]);
		fruittextures.enable(gl2);
		gl2.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
		gl2.glEnable(GL2.GL_LIGHTING);
		gl2.glPopMatrix();
		//time change from 0.0 to 99.95, every step increase 0.05
		if (time < introTime) {
			//System.out.println(time);
			time += 1.0f / fps;
		}
	}

	/**
	 * translation/rotation algorithms for letters
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 */
	public void transAndRotateLetters(GL2 gl2,GLU glu, GLUT glut,int num) {
		float scale = 1f;
		//falling down speed(20,30)-starttime(0-3)
		float speedDown = (speedLetter[num]*10+20)*(time / introTime)-startTimeLetter[num];//falling down routine
		float position = 10f-speedDown;//position now
		float rotation = rotateLetters* (time / introTime);//rotate letters

		gl2.glPushMatrix();
		//if it's not your turn stay in the pos (0,20,0)
		if (speedDown < 0) {
			gl2.glTranslatef(0,20,0.0f);
		} else if (position>stopPosLetters) {
			gl2.glTranslatef(startPosLetter[num], (10.0f) - (speedDown), 0);
			//every second rotate 360 with z axis
			gl2.glRotatef(360.0f * (rotation), 0f, 1f, 0f);
		} else {
			gl2.glTranslatef(startPosLetter[num], stopPosLetters, 0);//every second rotate 180 with z axis
			//gl2.glRotatef(180.0f * (rotation), 0f, 1f, 0f);
		}

		gl2.glScaled(scale,scale,scale);

		drawLetters(gl2, glu, glut, letterFileName[num], 0.0f);

		gl2.glPopMatrix();

		gl2.glPushMatrix();
		gl2.glScaled(scale,scale,scale);
		gl2.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
		gl2.glDisable(GL2.GL_LIGHTING);
		gl2.glPolygonOffset(-0.5f, -0.5f);
		float[] rgb=Color.darkGray.getColorComponents(null);
		gl2.glColor3d(rgb[0],rgb[1],rgb[2]);
		projectShadow(gl2, groundShadow, groundnormal, lightpos);
		//if it's not your turn stay in the pos (0,20,0)
		if (speedDown < 0) {
			gl2.glTranslatef(0,20,0.0f);
		} else if (position>stopPosLetters) {
			gl2.glTranslatef(startPosLetter[num], (10.0f) - (speedDown), 0);
			gl2.glRotatef(360.0f * (rotation), 0f, 1f, 0f);
		} else {
			gl2.glTranslatef(startPosLetter[num], stopPosLetters, 0);
		}
		ninjatextures.disable(gl2);
		drawShadow(gl2, glu, glut, letterFileName[num]);
		ninjatextures.enable(gl2);
		gl2.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
		gl2.glEnable(GL2.GL_LIGHTING);
		gl2.glPopMatrix();

		//time change from 0.0 to 99.95, every step increase 0.05
		if (time < introTime) {
			//System.out.println(time);
			time += 1.0f / fps;
		}
	}

	/**
	 * using glusphere draw a simple sphere
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 */
	public void drawFruits(GL2 gl2, GLU glu, GLUT glut, String file, float xOffset) {
		gl2.glPushMatrix();
		gl2.glScalef(0.5f, 0.5f, 0.5f);
//		xOffset aligns each letter consecutively based off manual refinement
		gl2.glTranslatef(xOffset, 0.0f, 0.0f);
	
		Vector<float[]> verts = new Vector<float[]>();
		Vector<float[]> uvs = new Vector<float[]>();
		Vector<float[]> norms = new Vector<float[]>();
		System.out.println(file.split("/")[1]);
		Mesh.loadObjModel(file, verts, uvs, norms);
		fruittextures.bind(gl2);

		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glBegin(GL2.GL_TRIANGLES);
		for (int i = 0; i < verts.size(); i++) {
			if(i%3 == 0) { 			
//				first vertex normal, vertex, uv
				gl2.glNormal3f(norms.get(i)[0], norms.get(i)[1], norms.get(i)[2]);
				gl2.glVertex3f(verts.get(i)[0], verts.get(i)[1], verts.get(i)[2]);
				gl2.glTexCoord2f(uvs.get(i)[0], uvs.get(i)[1]);
//				second vertex normal, vertex, uv
				gl2.glNormal3f(norms.get(i+1)[0], norms.get(i+1)[1], norms.get(i+1)[2]);
				gl2.glVertex3f(verts.get(i+1)[0], verts.get(i+1)[1], verts.get(i+1)[2]);
				gl2.glTexCoord2f(uvs.get(i+1)[0], uvs.get(i+1)[1]);
//				third vertex: normal, vertex, uv
				gl2.glNormal3f(norms.get(i+2)[0], norms.get(i+2)[1], norms.get(i+2)[2]);
				gl2.glVertex3f(verts.get(i+2)[0], verts.get(i+2)[1], verts.get(i+2)[2]);
				gl2.glTexCoord2f(uvs.get(i+2)[0], uvs.get(i+2)[1]);
				
			}
		
		}
		gl2.glDisable(GL2.GL_TEXTURE_2D);
		gl2.glEnd();
		gl2.glPopMatrix();
	}
	/**
	 * test draw something
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 */
/*	public void drawSomething(GL2 gl2, GLU glu, GLUT glut) {
		gl2.glPushMatrix();
		test.bind(gl2);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		GLUquadric some=glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(some,GLU.GLU_FILL);
		glu.gluQuadricTexture(some, true);
		glu.gluQuadricNormals(some, GLU.GLU_SMOOTH);
		glu.gluSphere(some,0.5,50,50);
		//glu.gluCylinder(some,1,1,1,1,1);
		gl2.glPopMatrix();
	}*/
	
	/**
	 * drawLetters - uses vector data and triangle indicies to map textures and verticies to buffer for displaying .obj imported objects.
	 * @auther:Jaryd Sartori
	 * @uid: u5799628
	 */
	public void drawLetters(GL2 gl2, GLU glu, GLUT glut, String file, float xOffset) {
		gl2.glPushMatrix();
		gl2.glScalef(2.0f, 2.0f, 2.0f);
//		xOffset aligns each letter consecutively based off manual refinement
		gl2.glTranslatef(xOffset, 0.0f, 0.0f);
	
		Vector<float[]> verts = new Vector<float[]>();
		Vector<float[]> uvs = new Vector<float[]>();
		Vector<float[]> norms = new Vector<float[]>();
		System.out.println(file.split("/")[1]);
		Mesh.loadObjModel(file, verts, uvs, norms);

		ninjatextures.bind(gl2);

		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glBegin(GL2.GL_TRIANGLES);
		for (int i = 0; i < verts.size(); i++) {
			if(i%3 == 0) { 			
//				first vertex normal, vertex, uv
				gl2.glNormal3f(norms.get(i)[0], norms.get(i)[1], norms.get(i)[2]);
				gl2.glVertex3f(verts.get(i)[0], verts.get(i)[1], verts.get(i)[2]);
				gl2.glTexCoord2f(uvs.get(i)[0], uvs.get(i)[1]);
//				second vertex normal, vertex, uv
				gl2.glNormal3f(norms.get(i+1)[0], norms.get(i+1)[1], norms.get(i+1)[2]);
				gl2.glVertex3f(verts.get(i+1)[0], verts.get(i+1)[1], verts.get(i+1)[2]);
				gl2.glTexCoord2f(uvs.get(i+1)[0], uvs.get(i+1)[1]);
//				third vertex: normal, vertex, uv
				gl2.glNormal3f(norms.get(i+2)[0], norms.get(i+2)[1], norms.get(i+2)[2]);
				gl2.glVertex3f(verts.get(i+2)[0], verts.get(i+2)[1], verts.get(i+2)[2]);
				gl2.glTexCoord2f(uvs.get(i+2)[0], uvs.get(i+2)[1]);
				
			}
		
		}
		gl2.glDisable(GL2.GL_TEXTURE_2D);
		gl2.glEnd();
		gl2.glPopMatrix();
	}

	/**
	 * disable bind and draw sphere shadow
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 */
/*	public void drawShadow(GL2 gl2, GLU glu, GLUT glut) {
		object.disable(gl2);
		gl2.glPushMatrix();
		GLUquadric sphere=glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(sphere,GLU.GLU_FILL);
		glu.gluQuadricTexture(sphere, true);
		glu.gluQuadricNormals(sphere, GLU.GLU_SMOOTH);
		glu.gluSphere(sphere,0.5,50,50);
		gl2.glPopMatrix();
		object.enable(gl2);
	}*/

	/**
	 * disable bind and draw letters&fruits shadow
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 */
	public void drawShadow(GL2 gl2, GLU glu, GLUT glut,String file) {
		gl2.glPushMatrix();
		gl2.glScalef(2.0f, 2.0f, 2.0f);

		Vector<float[]> verts = new Vector<float[]>();
		Vector<float[]> uvs = new Vector<float[]>();
		Vector<float[]> norms = new Vector<float[]>();

		Mesh.loadObjModel(file, verts, uvs, norms);
		gl2.glBegin(GL2.GL_TRIANGLES);
		for (int i = 0; i < verts.size(); i++) {
			if(i%3 == 0) {
//				first vertex normal, vertex, uv
				gl2.glNormal3f(norms.get(i)[0], norms.get(i)[1], norms.get(i)[2]);
				gl2.glVertex3f(verts.get(i)[0], verts.get(i)[1], verts.get(i)[2]);
//				second vertex normal, vertex, uv
				gl2.glNormal3f(norms.get(i+1)[0], norms.get(i+1)[1], norms.get(i+1)[2]);
				gl2.glVertex3f(verts.get(i+1)[0], verts.get(i+1)[1], verts.get(i+1)[2]);
//				third vertex: normal, vertex, uv
				gl2.glNormal3f(norms.get(i+2)[0], norms.get(i+2)[1], norms.get(i+2)[2]);
				gl2.glVertex3f(verts.get(i+2)[0], verts.get(i+2)[1], verts.get(i+2)[2]);
			}
		}
		gl2.glEnd();
		gl2.glPopMatrix();
	}


	/**
	 * bind texture and draw the background
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 * todo choose a more suitable image
	 */
	public void drawBackground(GL2 gl2, GLU glu, GLUT glut) {
		gl2.glPushMatrix();
		backgroundtexture.bind(gl2);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glVertex3d(-(background[0]/2), -(background[1]/2), background[2]);
		gl2.glTexCoord3d(0.0,0.0,background[2]);
		gl2.glVertex3d(-(background[0]/2), (background[1]/2), background[2]);
		gl2.glTexCoord3d(1.0,0.0,background[2]);
		gl2.glVertex3d((background[0]/2), (background[1]/2), background[2]);
		gl2.glTexCoord3d(1.0,1.0,background[2]);
		gl2.glVertex3d((background[0]/2), -(background[1]/2), background[2]);
		gl2.glTexCoord3d(0.0,1.0,background[2]);
		gl2.glEnd();
		gl2.glDisable(GL2.GL_TEXTURE_2D);
		gl2.glPopMatrix();
	}

	/**
	 * init the setting, start random, and play music
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 */
	public CGIntro() throws IOException, UnsupportedAudioFileException {
		for (int i = 0; i < objectNum; i++) {
			randomFruits(i);
		}
		for (int j = 0; j < letterNum; j++) {
			randomLetters(j);
		}
		jf = new JFrame("Falling Fruits");
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
	 * init the opengl setting with 2 light and load texture file
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 *
	 */
	public void init(GLAutoDrawable dr) {
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
		glu.gluLookAt(0.0, 0, 15.0, 0, 0.0, 0.0, 0.0, 1.0, 0.0);

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
			backgroundtexture = TextureIO.newTexture(new File("src/images/backgound.jpg"), true);
			object = TextureIO.newTexture(new File("src/images/strawberry_2.jpg"), true);
			ninjatextures = TextureIO.newTexture(new File("src/images/ninjatextures.jpeg"), true);
			fruittextures = TextureIO.newTexture(new File("src/images/fruittextures.png"), true);
			
			object.enable(gl2);
			backgroundtexture.enable(gl2);
			ninjatextures.enable(gl2);
			fruittextures.enable(gl2);
		} catch (GLException | IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * draw 10 objects with texture and shadows, can falling down in different speed direction and start from different position
	 * @auther: Xiran Yan(Siya)
	 * @uid: u7167582
	 */
	public void display(GLAutoDrawable dr) {
		GL2 gl2 = dr.getGL().getGL2();
		GLU glu = new GLU();
		GLUT glut = new GLUT();

		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		drawBackground(gl2,glu,glut);
		//10 objects
		for (int i = 0; i < objectNum; i++) {
			transAndRotateFruits(gl2,glu,glut,i);
		}
		for (int i = 0; i < letterNum; i++) {
			transAndRotateLetters(gl2,glu,glut,i);
		}

		if (time < introTime) {
			//System.out.println(time);
			time += 1.0f / fps;
		}
		gl2.glFlush();
	}

	//Eric McCreath 2009, 2011, 2015
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

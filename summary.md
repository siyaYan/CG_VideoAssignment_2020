# CGintro Summary

# Overall:

A program for Youtube Ninjaporium channel introduction video,which contains a Scene that watermelons,oranges,apples can randomly falling down with music.

# Functions:

+ Randomly produce various parameters for fruits and letters.
1.  Fruits: start_position/falling_direction/falling_speed/rotate_speed
2.  Letters: letterSpeed/letterStartTime
+ play background music.
+ create watermelons,oranges,and apples.(Blender)
+ create channel letters.(Blender)
+ import blender object into OpenGl.
+ draw blender object by OpenGl.
+ various objects falling down with rotation in different methods.
+ channel letters falling down with rotation and then suspend in the midair.

# Details:(add more details)

+   using javax.sound to obtain audio stream and play the music.

+   these parameters are not randomly but are changable in the setting:

Fruits:
1.  objectNum (number of obejcts)
2.  xSpeed/zSpeed (speed in x&z axis)
3.  rotateSpeed (speed of rotation)
4.  lightpos[] (position of light)
5.  background[] (position of background)

Letters:
1.  startPosLetters (11 letters' position in x axis)
2.  stopPosLetters (letters stop position)
3.  rotateLetters (rotate speed for letters)

+   randomly parameters are as following:

Fruits:
1.  startposx&startposz - objects start from which position (can start from -10 to 10 in x axis& -1 to 1 in z axis)
(notice: position in y axis is fixed at the begining) 
2.  rotate - rotation options for x,y,z axis(rotate true/false for x,y,z)
3.	directx&directz - falling direction for x&z axis (falling down with righe/left/front/back direction or just straight falling down)
(notice: direction about y is fixed) 
4.	speed - 3 types of falling down speed (slow/nomal/fast)
5.	starttime - when do the object start falling?(from 0-10 secends)

Letters:
1.  speedLetter - the speed of letters falling down (2 types: nomal/fast)
2.  startTimeLetter - when is the letter start falling (3 types  start from 0,1,2 second)

# Design process:

1.  analyse functions can meet requirements.
2.  design the whole process in the scene.
3.  search the sources like images and music.
4.  design the objects for falling in the scene in Blender.
5.  design the drawing sphere and background functions.
6.  design the randomly produce parameters function.
7.  design the translation/rotation function for objects.
8.  design the play music function.
10. import the objects build in Blender
11. design the objects drawing function.
12.  Test every unit functions, fix the bugs.
13.  Test the whole program.

# Test:(add more testing plan)

1.  Seperately test following functions(white box testing/Debug model)

    +   Random(int num)(Debug&print the values)
    +   play(String filePath)
    +   transAndRotate(GL2 gl2,GLU glu, GLUT glut,int num)
    +   drawSphere(GL2 gl2, GLU glu, GLUT glut)
    +   drawShadow(GL2 gl2, GLU glu, GLUT glut)
    +   drawBackground(GL2 gl2, GLU glu, GLUT glut)

2.  Test the whole program.(black Box testing)

    +   run the program several times to test the randomly parameters and different outcomes

# Notice:
+   if you can't use the makefile(written jogl jar in our path), use makefile_original((original_path) alternatively!
 Because don't know the makefile testing will use jogl in our file path or not.


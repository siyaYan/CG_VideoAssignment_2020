bin/CGIntro.class: src/CGIntro.java
	javac -d bin -cp /usr/share/java/jogl2.jar:/usr/share/java/gluegen2-rt.jar src/CGIntro.java

run: bin/CGIntro.class
	java -cp bin:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-rt.jar CGIntro


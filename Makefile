bin/CGIntro.class: src/CGIntro.java
	javac -d bin -cp lib/jogl-all.jar:lib/gluegen-rt.jar src/CGIntro.java

run: bin/CGIntro.class
	java -cp bin:lib/jogl-all.jar:lib/gluegen-rt.jar CGIntro
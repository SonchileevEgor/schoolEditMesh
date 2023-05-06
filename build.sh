SRC_PATHS="./src:jogl/jar/gluegen-rt.jar:jogl/jar/jogl-all.jar:Junit/junit-4.11.jar:Junit/hamcrest-core-1.3.jar:jlatexmath/jlatexmath-1.0.3.jar"

javac -d ./obj -classpath $SRC_PATHS src/*.java
javac -d ./obj -classpath $SRC_PATHS src/util/*.java
javac -d ./obj -classpath $SRC_PATHS src/gui/*.java
javac -d ./obj -classpath $SRC_PATHS src/geom/*.java
javac -d ./obj -classpath $SRC_PATHS src/editor/*.java
javac -d ./obj -classpath $SRC_PATHS src/builders/*.java
javac -d ./obj -classpath $SRC_PATHS src/bodies/*.java
javac -d ./obj -classpath $SRC_PATHS src/minjson/*.java
javac -d ./obj -classpath $SRC_PATHS src/OpenGL/*.java
jar cvmf manifest.mf sch3dedit.jar -C ./obj . >jar.log

SET SRC_PATHS="./src;jogl/jar/gluegen-rt.jar;jogl/jar/jogl-all.jar;Junit/junit-4.11.jar;Junit/hamcrest-core-1.3.jar;jlatexmath/jlatexmath-1.0.3.jar"

javac -d ./obj -classpath %SRC_PATHS% -encoding Utf-8 src/*.java
javac -d ./obj -classpath %SRC_PATHS% -encoding Utf-8 src/util/*.java
javac -d ./obj -classpath %SRC_PATHS% -encoding Utf-8 src/OpenGL/*.java
javac -d ./obj -classpath %SRC_PATHS% -encoding Utf-8 src/gui/*.java
javac -d ./obj -classpath %SRC_PATHS% -encoding Utf-8 src/geom/*.java
javac -d ./obj -classpath %SRC_PATHS% -encoding Utf-8 src/editor/*.java
javac -d ./obj -classpath %SRC_PATHS% -encoding Utf-8 src/builders/*.java
javac -d ./obj -classpath %SRC_PATHS% -encoding Utf-8 src/bodies/*.java
javac -d ./obj -classpath %SRC_PATHS% -encoding Utf-8 src/minjson/*.java
jar cvmf manifest.mf sch3dedit.jar -C ./obj .

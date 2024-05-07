JAVAC     = javac
JAVA      = java
JAR       = jar
CLASSPATH = src/main/java/ru/game/reversi

all: 
	$(JAVAC) $(CLASSPATH)/*.java
	cd src/main/java/ && $(JAR) cfe ../../../Reversi.jar ru.game.reversi.Main ru/game/reversi/*.class

clean:
	@rm -vf $(CLASSPATH)/*.class *.jar

run: all
	$(JAVA) -jar Reversi.jar

# vim: et!

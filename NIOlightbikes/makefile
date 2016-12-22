all: LightBikes.jar

LightBikes.jar: manifest.mf *.class
	jar -cvfm LightBikes.jar manifest.mf *.class
	
%.class: %.java
	javac -Xlint *.java
	
clean: 
	rm *.class
	
proper:
	rm *.jar

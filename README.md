set ASM_JAR=C:\Projects\java\eltoar-mapper\bin\asm-9.6.jar
set ASM_UTIL_JAR=C:\Projects\java\eltoar-mapper\bin\asm-util-9.6.jar
set CLASSPATH=app\build\classes\java\main;app\build\classes\java\test;%ASM_JAR%;%ASM_UTIL_JAR%

java -cp %CLASSPATH% org.objectweb.asm.util.ASMifier com.akilisha.reactive.Data0
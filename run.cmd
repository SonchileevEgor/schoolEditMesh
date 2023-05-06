@ECHO OFF
IF %PROCESSOR_ARCHITECTURE% == x86 (
  java -Djava.library.path=jogl/lib/windows-i586 -jar sch3dedit.jar %*
) ELSE (
  java -Djava.library.path=jogl/lib/windows-amd64 -jar sch3dedit.jar %*
)



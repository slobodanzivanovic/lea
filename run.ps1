javac -cp src/main/java src/main/java/com/slobodan/*.java

if ($LastExitCode -eq 0) {
    java -cp src/main/java com.slobodan.Main
} else {
    Write-Host "Compilation failed, what is wrong with you bruhh"
}
:: Setup shared simlinks

cd %~dp0

rmdir /s /q "apix-impl/60/src/main/java-shared"
rmdir /s /q "apix-impl/52/src/main/java-shared"
rmdir /s /q "apix-impl/51/src/main/java-shared"
rmdir /s /q "apix-impl/50/src/main/java-shared"
mklink /J "apix-impl/60/src/main/java-shared" "apix-impl/src/main/java/"
mklink /J "apix-impl/52/src/main/java-shared" "apix-impl/src/main/java/"
mklink /J "apix-impl/51/src/main/java-shared" "apix-impl/src/main/java/"
mklink /J "apix-impl/50/src/main/java-shared" "apix-impl/src/main/java/"

rmdir /s /q "apix-impl/60/src/test/java-shared"
rmdir /s /q "apix-impl/52/src/test/java-shared"
rmdir /s /q "apix-impl/51/src/test/java-shared"
rmdir /s /q "apix-impl/50/src/test/java-shared"
mkdir "apix-impl/60/src/test/"
mkdir "apix-impl/52/src/test/"
mkdir "apix-impl/51/src/test/"
mkdir "apix-impl/50/src/test/"
mklink /J "apix-impl/60/src/test/java-shared" "apix-impl/src/test/java/"
mklink /J "apix-impl/52/src/test/java-shared" "apix-impl/src/test/java/"
mklink /J "apix-impl/51/src/test/java-shared" "apix-impl/src/test/java/"
mklink /J "apix-impl/50/src/test/java-shared" "apix-impl/src/test/java/"

rmdir /s /q "apix-integrationtests/60/src/integration-test/java-shared"
rmdir /s /q "apix-integrationtests/52/src/integration-test/java-shared"
rmdir /s /q "apix-integrationtests/51/src/integration-test/java-shared"
rmdir /s /q "apix-integrationtests/50/src/integration-test/java-shared"
mkdir "apix-integrationtests/60/src/integration-test"
mkdir "apix-integrationtests/52/src/integration-test"
mkdir "apix-integrationtests/51/src/integration-test"
mkdir "apix-integrationtests/50/src/integration-test"

mklink /J "apix-integrationtests/60/src/integration-test/java-shared" "apix-impl/src/integration-test/java"
mklink /J "apix-integrationtests/52/src/integration-test/java-shared" "apix-impl/src/integration-test/java"
mklink /J "apix-integrationtests/51/src/integration-test/java-shared" "apix-impl/src/integration-test/java"
mklink /J "apix-integrationtests/50/src/integration-test/java-shared" "apix-impl/src/integration-test/java"
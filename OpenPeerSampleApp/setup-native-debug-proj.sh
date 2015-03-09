ln -s ../openpeer-android-sdk/assets/openpeer_database.sql assets/openpeer_database.sql

ln -s ../../../../openpeer-android-sdk/src/com/openpeer/javaapi src/com/openpeer/javaapi
ln -s ../../../../openpeer-android-sdk/src/com/openpeer/sdk src/com/openpeer/sdk
ln -s ../openpeer-android-sdk/jni jni
ln -s ../../openpeer-android-sdk/assets/openpeer_database.sql openpeer_database.sql
cp .project-native-debug .project
cp .classpath-native-debug .classpath
cp project.properties-native project.properties

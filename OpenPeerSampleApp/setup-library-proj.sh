pushd .
cd assets
ln -s ../../openpeer-android-sdk/assets/openpeer_database.sql openpeer_database.sql
popd

rm src/com/openpeer/javaapi
rm src/com/openpeer/sdk
rm jni
rm -rf obj
rm -rf libs/armeabi-v7a
cp .project-library .project
cp .classpath-library .classpath
cp project.properties-library project.properties

#!/bin/sh

#Note [TBD] : There is no check for ndk-version
#Please use the ndk-version as per host machine for now

#Get the machine type
PROCTYPE=`uname -m`

if [ "$PROCTYPE" = "i686" ] || [ "$PROCTYPE" = "i386" ] || [ "$PROCTYPE" = "i586" ] ; then
        echo "Host machine : x86"
        ARCHTYPE="x86"
else
        echo "Host machine : x86_64"
        ARCHTYPE="x86_64"
fi

#Get the Host OS
HOST_OS=`uname -s`
case "$HOST_OS" in
    Darwin)
        HOST_OS=darwin
        ;;
    Linux)
        HOST_OS=linux
        ;;
esac

#ndk-path
if [[ $1 == *android-ndk-* ]]; then
	echo "----------------- NDK Path is : $1 ----------------"
	Input=$1;
else
	echo "Please enter your android ndk path:"
	echo "For example:/home/astro/android-ndk-r8e"
	read Input
	echo "You entered:$Input"
fi
if [[ $2 == *clean* ]]; then
	echo "------cleaning old build----------"
	rm -rf libs/op/libs/build
	rm -rf libs/op/libs/ortc-lib/libs/build/
fi


#Set path
echo "----------------- Exporting the android-ndk path ----------------"
export PATH=$PATH:$Input:$Input/toolchains/arm-linux-androideabi-4.7/prebuilt/$HOST_OS-$ARCHTYPE/bin

#build all modules
echo "******************BUILD STARTED FOR ALL ORTC LIBS*******************"

#boost build -- 1
#pushd `pwd`
#cd libs/op/libs/ortc-lib/libs/boost/projects
#./build_boost_android.sh $Input
#popd

#openssl build -- 2
#pushd `pwd`
#cd libs/op/libs/ortc-lib/libs/openssl/projects/android
#./build-libssl_android.sh $Input
#popd

#curl build -- 3
pushd `pwd`
cd libs/op/libs/ortc-lib/libs/curl-build-scripts/
./build_curl_android.sh $Input
popd

#cryptopp build -- 4
pushd `pwd`
cd libs/op/libs/ortc-lib/libs/cryptopp/projects/android
./build_cryptopp_android.sh $Input
popd

#udns build -- 5
pushd `pwd`
cd libs/op/libs/ortc-lib/libs/udns/projects/android
./build_udns_android.sh $Input
popd

#zsLib build -- 6
pushd `pwd`
cd libs/op/libs/ortc-lib/libs/zsLib/projects/android
./build_zsLib_android.sh $Input
popd

#idnkit build -- 6
pushd `pwd`
cd libs/op/libs/ortc-lib/libs/idnkit/projects/android
./build_idnkit_android.sh $Input
popd

#punycode build -- 7
#pushd `pwd`
#cd libs/op/libs/ortc-lib/libs/punycode/projects/android
#./build_punycode_android.sh $Input
#popd

#op-services-cpp build -- 8
pushd `pwd`
cd libs/op/libs/ortc-lib/libs/op-services-cpp/projects/android
./build_op-services-cpp_android.sh $Input
popd

#webrtc build -- 9
pushd `pwd`
cd libs/op/libs/ortc-lib/libs/webrtc/projects/android
./build_webrtc_android.sh $Input
popd

#sqlite build -- 10
pushd `pwd`
cd libs/op/libs/sqlite/projects/android
./build_sqlite_android.sh $Input
popd

#easysqlite build -- 10
pushd `pwd`
cd libs/op/libs/easysqlite/projects/android
./build_easysqlite_android.sh $Input
popd

#op-stack-cpp build -- 10
pushd `pwd`
cd libs/op/libs/op-stack-cpp/projects/android
./build_op-stack-cpp_android.sh $Input
popd

#op-core-cpp build - 11
pushd `pwd`
cd libs/op/libs/op-core-cpp/projects/android
./build_op-core-cpp_android.sh $Input
popd

echo "******************ALL STATIC LIBRARIES BUILT*******************"
pause

#openpeer-android-sdk shared build - 12
pushd `pwd`
cd openpeer-android-sdk
./build_openpeer_android_sdk_android.sh $Input
popd

echo "******************BUILD COMPLETED FOR ALL ANDROID SDK LIBS*******************"




#*******************************************************************************
#
#  Copyright (c) 2014 , Hookflash Inc.
#  All rights reserved.
#  
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions are met:
#  
#  1. Redistributions of source code must retain the above copyright notice, this
#  list of conditions and the following disclaimer.
#  2. Redistributions in binary form must reproduce the above copyright notice,
#  this list of conditions and the following disclaimer in the documentation
#  and/or other materials provided with the distribution.
#  
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
#  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
#  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
#  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
#  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
#  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
#  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
#  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
#  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#  
#  The views and conclusions contained in the software and documentation are those
#  of the authors and should not be interpreted as representing official policies,
#  either expressed or implied, of the FreeBSD Project.
#*******************************************************************************
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

if [[ $1 == *android-ndk-* ]]; then
	echo "----------------- NDK Path is : $1 ----------------"
	Input=$1;
else
	echo "Please enter your android ndk path:"
	echo "For example:/home/astro/android-ndk-r8e"
	read Input
	echo "You entered:$Input"
fi

#Set path
echo "----------------- Exporting the android-ndk path ----------------"
export PATH=$PATH:$Input:$Input/toolchains/arm-linux-androideabi-4.7/prebuilt/$HOST_OS-$ARCHTYPE/bin

#create install directories
mkdir -p build

#ortc-lib module build
echo "------------------- Building ortc-lib for ANDROID platform ---------------"
pushd `pwd`

#rm -rf ./obj

export ANDROIDNDK_PATH=$Input
export NDK_PROJECT_PATH=`pwd`
ndk-build NDK_DEBUG=1 APP_PLATFORM=android-9 HOST_OS=$HOST_OS V=1 NDK_LOG=1
popd

echo "-------- Done building ortc-lib -----"

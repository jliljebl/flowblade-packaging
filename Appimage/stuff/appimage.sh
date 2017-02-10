#!/bin/bash

########################################################################
# Package the binaries built on Travis-CI as an AppImage
# By Simon Peter 2016
# For more information, see http://appimage.org/
########################################################################

export ARCH=$(arch)

APP=MyPaint
LOWERAPP=${APP,,}

mkdir -p $HOME/$APP/$APP.AppDir/usr/

cd $HOME/$APP/

wget -q https://github.com/probonopd/AppImages/raw/master/functions.sh -O ./functions.sh
. ./functions.sh

cd $APP.AppDir

sudo chown -R $USER /app/

cp -r /app/* ./usr/

########################################################################
# Copy desktop and icon file to AppDir for AppRun to pick them up
########################################################################

get_apprun
get_desktop
get_icon


# Flowblade appimage

#APP=Flowblade
#LOWERAPP=flowblade

#export LOWERAPP

#mkdir -p ./$APP/$APP.AppDir/usr/bin
#cd ./$APP

# Get functions.sh helper script
#wget -q https://github.com/probonopd/AppImages/raw/master/functions.sh -O ./functions.sh
#. ./functions.sh

# Get appimage tools
#wget "https://github.com/probonopd/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage"
#chmod a+x appimagetool-x86_64.AppImage

# This takes time, comment out when testing
#get_apprun
#get_icon

# wget -c https://github.com/probonopd/AppImageKit/releases/download/5/AppRun -O ./AppRun # 64-bit
#wget -c https://github.com/probonopd/AppImageKit/releases/download/6/AppRun_6-x86_64 -O AppRun # 64-bit
#chmod a+x AppRun




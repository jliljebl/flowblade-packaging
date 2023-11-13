#!/bin/sh

ROOT_DIR= "" # e.g. /home/janne/codes/mlt-bindings
APP_DIR="" # e.g. /home/janne/codes/flowblade/flowblade/flowblade-trunk

SOURCE_DIR=$ROOT_DIR/mlt
BUILD_DIR=$ROOT_DIR/build
INSTALL_DIR=$ROOT_DIR/install

cd $ROOT_DIR

rm -rf $BUILD_DIR
rm -rf $INSTALL_DIR

cmake -DCMAKE_BUILD_TYPE=Debug -DSWIG_PYTHON=ON -DMOD_GLAXNIMATE_QT6=OFF -DMOD_GLAXNIMATE=OFF -DMOD_QT=OFF -DMOD_QT6=OFF -DMOD_MOVIT=OFF -S $SOURCE_DIR -B $BUILD_DIR

cmake --build $BUILD_DIR --config Release

mkdir $INSTALL_DIR

cmake --install $BUILD_DIR --prefix $INSTALL_DIR

cp $INSTALL_DIR/lib/python3.11/dist-packages/mlt7.py $APP_DIR/mlt7.py 
cp $INSTALL_DIR/lib/python3.11/dist-packages/_mlt7.so $APP_DIR/_mlt7.so


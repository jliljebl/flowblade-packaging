#!/bin/sh

# Set MLT environment variables to point
# where you have the binaries and libraries
# so MLT finds them runtime.
ROOT_DIR= # e.g. /home/janne/codes/mlt-bindings
LAUNCH_FILE= # e.g. /home/janne/codes/flowblade/flowblade/flowblade-trunk/flowblade

INSTALL_DIR=$ROOT_DIR/install
export PATH=$INSTALL_DIR/bin:$PATH

export MLT_REPOSITORY=$INSTALL_DIR/lib/mlt-7
export MLT_DATA=$INSTALL_DIR/share/mlt-7
export MLT_PROFILES_PATH=$INSTALL_DIR/share/mlt-7/profiles
export LD_LIBRARY_PATH=$INSTALL_DIR/lib:$LD_LIBRARY_PATH

# Launch repository Flowblade
$LAUNCH_FILE

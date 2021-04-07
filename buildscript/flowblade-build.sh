#!/bin/bash

################################################################################
# HELPER FUNCTIONS                                                             #
################################################################################
# die
# Function that prints a line and exists.
# $@ : arguments to be printed
function die {
    echo "ERROR: $@"
    exit -1
}

# fetch-git
# Downloads git repo if no folder.
# $1 : SOURCE_DIR
# $2 : SUBDIR
# $3 : REVISION
# $4 : REPOLOC
function fetch-git {
    if [ ! -d "$1/$2" ]
    then
        cd "$1"
        git --no-pager clone $4 || die "Unable to git clone source for $1 from $4"
        mkdir -p "$1/$2"
        cd "$2"
        git checkout $3 || die "Unable to git checkout $3"
        SUB_PROJECT_COMPILE_LIST="$2 $SUB_PROJECT_COMPILE_LIST"
    else
        echo "Source dir $2 exists, no git cloning."
    fi
}

# copy-local-git
# Downloads git repo if no folder.
# $1 : SOURCE_DIR
# $2 : SUBDIR
# $3 : REVISION
# $4 : COPY_SOURCE_DIR
function copy-local-git {
    if [ ! -d "$1/$2" ]
    then
        echo "Copying local dir $2."
        cd "$1"
        mkdir -p "$1/$2"
        cp -r "$4" "."
        cd "$2"
        git checkout $3 || die "Unable to git checkout $3"
        SUB_PROJECT_COMPILE_LIST="$2 $SUB_PROJECT_COMPILE_LIST"
    else
        echo "Source dir $2 exists, no local copy."
    fi
}

# fetch-tar
# Downloads tar file, unpacks it to temp dirtectory and moves data to another directory.
# $1 : SOURCE_DIR
# $2 : SUBDIR
# $3 : REPOLOC
# $4 : move source dir
function fetch-tar {
    if [ ! -d "$1/$2" ]
    then
        mkdir -p "$1/$2"
        cd "$1/$2"
        wget -O - "$3" | tar -xz
        cp -ar "$1/$2/$4"/* .
        rm -r "$1/$2/$4" # bad input here could wipe a lot stuff, take care when using this.
        SUB_PROJECT_COMPILE_LIST="$2 $SUB_PROJECT_COMPILE_LIST"
    fi
}

# setup-compile-for-subproject
# Sets subproject to be compiled if source changed and moves to source dir.
# $1 : subproject
function setup-compile-for-subproject {
    if [[ $SUB_PROJECT_COMPILE_LIST == *"$1"* ]]; then
       echo "Compiling subproject $1..."
       DO_COMPILE=1
       cd "$SOURCE_DIR/$1"
    else
       echo "Subproject $1 has not changed, not compiling."
       DO_COMPILE=0
    fi
}



################################################################################
# ARGS AND GLOBALS                                                             #
################################################################################

# Compiling data.
DO_COMPILE=0 # global flag that gets set when deciding wheather to compli subproject.
SUB_PROJECT_COMPILE_LIST="" # We only compile subproject subprojects, 

# Set directories.
INSTALL_DIR="$PWD/flowblade" # Change this to point to dir you wish to install the dev build, e.g. "/home/janne/codes/dev-build"
SOURCE_DIR="$INSTALL_DIR/src"
FINAL_INSTALL_DIR="$INSTALL_DIR/Flowblade"

# Create source dir if needed.
mkdir -p "$SOURCE_DIR"

# Subprojects
FREI0R="frei0r"
OPUS="opus"
X264="x264"
X265="x265"
LIBVPX="libvpx"
#DAV1D="dav1d"
NV_CODEC_HEADERS="nv-codec-headers"
SWH_PLUGINS="swh-plugins"
FFMPEG="FFmpeg"
MLT="mlt"
FLOWBLADE="flowblade"

# Figure out the number of cores in the system. Use as make flag to make 
# building faster with multiple CPU threads.
CPUS=$(grep "processor.*:" /proc/cpuinfo | wc -l)
# Sanity check
if test 0 = $CPUS ; then
 CPUS=1
fi
MAKEJ=$(( $CPUS ))



################################################################################
# FETCH SOURCE FOR SUBPROJECTS                                                 #
################################################################################

echo "Fetching sources for subprojects..."

# Frei0r
SUBDIR=$FREI0R
REVISION="master"
REPOLOC="git://github.com/dyne/frei0r.git"
fetch-git "$SOURCE_DIR" "$SUBDIR" "$REVISION" "$REPOLOC"

# libvpx
SUBDIR=$LIBVPX
REVISION="v1.9.0"
REPOLOC="https://chromium.googlesource.com/webm/libvpx.git"
fetch-git "$SOURCE_DIR" "$SUBDIR" "$REVISION" "$REPOLOC"
#--enable-libvpx

# opus
SUBDIR=$OPUS
REVISION="v1.3.1"
REPOLOC="https://github.com/xiph/opus.git"
fetch-git "$SOURCE_DIR" "$SUBDIR" "$REVISION" "$REPOLOC"
# --enable-libopus"

# x265
SUBDIR=$X265
REVISION="origin/stable"
REPOLOC="https://github.com/videolan/x265"
fetch-git "$SOURCE_DIR" "$SUBDIR" "$REVISION" "$REPOLOC"
#--enable-libx265"

# x264
SUBDIR=$X264
REVISION="origin/stable"
REPOLOC="git://github.com/mirror/x264.git"
fetch-git "$SOURCE_DIR" "$SUBDIR" "$REVISION" "$REPOLOC"
#--enable-libx264"

# dav1d
#SUBDIR=$DAV1D
#REVISION="master"
#REPOLOC="https://code.videolan.org/videolan/dav1d.git"
#fetch-git "$SOURCE_DIR" "$SUBDIR" "$REVISION" "$REPOLOC"
# --enable-libdav1d

# nv-codec-headers
SUBDIR=$NV_CODEC_HEADERS
REVISION="master"
REPOLOC="git://github.com/FFmpeg/nv-codec-headers.git"
fetch-git "$SOURCE_DIR" "$SUBDIR" "$REVISION" "$REPOLOC"
#  CONFIG[15]="sed -i s,/usr/local,$FINAL_INSTALL_DIR, Makefile"
  
# swh-plugins
SUBDIR=$SWH_PLUGINS
REPOLOC="http://ftp.us.debian.org/debian/pool/main/s/swh-plugins/swh-plugins_0.4.15+1.orig.tar.gz"
MV_DIR="swh-plugins-0.4.15+1"
fetch-tar "$SOURCE_DIR" "$SUBDIR" "$REPOLOC" "$MV_DIR"

# zimg, no for now
# bigsh0t, no for now
# aom, no for now

# FFmpeg
SUBDIR=$FFMPEG
REVISION="master"
REPOLOC="git://github.com/FFmpeg/FFmpeg.git"
fetch-git "$SOURCE_DIR" "$SUBDIR" "$REVISION" "$REPOLOC"

SUBDIR=$MLT
REVISION="master"
REPOLOC="git://github.com/mltframework/mlt.git"
fetch-git "$SOURCE_DIR" "$SUBDIR" "$REVISION" "$REPOLOC"

# Flowblade
SUBDIR=$FLOWBLADE
REVISION="master"
REPOLOC="git://github.com/jliljebl/flowblade.git"
fetch-git "$SOURCE_DIR" "$SUBDIR" "$REVISION" "$REPOLOC"


################################################################################
# COMPILE SUBPROJECTS                                                          #
################################################################################

echo "Compiling subprojects..."
echo "Using $MAKEJ CPUs for build."

export PATH="$FINAL_INSTALL_DIR/bin:$PATH"
export LD_RUN_PATH="$FINAL_INSTALL_DIR/lib"
export PKG_CONFIG_PATH="$FINAL_INSTALL_DIR/lib/pkgconfig:$PKG_CONFIG_PATH"

export CFLAGS=-DNDEBUG # works, but we may need to think more about setting these.
export CXXFLAGS=-DNDEBUG
export LDFLAGS=

# Frei0r
setup-compile-for-subproject $FREI0R
if [[ DO_COMPILE -eq 1 ]]; then
    export CFLAGS=`-O2`
    ./autogen.sh 
    ./configure --prefix=$FINAL_INSTALL_DIR
    make -j$MAKEJ
    make install
 
  #tsekkaa tämä, tarviiko laittaa että MLT löytää?
  #log Copying frei0r plugins
  #cmd mkdir PlugIns/frei0r-1 2>/dev/null
  #cmd cp -a "$FINAL_INSTALL_DIR"/lib/frei0r-1 PlugIns
  #for lib in PlugIns/frei0r-1/*; do
  #  fixlibs "$lib"
  #done
  
fi

# opus
setup-compile-for-subproject $OPUS
if [[ DO_COMPILE -eq 1 ]]; then
    ./autogen.sh
    ./configure --prefix=$FINAL_INSTALL_DIR
    make -j$MAKEJ 
    make install || die "Unable to install $OPUS"
fi

# x264
setup-compile-for-subproject $X264
if [[ DO_COMPILE -eq 1 ]]; then
    ./configure --prefix=$FINAL_INSTALL_DIR --disable-lavf --disable-ffms --disable-gpac --disable-swscale --enable-shared --disable-cli $CONFIGURE_DEBUG_FLAG
    make -j$MAKEJ 
    make install || die "Unable to install $X264"
fi
# --enable-libx264"

# x265
setup-compile-for-subproject $X265
if [[ DO_COMPILE -eq 1 ]]; then
    cd source # x265 source is not in subdir root.
    # CMAKE_DEBUG_FLAG="-DCMAKE_BUILD_TYPE=Debug # if doing debug then uncomment
    cmake -DCMAKE_INSTALL_PREFIX=$FINAL_INSTALL_DIR -DENABLE_CLI=OFF -DHIGH_BIT_DEPTH=ON $CMAKE_DEBUG_FLAG
    make -j$MAKEJ
    make install
fi
# --enable-libx265

# libvpx
setup-compile-for-subproject $LIBVPX
if [[ DO_COMPILE -eq 1 ]]; then
    ./configure --prefix=$FINAL_INSTALL_DIR --enable-vp8 --enable-postproc --enable-multithread --enable-runtime-cpu-detect --disable-install-docs --disable-debug-libs --disable-examples --disable-unit-tests --extra-cflags=-std=c99 --enable-shared $CONFIGURE_DEBUG_FLAG
    make -j$MAKEJ
    make install
fi
# --enable-libvpx
 
# dav1d
#setup-compile-for-subproject $DAV1D
#if [[ DO_COMPILE -eq 1 ]]; then
#    BUILDTYPE=" --buildtype=release"
#    # BUILDTYPE=" --buildtype=debug" # uncomment for ebug builds
#    meson setup builddir --prefix=$FINAL_INSTALL_DIR --libdir=$FINAL_INSTALL_DIR/lib
#    ninja -C builddir -j $MAKEJ || die "Unable to build $1"
#    meson install -C builddir || die "Unable to install $1"
#fi 
# --enable-libdav1d


# nv-codec-headers
setup-compile-for-subproject $NV_CODEC_HEADERS
if [[ DO_COMPILE -eq 1 ]]; then
    # These are not compiled, just manipulated to work when compiling ffmpeg
    sed -i s,/usr/local,$FINAL_INSTALL_DIR, Makefile
fi

# swh-plugins
setup-compile-for-subproject $SWH_PLUGINS
if [[ DO_COMPILE -eq 1 ]]; then
    ./configure --prefix=$FINAL_INSTALL_DIR --enable-sse
    make -j$MAKEJ
    make install
fi
  
# FFmpeg
setup-compile-for-subproject $FFMPEG
if [[ DO_COMPILE -eq 1 ]]; then
    ./configure --prefix=$FINAL_INSTALL_DIR --disable-static --disable-doc --enable-gpl --enable-version3 --enable-shared --enable-runtime-cpudetect $CONFIGURE_DEBUG_FLAG --enable-libtheora --enable-libvorbis --enable-libmp3lame --enable-nonfree --enable-libx264 --enable-libx265 --enable-libvpx --enable-libopus
    make -j$MAKEJ
    make install
fi

# mlt
setup-compile-for-subproject $MLT
if [[ DO_COMPILE -eq 1 ]]; then
    export CXXFLAGS="$CFLAGS -std=c++11"
    export CFLAGS="-I$FINAL_INSTALL_DIR/include $ASAN_CFLAGS $CFLAGS"
    export LDFLAGS="-L$FINAL_INSTALL_DIR/lib $LDFLAGS"
    
    ./configure --prefix=$FINAL_INSTALL_DIR --enable-gpl --enable-gpl3 --disable-xine --disable-vmfx --disable-swfdec --disable-videostab --disable-jackrack --disable-kdenlive --without-kde --disable-gtk2 --swig-languages=python
    make -j$MAKEJ
    make install
fi

# flowblade
setup-compile-for-subproject $FLOWBLADE
if [[ DO_COMPILE -eq 1 ]]; then
    cp -a flowblade-trunk "$FINAL_INSTALL_DIR" || die "Unable to install $1" # Copy app code
    cp -a ../mlt/src/swig/python/{_mlt.so,mlt.py} "$FINAL_INSTALL_DIR"/flowblade-trunk # Copy mlt bindings
fi

################################################################################
# CREATE START_UP SCRIPT                                                       #
################################################################################


TMPFILE=`mktemp -t build-flowblade.start.XXXXXXXXX`
 
cat > "$TMPFILE" <<End-of-startup-script-template

#!/bin/sh

# Setup the environment
export INSTALL_DIR=$FINAL_INSTALL_DIR 
export PATH="\$INSTALL_DIR/bin:$PATH"
export LD_LIBRARY_PATH="\$INSTALL_DIR/lib:\$LD_LIBRARY_PATH"
export MLT_REPOSITORY="\$INSTALL_DIR/lib/mlt"
export MLT_DATA="\$INSTALL_DIR/share/mlt"
export MLT_PROFILES_PATH=\$INSTALL_DIR/share/mlt/profiles
export MLT_MOVIT_PATH="\$INSTALL_DIR/share/movit"
export FREI0R_PATH="\$INSTALL_DIR/lib/frei0r-1"

python3 "\$INSTALL_DIR/flowblade-trunk/flowblade" "\$@"

End-of-startup-script-template

if test 0 != $? ; then
die "Unable to create environment script"
fi
chmod 755 $TMPFILE || die "Unable to make environment script executable"
cp $TMPFILE "$FINAL_INSTALL_DIR/start-flowblade" || die "Unable to create environment script - cp failed"


echo "Build finished."




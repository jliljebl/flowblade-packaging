## Tools for creating AppImage for Flowblade

### Steps

#### Setup
**git clone https://github.com/jliljebl/flowblade-packaging.git** Download this repo.

**cd <CLONE_DIR>/flowblade-packaging/AppImage** Go to AppImage folder.

**./buildcommands install-base** Installs base system dependncies.

**./buildcommands install-mltdeps** Installs dependenciens needed to build MLT.

**./tempenv.sh setup** Sets up union-fs tempenv needed to create AppImage.

#### Working in tempenv
**./tempenv.sh start** Starts up union-fs file system.

Now you need to navigete back to *<CLONE_DIR>/flowblade-packaging/AppImage* in the chroot file system you are in.

**./libsbuild build-all** Downloads and builds allMLT dependecies in chroot file system.

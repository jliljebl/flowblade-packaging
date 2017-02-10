"""
    Flowblade Movie Editor is a nonlinear video editor.
    Copyright 2014 Janne Liljeblad.

    This file is part of Flowblade Movie Editor <http://code.google.com/p/flowblade>.

    Flowblade Movie Editor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Flowblade Movie Editor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Flowblade Movie Editor. If not, see <http://www.gnu.org/licenses/>.
"""

import md5
import os
import subprocess
import sys

import appconsts
import respaths
import utils

_natron_found = False

def init():
    global _natron_found
    if utils.program_is_installed("Natron"):
        _natron_found = True
        print "Natron found"
    else:
        _natron_found = False
        print "Natron not found"

def natron_available():
    return _natron_found

def export_clip(clip):
    # Write export data file
    natron_dir = utils.get_hidden_user_dir_path() + appconsts.NATRON_DIR + "/"
    file_path = natron_dir + "clipexport_" + md5.new(str(os.urandom(32))).hexdigest()
    data_text = clip.path + " " + str(clip.clip_in) + " " + str(clip.clip_out + 1)
    
    export_data_file = open(file_path, "w")
    export_data_file.write(data_text)
    export_data_file.close()

    # Launch Natron
    print "Launch Natron..."
    args = [str(respaths.LAUNCH_DIR + "natron_clip_export_start.sh"), str(respaths.LAUNCH_DIR)]
    subprocess.Popen(args)

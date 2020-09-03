# FLATPAK

## INSTALLING APPLICATION FLATPAK

#### USING GUI

Use Gnome SOFTWARE can presents installing Flatpak in addition to installing distro repo version

#### INSTALLING WITH CLI FROM FLATHUB

Install flatpack tool:

    sudo apt-get install flatpak

Add Flathub remote

    flatpak remote-add --if-not-exists flathub https://flathub.org/repo/flathub.flatpakrepo 

Install application

      flatpak install flathub io.github.jliljebl.Flowblade
    
      flatpak install https://flathub.org/repo/appstream/io.github.jliljebl.Flowblade.flatpakref

## BUILDING LOCAL FLATPAK

    flatpak remote-add --if-not-exists flathub https://flathub.org/repo/flathub.flatpakrepo
    
    flatpak install flathub org.gnome.Platform//3.xx (system)
    
    flatpak install flathub org.gnome.Sdk//3.xx (system)
    
    git clone --recurse-submodules https://github.com/flathub/io.github.jliljebl.Flowblade.git 
    
    cd io.github.jliljebl.Flowblade 
    
    sudo flatpak-builder --ccache --install --force-clean build_dir io.github.jliljebl.Flowblade.yaml 
    
    flatpak run io.github.jliljebl.Flowblade

## UPDATE WORKFLOW

git fetch flathub

git checkout master

git merge flathub/master

...build and test local



## OTHER COMMANDS

flatpak list
flatpak uninstall io.github.jliljebl.Flowblade

## Flowblade on Flathub

https://github.com/flathub/io.github.jliljebl.Flowblade

## Web resources

http://docs.flatpak.org/en/latest/command-reference.html

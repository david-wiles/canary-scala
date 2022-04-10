# -*- mode: ruby -*-
# vi: set ft=ruby :

$install = <<-'SCRIPT'
sudo apt-get update
sudo apt-get -yqq install \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    apt-transport-https \
    curl \
    gnupg
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo -H gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
sudo chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
sudo apt-get update
sudo apt-get -y install \
    docker-ce \
    docker-ce-cli \
    containerd.io \
    sbt

sudo apt update
sudo apt install -y \
    nginx \
    qemu-kvm \
    libvirt-daemon-system \
    openjdk-11-jre-headless \

SCRIPT

Vagrant.configure("2") do |config|
  config.vm.box = "bento/ubuntu-20.04"
  config.vm.boot_timeout = 600 # My computer is slow
  config.vm.provision "shell", inline: $install
  config.vm.provider "virtualbox" do |vb|
    # Enable hardware virtualization on the vm
    vb.customize ["modifyvm", :id, "--nested-hw-virt", "on"]
  end
end

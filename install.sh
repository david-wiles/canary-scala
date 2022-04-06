#!/bin/bash

if ! [ -x "$(command -v java)" ]; then
  echo "Error: java is not installed. canary requires to run. You can install java globally with 'sudo apt install openjdk-11-jre-headless'. Exiting."
  exit 1
fi

if ! [ -x "$(command -v sbt)" ]; then
  echo "Error: sbt is not installed. sbt is required to build canary. Exiting."
  exit 1
fi

# Build jar
sbt assembly

# Make directories to store jar
mkdir -p "$1/canary/target/scala-2.13"
cp ./target/scala-2.13/canary.jar "$1/canary/target/scala-2.13/canary.jar"

# Create executable
cat <<EOF > ./canary
#!/bin/bash
java -jar $1/target/scala-2.13/canary.jar \$@
EOF
chmod 722 ./canary

# Set path in bashrc if user is using bash
if [[ ":$PATH:" === *":$1:" ]]; then
  if [ -n "$BASH_VERSION" ]; then
    echo "export PATH=\$PATH:$1" >> ~./bashrc
    echo "Your PATH variable has not yet been updated. You will need to run 'source ~/.bashrc' to run canary."
  else
    echo "Warning: the location you choose to install canary in is not in your PATH."
  fi
fi

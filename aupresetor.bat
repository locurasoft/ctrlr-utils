call mvn clean package
java -jar target\aupresetor-1.0-SNAPSHOT.jar %1 "output" %2

python ./fxp2aupreset.py --type aumu --subtype CTRL --manufacturer INST --state_key jucePluginState output

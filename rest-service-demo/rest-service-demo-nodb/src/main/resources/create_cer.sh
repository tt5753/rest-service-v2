
#!/bin/bash

echo "==== Removing old files ===="
find . -name "*.jks" -exec rm -rf {} \;

echo "==== Generating private keys ===="
keytool -genkey -noprompt -trustcacerts -keyalg RSA -alias "default" -dname "CN=localhost, OU=RestEasy, O=JBoss, L=Red Hat, ST=World, C=WW" -keypass "secret" -storepass "secret" -keystore "default_server_keystore.jks"
keytool -genkey -noprompt -trustcacerts -keyalg RSA -alias "sni" -dname "CN=localhost, OU=RestEasy, O=JBoss, L=Red Hat, ST=World, C=WW" -keypass "secret" -storepass "secret" -keystore "sni_server_keystore.jks"
keytool -genkey -noprompt -trustcacerts -keyalg RSA -alias "sni" -dname "CN=localhost, OU=RestEasy, O=JBoss, L=Red Hat, ST=World, C=WW" -keypass "secret" -storepass "secret" -keystore "no_trusted_clients_keystore.jks"

echo "==== Generating certificates ===="
keytool -export -keyalg RSA -alias "default" -storepass "secret" -file "default_client_cert.cer" -keystore "default_server_keystore.jks"
keytool -export -keyalg RSA -alias "sni" -storepass "secret" -file "sni_client_cert.cer" -keystore "sni_server_keystore.jks"

echo "==== Importing certificates ===="
keytool -import -noprompt -v -trustcacerts -keyalg RSA -alias "default" -file "default_client_cert.cer" -keypass "secret" -storepass "secret" -keystore "default_client_truststore.jks"
keytool -import -noprompt -v -trustcacerts -keyalg RSA -alias "sni" -file "sni_client_cert.cer" -keypass "secret" -storepass "secret" -keystore "sni_client_truststore.jks"

echo "==== Removing cert files (all you need is truststore.jks) ===="
rm "default_client_cert.cer"
rm "sni_client_cert.cer"
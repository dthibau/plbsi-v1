#!/bin/bash

if [ -z "$1" ]
  then
    echo "Pleaaase ! Indiquer un tag "
    exit 1
fi




# Bakup n-1
cp ../jboss-eap-6.4/standalone/deployments/plbsi.war ../plbsi.war.n-1

# Build with prod profile
ant clean deploy -Djboss.home=/home/jboss/jboss-eap-6.4 -Dserver=https://plbsi-prod.plb.fr:8443 -Dprofile=prod -Djasper_home=/home/jboss/jasperreports-6.9.0

git tag $1
git push origin $1



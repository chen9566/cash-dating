#!/usr/bin/env bash

mvn clean package -Dmaven.test.skip=true
sftp alisz248.server.huobanplus.com <<EOF
put web/target/web-1.0-SNAPSHOT.war /home/debuger/tomcat/webapps/cashtest.war
EOF


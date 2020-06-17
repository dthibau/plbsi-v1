pipeline {
   agent any
  stages {
    stage('Build') {
      steps {  echo 'Building..'   
          sh 'ant clean deploy -Djboss.home=/home/jboss/jboss-eap-6.4 -Dserver=https://plbsi-prod.plb.fr:8443 -Dprofile=int -Djasper_home=/home/jboss/jasperreports-6.9.0'
      }
   }
   stage('Deploy newsite') {
     when { branch 'newsite'}
     environment {
       CC = 'clang'
      plbsi_jdbc_url = 'jdbc:mysql://plb-rec.plb.fr:3306/plbconsult_dev'
     }
      steps {  
        echo 'Restarting Jboss with newSite environment..'
        sh 'sudo /etc/init.d/jboss restart'
        sleep time: 1, unit: 'MINUTES'
      }
   }
   stage('Deploy') {
     when { not { branch 'newsite'}}
     steps {
       echo 'Restarting Jboss ..'
       sh 'sudo /etc/init.d/jboss restart'
       sleep time: 1, unit: 'MINUTES'
     }
   }
   stage('Test') {
     steps {  
       echo 'Testing Navigation' 
       dir ('jmeter') {
         sh './apache-jmeter-5.2.1/bin/jmeter -JSERVER=plbsi-rec.plb.fr -JPORT=8443 -JSCHEME=https -n -t checkNavigation.jmx -l checkNavigation.jtl'  
       }
     }
     post {
       always {
         perfReport relativeFailedThresholdNegative: 1.2, relativeFailedThresholdPositive: 1.89, relativeUnstableThresholdNegative: 1.8, relativeUnstableThresholdPositive: 1.5, sourceDataFiles: './jmeter/checkNavigation.jtl'
       }
      }
    }
  }
}


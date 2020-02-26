pipeline {
   agent any
  stages {
    stage('Build') {
      steps {  echo 'Building..'   
          sh 'ant clean deploy -Djboss.home=/home/jboss/jboss-eap-6.4 -Dserver=http://plbsi-rec.plb.fr:8081 -Dprofile=int -Djasper_home=/home/jboss/jasperreports-6.9.0'
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
     steps {  echo 'Testing..' 
       sh 'ant -f jmeter/build.xml check'
     }
     post {
       always {
         perfReport 'jmeter/logs/checkNavigation.jtl'
       }
      }
    }
  }
}


pipeline {
   agent any
  stages {
    stage('Build') {
      steps {  echo 'Building..'   
          sh 'ant clean deploy -Djboss.home=/home/jboss/jboss-eap-6.4 -Dserver=http://plbsi-rec.plb.fr:8081 -Dprofile=int -Djasper_home=/home/jboss/jasperreports-5.5.0'
      }
   }
    stage('Deploy') {
      steps {echo 'Restarting Jboss ..'
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


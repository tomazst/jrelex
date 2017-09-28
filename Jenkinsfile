pipeline {
  agent any
  stages {
    stage('git pull') {
      steps {
        git(url: 'https://github.com/tomazst/jrelex.git', branch: 'development', credentialsId: 'b7b14afb-cec0-4f13-aeae-edc972135f82')
      }
    }
  }
}
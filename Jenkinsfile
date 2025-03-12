pipeline {
    agent none
    
    stages {
        stage('Checkout') {
            agent { docker { image 'alpine/git:latest' reuseNode true }
            }
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            agent {
                docker { image 'maven:3.9.5-eclipse-temurin-17-alpine' reuseNode true }
            }
            steps {
                sh '''
                    mvn clean test
                    mvn jacoco:report
                '''
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }
        
        stage('Build') {
            agent {
                docker { image 'docker:dind' reuseNode true }
            }
            steps {
                sh '''
                    docker build -t myapp:${BUILD_NUMBER} .
                    docker tag myapp:${BUILD_NUMBER} myregistry.example.com/myapp:${BUILD_NUMBER}
                    docker push myregistry.example.com/myapp:${BUILD_NUMBER}
                '''
            }
        }
        
        stage('Update Manifests') {
            agent {
                docker {
                    image 'alpine/git:latest' reuseNode true }
            }
            steps {
                sh '''
                    git clone https://github.com/your-org/gitops-repo.git
                    cd gitops-repo
                    sed -i "s|image: myregistry.example.com/myapp:.*|image: myregistry.example.com/myapp:${BUILD_NUMBER}|" kubernetes/deployment.yaml
                    git config --global user.email "jenkins@example.com"
                    git config --global user.name "Jenkins"
                    git add kubernetes/deployment.yaml
                    git commit -m "Update image to ${BUILD_NUMBER}"
                    git push
                '''
            }
        }
    }
}

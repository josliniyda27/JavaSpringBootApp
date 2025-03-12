pipeline {
    agent none

    environment {
        GITHUB_PAT = credentials('GITHUB_PAT')  // Securely fetch GitHub PAT
        GITHUB_USERNAME = 'josliniyda27'
    }

    stages {
        stage('Checkout') {
            agent { 
                docker { image 'alpine:latest' }
                }
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            agent { 
                docker { image 'maven:3.9.5-eclipse-temurin-17-alpine' } 
            }
            steps {
                   sh 'mvn clean test'       
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Build') {
            agent { 
                docker { image 'docker:dind' } 
            }
            steps {
                withCredentials([string(credentialsId: 'GITHUB_PAT', variable: 'GITHUB_TOKEN')]) {
                    sh """
                    echo \$GITHUB_TOKEN | docker login ghcr.io -u ${GITHUB_USERNAME} --password-stdin
                    """
                }
                    sh docker build -t ghcr.io/${GITHUB_USERNAME}/myapp:${BUILD_NUMBER}
                    sh docker push ghcr.io/${GITHUB_USERNAME}/myapp:${BUILD_NUMBER}
                
            }
        }
        
        stage('Update Manifests') {
            agent { 
                docker { image 'alpine/git:latest' } 
            }
            steps {
                sh '''
                    git clone https://${GITHUB_USERNAME}:${GITHUB_PAT}@github.com/josliniyda27/JavaSpringBootApp.git
                    cd JavaSpringBootApp
                    
                    sed -i "s|image: ghcr.io/${GITHUB_USERNAME}/myapp:.*|image: ghcr.io/${GITHUB_USERNAME}/myapp:${BUILD_NUMBER}|" kubernetes/deployment.yaml
                    
                    git config --global user.email "test123@gmail.com"
                    git config --global user.name "Jenkins"
                    git add kubernetes/deployment.yaml
                    git commit -m "Update image to ${BUILD_NUMBER}"
                    git push https://${GITHUB_USERNAME}:${GITHUB_PAT}@github.com/josliniyda27/JavaSpringBootApp.git
                '''
            }
        }
    }
}

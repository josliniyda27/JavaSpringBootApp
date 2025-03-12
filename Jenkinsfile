pipeline {
    agent any

    environment {
        GITHUB_PAT = credentials('GITHUB_PAT')  // Securely fetch GitHub PAT
        GITHUB_USERNAME = 'josliniyda27'
    }

    stages {
        stage('Checkout') {
            agent { docker { image 'alpine/git:latest' reuseNode true } }
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            agent { docker { image 'maven:3.9.5-eclipse-temurin-17-alpine' reuseNode true } }
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
                docker { 
                    image 'docker:dind' 
                    args '--privileged'   // Fix for Docker-in-Docker
                    reuseNode true 
                }
            }
            steps {
                sh '''
                    echo "${GITHUB_PAT}" | docker login ghcr.io -u "${GITHUB_USERNAME}" --password-stdin
        
                    docker build -t ghcr.io/${GITHUB_USERNAME}/myapp:${BUILD_NUMBER} .
                    docker push ghcr.io/${GITHUB_USERNAME}/myapp:${BUILD_NUMBER}
                '''
            }
        }
        
        stage('Update Manifests') {
            agent { docker { image 'alpine/git:latest' reuseNode true } }
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

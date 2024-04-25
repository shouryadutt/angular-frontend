def call(String imageName, String awsAccountId, String awsRegion) {
    pipeline {
        agent any
      
        stages {
            stage('Authenticate with AWS ECR') {
                steps {
                    script {
                        withCredentials([[
                            $class: 'AmazonWebServicesCredentialsBinding',
                            accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                            credentialsId: 'K8s',
                            secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                        ]]) {
                            bat 'aws ecr get-login-password --region eu-north-1 | docker login --username AWS --password-stdin 810678507647.dkr.ecr.eu-north-1.amazonaws.com'
                        }
                    }
                }
            }
            stage('Build Docker Image') {
                steps {
                    script {
                        docker.build('angular-build')
                    }
                }
            }
            stage('Push Docker Image to AWS ECR') {
                steps {
                    script {
                        docker.withRegistry('https://810678507647.dkr.ecr.eu-north-1.amazonaws.com/angular-build:latest') {
                            docker.image('angular-build').push('latest')
                        }
                    }
                }
            }
            // stage('Pull and Run docker image') {
            //     steps {
            //         script {
            //             bat 'docker pull 891376912626.dkr.ecr.us-east-1.amazonaws.com/angular-app:latest'
            //             bat 'docker run -d -p 4200:4200 891376912626.dkr.ecr.us-east-1.amazonaws.com/angular-app:latest'
            //             bat 'docker ps'
            //             bat 'docker ps -a'
            //         }
            //     }
            // }
        }
    }
}

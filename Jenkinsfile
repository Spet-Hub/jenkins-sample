pipeline {
    agent any

    options {
        timestamps()
    }

    parameters {
        choice(
            name: 'DEPLOY_ENV',
            choices: ['none', 'dev', 'qa', 'prod'],
            description: 'Environment to deploy after building the image'
        )
        string(
            name: 'IMAGE_REPOSITORY',
            defaultValue: 'your-dockerhub-user/jenkins-sample-java',
            description: 'Container image repository'
        )
        string(
            name: 'IMAGE_TAG',
            defaultValue: '',
            description: 'Optional image tag override. Defaults to BUILD_NUMBER.'
        )
        string(
            name: 'BRANCH_NAME_OVERRIDE',
            defaultValue: '',
            description: 'Optional branch name override for deployment metadata'
        )
        booleanParam(
            name: 'PUSH_IMAGE',
            defaultValue: false,
            description: 'Push the built image to the configured repository'
        )
    }

    environment {
        APP_NAME = 'jenkins-sample-java'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Resolve Build Metadata') {
            steps {
                script {
                    env.RESOLVED_IMAGE_TAG = params.IMAGE_TAG?.trim() ? params.IMAGE_TAG.trim() : env.BUILD_NUMBER
                    env.RESOLVED_BRANCH = params.BRANCH_NAME_OVERRIDE?.trim() ? params.BRANCH_NAME_OVERRIDE.trim() : (env.BRANCH_NAME ?: 'local')
                    env.FULL_IMAGE = "${params.IMAGE_REPOSITORY}:${env.RESOLVED_IMAGE_TAG}"
                }
                echo "Using image ${env.FULL_IMAGE}"
                echo "Using branch metadata ${env.RESOLVED_BRANCH}"
            }
        }

        stage('Compile') {
            steps {
                sh '''
                    set -eu
                    rm -rf out
                    mkdir -p out
                    javac -d out $(find src/main/java -name "*.java")
                    cp -R src/main/resources/. out/
                '''
            }
        }

        stage('Smoke Test') {
            steps {
                sh '''
                    set -eu
                    java -cp out com.example.jenkinssample.App &
                    APP_PID=$!
                    trap 'kill $APP_PID' EXIT
                    sleep 3
                    curl -fsS http://127.0.0.1:18888/ | grep -q "Hello from Java"
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t "$FULL_IMAGE" .'
            }
        }

        stage('Push Docker Image') {
            when {
                expression { params.PUSH_IMAGE }
            }
            steps {
                sh 'docker push "$FULL_IMAGE"'
            }
        }

        stage('Deploy To Kubernetes') {
            when {
                expression { params.DEPLOY_ENV != 'none' }
            }
            steps {
                sh '''
                    set -eu
                    chmod +x k8s-deploy.sh
                    ./k8s-deploy.sh "$DEPLOY_ENV" "$FULL_IMAGE" "$RESOLVED_BRANCH"
                '''
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully for ${env.FULL_IMAGE}"
        }
        cleanup {
            sh 'rm -rf out || true'
        }
    }
}

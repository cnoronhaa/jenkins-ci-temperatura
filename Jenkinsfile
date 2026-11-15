pipeline {
    agent any

    options {
        skipDefaultCheckout()
        disableConcurrentBuilds()
    }

    parameters {
        booleanParam(name: 'COBERTURA', defaultValue: false, description: 'Medir a cobertura de linhas com JaCoCo')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                bat 'git rev-parse HEAD'
            }
        }

        stage('Compilação') {
            steps {
                bat 'mvnw.cmd -B clean compile'
            }
        }

        stage('Testes') {
            steps {
                script {
                    String perfil = params.COBERTURA ? '-Pcobertura ' : ''
                    // O Surefire grava os resultados. O Jenkins classifica as falhas dos testes pelo XML.
                    bat "mvnw.cmd -B ${perfil}-Dmaven.test.failure.ignore=true test"
                }
                junit testResults: 'target/surefire-reports/TEST-*.xml'
            }
        }

        stage('Cobertura') {
            when {
                expression { params.COBERTURA && currentBuild.currentResult == 'SUCCESS' }
            }
            steps {
                bat 'mvnw.cmd -B -Pcobertura jacoco:report'
                recordCoverage tools: [[parser: 'JACOCO', pattern: 'target/site/jacoco/jacoco.xml']], failOnError: true
            }
        }
    }
}

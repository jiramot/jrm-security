#!/usr/bin/env groovy

def gitRepo = 'ssh://git@192.168.1.245:2222/uchoose2/api/security-module.git'

def handleCheckout() {
    if (env.gitlabMergeRequestId) {
        checkout([
                $class           : 'GitSCM',
                branches         : [[name: "${env.gitlabSourceNamespace}/${env.gitlabSourceBranch}"]],
                extensions       : [
                        [$class: 'PruneStaleBranch'],
                        [$class: 'CleanCheckout'],
                        [
                                $class : 'PreBuildMerge',
                                options: [
                                        fastForwardMode: 'NO_FF',
                                        mergeRemote    : env.gitlabTargetNamespace,
                                        mergeTarget    : env.gitlabTargetBranch
                                ]
                        ]
                ],
                userRemoteConfigs: [
                        [
                                name: env.gitlabTargetNamespace,
                                url : env.gitlabTargetRepoSshURL
                        ],
                        [
                                name: env.gitlabSourceNamespace,
                                url : env.gitlabSourceRepoSshURL
                        ]
                ]
        ])
    } else {
        checkout([
                $class           : 'GitSCM',
                branches         : scm.branches,
                extensions       : [
                        [$class: 'PruneStaleBranch'],
                        [$class: 'CleanCheckout']
                ],
                userRemoteConfigs: scm.userRemoteConfigs
        ])
    }
}

updateGitlabCommitStatus state: 'pending'

pipeline {
    agent { label 'api' }

    options {
        ansiColor('xterm')
        gitLabConnection('gitlab245')
        gitlabCommitStatus(name: 'jenkins')
        timeout(time: 10, unit: 'MINUTES')
        timestamps()
    }

    environment {
        GIT_SHA_SHORT = sh(returnStdout: true, script: 'git rev-parse --short=8 ${GIT_COMMIT}').trim()
    }

    stages {
        stage('checkout') {
            when {
                expression { env.gitlabActionType != "MERGE" }
            }
            steps {
                git branch: BRANCH_NAME, url: gitRepo
            }
        }
        stage('unit test') {
            when {
                expression { env.gitlabActionType != "MERGE" }
            }
            steps {
                sh '''./gradlew clean check jacocoTestReport'''
            }
            post {
                success {
                    updateGitlabCommitStatus name: 'tests', state: 'success'
                    publishHTML([allowMissing         : false,
                                 alwaysLinkToLastBuild: false,
                                 keepAll              : false,
                                 reportDir            : 'build/reports/tests/test',
                                 reportFiles          : 'index.html',
                                 reportName           : 'Unit Test Report',
                                 reportTitles         : 'Unit Test Report'])
                    publishHTML([allowMissing         : false,
                                 alwaysLinkToLastBuild: false,
                                 keepAll              : false,
                                 reportDir            : 'build/jacocoHtml',
                                 reportFiles          : 'index.html',
                                 reportName           : 'Coverate Test Report',
                                 reportTitles         : 'Coverate Test Report'])
                }
                failure {
                    updateGitlabCommitStatus name: 'tests', state: 'failed'
                }
            }
        }

        stage('merge request test') {
            when {
                expression { env.gitlabActionType == "MERGE" }
            }
            steps {
                handleCheckout()
                sh '''./gradlew clean build'''
            }
            post {
                success {
                    echo 'merge success'
                    updateGitlabCommitStatus name: 'mergeRequest', state: 'success'
                }
                failure {
                    echo 'merge fail'
                    updateGitlabCommitStatus name: 'mergeRequest', state: 'failed'
                }
            }
        }

        stage('owasp dependencies check') {
            when {
                expression { env.gitlabActionType != "MERGE" }
            }
            steps {
                sh '''./gradlew dependencyCheckAnalyze || true'''
            }
            post {
                success {
                    publishHTML([allowMissing         : true,
                                 alwaysLinkToLastBuild: false,
                                 keepAll              : false,
                                 reportDir            : 'build/reports',
                                 reportFiles          : 'dependency-check-report.html',
                                 reportName           : 'OWASP Dependencies Report',
                                 reportTitles         : 'OWASP Dependencies Report'])
                }
            }
        }

        stage('sonar') {
            when {
                expression { env.gitlabActionType != "MERGE" && env.BRANCH_NAME != "master" }
            }
            steps {
                sh '''./gradlew sonarqube'''
            }

        }

        stage('build') {
            when {
                expression { env.gitlabActionType != "MERGE" }
            }
            steps {
                sh '''./gradlew build'''
            }
            post {
                success {
                    updateGitlabCommitStatus name: 'build', state: 'success'
                }
                failure {
                    updateGitlabCommitStatus name: 'build', state: 'failed'
                }
            }
        }

        stage('archive') {
            when {
                expression {
                    env.gitlabActionType != "MERGE" && (env.BRANCH_NAME == "master" || env.BRANCH_NAME == "develop")
                }
            }
            steps {
                withCredentials([[$class          : 'UsernamePasswordMultiBinding', credentialsId: 'nexus',
                                  usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD']]) {
                    script {
                        sh './gradlew publish'
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Always'
        }

        success {
            echo 'success'
            updateGitlabCommitStatus state: 'success'
        }

        failure {
            echo 'fail'
            updateGitlabCommitStatus state: 'failed'
        }
    }
}

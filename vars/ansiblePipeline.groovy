def call(Map config = [:]) {

    pipeline {
        agent any

        stages {

            stage('Load Configuration') {
                steps {
                    script {

                        def props = readProperties(
                            text: libraryResource('ansible-config.properties')
                        )

                        env.SLACK_CHANNEL_NAME  = props['SLACK_CHANNEL_NAME']
                        env.ENVIRONMENT         = props['ENVIRONMENT']
                        env.CODE_BASE_PATH      = props['CODE_BASE_PATH']
                        env.ACTION_MESSAGE      = props['ACTION_MESSAGE']
                        env.KEEP_APPROVAL_STAGE = props['KEEP_APPROVAL_STAGE']

                        env.GIT_REPO  = props['GIT_REPO']
                        env.GIT_BRANCH = props['GIT_BRANCH']

                        env.PLAYBOOK = props['PLAYBOOK']
                        env.INVENTORY = props['INVENTORY']

                        env.EMAIL_TO = props['EMAIL_TO']
                    }
                }
            }

            stage('Clone Repository') {
                steps {

                    git branch: "${env.GIT_BRANCH}",
                        url: "${env.GIT_REPO}"

                    echo "Repository Cloned Successfully"
                }
            }

            stage('User Approval') {

                when {
                    expression {
                        return env.KEEP_APPROVAL_STAGE == "true"
                    }
                }

                steps {

                    input message: "Approve Deployment to ${env.ENVIRONMENT} ?",
                          ok: "Deploy"

                    echo "Deployment Approved"
                }
            }

            stage('Playbook Execution') {
                steps {

                    sh """
                    ansible-playbook \
                    -i ${env.INVENTORY} \
                    ${env.PLAYBOOK}
                    """

                    echo "Playbook Executed Successfully"
                }
            }
        }

        post {

            success {

                slackSend(
                    channel: "#${env.SLACK_CHANNEL_NAME}",
                    message: "SUCCESS: ${env.ACTION_MESSAGE}"
                )

                mail to: "${env.EMAIL_TO}",
                     subject: "Jenkins Build Success",
                     body: "Deployment completed successfully."
            }

            failure {

                slackSend(
                    channel: "#${env.SLACK_CHANNEL_NAME}",
                    message: "FAILED: ${env.ACTION_MESSAGE}"
                )

                mail to: "${env.EMAIL_TO}",
                     subject: "Jenkins Build Failed",
                     body: "Deployment failed."
            }
        }
    }
}

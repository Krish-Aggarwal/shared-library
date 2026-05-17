def call(String branch = "main") {

    withCredentials([usernamePassword(
        credentialsId: 'github-credentials',
        usernameVariable: 'GIT_USERNAME',
        passwordVariable: 'GIT_PASSWORD'
    )]) {

        sh """
            git config user.email "krishagg004@gmail.com"
            git config user.name "Krish Aggarwal"

            git add .
            git commit -m "Automated build changes" || true

            git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/your-org/your-repo.git HEAD:${branch}
        """
    }
}
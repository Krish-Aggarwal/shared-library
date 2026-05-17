def call() {
    sh '''
        echo "Running GitLeaks Scan..."
        gitleaks detect --source . --verbose
    '''
}
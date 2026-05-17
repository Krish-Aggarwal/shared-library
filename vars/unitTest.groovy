def call() {
    sh '''
        echo "Running Unit Tests..."
        mvn test
    '''
}
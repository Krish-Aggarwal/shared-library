def call() {
    sh '''
        echo "Building Application..."
        mvn package
    '''
}
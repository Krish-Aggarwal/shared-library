def call() {
    sh '''
        echo "Compiling Application..."
        mvn clean compile
    '''
}
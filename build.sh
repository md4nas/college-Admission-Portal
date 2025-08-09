#!/bin/bash

# Build script for Render deployment
echo "🚀 Starting build process..."

# Make Maven wrapper executable
chmod +x ./mvnw

# Clean and build the application
echo "📦 Building application..."
./mvnw clean package -DskipTests -Dmaven.javadoc.skip=true

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build completed successfully!"
    echo "📁 JAR file location: target/UserManagemetPortal-0.0.1-SNAPSHOT.jar"
else
    echo "❌ Build failed!"
    exit 1
fi
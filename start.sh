#!/bin/bash

# Start script for Render deployment
echo "🚀 Starting College Admission Portal..."

# Set production profile
export SPRING_PROFILES_ACTIVE=prod

# Start the application with optimized JVM settings for Render's free tier
java -Xmx400m -Xms200m \
     -Dserver.port=${PORT:-8080} \
     -Dspring.profiles.active=prod \
     -jar target/UserManagemetPortal-0.0.1-SNAPSHOT.jar
# ⚡ Quick Deploy to Render - 5 Minutes Setup

## 🎯 **Fastest Way to Deploy**

### 1️⃣ **Push to GitHub** (1 minute)
```bash
git add .
git commit -m "Deploy to Render"
git push origin main
```

### 2️⃣ **Create Database** (2 minutes)
1. Go to [render.com](https://render.com) → Login
2. **New +** → **PostgreSQL**
3. **Name**: `college-portal-db`
4. **Plan**: Free
5. **Create Database**
6. **Copy External Database URL**

### 3️⃣ **Deploy App** (2 minutes)
1. **New +** → **Web Service**
2. **Connect GitHub** → Select your repo
3. **Settings**:
   - **Name**: `college-admission-portal`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -Dspring.profiles.active=prod -Dserver.port=$PORT -jar target/UserManagemetPortal-0.0.1-SNAPSHOT.jar`

4. **Environment Variables**:
```env
DATABASE_URL=your_postgres_url_from_step_2
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_db_password
SPRING_PROFILES_ACTIVE=prod
ADMIN_EMAIL=admin@test.com
ADMIN_PASSWORD=Admin@123
ADMIN_NAME=Administrator
MAIL_USERNAME=your_email@gmail.com
BREVO_SMTP_PASSWORD=your_smtp_password
```

5. **Create Web Service**

## 🎉 **Done!**
Your app will be live at: `https://your-app-name.onrender.com`

## ⚠️ **Important Notes**
- **First deployment**: Takes 5-10 minutes
- **Free tier**: App sleeps after 15 minutes inactivity
- **Email setup**: Required for user registration
- **Admin login**: Use credentials from environment variables

## 🔧 **Quick Fixes**
- **Build fails**: Check Java 17 in pom.xml
- **Database error**: Verify DATABASE_URL format
- **Email not working**: Set up Brevo SMTP
- **App not starting**: Check logs in Render dashboard
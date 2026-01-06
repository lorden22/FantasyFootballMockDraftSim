# Fantasy Football Mock Draft Sim

A comprehensive fantasy football mock draft simulation application with secure HTTPS-only access.

## 🚀 Quick Start

### Prerequisites
- Docker (20.10+)
- Docker Compose (2.0+)

### Deploy in 3 Steps

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd FantasyFootballMockDraftSim
   ```

2. **Run the deployment script**
   ```bash
   ./deploy.sh
   ```

3. **Access your application**
   - Frontend: https://localhost
   - API: https://localhost/api/
   - Health: https://localhost/health

## 🏗️ Architecture

This application is deployed as a **single container** with all components:

- **Frontend**: HTML/CSS/JavaScript served by nginx
- **Backend**: Spring Boot REST API with JPA/Hibernate
- **Database**: MariaDB with persistent storage
- **Security**: HTTPS-only with SSL/TLS encryption
- **Proxy**: nginx reverse proxy with load balancing

## 🔒 Security Features

- ✅ **HTTPS-only access** - HTTP automatically redirects to HTTPS
- ✅ **SSL/TLS encryption** - All traffic encrypted
- ✅ **Security headers** - XSS protection, content type validation
- ✅ **CORS protection** - Configured for secure origins
- ✅ **Database security** - Isolated database with secure credentials

## 📁 Project Structure

```
FantasyFootballMockDraftSim/
├── WebApp/
│   ├── Backend/                 # Spring Boot application
│   │   ├── src/main/java/      # Java source code
│   │   ├── src/main/resources/ # Configuration files
│   │   ├── Dockerfile          # Backend container config
│   │   └── startup.sh          # Database initialization
│   └── webpages/               # Frontend files
│       ├── *.html              # HTML pages
│       ├── *.js                # JavaScript files
│       ├── *.ts                # TypeScript files
│       └── styles.css          # CSS styles
├── shared/                     # Shared Java classes
├── Dockerfile                  # Main container configuration
├── docker-compose.yml          # Multi-service orchestration
├── nginx.conf                  # Web server configuration
├── deploy.sh                   # Deployment automation script
└── DEPLOYMENT_GUIDE.md         # Comprehensive deployment guide
```

## 🛠️ Development

### Local Development
```bash
# Start development environment
./deploy.sh

# View logs
./deploy.sh logs

# Stop application
./deploy.sh stop

# Restart application
./deploy.sh restart
```

### Building from Source
```bash
# Build only
./deploy.sh build

# Check health
./deploy.sh health
```

## 🌐 Production Deployment

### Cloud Platforms Supported
- **AWS EC2** - Ubuntu instances with Docker
- **Google Cloud Platform** - Compute Engine VMs
- **DigitalOcean** - Droplets with Docker
- **Azure** - Virtual Machines
- **Kubernetes** - Container orchestration

### Deployment Options
1. **Docker Host** - Direct deployment on Linux server
2. **Cloud Platforms** - Managed cloud services
3. **Container Orchestration** - Kubernetes, Docker Swarm

See [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) for detailed instructions.

## 📊 Monitoring

### Health Checks
- Application health: `https://yourdomain.com/health`
- Container status: `docker-compose ps`
- Logs: `docker-compose logs -f`

### Performance Monitoring
- Resource usage: `docker stats`
- Database performance: MariaDB slow query log
- Application metrics: Spring Boot Actuator

## 🔧 Configuration

### Environment Variables
Copy `env.production` to `.env` and modify:
```bash
cp env.production .env
# Edit .env with your production values
```

### SSL Certificates
- **Development**: Self-signed certificates (auto-generated)
- **Production**: Let's Encrypt or commercial certificates

### Database Configuration
- **Development**: In-memory MariaDB
- **Production**: Persistent MariaDB with backups

## 🚀 Features

### Frontend
- ✅ Responsive design
- ✅ User authentication
- ✅ Draft simulation
- ✅ Team management
- ✅ Draft history
- ✅ Real-time updates

### Backend
- ✅ RESTful API
- ✅ JWT authentication
- ✅ Database persistence
- ✅ CORS support
- ✅ Health monitoring
- ✅ Security headers

### Database
- ✅ Player data management
- ✅ Draft tracking
- ✅ User accounts
- ✅ Team rosters
- ✅ Historical data

## 📈 Scaling

### Horizontal Scaling
```yaml
# docker-compose.yml
services:
  fantasy-football-app:
    deploy:
      replicas: 3
```

### Load Balancing
- nginx reverse proxy
- Round-robin load balancing
- Health check integration

## 🔍 Troubleshooting

### Common Issues
1. **Port conflicts** - Check if ports 80, 443, 8443 are available
2. **SSL certificate warnings** - Normal for self-signed certificates
3. **Database connection issues** - Check container logs
4. **Memory issues** - Increase container memory limits

### Debug Commands
```bash
# View logs
docker-compose logs -f

# Access container
docker-compose exec fantasy-football-app bash

# Check health
curl -k https://localhost/health

# Database backup
docker exec fantasy-football-mock-draft mysqldump -u root -ppassword db > backup.sql
```

## 📞 Support

For issues and questions:
1. Check the [troubleshooting section](#troubleshooting)
2. Review [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)
3. Check application logs
4. Verify system requirements

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

---

**Ready to deploy?** Run `./deploy.sh` to get started!



A full-stack internal web application that streamlines the trailer quoting process. 
Sales staff can build quotes in the browser, select customers and assembly items synced 
directly from QuickBooks Enterprise, and push estimates back to QuickBooks automatically. 
Built with Java/Spring Boot, React, PostgreSQL, and deployed on AWS EC2.

Features:
- JWT-authenticated user accounts with email verification
- Real-time dashboard with quote status tracking
- QuickBooks Enterprise sync via SOAP/Web Connector (customers, assemblies, estimates)
- One-click quote to QuickBooks estimate conversion
- PDF quote generation and download
- Deployed on AWS EC2 with Nginx, Let's Encrypt SSL, and systemd

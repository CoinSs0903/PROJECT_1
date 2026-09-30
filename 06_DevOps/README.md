# 📁 06_DevOps (Tự Động Hóa CI/CD, Hạ Tầng & Triển Khai)

## 📌 1. Mục Đích & Vai Trò
Thư mục này chịu trách nhiệm tự động hóa quy trình xây dựng, kiểm tra, đóng gói, bảo mật và triển khai mã nguồn từ `04_Dev` lên các môi trường máy chủ (Development, Staging/UAT, Production) một cách an toàn và liên tục.
- **Trách nhiệm chính (Owner):** DevOps Engineer, Cloud Architect, Site Reliability Engineer (SRE).
- **Phối hợp cùng:** Tech Lead, Developers, QA/QC.

---

## 📂 2. Cấu Trúc Thư Mục Con
```text
06_DevOps/
├── ci_cd/                      # Kịch bản pipeline CI/CD tự động (GitHub Actions, GitLab CI, Jenkinsfile)
├── docker/                     # Cấu hình containerization (Dockerfile, docker-compose.yml)
├── k8s/                        # Cấu hình điều phối container (K8s Deployments, Services, Helm Charts)
├── environments/               # Quản lý cấu hình các môi trường (.env.example, config maps)
│   ├── dev/                    # Cấu hình môi trường Phát triển
│   ├── staging/                # Cấu hình môi trường Kiểm thử tiền sản phẩm (UAT)
│   └── prod/                   # Cấu hình môi trường Sản phẩm chính thức (Production)
├── monitoring/                 # Cấu hình giám sát hệ thống (Prometheus, Grafana, ELK/Loki stack)
└── README.md                   # Hướng dẫn quy trình triển khai và vận hành hệ thống
```

---

## 📋 3. Danh Mục Các Đầu Việc Cụ Thể Của DevOps
1. **Thiết lập Pipeline Tự động hóa CI (Continuous Integration):**
   - Tự động kích hoạt khi có Pull Request vào nhánh `develop` hoặc `main`.
   - Các bước trong CI:
     1. `Lint & Formatting`: Kiểm tra chuẩn code style.
     2. `Compile & Build`: Biên dịch mã nguồn.
     3. `Automated Tests`: Chạy toàn bộ Unit Tests & Integration Tests.
     4. `Static Code Analysis & Security Scan`: Quét lỗ hổng và code smells qua SonarQube / Trivy / Snyk.
     5. `Build Docker Image`: Đóng gói thành Docker Container Image và gắn thẻ phiên bản (Tagging).
     6. `Push Registry`: Đẩy Image lên kho lưu trữ an toàn (Docker Hub, AWS ECR, Harbor).
2. **Thiết lập Quy trình Triển khai CD (Continuous Delivery / Deployment):**
   - **Triển khai tự động lên môi trường Staging:** Ngay sau khi CI vượt qua trên nhánh `develop`.
   - **Triển khai an toàn lên Production:** Kích hoạt có phê duyệt (Manual Approval Gate) từ PM / Tech Lead khi release tag được tạo trên nhánh `main`.
   - Áp dụng các chiến lược triển khai không thời gian chết (**Zero-Downtime Deployment**): Blue-Green Deployment, Rolling Update hoặc Canary Release.
3. **Quản trị Môi trường & Hạ tầng (Infrastructure as Code - IaC):**
   - Quản lý máy chủ và tài nguyên đám mây (AWS, GCP, Azure) bằng Terraform hoặc Ansible.
   - Quản lý an toàn các biến môi trường và khóa bí mật (Secrets Management với HashiCorp Vault, AWS Secrets Manager, GitHub Secrets).
4. **Giám sát & Cảnh báo (Monitoring & Alerting):**
   - Thu thập Metrics tài nguyên (CPU, RAM, Disk I/O, Network) bằng Prometheus & Node Exporter.
   - Trực quan hóa bảng theo dõi hiệu năng hệ thống trên Grafana Dashboards.
   - Tích hợp Bot cảnh báo tự động gửi thông báo đến Telegram / Slack khi có sự cố hệ thống hoặc tỉ lệ lỗi HTTP 5xx tăng cao.

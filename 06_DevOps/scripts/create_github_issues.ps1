<#
.SYNOPSIS
    Tự động khởi tạo Labels, Milestones và 11 Issues trên GitHub repository từ tài liệu SRS.

.DESCRIPTION
    Script sử dụng GitHub REST API v3 để tạo tự động toàn bộ backlog cho dự án.

.PARAMETER Repo
    Tên repository trên GitHub theo định dạng: "owner/repo" (Ví dụ: "giabao/PROJECT_1")

.PARAMETER Token
    GitHub Personal Access Token (classic hoặc fine-grained với quyền 'repo')

.EXAMPLE
    .\create_github_issues.ps1 -Repo "myaccount/PROJECT_1" -Token "ghp_xxxxxxxxxxxx"
#>

param (
    [Parameter(Mandatory=$true)]
    [string]$Repo,

    [Parameter(Mandatory=$true)]
    [string]$Token
)

$headers = @{
    "Authorization" = "Bearer $Token"
    "Accept"        = "application/vnd.github+json"
    "User-Agent"    = "PowerShell-GitHub-Issue-Creator"
}

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "🚀 BẮT ĐẦU CẤU HÌNH GITHUB BACKLOG CHO REPO: $Repo" -ForegroundColor Yellow
Write-Host "=====================================================" -ForegroundColor Cyan

# 1. TẠO LABELS
$labels = @(
    @{ name = "module:01-user-role"; color = "0e8a16"; description = "Phân hệ 1: Quản lý người dùng & phân quyền" },
    @{ name = "module:02-core-ops"; color = "1d76db"; description = "Phân hệ 2: Quản lý nghiệp vụ cốt lõi" },
    @{ name = "module:03-finance"; color = "fbca04"; description = "Phân hệ 3: Thanh toán & tài chính" },
    @{ name = "module:04-incident"; color = "d93f0b"; description = "Phân hệ 4: Xử lý sự cố & phát sinh" },
    @{ name = "module:05-reporting"; color = "5319e7"; description = "Phân hệ 5: Báo cáo & thống kê" },
    @{ name = "priority:high"; color = "b60205"; description = "Độ ưu tiên cao" },
    @{ name = "priority:medium"; color = "e99695"; description = "Độ ưu tiên trung bình" },
    @{ name = "priority:low"; color = "c5def5"; description = "Độ ưu tiên thấp" }
)

Write-Host "`n📌 Đang tạo Labels..." -ForegroundColor Green
foreach ($lbl in $labels) {
    $body = @{
        name        = $lbl.name
        color       = $lbl.color
        description = $lbl.description
    } | ConvertTo-Json

    try {
        $res = Invoke-RestMethod -Uri "https://api.github.com/repos/$Repo/labels" -Method Post -Headers $headers -Body $body -ContentType "application/json" -ErrorAction Stop
        Write-Host "  ✅ Đã tạo Label: $($lbl.name)" -ForegroundColor Gray
    } catch {
        Write-Host "  ℹ️ Label đã tồn tại hoặc bỏ qua: $($lbl.name)" -ForegroundColor DarkGray
    }
}

# 2. TẠO MILESTONES
$milestones = @(
    @{ title = "Sprint 1 - Foundation & Core Setup"; description = "Thiết lập nền tảng, kiến trúc và phân hệ Người dùng" },
    @{ title = "Sprint 2 - Core Operations"; description = "Phát triển phân hệ nghiệp vụ cốt lõi (FR-CORE-01)" },
    @{ title = "Sprint 3 - Finance & Payments"; description = "Phát triển phân hệ thanh toán, hóa đơn và đối soát" },
    @{ title = "Sprint 4 - Incident & Support"; description = "Phát triển phân hệ khiếu nại và xử lý sự cố" },
    @{ title = "Sprint 5 - Analytics & Release"; description = "Phát triển Dashboard báo cáo, nghiệm thu và phát hành" }
)

Write-Host "`n📌 Đang tạo Milestones..." -ForegroundColor Green
$milestoneMap = @{}
foreach ($ms in $milestones) {
    $body = @{
        title       = $ms.title
        description = $ms.description
    } | ConvertTo-Json

    try {
        $res = Invoke-RestMethod -Uri "https://api.github.com/repos/$Repo/milestones" -Method Post -Headers $headers -Body $body -ContentType "application/json" -ErrorAction Stop
        $milestoneMap[$ms.title] = $res.number
        Write-Host "  ✅ Đã tạo Milestone: $($ms.title)" -ForegroundColor Gray
    } catch {
        Write-Host "  ℹ️ Milestone đã tồn tại hoặc bỏ qua: $($ms.title)" -ForegroundColor DarkGray
    }
}

# 3. TẠO 11 CHỨC NĂNG DƯỚI DẠNG GITHUB ISSUES
$issues = @(
    @{
        title = "[FR-01.1] Quản lý thông tin tài khoản & phân quyền người dùng (RBAC)"
        milestone = "Sprint 1 - Foundation & Core Setup"
        labels = @("module:01-user-role", "priority:high", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-01.1`
- **Phân hệ:** Phân hệ 1 - Quản lý danh mục & Thông tin người dùng
- **Nhánh Git:** `feature/FR-01_user-role-management`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Quản trị viên (Admin)**, tôi muốn tạo tài khoản, gán quyền truy cập (Admin, Manager, Staff, Customer) và quản lý trạng thái tài khoản (ACTIVE, PENDING, BLOCKED) để bảo mật hệ thống và phân định rõ vai trò làm việc.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Mật khẩu được băm an toàn bằng Bcrypt/Argon2 theo NFR-02.
- [ ] Cơ chế xác thực JWT và phân quyền Middleware Role-Based Access Control (RBAC).
- [ ] Cho phép Admin khóa/mở khóa tài khoản ngay lập tức.
- [ ] Unit Test kiểm tra phân quyền đạt độ bao phủ > 80%.
"@
    },
    @{
        title = "[FR-01.2] Quản lý danh mục dùng chung hệ thống"
        milestone = "Sprint 1 - Foundation & Core Setup"
        labels = @("module:01-user-role", "priority:medium", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-01.2`
- **Phân hệ:** Phân hệ 1 - Quản lý danh mục & Thông tin người dùng
- **Nhánh Git:** `feature/FR-01_user-role-management`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Quản trị viên**, tôi muốn thêm, sửa, xóa, tìm kiếm danh mục dịch vụ, bảng giá và trạng thái dùng chung để các phân hệ nghiệp vụ có thể tham chiếu dữ liệu đồng nhất.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Đầy đủ API CRUD cho danh mục dịch vụ.
- [ ] Áp dụng cơ chế Soft-delete (xóa mềm).
- [ ] Có bộ nhớ đệm (Cache Redis) cho các danh mục ít thay đổi để tối ưu hiệu năng theo NFR-01.
"@
    },
    @{
        title = "[FR-02.1] Tiếp nhận và xử lý hồ sơ / đơn hàng dịch vụ"
        milestone = "Sprint 2 - Core Operations"
        labels = @("module:02-core-ops", "priority:high", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-02.1`
- **Phân hệ:** Phân hệ 2 - Quản lý nghiệp vụ cốt lõi
- **Nhánh Git:** `feature/FR-02_core-business-operations`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Nhân viên nghiệp vụ / Khách hàng**, tôi muốn tạo mới và theo dõi tiến trình xử lý hồ sơ giao dịch để các bước công việc được diễn ra liền mạch.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Tiếp nhận đầy đủ thông tin: họ tên, CCCD (12 số), số điện thoại hợp lệ (theo Từ điển dữ liệu SRS).
- [ ] Lưu vết trạng thái hồ sơ: RECEIVED, PROCESSING, VERIFIED, COMPLETED.
- [ ] Gửi thông báo cập nhật tiến độ qua Email / Hệ thống.
"@
    },
    @{
        title = "[FR-02.2] Kiểm tra điều kiện hợp lệ & tính khả thi hồ sơ"
        milestone = "Sprint 2 - Core Operations"
        labels = @("module:02-core-ops", "priority:high", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-02.2`
- **Phân hệ:** Phân hệ 2 - Quản lý nghiệp vụ cốt lõi
- **Nhánh Git:** `feature/FR-02_core-business-operations`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Hệ thống / Chuyên viên thẩm định**, tôi muốn hệ thống tự động kiểm tra giấy tờ, tình trạng nợ quá hạn và tính hợp lệ trước khi cho phép lập phiếu để giảm thiểu rủi ro vận hành.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Kiểm tra số CCCD đã tồn tại và không bị đánh dấu vi phạm.
- [ ] Từ chối tiếp nhận nếu đối tượng đang có công nợ quá hạn hoặc tài khoản bị khóa.
- [ ] Trả về thông báo lỗi rõ ràng nếu không đáp ứng điều kiện.
"@
    },
    @{
        title = "[FR-02.3] Lập và phê duyệt phiếu nghiệp vụ / hợp đồng (FR-CORE-01)"
        milestone = "Sprint 2 - Core Operations"
        labels = @("module:02-core-ops", "priority:high", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-02.3` / `FR-CORE-01`
- **Phân hệ:** Phân hệ 2 - Quản lý nghiệp vụ cốt lõi
- **Nhánh Git:** `feature/FR-02_core-business-operations`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Nhân viên nghiệp vụ / Lễ tân**, tôi muốn sinh mã phiếu tự động định dạng `GD-YYYYMMDD-XXXX` và chuyển cấp Quản lý phê duyệt để hoàn tất thủ tục giao dịch chính thức.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Sinh mã phiếu tự động duy nhất theo mẫu: `GD-YYYYMMDD-XXXX`.
- [ ] Lưu trạng thái phiếu vào CSDL và xuất bản in PDF phiếu xác nhận.
- [ ] Quản lý có quyền Duyệt (Approve) hoặc Từ chối kèm lý do (Reject with reasons).
"@
    },
    @{
        title = "[FR-03.1] Lập hóa đơn, phiếu thu & phiếu chi tự động"
        milestone = "Sprint 3 - Finance & Payments"
        labels = @("module:03-finance", "priority:high", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-03.1`
- **Phân hệ:** Phân hệ 3 - Quản lý thanh toán & tài chính
- **Nhánh Git:** `feature/FR-03_payment-finance`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Nhân viên thu ngân / Kế toán**, tôi muốn lập phiếu thu/chi và xuất hóa đơn điện tử cho từng phiếu giao dịch với tính toán thuế chính xác.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Tự động tính tiền hàng, thuế VAT và các khoản giảm giá.
- [ ] Đảm bảo tính toàn vẹn dữ liệu tài chính (ACID transaction).
- [ ] Hỗ trợ xuất mẫu hóa đơn chuẩn PDF để gửi khách hàng.
"@
    },
    @{
        title = "[FR-03.2] Tích hợp cổng thanh toán trực tuyến & Đối soát giao dịch"
        milestone = "Sprint 3 - Finance & Payments"
        labels = @("module:03-finance", "priority:medium", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-03.2`
- **Phân hệ:** Phân hệ 3 - Quản lý thanh toán & tài chính
- **Nhánh Git:** `feature/FR-03_payment-finance`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Khách hàng & Kế toán**, tôi muốn thanh toán qua cổng VNPAY/Momo/Chuyển khoản QR và đối soát tự động hàng ngày để đảm bảo minh bạch dòng tiền.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Tích hợp Webhook nhận kết quả thanh toán tức thời từ Cổng thanh toán.
- [ ] Kiểm tra chữ ký số (HMAC SHA512) để chống giả mạo giao dịch.
- [ ] Module tự động đối soát chênh lệch cuối ngày và gửi cảnh báo nếu lệch sổ.
"@
    },
    @{
        title = "[FR-04.1] Tiếp nhận phản ánh, khiếu nại & lập biên bản sự cố"
        milestone = "Sprint 4 - Incident & Support"
        labels = @("module:04-incident", "priority:medium", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-04.1`
- **Phân hệ:** Phân hệ 4 - Xử lý sự cố & Phát sinh
- **Nhánh Git:** `feature/FR-04_incident-handling`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Khách hàng / Nhân viên CSKH**, tôi muốn gửi yêu cầu hỗ trợ và lập biên bản ghi nhận khi có sự cố phát sinh trong quá trình sử dụng dịch vụ.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Cung cấp form gửi khiếu nại kèm ảnh bằng chứng sự cố.
- [ ] Sinh mã sự cố `SC-YYYYMMDD-XXXX` và phân loại mức độ nghiêm trọng (Blocker/High/Normal/Low).
- [ ] Đặt SLA phản hồi tự động theo quy định của công ty.
"@
    },
    @{
        title = "[FR-04.2] Quy trình thẩm định bồi thường, gia hạn hoặc hủy giao dịch"
        milestone = "Sprint 4 - Incident & Support"
        labels = @("module:04-incident", "priority:medium", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-04.2`
- **Phân hệ:** Phân hệ 4 - Xử lý sự cố & Phát sinh
- **Nhánh Git:** `feature/FR-04_incident-handling`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Quản lý phê duyệt**, tôi muốn xem xét biên bản sự cố để duyệt phương án bồi thường, gia hạn thời gian thực hiện hoặc hoàn tiền/hủy giao dịch.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Luồng phê duyệt đa cấp tùy theo giá trị bồi thường.
- [ ] Tự động đồng bộ sang Phân hệ 3 (Tài chính) nếu có quyết định hoàn tiền (Refund).
- [ ] Ghi lại đầy đủ lịch sử Audit Log người duyệt và thời điểm duyệt.
"@
    },
    @{
        title = "[FR-05.1] Báo cáo doanh thu & số lượng nghiệp vụ định kỳ"
        milestone = "Sprint 5 - Analytics & Release"
        labels = @("module:05-reporting", "priority:medium", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-05.1`
- **Phân hệ:** Phân hệ 5 - Báo cáo & Thống kê
- **Nhánh Git:** `feature/FR-05_reporting-analytics`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Trưởng phòng nghiệp vụ**, tôi muốn xem và xuất báo cáo số lượng giao dịch, doanh số theo ngày, tuần, tháng, quý dưới định dạng Excel và PDF.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Bộ lọc thời gian linh hoạt (Từ ngày -> Đến ngày), lọc theo nhân viên, loại dịch vụ.
- [ ] Thời gian xử lý truy vấn báo cáo < 1.5 giây theo NFR-01.
- [ ] Xuất dữ liệu ra file Excel (.xlsx) chuẩn biểu mẫu kế toán.
"@
    },
    @{
        title = "[FR-05.2] Dashboard tổng hợp số liệu trực quan cho Ban Lãnh đạo"
        milestone = "Sprint 5 - Analytics & Release"
        labels = @("module:05-reporting", "priority:high", "enhancement")
        body = @"
## 📌 THÔNG TIN CHỨC NĂNG
- **Mã FR:** `FR-05.2`
- **Phân hệ:** Phân hệ 5 - Báo cáo & Thống kê
- **Nhánh Git:** `feature/FR-05_reporting-analytics`

## 📝 MÔ TẢ YÊU CẦU (USER STORY)
Là **Ban Giám đốc**, tôi muốn có màn hình Dashboard hiển thị biểu đồ trực quan về doanh thu thời gian thực, tỷ lệ tăng trưởng và chỉ số KPI vận hành.

## ✅ TIÊU CHÍ CHẤP NHẬN (ACCEPTANCE CRITERIA)
- [ ] Biểu đồ đường (Line chart) doanh thu theo chu kỳ.
- [ ] Biểu đồ tròn (Pie chart) tỷ trọng các loại dịch vụ.
- [ ] Tỷ lệ xử lý sự cố thành công và đánh giá mức độ hài lòng khách hàng.
"@
    }
)

Write-Host "`n📌 Đang tạo 11 Issues từ tài liệu SRS..." -ForegroundColor Green
foreach ($issue in $issues) {
    $msNum = $null
    if ($milestoneMap.ContainsKey($issue.milestone)) {
        $msNum = $milestoneMap[$issue.milestone]
    }

    $payload = @{
        title  = $issue.title
        body   = $issue.body
        labels = $issue.labels
    }
    if ($msNum) {
        $payload["milestone"] = $msNum
    }

    $jsonBody = $payload | ConvertTo-Json -Depth 5
    try {
        $res = Invoke-RestMethod -Uri "https://api.github.com/repos/$Repo/issues" -Method Post -Headers $headers -Body ([System.Text.Encoding]::UTF8.GetBytes($jsonBody)) -ContentType "application/json; charset=utf-8" -ErrorAction Stop
        Write-Host "  ✅ Đã tạo Issue #$($res.number): $($issue.title)" -ForegroundColor Cyan
    } catch {
        Write-Host "  ❌ Lỗi khi tạo issue: $($issue.title) - $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n🎉 HOÀN TẤT KHỞI TẠO BACKLOG LÊN GITHUB!" -ForegroundColor Green
Write-Host "👉 Hãy truy cập: https://github.com/$Repo/issues để xem kết quả." -ForegroundColor Yellow

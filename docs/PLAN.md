# PLAN – Kế hoạch giao việc cho AI theo từng ngày

Dự án: Hệ thống đấu giá trực tuyến (Java 17, Spring Boot, Thymeleaf + Bootstrap, MySQL qua XAMPP, Flyway).
SRS: docs/SRS.md. Mỗi "Ngày" là một phiên làm việc với AI. Đầu mỗi phiên AI đọc PROJECT_CONTEXT.md, hai mục cuối docs/PROGRESS_LOG.md và dòng "Đọc trước" của ngày hôm nay.

## Quy tắc vàng

1. Chỉ làm việc của ngày hiện tại. Không sửa module khác, trừ thêm hàm vào facade và ghi vào PROGRESS_LOG.
2. Module chỉ gọi module khác qua facade. Chỉ AuctionStateMachine đổi trạng thái phiên. Chỉ WalletFacade đổi số dư.
3. Từ Ngày 5, không sửa file migration đã commit; thay đổi bảng là file V mới và cập nhật docs/ERD.md.
4. Không sửa test để cho qua. Gặp điểm SRS chưa rõ thì ghi docs/DECISIONS.md và hỏi người dùng.
5. Chỗ làm tạm ghi vào "Nợ kỹ thuật" của PROGRESS_LOG kèm ngày sẽ xử lý.
6. Cuối phiên cập nhật PROGRESS_LOG, bảng trạng thái module, BUSINESS_RULES, MODULE_INTERFACES.

## Module và phụ thuộc

| Module | Trách nhiệm | Mã SRS chính | Phụ thuộc | Ngày |
| --- | --- | --- | --- | --- |
| common | Giờ server, tiền, lỗi, entity gốc, layout nền | NFR-02, NFR-08 | — | 5 |
| user | Tài khoản, đăng nhập, phân quyền, hồ sơ cá nhân, khóa tài khoản | FR-GU-03, FR-US-01/02, FR-AD-04 | common | 6, 25 |
| wallet | Ví, giao dịch bất biến, phong tỏa, hoàn, chuyển, đối soát | FR-US-05/06/07, NFR-05, BR-10 | common, user (qua sự kiện) | 7 |
| system | Cấu hình, ngày nghỉ lễ, lịch ngày làm việc | FR-AD-07, BR-03/09/28 | common | 8 |
| audit | Nhật ký chỉ ghi thêm | BR-32, NFR-06, FR-AD-09 | common | 8, 25 |
| notification | Thông báo trong ứng dụng | FR-US-08, BR-33 | common | 9 |
| seller | Hồ sơ Seller và duyệt | FR-SE-01, FR-AD-01 | user, notification | 10 |
| auction | Tài sản, phiên, quy chế, máy trạng thái, bộ lập lịch vòng đời, thay đổi niêm yết, hủy | FR-SE-02..05, FR-AD-02, FR-GU-01/02, BR-01/02/03/11/34 | seller, system, audit, notification | 11, 12, 14, 15 |
| registration | Đăng ký tham gia, đặt trước, mã ẩn danh, xét duyệt hồ sơ | FR-BI-01/02/03, FR-AD-03, BR-04..BR-10 | auction, wallet, system | 13, 14, 15 |
| bidding | Trả giá, chấp nhận giá, rút lại, truất quyền, hết giờ | FR-BI-04..07, BR-15..BR-17, BR-19..BR-24 | auction, registration, audit | 16, 17, 18 |
| settlement | Công bố người trúng, thanh toán, người kế tiếp, biên bản | FR-BI-08, BR-18, BR-25..BR-27 | bidding, wallet, auction | 18, 19, 20 |
| realtime | WebSocket, đồng bộ giờ, phòng đấu giá | NFR-01, NFR-02, BR-31 | tất cả module phát sự kiện | 21, 22 |
| payout | Hoàn tiền, chuyển cọc, phí, chuyển Seller, nhận tài sản | UC-06, BR-12..14, BR-28..30 | settlement, wallet, registration | 23 |
| complaint | Khiếu nại | UC-08, BR-35 | auction, notification | 24 |
| admin | Màn hình Admin tổng hợp, thống kê, điều hành phiên | FR-AD-04/05/06/08/09 | đọc qua facade của các module | 23, 24, 25 |

## Kế hoạch từng ngày

## Giai đoạn 0 – Chuẩn bị

### Ngày 0 – Cài đặt môi trường (bạn tự làm, không cần AI)

Module: môi trường | Quy mô: Vừa | Phụ thuộc: —

**Đọc trước:**

- Mục 2 của tài liệu này

**Việc cần làm:**

- Cài JDK 17, Git, XAMPP (chỉ MySQL), các extension Antigravity (Mục 2.2).
- Tạo hai database auction_db và auction_test cùng user auction (Mục 2.3).
- Tạo project bằng Spring Initializr (Mục 2.4), đặt cấu hình (Mục 2.5), chạy thử <http://localhost:8080> và kiểm tra kết nối DB.
- git init, tạo .gitignore, commit đầu tiên.
- Chuyển SRS sang Markdown và đặt vào docs/SRS.md (có thể nhờ AI làm ở Ngày 1, hoặc dùng pandoc SRS.docx -t gfm -o docs/SRS.md). Đặt bản PLAN.md đi kèm tài liệu này vào docs/PLAN.md.
- Tạo PROJECT_CONTEXT.md từ mẫu ở Phụ lục A và docs/PROGRESS_LOG.md từ mẫu ở Phụ lục B.
- Thử quy trình phiên một lần với việc rất nhỏ (dán prompt mở đầu ở Phụ lục C) để chắc AI đọc được các file.

**Tham chiếu SRS:** NFR-07

**Đầu ra:**

- project Spring Boot chạy được
- hai database
- repo Git có commit đầu
- PROJECT_CONTEXT.md, docs/PROGRESS_LOG.md

**Hoàn thành khi:**

- mvnw spring-boot:run khởi động không lỗi và mở được <http://localhost:8080>.
- Kết nối được hai database bằng extension Database Client.
- Commit đầu tiên đã có PROJECT_CONTEXT.md và docs/SRS.md.

## Giai đoạn A – Thiết kế toàn hệ thống (chưa viết code nghiệp vụ)

> Mục tiêu: có đủ ERD, sơ đồ trạng thái, sequence, use case, kiến trúc và bản đồ API trước khi code. Trong giai đoạn này được phép xóa DB và sửa V1__init.sql, nhưng phải sửa ERD.md cùng lúc. Cuối giai đoạn: bạn đọc và duyệt toàn bộ docs/, rồi git tag phase-a.

### Ngày 1 – Tài liệu nền và ERD

Module: docs, db | Quy mô: Nặng | Phụ thuộc: Ngày 0

**Đọc trước:**

- docs/SRS.md (đặc biệt Mục 5, 7, 8)

**Việc cần làm:**

- Nếu chưa có thì chuyển SRS sang docs/SRS.md, giữ nguyên mã BR, FR, UC, NFR. Không tự sửa nội dung SRS.
- Soạn docs/ERD.md: sơ đồ Mermaid erDiagram cho 14 thực thể ở Mục 7 (User, SellerProfile, Asset, Auction, Registration, Deposit, Bid, Settlement, Wallet, Transaction, Notification, AuditLog, Complaint, SystemConfig).
- Bổ sung các bảng SRS chưa liệt kê nhưng nghiệp vụ cần, và ghi thành một danh sách riêng “Bảng ngoài SRS” để bạn duyệt. Gợi ý: holiday (ngày nghỉ lễ), auction_follow (FR-US-04), asset_image, auction_minutes (biên bản điện tử), listing_change_request (UC-07), ví hệ thống cho ký quỹ và phí (BR-10, BR-29), cột hạn hoàn tiền và hạn chuyển cọc (BR-12, BR-13).
- Ghi quy ước ở đầu ERD.md: tên bảng snake_case số nhiều (users, không dùng user vì trùng từ khóa); tiền là BIGINT đơn vị VND; thời gian DATETIME(3) theo một múi giờ duy nhất Asia/Ho_Chi_Minh; trạng thái là VARCHAR lưu tên enum tiếng Anh.
- Phản ánh ràng buộc SRS bằng khóa và CHECK: một User một Wallet; Registration duy nhất theo (auction_id, bidder_id); mã ẩn danh duy nhất trong mỗi phiên; số dư không âm; Bid, Transaction, AuditLog chỉ ghi thêm.
- Viết src/main/resources/db/migration/V1__init.sql khớp 100% với ERD.md.
- Chạy ứng dụng để Flyway áp dụng V1 và ddl-auto=validate không báo lỗi.
- Tạo docs/DECISIONS.md và ghi mọi giả định AI đã tự đặt.

**Tham chiếu SRS:** Mục 7 (thực thể), 5.3, NFR-05, NFR-06, BR-10, BR-31

**Đầu ra:**

- docs/SRS.md
- docs/ERD.md
- src/main/resources/db/migration/V1__init.sql
- docs/DECISIONS.md

**Hoàn thành khi:**

- Flyway chạy sạch trên database trống.
- Mọi thực thể ở Mục 7 đều có bảng; danh sách bảng ngoài SRS đã được bạn duyệt.
- ERD.md và V1__init.sql khớp nhau từng cột.

### Ngày 2 – Sơ đồ trạng thái và ma trận quy tắc nghiệp vụ

Module: docs | Quy mô: Vừa | Phụ thuộc: Ngày 1

**Đọc trước:**

- docs/SRS.md Mục 4 và 5
- docs/ERD.md

**Việc cần làm:**

- Soạn docs/STATE_MACHINES.md với stateDiagram-v2 (Mermaid) cho: Auction (9 trạng thái, Mục 5.1 và 5.2), Deposit (Mục 5.3), Settlement (chờ, đã trả, hủy, quá hạn), Registration, SellerProfile, Complaint (Mới, Đang xử lý, Đã xử lý, Từ chối).
- Mỗi sơ đồ kèm một bảng: tên enum tiếng Anh, nhãn tiếng Việt, sự kiện chuyển, ai kích hoạt (người dùng, Admin hay bộ lập lịch), mã BR liên quan. Bảng này là nguồn để đặt tên enum trong code.
- Liệt kê các chỗ SRS chưa định nghĩa trạng thái hoặc chuyển trạng thái (ví dụ “tạm dừng phiên” ở FR-AD-06 không có trong Mục 5.1) và ghi vào DECISIONS.md. Không tự quyết.
- Soạn docs/BUSINESS_RULES.md: bảng 35 dòng BR-01 đến BR-35, gồm tóm tắt một dòng, module phụ trách, ngày dự kiến theo PLAN, trạng thái (Chưa, Đang, Xong), file code, file test. Hai cột cuối để trống, điền dần.
- Soạn bảng các việc do bộ lập lịch (scheduler) chạy: đóng đăng ký, mở phiên, giảm giá, hết giờ, hạn thanh toán, hoàn tiền sau 3 ngày làm việc, chuyển cọc.

**Tham chiếu SRS:** Mục 5, BR-01 đến BR-35

**Đầu ra:**

- docs/STATE_MACHINES.md
- docs/BUSINESS_RULES.md

**Hoàn thành khi:**

- Mọi dòng ở bảng chuyển trạng thái Mục 5.2 đều có trong sơ đồ Auction.
- Cả 35 BR đã gán module.
- Các điểm SRS chưa rõ đã nằm trong DECISIONS.md để bạn quyết.

### Ngày 3 – Sequence diagram: đăng ký, trả giá, chấp nhận giá, thanh toán

Module: docs | Quy mô: Vừa | Phụ thuộc: Ngày 2

**Đọc trước:**

- docs/SRS.md Mục 6 (UC-01 đến UC-04)
- docs/STATE_MACHINES.md
- docs/ERD.md

**Việc cần làm:**

- Soạn docs/SEQUENCES.md với sequenceDiagram (Mermaid) cho UC-01, UC-02, UC-03, UC-04, gồm luồng chính và các nhánh quan trọng: hết hạn thanh toán, người trúng hủy, người liền kề theo BR-18, giảm dần tiếp tục theo BR-22.
- Mỗi sơ đồ ghi rõ: ai gọi ai (Controller, Service của module nào, Repository), bước nào nằm trong cùng một giao dịch DB, chỗ khóa dòng (NFR-03), chỗ ghi Transaction của ví, chỗ ghi AuditLog, sự kiện nào đẩy qua WebSocket.
- Chốt “một giao dịch DB gồm những bước nào” cho bốn việc: trả giá, chấp nhận giá, công bố người trúng, thanh toán.
- Viết điều kiện BR-18 thành hai ví dụ số (một ví dụ hợp lệ, một ví dụ không hợp lệ) để làm dữ liệu test về sau.

**Tham chiếu SRS:** UC-01 đến UC-04, BR-15 đến BR-27, NFR-03

**Đầu ra:**

- docs/SEQUENCES.md (phần 1)

**Hoàn thành khi:**

- Bốn use case có sơ đồ cho cả luồng chính và luồng thay thế.
- Mỗi sơ đồ chỉ ra ranh giới giao dịch DB.

### Ngày 4 – Sequence còn lại, use case, kiến trúc module và bản đồ API

Module: docs | Quy mô: Vừa | Phụ thuộc: Ngày 3

**Đọc trước:**

- docs/SRS.md Mục 3 và Mục 6 (UC-05 đến UC-08)
- docs/SEQUENCES.md
- Mục 4 của tài liệu này (danh sách module)

**Việc cần làm:**

- Bổ sung vào SEQUENCES.md các sơ đồ cho UC-05, UC-06, UC-07, UC-08.
- Soạn docs/USE_CASES.md: sơ đồ use case cho Guest, User, Bidder, Seller, Admin (Mermaid không có use case chuẩn nên dùng flowchart; nếu giảng viên đòi UML chuẩn thì vẽ lại bằng draw.io hoặc PlantUML khi nộp bài). Sơ đồ này để nộp và để người đọc, không phải nguồn chuẩn cho AI.
- Soạn docs/ARCHITECTURE.md: sơ đồ package của các module ở Mục 4, mũi tên phụ thuộc một chiều, quy tắc “chỉ gọi qua facade”, các topic WebSocket.
- Soạn docs/API_MAP.md: bảng toàn bộ trang và endpoint (URL, phương thức, vai trò được phép, module, mã FR hoặc UC) và danh sách topic WebSocket với dạng thông điệp.
- Soạn docs/MODULE_INTERFACES.md ở dạng khung: mỗi module một mục, ghi dự kiến các hàm facade (tên, tham số, kết quả, ngoại lệ). Sẽ điền chính thức khi code từng module.
- Cập nhật PROJECT_CONTEXT.md: trỏ tới các file docs và đặt thứ tự đọc.

**Tham chiếu SRS:** UC-05 đến UC-08, Mục 3, NFR-01, NFR-02, NFR-04

**Đầu ra:**

- docs/SEQUENCES.md (hoàn chỉnh)
- docs/USE_CASES.md
- docs/ARCHITECTURE.md
- docs/API_MAP.md
- docs/MODULE_INTERFACES.md (khung)

**Hoàn thành khi:**

- Mọi FR trong Mục 3 xuất hiện ít nhất một lần trong API_MAP.md.
- Sơ đồ kiến trúc không có vòng phụ thuộc.
- Bạn duyệt xong toàn bộ docs/ và tag phase-a.

## Giai đoạn B – Nền tảng dùng chung

> Mục tiêu: khung dự án chạy được, đăng nhập được, ví và giao dịch đúng, lịch làm việc và nhật ký sẵn sàng. Từ ngày 5 trở đi không sửa file migration đã commit; mọi thay đổi bảng là file V mới. Cuối giai đoạn: đồng bộ MODULE_INTERFACES.md, git tag phase-b.

### Ngày 5 – Khung dự án, thành phần dùng chung, giao diện nền

Module: common | Quy mô: Vừa | Phụ thuộc: Ngày 4

**Đọc trước:**

- docs/ARCHITECTURE.md
- docs/API_MAP.md
- phần quy ước trong PROJECT_CONTEXT.md

**Việc cần làm:**

- Tạo cấu trúc package theo ARCHITECTURE.md. Mỗi module một package gốc, bên trong chia web, service, repository, domain, dto; facade là interface đặt ở gốc package.
- Cấu hình application.properties và application-test.properties (trỏ vào database auction_test), bật Flyway, ddl-auto=validate, múi giờ Asia/Ho_Chi_Minh.
- Module common: ServerClock (bọc java.time.Clock, mọi nơi lấy giờ đều đi qua đây để test được), Money (tiền là long VND, hàm định dạng 1.000.000 ₫), BusinessException kèm mã lỗi, @ControllerAdvice, trang lỗi tiếng Việt, entity gốc có id và created_at.
- Thymeleaf layout: fragment header, footer, menu đổi theo vai trò (chưa có logic), Bootstrap 5 qua CDN, một file CSS riêng, vùng hiển thị thông báo flash, định dạng tiền và ngày giờ tiếng Việt.
- Viết test kiến trúc bằng ArchUnit: module không import repository hoặc entity nội bộ của module khác; controller không gọi repository trực tiếp.
- Điền lệnh chạy và lệnh test vào PROJECT_CONTEXT.md.

**Tham chiếu SRS:** NFR-02, NFR-07, NFR-08

**Đầu ra:**

- cấu trúc package
- common/*
- templates/layout
- test ArchUnit

**Hoàn thành khi:**

- mvnw test xanh, gồm cả test ArchUnit.
- Trang chủ hiển thị đúng layout trên máy tính và điện thoại.
- Kết nối DB và Flyway hoạt động.

### Ngày 6 – Tài khoản, đăng nhập, phân quyền

Module: user | Quy mô: Vừa | Phụ thuộc: Ngày 5

**Đọc trước:**

- docs/API_MAP.md (phần auth và hồ sơ)
- docs/SEQUENCES.md (nơi có đăng nhập)
- docs/MODULE_INTERFACES.md (mục user)

**Việc cần làm:**

- Spring Security dùng session và cookie, BCrypt, bật CSRF, phân quyền URL theo vai trò GUEST, USER, ADMIN. “Seller” không phải vai trò cứng: được suy ra từ hồ sơ Seller đã duyệt (làm ở Ngày 10).
- Trang đăng ký, đăng nhập, đăng xuất, đổi mật khẩu, cập nhật thông tin cá nhân (họ tên, CCCD, số điện thoại, tài khoản ngân hàng).
- Phát sự kiện UserRegistered khi đăng ký thành công. Module wallet (Ngày 7) lắng nghe để tạo ví, nhờ đó user không phụ thuộc wallet.
- Tài khoản Admin được tạo bằng seed khi khởi động (mật khẩu lấy từ cấu hình), không có đường đăng ký công khai. Admin không đăng ký tham gia phiên.
- Chặn tài khoản bị khóa đăng nhập (giao diện khóa, mở khóa làm ở Ngày 25).
- Tách DTO: PublicUserView (không có CCCD, ngân hàng) và AdminUserView để thông tin cá nhân chỉ Admin xem được.
- Test: đăng ký trùng tên hoặc email, sai mật khẩu, truy cập trái quyền trả về 403, thiếu CSRF bị từ chối.

**Tham chiếu SRS:** FR-GU-03, FR-US-01, FR-US-02, NFR-04, mục 2.1 và 2.2

**Đầu ra:**

- module user
- trang auth và hồ sơ
- seed Admin
- test bảo mật cơ bản

**Hoàn thành khi:**

- Đăng ký, đăng nhập, đăng xuất, đổi mật khẩu chạy được.
- Không có đường nào để người thường xem được CCCD hoặc tài khoản ngân hàng của người khác.

### Ngày 7 – Ví và giao dịch tiền

Module: wallet | Quy mô: Nặng | Phụ thuộc: Ngày 6

**Đọc trước:**

- docs/SEQUENCES.md (các bước tiền)
- docs/ERD.md (wallets, transactions)
- SRS NFR-05

**Việc cần làm:**

- WalletFacade là cửa duy nhất cho tiền: nạp, rút, phong tỏa (khả dụng sang phong tỏa), hoàn phong tỏa, chuyển phần phong tỏa sang ví khác, thanh toán. Module khác không được đụng bảng wallets hoặc transactions.
- Mọi thay đổi số dư phải ghi một Transaction trong cùng giao dịch DB. Số dư khả dụng và phong tỏa không bao giờ âm (khóa dòng ví hoặc cập nhật có điều kiện).
- Mỗi thao tác tiền nhận một khóa tham chiếu duy nhất (idempotency key): gọi lại với cùng khóa thì không ghi hai lần. Bộ lập lịch về sau sẽ chạy lại nhiều lần nên bắt buộc có.
- Ví hệ thống (ký quỹ, doanh thu phí) tạo bằng seed, tách khỏi ví người dùng (BR-10).
- Lắng nghe UserRegistered để tạo ví cho user mới.
- Giao diện: nạp tiền mô phỏng, rút tiền (yêu cầu đã có tài khoản ngân hàng), xem số dư khả dụng và phong tỏa, lịch sử giao dịch có phân trang.
- Hàm đối soát: tổng tiền phong tỏa của người dùng khớp tổng các khoản đặt trước đang giữ (làm đầy đủ ở Ngày 23).
- Test: không âm, 50 luồng đồng thời nạp và rút, idempotency, không có API sửa hay xóa Transaction.
- Điền chữ ký WalletFacade vào MODULE_INTERFACES.md.

**Tham chiếu SRS:** FR-US-05, FR-US-06, FR-US-07, NFR-05, BR-10

**Đầu ra:**

- module wallet
- trang ví
- seed ví hệ thống
- test đồng thời

**Hoàn thành khi:**

- Test đồng thời không làm số dư âm hay lệch tổng.
- Gọi cùng thao tác hai lần với cùng khóa chỉ tính một lần.
- MODULE_INTERFACES.md đã có WalletFacade.

### Ngày 8 – Cấu hình hệ thống, lịch ngày làm việc, nhật ký

Module: system, audit | Quy mô: Vừa | Phụ thuộc: Ngày 7

**Đọc trước:**

- docs/BUSINESS_RULES.md (BR-03, BR-09, BR-12, BR-28, BR-32)
- docs/ERD.md (system_config, holiday, audit_logs)

**Việc cần làm:**

- SystemConfigFacade: tỷ lệ phí (mặc định 5%), khoảng tiền đặt trước (5%–20%), hạn thanh toán sau khi trúng (mặc định 15 phút), danh sách ngày nghỉ lễ. Seed giá trị mặc định. Trang Admin để xem và sửa.
- BusinessCalendar: isWorkingDay, addWorkingDays, subtractWorkingDays, workingDaysBetween. Ngày làm việc là ngày không phải thứ Bảy, Chủ nhật và ngày nghỉ lễ Admin cấu hình.
- AuditFacade.record(đối tượng, hành động, người thực hiện, dữ liệu trước, dữ liệu sau). Chỉ ghi thêm, không có hàm sửa hay xóa. Nếu muốn chắc hơn, thêm trigger MySQL chặn UPDATE và DELETE cho transactions, bids, audit_logs bằng migration V mới.
- Test BusinessCalendar kỹ: qua cuối tuần, qua ngày lễ, ngày biên, cộng và trừ ngày làm việc.

**Tham chiếu SRS:** FR-AD-07, BR-03, BR-09, BR-12, BR-28, BR-32, NFR-06

**Đầu ra:**

- module system
- module audit
- trang cấu hình Admin
- test lịch

**Hoàn thành khi:**

- Các hàm ngày làm việc đúng với bộ test có cả ngày lễ.
- Không có đường nào trong code để sửa hoặc xóa AuditLog.

### Ngày 9 – Thông báo trong ứng dụng

Module: notification | Quy mô: Nhẹ | Phụ thuộc: Ngày 8

**Đọc trước:**

- docs/BUSINESS_RULES.md (BR-33)
- docs/API_MAP.md (thông báo)

**Việc cần làm:**

- NotificationFacade.notify(userId, loại, nội dung, đường dẫn). Dùng file messages tiếng Việt cho mọi loại thông báo SRS nhắc tới: kết quả xét duyệt, thay đổi niêm yết, trúng đấu giá, hạn thanh toán, hoàn tiền, kết quả phiên, khiếu nại.
- Biểu tượng chuông có số chưa đọc, trang danh sách, đánh dấu đã đọc (từng cái và tất cả). Việc đẩy tức thời qua WebSocket làm ở Ngày 21.
- Test: người dùng chỉ thấy thông báo của mình.

**Tham chiếu SRS:** FR-US-08, BR-33

**Đầu ra:**

- module notification
- trang thông báo
- messages_vi.properties

**Hoàn thành khi:**

- Module khác gọi được notify qua facade.
- Không đọc được thông báo của người khác.

## Giai đoạn C – Người bán và niêm yết

> Mục tiêu: từ hồ sơ Seller đến phiên được niêm yết công khai. Cuối giai đoạn: đồng bộ docs, git tag phase-c.

### Ngày 10 – Hồ sơ Seller và Admin duyệt

Module: seller | Quy mô: Nhẹ | Phụ thuộc: Ngày 9

**Đọc trước:**

- docs/STATE_MACHINES.md (SellerProfile)
- docs/API_MAP.md (seller, admin)

**Việc cần làm:**

- Seller gửi hồ sơ (họ tên, CCCD, liên hệ, tài khoản ngân hàng). Mỗi User tối đa một hồ sơ. Yêu cầu User đã cập nhật CCCD và ngân hàng trước khi gửi.
- Admin xem hàng đợi, duyệt hoặc từ chối kèm lý do. Thông báo kết quả cho User.
- SellerFacade.isApprovedSeller(userId) để module khác kiểm tra quyền Seller.
- Dựng khung layout và menu Admin (còn trống nhiều mục).
- Điểm cần chốt: cho phép gửi lại hồ sơ sau khi bị từ chối (mặc định: cho phép). Ghi vào DECISIONS.md.

**Tham chiếu SRS:** FR-SE-01, FR-AD-01, UC-05 (tiền điều kiện)

**Đầu ra:**

- module seller
- trang hồ sơ Seller
- trang duyệt của Admin

**Hoàn thành khi:**

- Luồng gửi, duyệt, từ chối, gửi lại chạy được.
- Người chưa được duyệt không vào được trang đăng bán.

### Ngày 11 – Đăng tài sản và tạo phiên đấu giá

Module: auction | Quy mô: Nặng | Phụ thuộc: Ngày 10

**Đọc trước:**

- docs/SEQUENCES.md (UC-05)
- docs/STATE_MACHINES.md (Auction)
- docs/BUSINESS_RULES.md (BR-01, 02, 03, 08)

**Việc cần làm:**

- Form đăng tài sản: tên, mô tả, hình ảnh, loại (động sản hoặc bất động sản). Ảnh lưu vào thư mục uploads cấu hình được, giới hạn loại tệp và dung lượng.
- Form thông số phiên: phương thức; giá khởi điểm; tăng dần có bước giá bắt buộc; giảm dần có bước giảm, khoảng thời gian giữa các lần giảm, giá sàn thấp hơn giá khởi điểm; tỷ lệ đặt trước nằm trong khoảng cấu hình (5%–20%); thời gian mở và kết thúc.
- Kiểm tra BR-03: thời gian niêm yết tối thiểu 7 ngày làm việc (động sản) hoặc 15 ngày (bất động sản) trước ngày mở. Dùng BusinessCalendar. Nếu không đủ thì từ chối gửi duyệt (UC-05 nhánh 2a). Điểm cần chốt: “15 ngày” là ngày làm việc hay ngày thường (xem Mục 6).
- Gửi duyệt là tạo phiên ở trạng thái Chờ duyệt (SRS không có bản nháp). Seller xem danh sách tài sản và trạng thái; sửa và gửi lại phiên Bị từ chối.
- Chặn gửi duyệt nếu người dùng chưa là Seller đã duyệt.
- Test: validate theo từng phương thức, ranh giới 7 và 15 ngày, tỷ lệ đặt trước ngoài khoảng.

**Tham chiếu SRS:** FR-SE-02, FR-SE-03, FR-SE-05 (xem danh sách), UC-05 bước 1–3, BR-01, BR-02, BR-03, BR-08

**Đầu ra:**

- Asset và Auction (tạo, sửa, gửi lại)
- trang đăng bán
- trang “Tài sản của tôi”

**Hoàn thành khi:**

- Tạo được phiên hợp lệ cho cả hai phương thức; dữ liệu sai bị từ chối kèm lý do rõ.
- Phiên mới ở trạng thái Chờ duyệt.

### Ngày 12 – Admin duyệt phiên, quy chế, niêm yết và trang công khai

Module: auction | Quy mô: Nặng | Phụ thuộc: Ngày 11

**Đọc trước:**

- docs/STATE_MACHINES.md (Auction)
- docs/API_MAP.md (trang công khai)
- docs/BUSINESS_RULES.md (BR-31)

**Việc cần làm:**

- Tạo AuctionStateMachine là nơi DUY NHẤT được đổi trạng thái phiên: kiểm tra chuyển hợp lệ theo STATE_MACHINES.md, ghi AuditLog, gửi thông báo. Mọi ngày sau chỉ gọi vào đây, không tự set trạng thái.
- Admin duyệt phiên: kiểm tra tài sản, giá, mức đặt trước, thời gian, phương thức. Hệ thống sinh Quy chế phiên từ cấu hình (lưu nguyên văn tại thời điểm duyệt) và Admin xác nhận. Duyệt thì Đã niêm yết, từ chối thì Bị từ chối kèm lý do. Thông báo cho Seller.
- Trang công khai: danh sách phiên có tìm kiếm, lọc theo trạng thái, phương thức, loại tài sản, phân trang; trang chi tiết gồm tài sản, Quy chế, giá hiện tại, lịch sử trả giá (rỗng), danh sách Bidder bằng mã ẩn danh (rỗng), trạng thái thanh toán của người trúng (chưa có). Guest xem được. Thông tin Seller hiển thị công khai, không lộ dữ liệu cá nhân khác.
- Theo dõi phiên (yêu thích) cho User.
- Tạo DevDataSeeder chạy ở profile local để tạo vài phiên mẫu cho các ngày sau.
- Test: chuyển trạng thái không hợp lệ bị chặn; Guest không thấy dữ liệu riêng tư.

**Tham chiếu SRS:** FR-AD-02, FR-GU-01, FR-GU-02 (phần tĩnh), FR-US-03, FR-US-04, UC-05 bước 4–5 và 4a, BR-01, BR-02, BR-31

**Đầu ra:**

- AuctionStateMachine
- trang duyệt phiên của Admin
- danh sách và chi tiết phiên công khai
- DevDataSeeder

**Hoàn thành khi:**

- Luồng gửi, duyệt, từ chối, sửa, gửi lại, niêm yết chạy trọn vẹn.
- Không có chỗ nào ngoài AuctionStateMachine đổi trạng thái phiên (ArchUnit hoặc tìm kiếm mã).

## Giai đoạn D – Đăng ký tham gia và tiền đặt trước

> Mục tiêu: Bidder đăng ký, đặt trước, được duyệt; phiên tự chuyển trạng thái theo giờ; xử lý thay đổi niêm yết và hủy phiên. Cuối giai đoạn: đồng bộ docs, git tag phase-d.

### Ngày 13 – Đăng ký tham gia và nộp tiền đặt trước

Module: registration | Quy mô: Nặng | Phụ thuộc: Ngày 12

**Đọc trước:**

- docs/SEQUENCES.md (UC-01)
- docs/STATE_MACHINES.md (Registration, Deposit)
- docs/MODULE_INTERFACES.md (Wallet, Auction)

**Việc cần làm:**

- RegistrationFacade. Kiểm tra: phiên đang Đã niêm yết; còn trong hạn nộp (đến trước ngày mở phiên 2 ngày làm việc, BR-09); không phải Seller của phiên; không phải Admin; đã tick cam kết không thuộc diện bị cấm (lưu thời điểm cam kết); số dư khả dụng đủ.
- Phong tỏa tiền qua WalletFacade.hold và tạo Deposit (Đã nộp, phong tỏa) cùng Registration (Chờ xét duyệt) trong MỘT giao dịch DB. Chống bấm hai lần (ràng buộc duy nhất cộng xử lý lỗi).
- Cấp mã ẩn danh: dãy 6 chữ số, duy nhất trong phiên, ngẫu nhiên (không đoán được theo thứ tự đăng ký). Ràng buộc UNIQUE (auction_id, anonymous_code).
- Giao diện: nút đăng ký trong trang chi tiết phiên, hiển thị Quy chế và mức đặt trước, trang “Phiên đã đăng ký của tôi”. Số dư không đủ thì gợi ý nạp tiền (nhánh 4a); quá hạn thì từ chối (4b).
- Ghi chú: hệ thống chỉ kiểm tra được cam kết, không kiểm tra được “người thân” theo BR-04. Ghi rõ điều này vào DECISIONS.md.
- Test: bấm đăng ký đồng thời hai lần, số dư không đủ, quá hạn, Seller và Admin đăng ký bị từ chối, mã ẩn danh không trùng.

**Tham chiếu SRS:** FR-BI-01, FR-BI-02, UC-01 bước 1–5, BR-04, BR-05, BR-06, BR-07, BR-08, BR-09, BR-10, BR-31

**Đầu ra:**

- module registration (Registration, Deposit)
- giao diện đăng ký
- test

**Hoàn thành khi:**

- Tiền bị phong tỏa đúng và có Transaction tương ứng.
- Một người chỉ đăng ký được một lần mỗi phiên.

### Ngày 14 – Admin xét duyệt hồ sơ tham gia và bộ lập lịch vòng đời phiên

Module: registration, auction | Quy mô: Vừa | Phụ thuộc: Ngày 13

**Đọc trước:**

- docs/STATE_MACHINES.md
- docs/SEQUENCES.md (UC-01 bước 6, mục scheduler)
- docs/BUSINESS_RULES.md (BR-05, BR-12, BR-24)

**Việc cần làm:**

- Admin duyệt hoặc loại hồ sơ tham gia; thông báo kết quả. Danh sách tô nổi hồ sơ sắp đến hạn thông báo (chậm nhất 1 ngày làm việc trước ngày mở phiên, BR-05).
- Hồ sơ bị loại: đánh dấu Deposit cần hoàn kèm hạn (3 ngày làm việc kể từ khi phiên kết thúc). Việc hoàn tiền thật do job ở Ngày 23 thực hiện.
- Bộ lập lịch vòng đời (Spring @Scheduled, chạy mỗi vài giây, lấy giờ từ ServerClock): Đã niêm yết sang Chờ mở phiên khi đến mốc đóng đăng ký; Chờ mở phiên sang Đang diễn ra khi đến giờ mở; không có đăng ký hợp lệ thì Không thành (BR-24).
- Bộ lập lịch phải chạy lại an toàn: tắt app rồi bật lại sẽ bù các mốc đã lỡ và không làm hai lần. Mọi đổi trạng thái đi qua AuctionStateMachine.
- Test với ServerClock giả: tua giờ qua các mốc; chạy bộ lập lịch hai lần liên tiếp không đổi kết quả.

**Tham chiếu SRS:** FR-AD-03, UC-01 bước 6 và 6a, BR-05, BR-12 (đánh dấu), BR-24

**Đầu ra:**

- trang xét duyệt hồ sơ tham gia
- AuctionLifecycleScheduler
- test tua giờ

**Hoàn thành khi:**

- Phiên tự đi qua Đã niêm yết, Chờ mở phiên, Đang diễn ra đúng giờ.
- Chạy lại bộ lập lịch không gây hai lần chuyển.

### Ngày 15 – Thay đổi thông tin niêm yết và hủy phiên trước khi mở

Module: auction, registration | Quy mô: Vừa | Phụ thuộc: Ngày 14

**Đọc trước:**

- docs/SEQUENCES.md (UC-07)
- docs/BUSINESS_RULES.md (BR-11, BR-34)

**Việc cần làm:**

- Seller đề xuất thay đổi (giá khởi điểm, số lượng, chất lượng, thời gian, phương thức...) khi phiên đang Đã niêm yết hoặc Chờ mở phiên. Lưu giá trị cũ và mới trong bảng listing_change_request. Thay đổi phải qua lại các kiểm tra của BR-03 và validate theo phương thức.
- Admin duyệt thì áp dụng thay đổi và thông báo mọi Bidder đã đăng ký (BR-11). Mỗi Bidder chọn tiếp tục hoặc từ chối và nhận lại tiền đặt trước; không phản hồi được coi là tiếp tục.
- Bidder từ chối: Registration chuyển trạng thái “từ chối do thay đổi”, Deposit đánh dấu cần hoàn (job ở Ngày 23 hoàn). Ngoài trường hợp này Bidder không tự rút để lấy lại tiền.
- Seller hủy phiên trước khi mở, Admin hủy phiên: phiên sang Đã hủy, không phạt Seller; hoàn đầy đủ cho các Bidder không vi phạm. Mặc định hoàn ngay lập tức (đáp ứng hạn “trong 3 ngày”); ghi vào DECISIONS.md.
- Test: không đổi được khi phiên đã Đang diễn ra; Bidder không phản hồi vẫn được giữ chỗ; hủy phiên hoàn đủ tiền, ví khớp.

**Tham chiếu SRS:** FR-SE-04, FR-SE-05 (hủy), FR-BI-03, UC-07, BR-11, BR-12, BR-34

**Đầu ra:**

- luồng thay đổi niêm yết
- luồng hủy phiên
- test hoàn tiền khi hủy

**Hoàn thành khi:**

- Mọi Bidder đã đăng ký nhận thông báo khi niêm yết thay đổi.
- Hủy phiên hoàn đủ tiền và Transaction khớp.

## Giai đoạn E – Phiên đấu giá lõi (khó nhất, làm tuần tự, mỗi ngày một việc)

> Mục tiêu: hai phương thức đấu giá chạy đúng, công bố người trúng, thanh toán, chuyển người kế tiếp, biên bản. Đây là phần dễ sai nhất: mỗi ngày phải có test đồng thời. Ngày 16 và 17 dùng một bản “xử lý kết quả” tạm (ghi log) vì phần thật làm ở Ngày 18; AI phải ghi rõ chỗ tạm trong PROGRESS_LOG. Cuối giai đoạn: đồng bộ docs, git tag phase-e.

### Ngày 16 – Đấu giá tăng dần (trả giá lên)

Module: bidding | Quy mô: Nặng | Phụ thuộc: Ngày 14, 15

**Đọc trước:**

- docs/SEQUENCES.md (UC-02)
- docs/STATE_MACHINES.md (Auction)
- docs/BUSINESS_RULES.md (BR-15, 16, 23, 24)

**Việc cần làm:**

- BiddingFacade.placeBid(auctionId, bidderId, amount) trong MỘT giao dịch DB: khóa dòng Auction (SELECT ... FOR UPDATE hoặc tương đương), kiểm tra phiên Đang diễn ra và giờ server nằm trong khoảng mở đến kết thúc, Bidder đã được duyệt và có Deposit đang giữ, không phải Seller của phiên, chưa bị truất quyền, giá hợp lệ (lần đầu không thấp hơn giá khởi điểm, sau đó không thấp hơn giá cao nhất cộng bước giá).
- Ghi Bid (hợp lệ hoặc không, kèm lý do) với giờ server, cập nhật giá hiện tại và người dẫn đầu, ghi AuditLog. Hai lượt cùng mức giá thì lượt lấy được khóa trước (đến trước theo server) thắng, lượt sau bị từ chối (BR-15).
- Điểm cần chốt: người đang dẫn đầu có được trả giá tiếp không (mặc định: không).
- Job hết giờ: có Bid hợp lệ cao nhất thì gọi AuctionOutcomeHandler.onWinner. Interface này đặt trong module bidding; module settlement cài đặt thật ở Ngày 18 (đến lúc đó dùng bản tạm ghi log), nhờ vậy bidding không phụ thuộc settlement; không có Bid nào thì Không thành (BR-24); chỉ một người trả giá hợp lệ thì vẫn bán (BR-23, phần “không có khiếu nại” gắn ở Ngày 24). Không gia hạn thời gian (BR-16).
- Giao diện tối thiểu để thử: form trả giá trong trang chi tiết. Phòng đấu giá hoàn chỉnh làm ở Ngày 21–22.
- Test bắt buộc: 100 luồng cùng trả đúng một mức giá thì đúng một người được nhận; giá thấp, giá quá hạn, Seller trả giá bị từ chối.

**Tham chiếu SRS:** FR-BI-04 (backend), FR-BI-05, UC-02, BR-15, BR-16, BR-23, BR-24, BR-06, NFR-03

**Đầu ra:**

- module bidding (Bid, placeBid)
- job hết giờ
- test đồng thời

**Hoàn thành khi:**

- Test 100 luồng đồng thời pass, không có hai người cùng thắng.
- Mọi Bid đều có trong nhật ký.

### Ngày 17 – Đấu giá giảm dần (đặt giá xuống)

Module: bidding | Quy mô: Nặng | Phụ thuộc: Ngày 16

**Đọc trước:**

- docs/SEQUENCES.md (UC-03)
- docs/BUSINESS_RULES.md (BR-19, 20, 21, 22)

**Việc cần làm:**

- Tính giá hiện hành bằng hàm thuần từ giờ server: giá = max(giá sàn, giá khởi điểm trừ số kỳ giảm nhân bước giảm), số kỳ tính từ thời điểm mở (hoặc từ mốc tiếp tục). Không lưu từng lần giảm để tránh lệch.
- BiddingFacade.acceptPrice(auctionId, bidderId, giáĐangThấy) trong MỘT giao dịch DB có khóa dòng: yêu cầu đến đầu tiên theo giờ server thì thắng, dừng giảm giá, các yêu cầu đến sau bị từ chối (BR-20). Đề xuất: yêu cầu mang theo mức giá người dùng đang thấy; khác giá hiện hành thì từ chối và báo giá mới. Ghi vào DECISIONS.md.
- Giá chạm sàn mà không ai chấp nhận thì Không thành (BR-21). Điểm cần chốt: không thành ngay khi chạm sàn hay giữ giá sàn thêm một khoảng (xem Mục 6).
- Gọi AuctionOutcomeHandler.onWinner (bản tạm) khi có người chấp nhận.
- Test bắt buộc: 100 luồng cùng bấm chấp nhận thì đúng một người trúng; kiểm tra các mốc giảm giá bằng ServerClock giả; Seller bị từ chối (BR-06).

**Tham chiếu SRS:** FR-BI-06, UC-03, BR-19, BR-20, BR-21, BR-06, NFR-02, NFR-03

**Đầu ra:**

- acceptPrice
- hàm tính giá giảm dần
- test đồng thời và test tua giờ

**Hoàn thành khi:**

- Đúng một người trúng khi 100 yêu cầu đồng thời.
- Giá tính đúng ở mọi mốc, kể cả sát giá sàn.

### Ngày 18 – Rút lại giá, truất quyền, công bố người trúng

Module: bidding, settlement | Quy mô: Nặng | Phụ thuộc: Ngày 17

**Đọc trước:**

- docs/SEQUENCES.md (UC-02, UC-03, UC-04)
- docs/BUSINESS_RULES.md (BR-14, 17, 22, 25)
- docs/DECISIONS.md (các điểm đã chốt ở Mục 6)

**Việc cần làm:**

- Rút lại giá trước khi công bố (FR-BI-07) theo phương án đã chốt ở Mục 6. Tăng dần: người dẫn đầu rút thì phiên tiếp tục từ giá của người trả giá liền kề (Bid hợp lệ cao kế tiếp của Bidder khác), người rút bị truất quyền và mất tiền đặt trước (Deposit chuyển Bị giữ).
- Truất quyền: Bidder bị truất không trả giá hay chấp nhận được nữa; Deposit chuyển Bị giữ (BR-14).
- Thay bản tạm bằng thật: AuctionOutcomeHandler.onWinner tạo Settlement (giá trúng, tiền cọc, số còn phải trả bằng giá trúng trừ cọc, hạn thanh toán theo cấu hình), chuyển Deposit của người trúng sang Chuyển thành cọc, chuyển phiên sang Chờ thanh toán, thông báo, ghi AuditLog.
- Dữ liệu công khai của phiên gồm trạng thái “Người trúng đấu giá đang thanh toán”, mã ẩn danh, hạn thanh toán (BR-25).
- Test: rút lại ở các thời điểm khác nhau, số tiền cọc và số còn phải trả đúng, Deposit đổi trạng thái đúng.

**Tham chiếu SRS:** FR-BI-07, BR-14, BR-17, BR-22 (phần rút lại), BR-25, UC-04 bước 1–2

**Đầu ra:**

- rút lại và truất quyền
- Settlement (tạo)
- công bố người trúng

**Hoàn thành khi:**

- Phiên sang Chờ thanh toán với Settlement đúng số tiền.
- Người bị truất quyền mất cọc và không thao tác được nữa.

### Ngày 19 – Thanh toán sau khi trúng

Module: settlement | Quy mô: Vừa | Phụ thuộc: Ngày 18

**Đọc trước:**

- docs/SEQUENCES.md (UC-04)
- docs/BUSINESS_RULES.md (BR-26, 27)

**Việc cần làm:**

- Người trúng thanh toán phần còn lại từ ví vào ví ký quỹ trong hạn (mặc định 15 phút). Đủ tiền thì Settlement Đã trả, phiên sang Hoàn tất, trạng thái công khai cập nhật “Đã thanh toán – phiên kết thúc”.
- Số dư không đủ nhưng còn hạn thì hướng dẫn nạp tiền ngay (nhánh 3b).
- Người trúng chủ động hủy hoặc quá hạn (job kiểm tra hạn): Settlement Hủy hoặc Quá hạn, Deposit chuyển Bị giữ (mất cọc), trạng thái công khai cập nhật ngay. Sau đó gọi AuctionOutcomeHandler.onWinnerRejected (bản tạm chuyển phiên sang Không thành, bản thật ở Ngày 20).
- Mọi thay đổi tiền đi qua WalletFacade kèm khóa tham chiếu; thông báo cho các bên.
- Test: thanh toán đủ, thiếu tiền rồi nạp thêm, hủy, quá hạn đúng giây (dùng ServerClock giả), bấm thanh toán hai lần chỉ tính một lần.

**Tham chiếu SRS:** FR-BI-08, UC-04 bước 3–5 và 3b, BR-26, BR-27, BR-14

**Đầu ra:**

- thanh toán Settlement
- job quá hạn thanh toán
- test

**Hoàn thành khi:**

- Số liệu ví khớp sau thanh toán.
- Quá hạn đúng mốc thì mất cọc và hiển thị công khai.

### Ngày 20 – Chuyển người kế tiếp, phiên không thành, biên bản điện tử

Module: settlement | Quy mô: Nặng | Phụ thuộc: Ngày 19

**Đọc trước:**

- docs/SEQUENCES.md (UC-04 nhánh 3a)
- docs/BUSINESS_RULES.md (BR-18, 22, 24, 27, 32)
- ví dụ số BR-18 ở Ngày 3

**Việc cần làm:**

- Tăng dần: tìm người trả giá liền kề (Bidder khác chưa bị truất quyền). Điều kiện BR-18: giá liền kề cộng tiền đặt trước của người từ chối ít nhất bằng giá của người từ chối, và người đó đồng ý mua. Thỏa thì tạo Settlement mới; không thì Không thành. Điểm cần chốt: hạn trả lời của người liền kề (xem Mục 6).
- Giảm dần: phiên về Đang diễn ra, tiếp tục từ mức giá của người từ chối (lưu mốc tiếp tục), người bị truất quyền không chấp nhận lại; không ai chấp nhận thì Không thành (BR-22).
- Hỗ trợ chuỗi nhiều Settlement liên tiếp trong một phiên.
- Biên bản điện tử (BR-32): sinh khi phiên Hoàn tất hoặc Không thành, lưu bản chụp gồm thông tin phiên, tài sản, danh sách Bid, kết quả, thời điểm. Bản công khai chỉ dùng mã ẩn danh.
- Thay bản tạm onWinnerRejected bằng bản thật.
- Test: ba người từ chối liên tiếp; giá trị biên của BR-18 (tổng đúng bằng giá thì hợp lệ, thiếu một đồng thì không); giảm dần tiếp tục đúng giá.

**Tham chiếu SRS:** UC-04 nhánh 3a-1 và 3a-2, BR-18, BR-22, BR-24, BR-27, BR-32

**Đầu ra:**

- NextBidderResolver
- tiếp tục phiên giảm dần
- auction_minutes (biên bản)

**Hoàn thành khi:**

- Chuỗi người kế tiếp chạy đúng ở cả hai phương thức.
- Mỗi phiên kết thúc đều có biên bản.

## Giai đoạn F – Thời gian thực

> Mục tiêu: mọi người xem thấy giá, trạng thái, đồng hồ cập nhật trong 2 giây; phòng đấu giá dùng được trên điện thoại. Cuối giai đoạn: đồng bộ docs, git tag phase-f.

### Ngày 21 – WebSocket và đồng bộ giờ server

Module: realtime | Quy mô: Nặng | Phụ thuộc: Ngày 20

**Đọc trước:**

- docs/ARCHITECTURE.md (topic WebSocket)
- docs/API_MAP.md (WebSocket)
- docs/BUSINESS_RULES.md (BR-31)

**Việc cần làm:**

- Spring WebSocket với STOMP, endpoint /ws. Topic theo phiên (ví dụ /topic/auction/{id}) đẩy: Bid mới, giá hiện hành, đổi trạng thái, người trúng và hạn thanh toán. Thông báo cá nhân đẩy qua kênh riêng của từng user, nối với module notification ở Ngày 9.
- Quyết định thiết kế (khuyến nghị): lệnh trả giá và chấp nhận giá gửi bằng HTTP (fetch) để dùng chung phân quyền và CSRF; WebSocket chỉ để đẩy dữ liệu xuống. Ghi vào DECISIONS.md.
- Đẩy sự kiện SAU KHI giao dịch DB commit (TransactionalEventListener AFTER_COMMIT) để không đẩy dữ liệu của giao dịch bị rollback.
- Endpoint giờ server; client tính độ lệch và chạy đồng hồ đếm ngược theo giờ server (NFR-02). Giá giảm dần ở client đồng bộ theo cùng công thức.
- Guest được subscribe topic công khai. Dữ liệu đẩy chỉ có mã ẩn danh, không có tên hay thông tin cá nhân (BR-31); viết test kiểm tra nội dung thông điệp.
- JavaScript thuần trong một file auction-room.js, không dùng framework.

**Tham chiếu SRS:** NFR-01, NFR-02, FR-BI-04, FR-GU-02 (real-time), FR-US-08, BR-31

**Đầu ra:**

- module realtime
- auction-room.js (phần kết nối)
- endpoint giờ server
- test thông điệp

**Hoàn thành khi:**

- Mở hai trình duyệt, trả giá ở một bên thì bên kia thấy ngay.
- Thông điệp WebSocket không chứa thông tin cá nhân.

### Ngày 22 – Phòng đấu giá hoàn chỉnh, trạng thái công khai, kiểm thử tải

Module: realtime, web | Quy mô: Vừa | Phụ thuộc: Ngày 21

**Đọc trước:**

- docs/API_MAP.md (phòng đấu giá)
- docs/BUSINESS_RULES.md (BR-25, 26)

**Việc cần làm:**

- Phòng đấu giá cho cả hai phương thức: giá hiện tại, bước giá, nút chọn nhanh giá, đồng hồ, lịch sử trả giá, danh sách mã ẩn danh, thông báo lỗi rõ ràng, hiển thị tình trạng kết nối và tự kết nối lại.
- Hiển thị công khai theo BR-25 và BR-26: “Người trúng đấu giá đang thanh toán” kèm mã ẩn danh và đồng hồ hạn thanh toán; khi xong là “Đã thanh toán – phiên kết thúc”; khi người trúng bị loại thì cập nhật ngay.
- Giao diện dùng tốt trên điện thoại (NFR-08), tiền định dạng VND rõ ràng.
- Kiểm thử tải NFR-09: 100 client giả (test Java dùng STOMP client) cùng xem và trả giá trong một phiên; đo độ trễ, mục tiêu không quá 2 giây. Ghi kết quả vào docs/TEST_REPORT.md. Với máy 8GB, chạy tải ở mức vừa phải và đóng bớt ứng dụng khác.
- Sửa các lỗi phát hiện.

**Tham chiếu SRS:** FR-BI-04, BR-25, BR-26, NFR-08, NFR-09

**Đầu ra:**

- phòng đấu giá hoàn chỉnh
- docs/TEST_REPORT.md

**Hoàn thành khi:**

- Trải nghiệm được toàn bộ một phiên từ đầu đến Hoàn tất trên hai thiết bị.
- TEST_REPORT.md ghi số đo độ trễ với 100 client.

## Giai đoạn G – Tiền sau phiên, khiếu nại, quản trị, nghiệm thu

> Mục tiêu: hoàn tiền, chuyển tiền cho Seller đúng luật; khiếu nại; màn hình quản trị; rà soát bảo mật và nghiệm thu. Cuối giai đoạn: git tag v1.0.

### Ngày 23 – Hoàn tiền, chuyển cọc, nhận tài sản, phí dịch vụ

Module: payout | Quy mô: Nặng | Phụ thuộc: Ngày 22

**Đọc trước:**

- docs/SEQUENCES.md (UC-06)
- docs/BUSINESS_RULES.md (BR-12, 13, 14, 28, 29, 30)
- docs/MODULE_INTERFACES.md (Wallet)

**Việc cần làm:**

- Job “đến hạn” chạy lại được: với phiên Hoàn tất, Không thành, Đã hủy thì (1) hoàn tiền đặt trước cho Bidder không trúng, bị loại, đã từ chối do thay đổi niêm yết, trong 3 ngày làm việc kể từ khi phiên kết thúc; (2) chuyển tiền cọc của người trúng cho Seller; (3) chuyển các khoản Bị giữ cho Seller. Trong lúc phiên còn Chờ thanh toán, giữ tiền của các Bidder khác (BR-28).
- Bidder xác nhận đã nhận tài sản (hoặc Admin xác nhận): chuyển phần còn lại cho Seller sau khi trừ phí (BR-29, BR-30). Phí vào ví doanh thu của hệ thống, làm tròn xuống đồng (ghi DECISIONS.md).
- Màn hình Admin theo dõi tiền đặt trước theo phiên với đủ trạng thái ở Mục 5.3, và nút xử lý thủ công khi job lỗi. Admin chỉ có các thao tác định sẵn, không có thao tác tự do trên tiền ký quỹ (BR-10).
- Seller xem kết quả phiên, biên bản, khoản tiền nhận (đã trừ phí) và lịch sử chuyển tiền. Bidder xem tình trạng đặt trước.
- Hoàn thiện hàm đối soát NFR-05: ví ký quỹ khớp tổng Deposit đang giữ cộng Settlement đã trả chưa chuyển.
- Test với ví dụ số (giả sử đặt trước 10%): giá trúng 100.000.000, cọc 10.000.000 chuyển cho Seller, người trúng trả thêm 90.000.000, phí 5.000.000, lần chuyển cuối 85.000.000, Seller nhận tổng 95.000.000. Chạy job hai lần không chuyển tiền hai lần.

**Tham chiếu SRS:** UC-06, FR-AD-05, FR-BI-09, FR-SE-06, FR-SE-07, BR-10, BR-12, BR-13, BR-14, BR-28, BR-29, BR-30, NFR-05

**Đầu ra:**

- module payout
- job đến hạn
- màn hình Admin tiền đặt trước
- đối soát

**Hoàn thành khi:**

- Ví dụ số khớp từng đồng.
- Job idempotent; đối soát báo khớp sau mọi kịch bản test.

### Ngày 24 – Khiếu nại và Admin điều hành phiên

Module: complaint, admin | Quy mô: Vừa | Phụ thuộc: Ngày 23

**Đọc trước:**

- docs/SEQUENCES.md (UC-08)
- docs/STATE_MACHINES.md (Complaint)
- docs/BUSINESS_RULES.md (BR-23, 34, 35)
- DECISIONS.md (điểm 9, 10 ở Mục 6)

**Việc cần làm:**

- Gửi khiếu nại: Bidder đã đăng ký hoặc Seller của phiên, có nội dung và minh chứng (upload). Admin tiếp nhận, xem xét, yêu cầu bổ sung, ghi kết quả, từ chối kèm lý do. Trạng thái Mới, Đang xử lý, Đã xử lý, Từ chối. Thông báo cho các bên.
- Nối với BR-23: khiếu nại chưa giải quyết thì không bán cho một người duy nhất (sửa job hết giờ ở Ngày 16 và giảm dần ở Ngày 17 để gọi kiểm tra này).
- Admin điều hành (FR-AD-06): hủy phiên đang diễn ra do vi phạm (hoàn đầy đủ cho Bidder không vi phạm, BR-34), truất quyền người vi phạm (Deposit Bị giữ). “Tạm dừng phiên” chỉ làm nếu bạn chốt có (xem Mục 6).
- Thời hạn gửi và xử lý khiếu nại theo điểm đã chốt ở Mục 6.
- Test: người không liên quan không gửi được; khiếu nại mở chặn bán cho một người; hủy do vi phạm hoàn tiền đúng.

**Tham chiếu SRS:** UC-08, FR-US-09, FR-AD-06, FR-AD-10, BR-23, BR-34, BR-35

**Đầu ra:**

- module complaint
- trang khiếu nại hai phía
- điều hành phiên của Admin

**Hoàn thành khi:**

- Khiếu nại đi hết vòng đời và có thông báo.
- BR-23 bị chặn khi còn khiếu nại mở.

### Ngày 25 – Quản trị tài khoản, thống kê, nhật ký, lịch sử người dùng

Module: admin, user | Quy mô: Vừa | Phụ thuộc: Ngày 24

**Đọc trước:**

- docs/API_MAP.md (admin)
- docs/BUSINESS_RULES.md (BR-32)

**Việc cần làm:**

- Quản lý tài khoản: tìm kiếm, khóa, mở khóa, xem thông tin định danh (CCCD, ngân hàng chỉ Admin thấy). Mỗi lần Admin xem định danh ghi AuditLog.
- Bảng điều khiển: số phiên theo trạng thái, số giao dịch, doanh thu phí; biểu đồ đơn giản (Chart.js qua CDN).
- Xem nhật ký chỉ đọc: lịch sử trả giá, đổi trạng thái, giao dịch tiền; lọc theo phiên, thời gian, loại.
- Bidder xem lịch sử tham gia: thắng hoặc thua, tình trạng đặt trước và thanh toán. Seller xem kết quả phiên và biên bản.
- Test: người thường không vào được trang Admin; tài khoản bị khóa không đăng nhập được.

**Tham chiếu SRS:** FR-AD-04, FR-AD-08, FR-AD-09, FR-BI-10, FR-SE-06

**Đầu ra:**

- các trang quản trị
- bảng điều khiển
- trang lịch sử

**Hoàn thành khi:**

- Tất cả FR của Admin trong Mục 3.1 đã có trang chạy được.
- Thông tin định danh chỉ hiển thị cho Admin.

### Ngày 26 – Rà soát bảo mật, dữ liệu demo, tài liệu, nghiệm thu

Module: toàn hệ thống | Quy mô: Nặng | Phụ thuộc: Ngày 25

**Đọc trước:**

- docs/BUSINESS_RULES.md
- docs/API_MAP.md
- toàn bộ docs/

**Việc cần làm:**

- Rà soát NFR-04: CSRF cho mọi form và fetch; XSS (không dùng th:utext với dữ liệu người dùng); không nối chuỗi SQL; phân quyền từng URL theo API_MAP; rò rỉ thông tin (CCCD, tên Bidder trong HTML, JSON, WebSocket); upload tệp. Ghi kết quả rà soát vào docs/SECURITY_CHECK.md.
- Mỗi BR có ít nhất một test; điền cột file code và file test trong BUSINESS_RULES.md; báo các BR còn thiếu.
- Kịch bản end-to-end cho UC-01 đến UC-08 (có thể là test tích hợp hoặc danh sách thao tay có kỳ vọng).
- Dữ liệu demo (profile local): 1 Admin, 3 Seller, 10 Bidder, mỗi phương thức vài phiên ở các trạng thái khác nhau, kèm tài khoản và mật khẩu demo trong README.md.
- README.md: cách cài, chạy, tài khoản demo, cách chạy test.
- Đồng bộ ERD.md, STATE_MACHINES.md, SEQUENCES.md với code thật (as-built). Vẽ docs/CLASS_DIAGRAM.md từ code thật. Chuẩn bị bộ sơ đồ nộp bài nếu giảng viên yêu cầu UML chuẩn.
- Tag v1.0.

**Tham chiếu SRS:** NFR-04, NFR-08, toàn bộ UC, toàn bộ BR

**Đầu ra:**

- docs/SECURITY_CHECK.md
- README.md
- dữ liệu demo
- docs/CLASS_DIAGRAM.md
- tag v1.0

**Hoàn thành khi:**

- mvnw test xanh toàn bộ.
- Làm theo README trên máy sạch chạy được.
- Bảng BR không còn dòng “Chưa” mà không có lý do ghi chú.

## Điểm cần chốt (SRS chưa rõ)

Nếu người dùng chưa trả lời, dùng mặc định và ghi vào docs/DECISIONS.md.

| # | Vấn đề | Đề xuất mặc định | Chốt trước Ngày |
| --- | --- | --- | --- |
| 1 | Hạn thanh toán sau khi trúng (SRS 9.3 mục 1). | Giữ mặc định 15 phút, Admin cấu hình được. | 8 |
| 2 | BR-03: bất động sản “15 ngày” là ngày làm việc hay ngày thường. | Ngày thường, đúng câu chữ SRS. Ghi rõ trong DECISIONS.md. | 11 |
| 3 | Người đang dẫn đầu (tăng dần) có được trả giá tiếp. | Không cho, vì không có ý nghĩa. | 16 |
| 4 | Giảm dần: giá chạm sàn thì Không thành ngay, hay giữ giá sàn thêm một thời gian (BR-21). | Giữ giá sàn thêm đúng một khoảng giảm (hoặc đến thời gian kết thúc nếu đến trước), rồi mới Không thành. | 17 |
| 5 | Giảm dần: SRS cho phép “rút lại trước khi công bố” (FR-BI-07, BR-22) nhưng BR-25 công bố ngay khi có người chấp nhận nên gần như không có khoảng thời gian này. | Không có chức năng rút lại riêng ở giảm dần; việc hủy ở bước Chờ thanh toán xử lý theo BR-27 và BR-22. | 18 |
| 6 | FR-AD-06 ghi Admin “công bố người trúng” nhưng BR-25 để hệ thống tự công bố. | Hệ thống tự công bố; Admin chỉ giám sát và can thiệp khi có vi phạm. | 18 |
| 7 | Tăng dần: ai được rút lại giá. | Chỉ người đang dẫn đầu (BR-17 chỉ nêu người cao nhất). | 18 |
| 8 | Người trả giá liền kề (BR-18) “đồng ý mua”: hạn trả lời bao lâu. | Dùng cùng hạn thanh toán cấu hình (đồng ý bằng cách thanh toán). | 20 |
| 9 | Tạm dừng phiên (FR-AD-06): SRS không có trạng thái này và BR-16 nói không gia hạn. | Bỏ “tạm dừng”; chỉ giữ hủy phiên và truất quyền. | 24 |
| 10 | Thời hạn gửi và xử lý khiếu nại (SRS 9.3 mục 4). | Gửi được đến 30 ngày sau khi phiên kết thúc (tham khảo Điều 75); Admin phản hồi trong 7 ngày, quá hạn thì tô nổi trên màn hình Admin. | 24 |
| 11 | Làm tròn phí dịch vụ khi giá trúng không chia hết. | Làm tròn xuống đồng. | 23 |
| 12 | Hủy phiên: hoàn tiền ngay hay đợi 3 ngày làm việc. | Hoàn ngay (vẫn đáp ứng hạn “trong 3 ngày”). | 15 |
| 13 | Seller bị từ chối hồ sơ có được gửi lại không. | Được gửi lại. | 10 |

## Truy vết BR → ngày

| Mã | Ngày |
| --- | --- |
| BR-01 | 11, 12 |
| BR-02 | 11, 12 |
| BR-03 | 8, 11 |
| BR-04 | 13 |
| BR-05 | 13, 14 |
| BR-06 | 13, 16, 17 |
| BR-07 | 13 |
| BR-08 | 11, 13 |
| BR-09 | 8, 13 |
| BR-10 | 7, 13, 23 |
| BR-11 | 15 |
| BR-12 | 14, 15, 23 |
| BR-13 | 18, 23 |
| BR-14 | 18, 19, 23, 24 |
| BR-15 | 16 |
| BR-16 | 16 |
| BR-17 | 18 |
| BR-18 | 20 |
| BR-19 | 17 |
| BR-20 | 17 |
| BR-21 | 17 |
| BR-22 | 18, 20 |
| BR-23 | 16, 24 |
| BR-24 | 14, 16, 20 |
| BR-25 | 18, 22 |
| BR-26 | 19, 22 |
| BR-27 | 19, 20 |
| BR-28 | 8, 23 |
| BR-29 | 23 |
| BR-30 | 23 |
| BR-31 | 12, 13, 21 |
| BR-32 | 8, 16, 20, 26 |
| BR-33 | 9 |
| BR-34 | 15, 24 |
| BR-35 | 24 |

## Truy vết FR → ngày

| Mã | Ngày |
| --- | --- |
| FR-AD-01 | 10 |
| FR-AD-02 | 12 |
| FR-AD-03 | 14 |
| FR-AD-04 | 25 |
| FR-AD-05 | 23 |
| FR-AD-06 | 15, 18, 24 |
| FR-AD-07 | 8 |
| FR-AD-08 | 25 |
| FR-AD-09 | 8, 25 |
| FR-AD-10 | 24 |
| FR-GU-01 | 12 |
| FR-GU-02 | 12, 21 |
| FR-GU-03 | 6 |
| FR-US-01 | 6 |
| FR-US-02 | 6 |
| FR-US-03 | 12 |
| FR-US-04 | 12 |
| FR-US-05 | 7 |
| FR-US-06 | 7 |
| FR-US-07 | 7 |
| FR-US-08 | 9, 21 |
| FR-US-09 | 24 |
| FR-BI-01 | 13 |
| FR-BI-02 | 13 |
| FR-BI-03 | 15 |
| FR-BI-04 | 16, 21, 22 |
| FR-BI-05 | 16 |
| FR-BI-06 | 17 |
| FR-BI-07 | 18 |
| FR-BI-08 | 19 |
| FR-BI-09 | 23 |
| FR-BI-10 | 25 |
| FR-SE-01 | 10 |
| FR-SE-02 | 11 |
| FR-SE-03 | 11 |
| FR-SE-04 | 15 |
| FR-SE-05 | 11, 15 |
| FR-SE-06 | 23, 25 |
| FR-SE-07 | 23 |

## Truy vết NFR → ngày

| Mã | Ngày |
| --- | --- |
| NFR-01 | 21, 22 |
| NFR-02 | 5, 17, 21 |
| NFR-03 | 16, 17, 19 |
| NFR-04 | 6, 26 |
| NFR-05 | 7, 23 |
| NFR-06 | 8 |
| NFR-07 | 0, 5 |
| NFR-08 | 5, 22 |
| NFR-09 | 22 |

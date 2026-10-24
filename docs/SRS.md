**TÀI LIỆU ĐẶC TẢ YÊU CẦU PHẦN MỀM**

*(Software Requirements Specification – SRS)*

**HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN**

Đấu giá tăng dần (trả giá lên) và giảm dần (đặt giá xuống)

| | |
| ---- | ---- |
| **Phiên bản** | 1.1 (bản nháp) |
| **Ngày lập** | 16/10/2026 |
| **Công nghệ** | Java |
| **Căn cứ pháp lý** | Luật Đấu giá tài sản số 01/2016/QH14, bản hợp nhất đã được sửa đổi, bổ sung bởi Luật Giá số 16/2023/QH15, Luật số 37/2024/QH15 và Luật Phục hồi, phá sản số 142/2025/QH15 |

**NỘI DUNG**

1\. Giới thiệu

2\. Tổng quan hệ thống (vai trò, giả định)

3\. Yêu cầu chức năng theo vai trò

4\. Quy tắc nghiệp vụ

5\. Vòng đời phiên đấu giá và tiền đặt trước

6\. Đặc tả use case chính

7\. Thực thể dữ liệu chính

8\. Yêu cầu phi chức năng

9\. Các quyết định đã chốt và điểm còn cần xác nhận

1\. Giới thiệu

1.1. Mục tiêu

Cung cấp môi trường đấu giá trực tuyến công bằng cho người bán và người
mua, có tính cạnh tranh theo thời gian thực. Hệ thống hỗ trợ hai phương
thức đấu giá: **tăng dần** (trả giá lên) và **giảm dần** (đặt giá
xuống), áp dụng cho cả động sản và bất động sản.

1.2. Phạm vi

**Trong phạm vi:**

- Người bán (Seller) đăng tài sản, thiết lập các thông số đấu giá; Admin
  duyệt hồ sơ và niêm yết.

- Người mua (Bidder) đăng ký tham gia, nộp tiền đặt trước, trả giá hoặc
  chấp nhận giá theo thời gian thực.

- Quản lý tiền đặt trước, thanh toán sau khi trúng, hoàn tiền, chuyển
  tiền cho Seller, thu phí dịch vụ.

- Công khai trạng thái phiên (kể cả trạng thái chờ thanh toán của người
  trúng) cho mọi người xem.

- Tiếp nhận và xử lý khiếu nại về phiên đấu giá.

**Ngoài phạm vi:**

- Thẩm định giá, giám định tài sản; thủ tục chuyển quyền sở hữu, sang
  tên bất động sản.

- Đấu giá bỏ phiếu, đấu giá trực tiếp bằng lời nói, đấu giá theo thủ tục
  rút gọn.

- Đấu giá quyền sử dụng đất giao/cho thuê để thực hiện dự án đầu tư,
  quyền khai thác khoáng sản, quyền sử dụng tần số vô tuyến điện, đấu
  giá nợ xấu.

- Tích hợp cổng thanh toán thật: hệ thống dùng ví nội bộ mô phỏng.

**Lưu ý pháp lý:** theo Điều 43a Luật Đấu giá tài sản, đấu giá trực
tuyến thực tế phải được thực hiện qua Cổng Đấu giá tài sản quốc gia hoặc
trang thông tin đấu giá trực tuyến được thẩm định, phê duyệt theo quy
định của Chính phủ. Hệ thống này mô phỏng nghiệp vụ phục vụ học tập,
không triển khai thực tế.

1.3. Thuật ngữ

| **Thuật ngữ** | **Giải thích** |
| ---- | ---- |
| Phiên đấu giá | Một lần đấu giá một tài sản, có một phương thức đấu giá, thời gian mở và kết thúc. |
| Seller (người bán) | Tương ứng “người có tài sản đấu giá”. Là User đã được Admin duyệt hồ sơ để đăng bán. |
| Bidder (người mua) | Tương ứng “người tham gia đấu giá”. Là User đã đăng ký tham gia một phiên. |
| Admin | Đại diện tổ chức đấu giá: duyệt hồ sơ, điều hành phiên (kiêm đấu giá viên), giữ và xử lý tiền đặt trước. |
| Trả giá lên (tăng dần) | Giá bắt đầu từ giá khởi điểm, người tham gia trả giá cao dần; người trả cao nhất khi hết giờ là người trúng. |
| Đặt giá xuống (giảm dần) | Giá bắt đầu từ mức cao nhất và giảm dần; người đầu tiên chấp nhận mức giá hiện hành là người trúng. |
| Giá khởi điểm | Mức giá bắt đầu của phiên (thấp nhất với tăng dần, cao nhất với giảm dần). |
| Bước giá / bước giảm | Mức chênh lệch tối thiểu giữa các lần trả giá (tăng dần) hoặc mức giảm giá mỗi lần (giảm dần). |
| Giá sàn | Mức giá thấp nhất trong đấu giá giảm dần; chạm sàn mà không ai chấp nhận thì phiên không thành. Đây là lựa chọn thiết kế của dự án. |
| Tiền đặt trước | Khoản tiền Bidder nộp để được tham gia, bằng 5%–20% giá khởi điểm; được phong tỏa trong ví cho đến khi hoàn, chuyển cọc hoặc bị giữ. |
| Tiền đặt cọc | Tiền đặt trước của người trúng đấu giá, được chuyển thành tiền cọc bảo đảm thực hiện việc mua tài sản. |
| Ngày làm việc | Ngày không phải thứ Bảy, Chủ nhật và ngày nghỉ lễ do Admin cấu hình. |
| Mã ẩn danh | Dãy số (không có chữ), duy nhất trong mỗi phiên, đại diện cho Bidder và dùng thay cho thông tin cá nhân khi hiển thị công khai (ví dụ 482917). |
| Tham dự phiên | Bidder đã đăng nhập vào phòng đấu giá trong khoảng thời gian phiên diễn ra. |

1.4. Tài liệu tham chiếu

- Luật Đấu giá tài sản số 01/2016/QH14 (bản hợp nhất đến Luật số
  142/2025/QH15). Các điều được viện dẫn ở cột “Căn cứ” trong Mục 4.

- Bản nháp SRS ban đầu của nhóm (file SRS Auction.docx).

*Lưu ý: các căn cứ điều luật được đối chiếu từ bản scan của Luật và chưa
xem các nghị định hướng dẫn. Cần soát lại với văn bản gốc khi nộp.*

2\. Tổng quan hệ thống

2.1. Vai trò (actor)

| **Actor** | **Mô tả** | **Ghi chú** |
| ---- | ---- | ---- |
| Admin | Quản trị, đại diện tổ chức đấu giá. Duyệt hồ sơ, điều hành phiên, công bố người trúng, giữ và xử lý tiền đặt trước. | Kiêm vai trò đấu giá viên. Không được đăng ký tham gia phiên đấu giá. |
| Guest (khách) | Người chưa đăng nhập, kể cả người đã có tài khoản nhưng chưa đăng nhập. Chỉ xem thông tin công khai. | Đăng ký hoặc đăng nhập để trở thành User. |
| User | Người dùng đã đăng nhập. Chỉ trở thành User khi đã đăng nhập, chưa đăng nhập thì là Guest. Có thể đảm nhận đồng thời nhiều vai trò con. | Có nhóm chức năng chung. |
| Bidder (người mua) | User khi đăng ký và tham gia một phiên đấu giá. | Là vai trò con của User. |
| Seller (người bán) | User đã được Admin duyệt hồ sơ, đăng tài sản để đấu giá. | Là vai trò con của User. |

Quan hệ: Bidder và Seller kế thừa chức năng của User đã đăng nhập; User
kế thừa chức năng xem của Guest.

2.2. Quy tắc về vai trò

- Một tài khoản User có thể đồng thời là Bidder (ở phiên này) và Seller
  (ở phiên khác).

- **Seller không được đăng ký, trả giá hay chấp nhận giá ở phiên do
  chính mình đăng bán** (và người thân thuộc diện bị cấm theo luật). Đây
  là thay đổi so với bản nháp SRS ban đầu, vốn cho phép Seller tham dự
  phiên của mình.

- Admin không được tham gia với tư cách Bidder.

2.3. Giả định và ràng buộc

- Tiền tệ là VND; tiền được xử lý qua ví nội bộ mô phỏng (nạp tiền giả
  lập).

- Mỗi phiên chỉ có một tài sản và một phương thức, do Seller chọn lúc
  đăng.

- Thông báo gửi trong ứng dụng.

- Công nghệ nền: Java (xem Mục 8).

3\. Yêu cầu chức năng theo vai trò

3.1. Admin

| **Mã** | **Chức năng** | **Mô tả** |
| ---- | ---- | ---- |
| FR-AD-01 | Duyệt hồ sơ Seller | Xem hồ sơ người bán đăng ký, duyệt hoặc từ chối kèm lý do. |
| FR-AD-02 | Duyệt tài sản và phiên | Kiểm tra tài sản, giá khởi điểm, mức đặt trước, thời gian, phương thức; duyệt để niêm yết. Hệ thống sinh Quy chế phiên từ cấu hình và Admin xác nhận. |
| FR-AD-03 | Xét duyệt hồ sơ tham gia | Xem hồ sơ đăng ký tham gia của Bidder, duyệt hoặc loại; thông báo kết quả chậm nhất 1 ngày làm việc trước ngày mở phiên. |
| FR-AD-04 | Quản lý tài khoản | Xem, tìm kiếm, khóa hoặc mở khóa tài khoản; xem thông tin định danh. |
| FR-AD-05 | Quản lý tiền đặt trước | Theo dõi tiền đặt trước theo phiên; hoàn tiền, chuyển cọc cho Seller, xử lý các trường hợp mất cọc đúng hạn. |
| FR-AD-06 | Điều hành và giám sát phiên | Theo dõi phiên đang diễn ra; công bố người trúng; tạm dừng hoặc hủy phiên khi có vi phạm; truất quyền tham dự người vi phạm. |
| FR-AD-07 | Cấu hình hệ thống | Tỷ lệ phí dịch vụ (mặc định 5%), khoảng tiền đặt trước (5%–20%), thời hạn thanh toán sau khi trúng (mặc định 15 phút), danh sách ngày nghỉ lễ. |
| FR-AD-08 | Xem thông tin quản lý chung | Thống kê số phiên theo trạng thái, giao dịch, doanh thu phí. |
| FR-AD-09 | Xem nhật ký | Xem lịch sử trả giá, thay đổi trạng thái và giao dịch tiền (chỉ đọc). |
| FR-AD-10 | Xử lý khiếu nại | Tiếp nhận, xem xét, phản hồi và ghi kết quả xử lý khiếu nại của Bidder và Seller (BR-35). |

3.2. Guest

| **Mã** | **Chức năng** | **Mô tả** |
| ---- | ---- | ---- |
| FR-GU-01 | Xem danh sách phiên | Xem, tìm kiếm, lọc theo trạng thái, phương thức, loại tài sản. |
| FR-GU-02 | Xem chi tiết phiên | Xem toàn bộ thông tin phiên: tài sản, Quy chế, giá hiện tại, lịch sử trả giá, người bán, danh sách Bidder (dưới mã ẩn danh) và trạng thái thanh toán của người trúng. Giá cập nhật real-time. |
| FR-GU-03 | Đăng ký, đăng nhập | Tạo tài khoản hoặc đăng nhập để trở thành User. |

3.3. User (vai trò chung)

| **Mã** | **Chức năng** | **Mô tả** |
| ---- | ---- | ---- |
| FR-US-01 | Đăng xuất, đổi mật khẩu | Kết thúc phiên đăng nhập, đổi mật khẩu. |
| FR-US-02 | Cập nhật thông tin cá nhân | Họ tên, CCCD, liên hệ, tài khoản ngân hàng. |
| FR-US-03 | Xem danh sách và chi tiết phiên | Như Guest. |
| FR-US-04 | Theo dõi phiên | Theo dõi (yêu thích) phiên quan tâm; nhận thông báo trong ứng dụng khi phiên diễn ra. |
| FR-US-05 | Nạp tiền vào ví | Nạp tiền (mô phỏng) vào tài khoản trên hệ thống. |
| FR-US-06 | Xem số dư và lịch sử giao dịch ví | Số dư khả dụng, số dư phong tỏa, danh sách giao dịch. |
| FR-US-07 | Rút tiền về ngân hàng | Rút số dư khả dụng về tài khoản ngân hàng (mô phỏng). |
| FR-US-08 | Nhận thông báo trong ứng dụng | Thông báo về phiên, tiền đặt trước, kết quả, thanh toán. |
| FR-US-09 | Gửi khiếu nại | Bidder đã đăng ký hoặc Seller của phiên gửi khiếu nại về phiên kèm minh chứng; theo dõi trạng thái xử lý. |

3.4. Bidder (người mua)

| **Mã** | **Chức năng** | **Mô tả** |
| ---- | ---- | ---- |
| FR-BI-01 | Đăng ký tham gia phiên | Chọn phiên, cam kết không thuộc diện bị cấm, gửi hồ sơ tham gia. |
| FR-BI-02 | Nộp tiền đặt trước | Nộp từ ví trong thời hạn quy định; tiền bị phong tỏa. |
| FR-BI-03 | Từ chối tham gia khi có thay đổi niêm yết | Khi phiên thay đổi thông tin đã niêm yết, chọn tiếp tục hoặc từ chối và nhận lại tiền đặt trước. |
| FR-BI-04 | Tham gia đấu giá | Vào phòng đấu giá khi phiên diễn ra, theo dõi giá và đồng hồ đếm ngược. |
| FR-BI-05 | Trả giá (tăng dần) | Nhập hoặc chọn mức giá hợp lệ. |
| FR-BI-06 | Chấp nhận giá (giảm dần) | Chấp nhận mức giá hiện hành. |
| FR-BI-07 | Rút lại giá đã trả hoặc đã chấp nhận | Cho phép trước khi công bố người trúng, kèm hậu quả mất cọc và bị truất quyền (BR-14, BR-17, BR-22). |
| FR-BI-08 | Thanh toán khi trúng | Thanh toán phần còn lại ngay sau khi trúng, trong thời hạn ngắn quy định ở BR-26; có thể chủ động hủy (coi như từ chối kết quả). |
| FR-BI-09 | Xác nhận đã nhận tài sản | Xác nhận sau khi nhận tài sản để hệ thống chuyển phần tiền còn lại cho Seller. |
| FR-BI-10 | Xem lịch sử tham gia và kết quả | Danh sách phiên đã tham gia, kết quả thắng hoặc thua, tình trạng tiền đặt trước và thanh toán. |

3.5. Seller (người bán)

| **Mã** | **Chức năng** | **Mô tả** |
| ---- | ---- | ---- |
| FR-SE-01 | Đăng ký hồ sơ Seller | Gửi hồ sơ (họ tên, CCCD, liên hệ, tài khoản ngân hàng), chờ Admin duyệt. |
| FR-SE-02 | Đăng bán tài sản | Nhập thông tin tài sản (tên, mô tả, hình ảnh, loại động sản hoặc bất động sản) và tạo phiên đấu giá. |
| FR-SE-03 | Thiết lập thông số phiên | Chọn phương thức; giá khởi điểm; bước giá (tăng dần) hoặc bước giảm, khoảng thời gian giữa các lần giảm và giá sàn (giảm dần); tỷ lệ đặt trước 5%–20%; thời gian mở và kết thúc. |
| FR-SE-04 | Sửa thông tin niêm yết | Đề xuất thay đổi sau khi niêm yết; Admin duyệt và hệ thống thông báo cho Bidder đã đăng ký. |
| FR-SE-05 | Quản lý tài sản đã đăng | Xem danh sách, trạng thái; hủy phiên trước khi phiên mở (không bị phạt, BR-34). |
| FR-SE-06 | Xem kết quả phiên | Giá trúng, trạng thái thanh toán, biên bản điện tử. |
| FR-SE-07 | Nhận thanh toán | Xem khoản tiền nhận (đã trừ phí dịch vụ) và lịch sử chuyển tiền. |

*Ghi chú: Seller không có chức năng tham dự phiên của chính mình (xem
Mục 2.2).*

4\. Quy tắc nghiệp vụ

Cột “Căn cứ” cho biết điều luật tương ứng trong Luật Đấu giá tài sản;
“Thiết kế” nghĩa là quy tắc do dự án đặt ra, luật không quy định.

4.1. Niêm yết và đăng ký tham gia

| **Mã** | **Quy tắc** | **Căn cứ** |
| ---- | ---- | ---- |
| BR-01 | Mỗi phiên gắn một tài sản và một phương thức (trả giá lên hoặc đặt giá xuống) do Seller chọn; hình thức và phương thức ghi trong Quy chế phiên và công bố công khai. | Điều 34, Điều 40 |
| BR-02 | Chỉ tài sản pháp luật cho phép đấu giá mới được duyệt. Seller đề xuất giá khởi điểm, Admin duyệt. | Điều 4, Điều 8 |
| BR-03 | Thời gian niêm yết tối thiểu 07 ngày làm việc (động sản) hoặc 15 ngày (bất động sản) trước ngày mở phiên. | Điều 35 |
| BR-04 | Không được đăng ký tham gia: người không có hoặc bị hạn chế năng lực hành vi dân sự; người làm trong tổ chức đấu giá và người trực tiếp định giá, giám định tài sản (cùng người thân); người có quyền quyết định bán tài sản, người ký hợp đồng dịch vụ đấu giá (cùng người thân); người không có quyền mua theo pháp luật. Admin và Seller không được đặt thêm điều kiện ngoài luật. | Điều 38 khoản 3, 4 |
| BR-05 | Hồ sơ tham gia gồm phiếu đăng ký và giấy tờ chứng minh điều kiện (nếu pháp luật yêu cầu). Admin thông báo người không đủ điều kiện chậm nhất 01 ngày làm việc trước ngày mở phiên. | Điều 38 khoản 2, 2đ |
| BR-06 | Seller không được đăng ký, trả giá, chấp nhận giá ở phiên của mình. Cấm thông đồng, nâng giá, dìm giá, làm sai lệch kết quả. | Điều 38 khoản 4, Điều 9 |

4.2. Tiền đặt trước

| **Mã** | **Quy tắc** | **Căn cứ** |
| ---- | ---- | ---- |
| BR-07 | Bidder phải nộp tiền đặt trước mới được tham gia. | Điều 39 khoản 1 |
| BR-08 | Mức đặt trước từ 5% đến 20% giá khởi điểm, do Seller đề xuất và Admin duyệt. | Điều 39 khoản 1a |
| BR-09 | Thời hạn nộp: từ ngày niêm yết đến trước ngày mở phiên 02 ngày làm việc. | Điều 39 khoản 2 |
| BR-10 | Tiền đặt trước được phong tỏa trong tài khoản ký quỹ riêng, tách khỏi tiền vận hành; Admin không dùng vào mục đích khác. | Điều 39 khoản 1, Điều 9 khoản 2 |
| BR-11 | Nếu thay đổi giá khởi điểm, số lượng, chất lượng, thời gian, hình thức hoặc phương thức đã niêm yết, hệ thống thông báo Bidder đã đăng ký; mỗi Bidder được chọn tiếp tục hoặc từ chối và nhận lại tiền đặt trước. Ngoài trường hợp này, Bidder không được tự rút khỏi phiên để nhận lại tiền đặt trước. | Điều 39 khoản 3, 6 |
| BR-12 | Trong 03 ngày làm việc kể từ khi phiên kết thúc (xem BR-28), hoàn tiền đặt trước cho: Bidder từ chối do thay đổi niêm yết; Bidder không đáp ứng điều kiện; Bidder không trúng. | Điều 39 khoản 4 |
| BR-13 | Với người trúng, tiền đặt trước chuyển thành tiền đặt cọc; Admin chuyển tiền đặt cọc cho Seller trong 03 ngày làm việc kể từ khi phiên kết thúc. | Điều 39 khoản 5 |
| BR-14 | Bidder không được nhận lại tiền đặt trước khi: đã nộp nhưng không tham dự phiên mà không do bất khả kháng; bị truất quyền do vi phạm; từ chối ký biên bản (nếu hệ thống yêu cầu xác nhận biên bản); rút lại giá đã trả hoặc đã chấp nhận; từ chối kết quả trúng (kể cả hủy hoặc không thanh toán đúng hạn). Không đặt thêm trường hợp khác. Khoản tiền này thuộc về Seller. | Điều 39 khoản 6, 7, 8 |

4.3. Đấu giá tăng dần (trả giá lên)

| **Mã** | **Quy tắc** | **Căn cứ** |
| ---- | ---- | ---- |
| BR-15 | Seller thiết lập giá khởi điểm, bước giá (bắt buộc trong hệ thống), thời gian mở và kết thúc. Lần trả giá đầu phải không thấp hơn giá khởi điểm; các lần sau phải không thấp hơn giá cao nhất hiện tại cộng bước giá. Lượt trả giá được xử lý theo thứ tự thời gian của server; hai lượt cùng một mức giá thì lượt đến trước được chấp nhận, lượt sau bị từ chối. | Điều 40; bước giá bắt buộc là Thiết kế |
| BR-16 | Hết giờ, người trả giá cao nhất là người trúng. Không gia hạn thời gian. | Điều 41; không gia hạn là Thiết kế |
| BR-17 | Nếu người trả giá cao nhất rút lại giá trước khi công bố người trúng, phiên tiếp tục từ mức giá của người trả giá liền kề; người rút bị truất quyền tham dự và mất tiền đặt trước. | Điều 50 khoản 1, 4 |
| BR-18 | Nếu người trúng từ chối kết quả (hoặc hủy, hết hạn không thanh toán): người trả giá liền kề là người trúng nếu giá liền kề cộng với khoản tiền đặt trước (của người từ chối) ít nhất bằng giá của người từ chối và người đó đồng ý mua; nếu không thì phiên không thành. | Điều 51 khoản 1 |

4.4. Đấu giá giảm dần (đặt giá xuống)

| **Mã** | **Quy tắc** | **Căn cứ** |
| ---- | ---- | ---- |
| BR-19 | Seller thiết lập giá khởi điểm (cao nhất), bước giảm, khoảng thời gian giữa các lần giảm và giá sàn. Hệ thống tự giảm giá theo lịch, lấy giờ server làm chuẩn. | Điều 40 khoản 2; bước giảm, giá sàn là Thiết kế |
| BR-20 | Người đầu tiên xác nhận chấp nhận mức giá hiện hành (theo thời gian server) là người trúng. Hệ thống dừng giảm giá và từ chối các yêu cầu đến sau. | Điều 40 khoản 2 |
| BR-21 | Giá chạm giá sàn mà không ai chấp nhận thì phiên không thành. | Điều 52 khoản 1 |
| BR-22 | Nếu người chấp nhận giá rút lại trước khi công bố, hoặc người trúng từ chối kết quả (hủy, hết hạn không thanh toán): phiên tiếp tục từ mức giá của người đó và giá tiếp tục giảm; người rút lại bị truất quyền. Không còn ai chấp nhận thì phiên không thành. | Điều 50 khoản 3, 4; Điều 51 khoản 2 |

4.5. Chỉ có một người và đấu giá không thành

| **Mã** | **Quy tắc** | **Căn cứ** |
| ---- | ---- | ---- |
| BR-23 | Nếu chỉ có một người đăng ký, tham dự, trả giá hoặc chấp nhận giá thì vẫn bán cho người đó (tăng dần: giá không thấp hơn giá khởi điểm; giảm dần: có chấp nhận giá). Chỉ áp dụng khi đã hoàn tất thủ tục và không còn khiếu nại chưa giải quyết đến ngày mở phiên. | Điều 49 |
| BR-24 | Phiên không thành khi: hết hạn đăng ký mà không có người đăng ký; tất cả người đăng ký hợp lệ không tham dự; không có người trả giá hoặc chấp nhận giá; hoặc không còn người kế tiếp sau khi người trúng từ chối (BR-18, BR-22). | Điều 52 khoản 1; phần cuối là Thiết kế |

4.6. Kết thúc, thanh toán, phí và hiển thị

| **Mã** | **Quy tắc** | **Căn cứ** |
| ---- | ---- | ---- |
| BR-25 | Khi xác định có người trúng (hết giờ ở tăng dần, bấm chấp nhận ở giảm dần), hệ thống công bố và chuyển phiên sang “Chờ thanh toán”. Mọi người xem (kể cả Guest) thấy trạng thái “Người trúng đấu giá đang thanh toán” cùng mã ẩn danh và hạn thanh toán, cập nhật real-time. | Thiết kế |
| BR-26 | Người trúng phải thanh toán ngay sau khi trúng: thanh toán phần còn lại (giá trúng trừ tiền đặt cọc) từ ví trong thời hạn ngắn do Admin cấu hình (mặc định 15 phút kể từ lúc công bố). Trong thời gian đó trạng thái “Chờ thanh toán” hiển thị cho mọi người. Thanh toán đủ thì phiên chuyển “Hoàn tất”, trạng thái “Đã thanh toán – phiên kết thúc” hiển thị công khai. | Thiết kế |
| BR-27 | Người trúng hủy thanh toán hoặc hết hạn chưa thanh toán thì coi là từ chối kết quả: mất tiền đặt cọc (BR-14), trạng thái công khai cập nhật ngay, và áp dụng BR-18 (tăng dần) hoặc BR-22 (giảm dần). Phiên chỉ thật sự kết thúc khi có người thanh toán đủ hoặc phiên không thành. | Điều 51; phần còn lại là Thiết kế |
| BR-28 | “Thời điểm phiên kết thúc” dùng để tính hạn hoàn tiền và chuyển cọc (03 ngày làm việc) là lúc phiên chuyển sang “Hoàn tất” hoặc “Không thành”. Trong thời gian “Chờ thanh toán”, tiền đặt trước của các Bidder khác được giữ vì họ có thể trở thành người kế tiếp. | Điều 39 khoản 4, 5; cách xác định mốc là Thiết kế |
| BR-29 | Phí dịch vụ bằng 5% giá trúng (Admin cấu hình), trừ vào số tiền Seller nhận. Ví dụ: giá trúng 100.000.000 đồng, phí 5.000.000 đồng, Seller nhận 95.000.000 đồng. | Điều 66; tỷ lệ là Thiết kế |
| BR-30 | Tiền đặt cọc chuyển cho Seller trong 03 ngày làm việc kể từ khi phiên kết thúc (BR-13). Phần còn lại, sau khi trừ phí, chuyển cho Seller khi Bidder xác nhận đã nhận tài sản (hoặc Admin xác nhận). Khoản phí được trừ ở lần chuyển cuối. | Điều 39 khoản 5; phần còn lại là Thiết kế |
| BR-31 | Mọi Bidder hiển thị bằng mã ẩn danh với tất cả người xem, kể cả Guest và các Bidder khác; chỉ Admin thấy danh tính. Mã ẩn danh là một dãy số (không có chữ), duy nhất trong mỗi phiên, do hệ thống cấp. Lịch sử trả giá hiển thị cho mọi người với mã ẩn danh. Thông tin Seller hiển thị công khai. | Điều 9 khoản 1, 2 (cấm để lộ thông tin người đăng ký) |
| BR-32 | Hệ thống lưu nhật ký không sửa, không xóa: mọi lượt trả giá, chấp nhận giá, thay đổi trạng thái, giao dịch tiền. Khi phiên kết thúc, hệ thống lập biên bản đấu giá điện tử. | Điều 44, Điều 54 |
| BR-33 | Kết quả phiên được đăng công khai và thông báo trong ứng dụng cho người tham gia. | Điều 43b khoản 5 (luật nêu thêm gửi email; dự án chọn thông báo trong ứng dụng) |
| BR-34 | Seller được hủy phiên trước khi phiên mở và không bị phạt. Khi phiên bị hủy (kể cả do Admin hủy vì vi phạm), tiền đặt trước hoàn đầy đủ cho các Bidder không vi phạm. | Thiết kế |
| BR-35 | Bidder đã đăng ký và Seller của phiên được gửi khiếu nại về phiên (trình tự, thủ tục, quyết định, hành vi của Admin hoặc bên kia). Admin tiếp nhận, xử lý, phản hồi và lưu kết quả. Khiếu nại chưa giải quyết thì không bán cho một người duy nhất (BR-23). Thời hạn gửi và thời hạn xử lý chưa chốt. | Điều 49 khoản 2; Điều 74, 75 (tham khảo); còn lại là Thiết kế |

5\. Vòng đời phiên đấu giá và tiền đặt trước

5.1. Trạng thái phiên đấu giá

| **Trạng thái** | **Ý nghĩa** |
| ---- | ---- |
| Chờ duyệt | Seller đã gửi, Admin chưa duyệt. |
| Bị từ chối | Admin từ chối kèm lý do; Seller chỉnh sửa và gửi lại. |
| Đã niêm yết | Đã công khai; nhận đăng ký và tiền đặt trước. |
| Chờ mở phiên | Đã đóng nhận đăng ký (trước ngày mở phiên 02 ngày làm việc); chờ đến giờ mở. |
| Đang diễn ra | Phiên đang chạy: trả giá (tăng dần) hoặc giá đang giảm (giảm dần). |
| Chờ thanh toán | Đã có người trúng, chờ thanh toán. Trạng thái này hiển thị công khai. |
| Hoàn tất | Người trúng đã thanh toán đủ; phiên thật sự kết thúc. |
| Không thành | Phiên kết thúc mà không có người mua. |
| Đã hủy | Seller hoặc Admin hủy trước khi phiên mở, hoặc Admin hủy khi đang diễn ra do vi phạm. |

5.2. Chuyển trạng thái phiên

| **Từ** | **Sự kiện** | **Đến** | **Ghi chú** |
| ---- | ---- | ---- | ---- |
| Chờ duyệt | Admin duyệt | Đã niêm yết | |
| Chờ duyệt | Admin từ chối | Bị từ chối | |
| Bị từ chối | Seller sửa và gửi lại | Chờ duyệt | |
| Đã niêm yết | Hết hạn đăng ký | Chờ mở phiên | 02 ngày làm việc trước ngày mở |
| Chờ mở phiên | Đến giờ mở | Đang diễn ra | |
| Chờ mở phiên | Không có đăng ký hợp lệ | Không thành | BR-24 |
| Đang diễn ra | Có người trúng | Chờ thanh toán | Tăng dần: hết giờ. Giảm dần: có người chấp nhận. |
| Đang diễn ra | Không ai trả giá, chấp nhận hoặc tham dự | Không thành | BR-21, BR-24 |
| Chờ thanh toán | Người trúng thanh toán đủ | Hoàn tất | BR-26 |
| Chờ thanh toán | Người trúng hủy hoặc quá hạn (tăng dần) | Chờ thanh toán | Chuyển sang người liền kề nếu thỏa BR-18 |
| Chờ thanh toán | Người trúng hủy hoặc quá hạn (giảm dần) | Đang diễn ra | Tiếp tục ngay từ mức giá của người từ chối (BR-22) |
| Chờ thanh toán | Không còn người kế tiếp hợp lệ | Không thành | BR-18, BR-22 |
| Đã niêm yết, Chờ mở phiên | Admin hoặc Seller hủy | Đã hủy | Không phạt Seller; hoàn đầy đủ tiền đặt trước (BR-34) |
| Đang diễn ra | Admin hủy do vi phạm | Đã hủy | BR-34 |

5.3. Trạng thái tiền đặt trước

| **Trạng thái** | **Ý nghĩa** | **Chuyển tiếp** |
| ---- | ---- | ---- |
| Đã nộp (phong tỏa) | Tiền trừ từ số dư khả dụng, giữ trong ký quỹ. | Đã hoàn; Chuyển thành cọc; Bị giữ |
| Đã hoàn | Hoàn vào ví trong 03 ngày làm việc kể từ khi phiên kết thúc (BR-12). | Trạng thái cuối |
| Chuyển thành cọc | Của người trúng đấu giá (BR-13). | Đã chuyển cho Seller |
| Bị giữ (mất cọc) | Thuộc các trường hợp ở BR-14. | Đã chuyển cho Seller |
| Đã chuyển cho Seller | Admin chuyển tiền cho Seller (BR-30). | Trạng thái cuối |

6\. Đặc tả use case chính

Các use case dưới đây là cơ sở để vẽ sơ đồ use case và sơ đồ sequence.
Mã BR trong ngoặc là quy tắc nghiệp vụ liên quan ở Mục 4.

UC-01. Đăng ký tham gia phiên và nộp tiền đặt trước

<table style="width:99%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 79%" />
</colgroup>
<tbody>
<tr>
<td><p><strong>Actor</strong></p></td>
<td><p>Bidder; Admin (xét duyệt hồ sơ).</p></td>
</tr>
<tr>
<td><p><strong>Tiền điều kiện</strong></p></td>
<td><p>Phiên ở trạng thái “Đã niêm yết”, còn trong hạn nộp (BR-09).</p>
<p>Bidder không phải Seller của phiên và không thuộc diện bị cấm (BR-04,
BR-06).</p>
<p>Số dư ví khả dụng không thấp hơn mức đặt trước.</p></td>
</tr>
<tr>
<td><p><strong>Luồng chính</strong></p></td>
<td><p>1. Bidder mở chi tiết phiên và chọn “Đăng ký tham gia”.</p>
<p>2. Hệ thống hiển thị Quy chế, mức đặt trước và yêu cầu cam kết không
thuộc diện bị cấm.</p>
<p>3. Bidder xác nhận.</p>
<p>4. Hệ thống kiểm tra điều kiện, phong tỏa tiền đặt trước từ ví.</p>
<p>5. Hệ thống tạo hồ sơ tham gia (“Chờ xét duyệt”) và cấp mã ẩn
danh.</p>
<p>6. Admin xét duyệt; hệ thống thông báo kết quả chậm nhất 01 ngày làm
việc trước ngày mở phiên (BR-05).</p></td>
</tr>
<tr>
<td><p><strong>Luồng thay thế</strong></p></td>
<td><p>4a. Số dư không đủ: thông báo và gợi ý nạp tiền.</p>
<p>4b. Quá hạn nộp: từ chối đăng ký.</p>
<p>6a. Hồ sơ không đạt: thông báo; hoàn tiền đặt trước theo
BR-12.</p></td>
</tr>
<tr>
<td><p><strong>Hậu điều kiện</strong></p></td>
<td><p>Có khoản tiền đặt trước “Đã nộp (phong tỏa)”; Bidder vào được
phòng đấu giá khi phiên mở.</p></td>
</tr>
</tbody>
</table>

UC-02. Trả giá (tăng dần)

<table style="width:99%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 79%" />
</colgroup>
<tbody>
<tr>
<td><p><strong>Actor</strong></p></td>
<td><p>Bidder; hệ thống.</p></td>
</tr>
<tr>
<td><p><strong>Tiền điều kiện</strong></p></td>
<td><p>Phiên “Đang diễn ra”, phương thức trả giá lên; Bidder đã được
duyệt hồ sơ và đã nộp tiền đặt trước.</p></td>
</tr>
<tr>
<td><p><strong>Luồng chính</strong></p></td>
<td><p>1. Bidder vào phòng đấu giá, xem giá hiện tại, bước giá, đồng hồ
đếm ngược.</p>
<p>2. Bidder nhập mức giá (hoặc chọn nhanh giá hiện tại cộng bước
giá).</p>
<p>3. Hệ thống kiểm tra tính hợp lệ theo thứ tự thời gian server
(BR-15).</p>
<p>4. Hệ thống ghi nhận, cập nhật giá cao nhất và đẩy real-time cho mọi
người xem (người trả giá hiển thị bằng mã ẩn danh).</p>
<p>5. Hết giờ, hệ thống xác định người trả giá cao nhất là người trúng
và chuyển phiên sang “Chờ thanh toán” (UC-04).</p></td>
</tr>
<tr>
<td><p><strong>Luồng thay thế</strong></p></td>
<td><p>3a. Giá không hợp lệ hoặc đến sau: từ chối kèm lý do.</p>
<p>3b. Người trả giá là Seller của phiên: từ chối (BR-06).</p>
<p>5a. Không ai trả giá: phiên “Không thành”.</p>
<p>5b. Chỉ một người trả giá hợp lệ và không thấp hơn giá khởi điểm:
người đó trúng (BR-23).</p>
<p>*. Người trả giá cao nhất rút lại trước khi công bố: áp dụng
BR-17.</p></td>
</tr>
<tr>
<td><p><strong>Hậu điều kiện</strong></p></td>
<td><p>Mọi lượt trả giá được ghi nhật ký; có thể xác định người
trúng.</p></td>
</tr>
</tbody>
</table>

UC-03. Chấp nhận giá (giảm dần)

<table style="width:99%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 79%" />
</colgroup>
<tbody>
<tr>
<td><p><strong>Actor</strong></p></td>
<td><p>Bidder; hệ thống.</p></td>
</tr>
<tr>
<td><p><strong>Tiền điều kiện</strong></p></td>
<td><p>Phiên “Đang diễn ra”, phương thức đặt giá xuống; Bidder đã được
duyệt hồ sơ và đã nộp tiền đặt trước.</p></td>
</tr>
<tr>
<td><p><strong>Luồng chính</strong></p></td>
<td><p>1. Bidder vào phòng đấu giá, thấy giá hiện hành giảm theo lịch
(BR-19).</p>
<p>2. Bidder bấm “Chấp nhận giá”.</p>
<p>3. Hệ thống xác nhận yêu cầu đến đầu tiên theo giờ server ở mức giá
hiện hành (BR-20).</p>
<p>4. Hệ thống dừng giảm giá, ghi nhận người trúng, chuyển phiên sang
“Chờ thanh toán” và đẩy real-time.</p></td>
</tr>
<tr>
<td><p><strong>Luồng thay thế</strong></p></td>
<td><p>3a. Có người khác chấp nhận trước: từ chối và thông báo phiên đã
có người chấp nhận.</p>
<p>3b. Người chấp nhận là Seller của phiên: từ chối (BR-06).</p>
<p>*. Giá chạm giá sàn mà không ai chấp nhận: “Không thành” (BR-21).</p>
<p>*. Người chấp nhận rút lại trước khi công bố: áp dụng BR-22.</p></td>
</tr>
<tr>
<td><p><strong>Hậu điều kiện</strong></p></td>
<td><p>Có người trúng ở mức giá đã chấp nhận.</p></td>
</tr>
</tbody>
</table>

UC-04. Thanh toán sau khi trúng

<table style="width:99%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 79%" />
</colgroup>
<tbody>
<tr>
<td><p><strong>Actor</strong></p></td>
<td><p>Bidder trúng; hệ thống; mọi người xem (xem trạng thái).</p></td>
</tr>
<tr>
<td><p><strong>Tiền điều kiện</strong></p></td>
<td><p>Phiên “Chờ thanh toán”.</p></td>
</tr>
<tr>
<td><p><strong>Luồng chính</strong></p></td>
<td><p>1. Hệ thống thông báo cho người trúng; công khai trạng thái
“Người trúng đấu giá đang thanh toán” kèm mã ẩn danh và hạn thanh toán
(BR-25).</p>
<p>2. Tiền đặt trước của người trúng chuyển thành tiền đặt cọc.</p>
<p>3. Người trúng thanh toán ngay phần còn lại (giá trúng trừ tiền cọc)
từ ví, trong thời hạn quy định ở BR-26.</p>
<p>4. Hệ thống ghi nhận; phiên chuyển “Hoàn tất”; trạng thái công khai
cập nhật thành “Đã thanh toán – phiên kết thúc”; lập biên bản điện
tử.</p>
<p>5. Hệ thống kích hoạt UC-06.</p></td>
</tr>
<tr>
<td><p><strong>Luồng thay thế</strong></p></td>
<td><p>3a. Người trúng chủ động hủy hoặc quá hạn: coi là từ chối kết quả
(BR-27), mất cọc, trạng thái công khai cập nhật.</p>
<p>3a-1. Tăng dần: xét người trả giá liền kề theo BR-18; nếu hợp lệ và
đồng ý thì lặp lại từ bước 1 với người đó, nếu không thì “Không
thành”.</p>
<p>3a-2. Giảm dần: phiên “Đang diễn ra” trở lại ngay, tiếp tục từ mức
giá của người từ chối (BR-22); nếu không ai chấp nhận thì “Không
thành”.</p>
<p>3b. Số dư không đủ nhưng còn trong thời hạn: yêu cầu nạp tiền
ngay.</p></td>
</tr>
<tr>
<td><p><strong>Hậu điều kiện</strong></p></td>
<td><p>Phiên “Hoàn tất” hoặc tiếp tục/không thành theo luồng thay thế;
mọi thay đổi trạng thái hiển thị công khai.</p></td>
</tr>
</tbody>
</table>

UC-05. Seller đăng tài sản và Admin duyệt

<table style="width:99%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 79%" />
</colgroup>
<tbody>
<tr>
<td><p><strong>Actor</strong></p></td>
<td><p>Seller; Admin.</p></td>
</tr>
<tr>
<td><p><strong>Tiền điều kiện</strong></p></td>
<td><p>User đã được duyệt hồ sơ Seller. Nếu chưa: User gửi hồ sơ Seller
(FR-SE-01), Admin duyệt hoặc từ chối (FR-AD-01).</p></td>
</tr>
<tr>
<td><p><strong>Luồng chính</strong></p></td>
<td><p>1. Seller nhập thông tin tài sản: tên, mô tả, hình ảnh, loại
(động sản hoặc bất động sản).</p>
<p>2. Seller chọn phương thức và nhập thông số: giá khởi điểm; bước giá
(tăng dần) hoặc bước giảm, khoảng thời gian, giá sàn (giảm dần); tỷ lệ
đặt trước 5%–20%; thời gian mở phiên thỏa BR-03.</p>
<p>3. Seller gửi duyệt; phiên “Chờ duyệt”.</p>
<p>4. Admin kiểm tra tài sản, giá và Quy chế phiên (sinh tự động từ cấu
hình).</p>
<p>5. Admin duyệt; phiên “Đã niêm yết” và công khai.</p></td>
</tr>
<tr>
<td><p><strong>Luồng thay thế</strong></p></td>
<td><p>4a. Admin từ chối kèm lý do: phiên “Bị từ chối”; Seller chỉnh sửa
và gửi lại.</p>
<p>2a. Thời gian mở phiên không đủ thời gian niêm yết: hệ thống từ chối
gửi duyệt.</p></td>
</tr>
<tr>
<td><p><strong>Hậu điều kiện</strong></p></td>
<td><p>Phiên được niêm yết, bắt đầu nhận đăng ký và tiền đặt
trước.</p></td>
</tr>
</tbody>
</table>

UC-06. Kết thúc phiên, hoàn tiền và chuyển tiền cho Seller

<table style="width:99%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 79%" />
</colgroup>
<tbody>
<tr>
<td><p><strong>Actor</strong></p></td>
<td><p>Admin; hệ thống.</p></td>
</tr>
<tr>
<td><p><strong>Tiền điều kiện</strong></p></td>
<td><p>Phiên “Hoàn tất” hoặc “Không thành” (BR-28).</p></td>
</tr>
<tr>
<td><p><strong>Luồng chính</strong></p></td>
<td><p>1. Hệ thống xác định thời điểm phiên kết thúc.</p>
<p>2. Trong 03 ngày làm việc, hoàn tiền đặt trước cho Bidder không
trúng, bị loại hồ sơ hoặc đã từ chối do thay đổi niêm yết (BR-12).</p>
<p>3. Trong 03 ngày làm việc, chuyển tiền đặt cọc của người trúng cho
Seller (BR-13).</p>
<p>4. Chuyển cho Seller các khoản tiền đặt trước bị giữ (BR-14).</p>
<p>5. Khi Bidder xác nhận đã nhận tài sản, chuyển phần còn lại cho
Seller sau khi trừ phí 5% (BR-29, BR-30).</p>
<p>6. Ghi giao dịch, nhật ký và thông báo cho các bên.</p></td>
</tr>
<tr>
<td><p><strong>Luồng thay thế</strong></p></td>
<td><p>*. Phiên “Không thành”: hoàn toàn bộ tiền đặt trước, trừ các
trường hợp mất cọc theo BR-14.</p></td>
</tr>
<tr>
<td><p><strong>Hậu điều kiện</strong></p></td>
<td><p>Mọi khoản tiền đặt trước ở trạng thái cuối; Seller nhận
tiền.</p></td>
</tr>
</tbody>
</table>

UC-07. Thay đổi thông tin niêm yết

<table style="width:99%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 79%" />
</colgroup>
<tbody>
<tr>
<td><p><strong>Actor</strong></p></td>
<td><p>Seller; Admin; Bidder đã đăng ký.</p></td>
</tr>
<tr>
<td><p><strong>Tiền điều kiện</strong></p></td>
<td><p>Phiên “Đã niêm yết” hoặc “Chờ mở phiên”.</p></td>
</tr>
<tr>
<td><p><strong>Luồng chính</strong></p></td>
<td><p>1. Seller đề xuất thay đổi (giá khởi điểm, số lượng, chất lượng,
thời gian, phương thức...).</p>
<p>2. Admin duyệt thay đổi.</p>
<p>3. Hệ thống cập nhật thông tin và thông báo cho mọi Bidder đã đăng ký
(BR-11).</p>
<p>4. Mỗi Bidder chọn “Tiếp tục tham gia” hoặc “Từ chối và nhận lại tiền
đặt trước”.</p></td>
</tr>
<tr>
<td><p><strong>Luồng thay thế</strong></p></td>
<td><p>4a. Bidder từ chối: tiền đặt trước hoàn theo BR-12. Bidder không
phản hồi: coi là tiếp tục tham gia.</p></td>
</tr>
<tr>
<td><p><strong>Hậu điều kiện</strong></p></td>
<td><p>Danh sách Bidder còn tham gia được cập nhật.</p></td>
</tr>
</tbody>
</table>

UC-08. Gửi và xử lý khiếu nại

<table style="width:99%;">
<colgroup>
<col style="width: 20%" />
<col style="width: 79%" />
</colgroup>
<tbody>
<tr>
<td><p><strong>Actor</strong></p></td>
<td><p>Bidder đã đăng ký hoặc Seller của phiên; Admin.</p></td>
</tr>
<tr>
<td><p><strong>Tiền điều kiện</strong></p></td>
<td><p>Người gửi đã đăng nhập và là Bidder đã đăng ký hoặc Seller của
phiên.</p></td>
</tr>
<tr>
<td><p><strong>Luồng chính</strong></p></td>
<td><p>1. Người dùng chọn phiên, chọn “Gửi khiếu nại”, nhập nội dung và
minh chứng.</p>
<p>2. Hệ thống ghi nhận (trạng thái “Mới”) và thông báo cho Admin.</p>
<p>3. Admin xem xét (“Đang xử lý”), có thể yêu cầu bổ sung thông
tin.</p>
<p>4. Admin ghi kết quả xử lý và phản hồi (“Đã xử lý”).</p>
<p>5. Hệ thống thông báo kết quả cho người khiếu nại.</p></td>
</tr>
<tr>
<td><p><strong>Luồng thay thế</strong></p></td>
<td><p>3a. Khiếu nại không thuộc phạm vi: Admin từ chối kèm lý do.</p>
<p>3b. Khiếu nại cho thấy có vi phạm: Admin có thể tạm dừng, hủy phiên
hoặc truất quyền người vi phạm (FR-AD-06).</p></td>
</tr>
<tr>
<td><p><strong>Hậu điều kiện</strong></p></td>
<td><p>Khiếu nại và kết quả xử lý được lưu; khiếu nại chưa giải quyết
ảnh hưởng đến BR-23.</p></td>
</tr>
</tbody>
</table>

7\. Thực thể dữ liệu chính

Bảng dưới là cơ sở để vẽ ERD hoặc sơ đồ lớp. Các thuộc tính chỉ nêu mức
chính, sẽ chi tiết hóa khi thiết kế cơ sở dữ liệu.

| **Thực thể** | **Thuộc tính chính** | **Quan hệ, ghi chú** |
| ---- | ---- | ---- |
| User | id, tên đăng nhập, email, mật khẩu (băm), họ tên, CCCD, số điện thoại, tài khoản ngân hàng, vai trò hệ thống (User/Admin), trạng thái. | Một User có một Wallet; có thể là Seller và Bidder. |
| SellerProfile | id, userId, thông tin hồ sơ, trạng thái duyệt, lý do từ chối, người duyệt, ngày duyệt. | Một User có tối đa một hồ sơ Seller. |
| Asset | id, sellerId, tên, mô tả, hình ảnh, loại (động sản/bất động sản). | Mỗi Asset thuộc một Seller. |
| Auction | id, assetId, phương thức, giá khởi điểm, bước giá hoặc bước giảm, khoảng thời gian giảm, giá sàn, tỷ lệ đặt trước, thời gian niêm yết/mở/kết thúc, trạng thái, giá hiện tại, Quy chế. | Một Asset có một Auction đang hoạt động. |
| Registration | id, auctionId, bidderId, mã ẩn danh (dãy số), trạng thái hồ sơ, thời điểm đăng ký, lựa chọn khi có thay đổi niêm yết. | Mỗi User đăng ký một lần cho mỗi Auction. |
| Deposit | id, registrationId, số tiền, trạng thái (xem 5.3), thời điểm. | Một Registration có một Deposit. |
| Bid | id, auctionId, bidderId, số tiền, loại (trả giá/chấp nhận giá), thời gian server, hợp lệ hay không. | Bất biến, không xóa. |
| Settlement | id, auctionId, bidderId, giá trúng, tiền cọc, số tiền còn phải trả, hạn thanh toán, trạng thái (chờ/đã trả/hủy/quá hạn). | Một Auction có thể có nhiều Settlement liên tiếp khi người trúng từ chối. |
| Wallet | id, userId, số dư khả dụng, số dư phong tỏa. | Số dư không được âm. |
| Transaction | id, walletId, loại (nạp, rút, phong tỏa, hoàn, cọc, thanh toán, phí, chuyển cho Seller), số tiền, tham chiếu, thời gian. | Bất biến; mọi thay đổi số dư phải có Transaction. |
| Notification | id, userId, nội dung, loại, đã đọc, thời gian. | Thông báo trong ứng dụng. |
| AuditLog | id, đối tượng, hành động, người thực hiện, thời gian, dữ liệu trước và sau. | Chỉ ghi thêm. |
| Complaint | id, auctionId, userId, nội dung, minh chứng, trạng thái (Mới, Đang xử lý, Đã xử lý, Từ chối), kết quả xử lý, người xử lý, thời gian. | Chỉ Bidder đã đăng ký hoặc Seller của phiên được gửi. |
| SystemConfig | tỷ lệ phí, khoảng đặt trước, hạn thanh toán, ngày nghỉ lễ. | Do Admin cấu hình. |

8\. Yêu cầu phi chức năng

| **Mã** | **Nhóm** | **Yêu cầu** |
| ---- | ---- | ---- |
| NFR-01 | Thời gian thực | Giá, trạng thái phiên và đồng hồ đếm ngược cập nhật real-time cho mọi người xem (đề xuất dùng WebSocket). Mục tiêu độ trễ không quá 2 giây trong điều kiện bình thường; con số cần kiểm thử lại. |
| NFR-02 | Đồng bộ thời gian | Server là nguồn thời gian duy nhất để xét thứ tự trả giá, chấp nhận giá và lịch giảm giá; đồng hồ phía client hiển thị theo giờ server. |
| NFR-03 | Đồng thời | Xử lý trả giá và chấp nhận giá nguyên tử (giao dịch cơ sở dữ liệu hoặc khóa), bảo đảm không có hai người cùng thắng. |
| NFR-04 | Bảo mật | Dùng HTTPS; mật khẩu băm; phân quyền theo vai trò; thông tin cá nhân (CCCD, tài khoản ngân hàng) chỉ Admin xem được; ẩn danh Bidder theo BR-31; phòng chống SQL injection, XSS, CSRF. |
| NFR-05 | Toàn vẹn tiền | Mọi thay đổi số dư thông qua Transaction bất biến; số dư không âm; có thể đối soát tiền phong tỏa với tiền đặt trước. |
| NFR-06 | Nhật ký và lưu trữ | Lưu nhật ký trả giá, thay đổi trạng thái và giao dịch; không sửa, không xóa (BR-32). |
| NFR-07 | Công nghệ | Ngôn ngữ Java. Đề xuất: Spring Boot, MySQL (hoặc PostgreSQL), WebSocket. Giao diện web chưa chỉ định. |
| NFR-08 | Giao diện | Web, tiếng Việt, hiển thị tiền VND định dạng rõ ràng, dùng được trên máy tính và điện thoại. |
| NFR-09 | Hiệu năng | Mỗi phiên phải phục vụ ổn định ít nhất 100 người đồng thời (xem và trả giá), vẫn giữ độ trễ ở NFR-01. |

9\. Các quyết định đã chốt và điểm còn cần xác nhận

9.1. Các quyết định đã chốt

| **\#** | **Nội dung** | **Quyết định** | **Áp dụng tại** |
| ---- | ---- | ---- | ---- |
| 1 | Phân biệt Guest và User | Guest là người chưa đăng nhập; chỉ trở thành User khi đã đăng nhập. | Mục 2.1 |
| 2 | Hiển thị Bidder | Mã ẩn danh là dãy số, không có chữ. | BR-31 |
| 3 | Thông báo | Chỉ trong ứng dụng. Lưu ý: Điều 43b khoản 5 nêu thêm việc gửi email; dự án chọn không gửi. | BR-33 |
| 4 | Mốc “kết thúc phiên” | Tính từ lúc phiên “Hoàn tất” hoặc “Không thành”. | BR-28 |
| 5 | Người trúng hủy hoặc quá hạn | Làm theo luật: tăng dần chuyển người trả giá liền kề (Điều 51), giảm dần tiếp tục từ mức giá của người từ chối. | BR-18, BR-22 |
| 6 | Giảm dần sau khi người trúng hủy | Tiếp tục ngay. | BR-22, Mục 5.2 |
| 7 | Thanh toán | Thanh toán ngay sau khi trúng; giai đoạn chờ thanh toán hiển thị cho mọi người. | BR-25, BR-26 |
| 8 | Seller hủy phiên | Không bị phạt; hoàn đầy đủ tiền đặt trước cho Bidder. | BR-34 |
| 9 | Khiếu nại, tranh chấp | Có chức năng khiếu nại. | FR-US-09, FR-AD-10, BR-35, UC-08 |
| 10 | Bidder tự rút khỏi phiên | Theo luật: chỉ được nhận lại tiền đặt trước khi có thay đổi niêm yết; không tham dự thì mất cọc. | BR-11, BR-14 |
| 11 | Hai lượt trả giá cùng mức giá | Lượt đến trước theo giờ server được chấp nhận. | BR-15 |
| 12 | Quy mô | 100 người đồng thời trong một phiên. | NFR-09 |

9.2. Chức năng thay đổi so với bản nháp SRS ban đầu

- **Bỏ:** “Seller tham dự cuộc đấu giá của bản thân” (trái Điều 38 khoản
  4 và Điều 9).

- **Thêm:** rút tiền về ngân hàng (mô phỏng), xem lịch sử giao dịch ví,
  Admin xét duyệt hồ sơ tham gia, Admin cấu hình hệ thống, nhật ký, Quy
  chế phiên sinh tự động, biên bản điện tử, xác nhận nhận tài sản, khiếu
  nại.

- **Sửa:** hạn 2 ngày và 3 ngày chuyển thành ngày làm việc; bổ sung mức
  đặt trước 5%–20% và danh sách các trường hợp mất cọc.

9.3. Giả định mình đặt, cần bạn xác nhận

| **\#** | **Giả định** | **Ghi chú** |
| ---- | ---- | ---- |
| 1 | Bạn chọn “thanh toán ngay sau khi trúng” nhưng chưa nêu thời hạn cụ thể. SRS đặt mặc định **15 phút** kể từ lúc công bố, Admin cấu hình được. | BR-26. Có thể đổi thành số khác hoặc cho Seller chọn. |
| 2 | Phần tiền còn lại chuyển cho Seller sau khi Bidder xác nhận đã nhận tài sản (hoặc Admin xác nhận). | BR-30. Bạn chưa trả lời mục này nên giữ mặc định. |
| 3 | “100 người” được hiểu là số người đồng thời (xem và trả giá) trong một phiên. | NFR-09. |
| 4 | Thời hạn gửi khiếu nại và thời hạn Admin xử lý chưa chốt. Luật có thời hạn 30 ngày cho khiếu nại đối với Hội đồng đấu giá tài sản (Điều 75), có thể tham khảo. | BR-35. |

---
name: "🐛 Báo cáo sự cố / Lỗi phần mềm (Bug Report)"
about: "Báo cáo lỗi phát sinh trong quá trình kiểm thử hoặc vận hành theo chuẩn QA Bug Life Cycle"
title: "[BUG] [Mã-Module]: Tóm tắt ngắn gọn lỗi phát sinh"
labels: ["bug"]
assignees: ""
---

## 🚨 1. MỨC ĐỘ NGHIÊM TRỌNG (SEVERITY)
- [ ] **Critical (Blocker):** Sập hệ thống / Mất dữ liệu / Chặn hoàn toàn luồng nghiệp vụ (SLA: 2-4h).
- [ ] **Major (High):** Sai lệch nghiệp vụ chính nhưng vẫn có giải pháp tạm thời (SLA: 24h).
- [ ] **Medium (Normal):** Lỗi chức năng phụ, không ảnh hưởng luồng chính (SLA: 2-3 ngày).
- [ ] **Minor (Low):** Lỗi giao diện, sai chính tả, thẩm mỹ (SLA: Cuối sprint).

---

## 🔍 2. CHI TIẾT LỖI & CÁC BƯỚC TÁI HIỆN (STEPS TO REPRODUCE)
- **Mã kịch bản kiểm thử (Test Case ID):** `TC-XXXX-XX`
- **Phân hệ / URL bị ảnh hưởng:** 

### Các bước tái hiện:
1. Đăng nhập với quyền `...`
2. Truy cập màn hình `...`
3. Nhập dữ liệu: `...`
4. Nhấn nút `...`

---

## ⚖️ 3. KẾT QUẢ MONG ĐỢI VS KẾT QUẢ THỰC TẾ
- **Kết quả mong đợi (Expected Result):** 
- **Kết quả thực tế (Actual Result):** 

---

## 📸 4. ẢNH CHỤP / VIDEO / LOG LỖI
*Đính kèm ảnh chụp màn hình hoặc log console/backend tại đây:*
```text
[Dán log hoặc stack trace nếu có]
```

---

## 💻 5. MÔI TRƯỜNG PHÁT SINH
- **Môi trường:** [ ] Local Dev [ ] Staging / UAT [ ] Production
- **Trình duyệt / OS:** Chrome / Edge / Windows / MacOS
- **Phiên bản (Build/Commit):** 

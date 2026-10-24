// Admin Console Logic
function approveAuction(btn) {
  const row = btn.closest('tr');
  const title = row.querySelector('b').innerText;
  if (confirm(`Xác nhận duyệt niêm yết phiên: "${title}"?\nHệ thống sẽ tự động sinh Quy chế phiên và chuyển trạng thái sang Đã niêm yết.`)) {
    btn.disabled = true;
    btn.innerText = "Đã Duyệt ✅";
    btn.style.background = "#10b981";
    btn.style.color = "#fff";
  }
}

function rejectAuction(btn) {
  const reason = prompt("Nhập lý do từ chối phiên đấu giá:");
  if (reason) {
    const row = btn.closest('tr');
    btn.disabled = true;
    btn.innerText = "Đã Từ Chối ❌";
    btn.style.background = "#ef4444";
    btn.style.color = "#fff";
    alert("Đã gửi thông báo kèm lý do từ chối cho Người bán!");
  }
}

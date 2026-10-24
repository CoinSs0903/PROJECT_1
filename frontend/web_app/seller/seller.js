// Seller Portal Logic
function openCreateModal() {
  const assetName = prompt("Nhập tên tài sản bạn muốn đăng bán:");
  if (assetName) {
    const startPrice = prompt("Nhập giá khởi điểm đề xuất (VNĐ):", "1000000000");
    if (startPrice) {
      alert(`Đã gửi tài sản "${assetName}" đến Admin duyệt niêm yết!\nTheo quy định BR-03: Admin sẽ thẩm định tối thiểu 7 ngày làm việc (động sản) hoặc 15 ngày (bất động sản) trước ngày mở phiên.`);
    }
  }
}
